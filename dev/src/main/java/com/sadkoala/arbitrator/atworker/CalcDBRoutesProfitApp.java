package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.PairPriceSlice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalcDBRoutesProfitApp {

    private static final Logger log = LogManager.getLogger(CalcDBRoutesProfitApp.class);

    private static Connection conn = null;

    private static final String TABLE_NAME_ROUTE_PROFITS = "at_profit";

    private static final String SQL_CHECK_ROUTE_PROFITS_TABLE_EXISTS = "select name from sqlite_master where type='table' and name='" + TABLE_NAME_ROUTE_PROFITS + "'";

    public static void main(String[] args) throws SQLException {

        log.info("CalcDBRoutesProfitApp start");

        conn = GlobalResources.openWorkerDbConnection("db/atworker.db", false);
        checkRouteProfitTableExists();
        Long lastProcessedTimestamp = readLastProcessedTimestamp();

        long timestampUntil = lastProcessedTimestamp + 3600000;

        List<PairPriceSlice> priceList = readPairPrices(lastProcessedTimestamp, timestampUntil);

        if (priceList.isEmpty()) {
            log.info("finding next min timestamp");
            Statement minTsStmt = conn.createStatement();
            ResultSet minTsRs = minTsStmt.executeQuery("select min(timestamp) from at_prices where timestamp > ?");
            minTsRs.next();
            lastProcessedTimestamp = minTsRs.getLong(1)-1; // minus 1 because query has > condition, not >=
            log.info("next min timestamp found = " + lastProcessedTimestamp);
        }

        while (priceList == null /*|| !priceList.isEmpty()*/) {
            // if last processed timestamp not set - find minimal timestamp to start with
            if (0 == lastProcessedTimestamp) {
                log.info("finding min timestamp");
                Statement minTsStmt = conn.createStatement();
                ResultSet minTsRs = minTsStmt.executeQuery("select min(timestamp) from at_prices");
                minTsRs.next();
                lastProcessedTimestamp = minTsRs.getLong(1)-1; // minus 1 because query has > condition, not >=
                log.info("min timestamp found = " + lastProcessedTimestamp);
            }

                // query bunch of price records
            long timestampUntil = lastProcessedTimestamp + 3600000;
            PreparedStatement pricesBatchStmt = conn.prepareStatement("select timestamp, pair, best_ask, best_bid from at_prices where timestamp > ? and timestamp <= ? order by timestamp");
            pricesBatchStmt.setLong(1, lastProcessedTimestamp);
            pricesBatchStmt.setLong(2, timestampUntil);
            ResultSet pricesBatchRs = pricesBatchStmt.executeQuery();
            log.info("pricesBatch query executed");

            lastProcessedTimestamp = timestampUntil;

            priceList = new ArrayList<>();

            // put price records to array
            while (pricesBatchRs.next()) {
                priceList.add(new PairPriceSlice(
                        pricesBatchRs.getLong(1),
                        pricesBatchRs.getString(2),
                        pricesBatchRs.getBigDecimal(3),
                        pricesBatchRs.getBigDecimal(4)
                ));
            }
            pricesBatchRs.close();
            log.info("priceList size=" + priceList.size());

            // find pair objects for each record
            String pairNameDb;
            for (int i = 0; i < priceList.size(); i++) {
                pairNameDb = priceList.get(i).getPairNameDb();
                for (int j = 0; j < Global.baseTokensStrList.size(); j++) {
                    String baseTokenStr = Global.baseTokensStrList.get(j);
                    if (pairNameDb.endsWith(baseTokenStr)) {
                        priceList.get(i).setPair(Global.findPairByTokenNames(pairNameDb.split(baseTokenStr)[0], baseTokenStr).get());
                        break;
                    }
                }
                log.info("timestamp=" + priceList.get(i) + " pairNameDb=" + pairNameDb + " pair not found");
            }
            log.info("priceList pairs found");

            //separate by timestamp
            List<PairPriceSlice> ppsTs = new ArrayList<>();
            Long prevTs = 0l;
            for (PairPriceSlice pps : priceList) {
                if (prevTs != 0 && pps.getTimestamp() != prevTs) {
                    // process bunch
                    processTimestampBunch(ppsTs);
                    ppsTs.clear();
                    break;// for testing on one bunch
                }
                ppsTs.add(pps);
                prevTs = pps.getTimestamp();
            }

            Statement updateTsStmt = conn.createStatement();
            updateTsStmt.executeUpdate("insert into worker_state(key, value) values ('last_processed_price_timestamp', ?) on conflict(key) do update set value = ? where key = 'last_processed_price_timestamp';");

        }

        /*
        * we need app property - last processed by calc routes timestamp
        * we need table - with profits found
        *
        * connect worker db
        *
        * if "last processed timestamp" property found, start with saved value. otherwise start with 0 value
        * find min timestamp greater then last processed timestamp.
        * if no such timestamp found - finish processing
        * fetch all price records with current timestamp
        * for each route
        *   fill price array for each token pairs
        *   if any price item not exist - exclude route calc for current timestamp
        *   calc route for current timestamp
        *       if profit found - add record to profits table
        * update "last processed timestamp" property
        *
        * */



        GlobalResources.closeWorkerDbConnection();
        log.info("CalcDBRoutesProfitApp finish");
    }

    private static void checkRouteProfitTableExists() throws SQLException {
        // check route profits table exists
        Statement tableCheckStmt = conn.createStatement();
        ResultSet tableCheckRs = tableCheckStmt.executeQuery(SQL_CHECK_ROUTE_PROFITS_TABLE_EXISTS);
        if (!tableCheckRs.next()) {
            // create table for processing results
            log.info("Table " + TABLE_NAME_ROUTE_PROFITS + " not found. Creating table.");
            Statement tableCreateStmt = conn.createStatement();
            tableCreateStmt.executeUpdate("create table " + TABLE_NAME_ROUTE_PROFITS + " (timestamp integer not null, route text not null, profit_pct integer not null);");
            conn.commit();
            tableCreateStmt.close();
        }
        tableCheckRs.close();
    }

    private static Long readLastProcessedTimestamp() throws SQLException {
        // read property "last processed timestamp"
        Statement readLastProcessedStmt = conn.createStatement();
        ResultSet readLastProcessedRs = readLastProcessedStmt.executeQuery("select value from worker_state where key = 'last_processed_price_timestamp'");
        long lastProcessedTimestamp = readLastProcessedRs.next() ? Long.valueOf(readLastProcessedRs.getString(1)) : 0l;
        log.info("lastProcessedTimestamp=" + lastProcessedTimestamp);
        readLastProcessedRs.close();
        return lastProcessedTimestamp;
    }

    private static List<PairPriceSlice> readPairPrices(Long timestampStart, Long timestampEnd) throws SQLException {
        PreparedStatement pricesBatchStmt = conn.prepareStatement("select timestamp, pair, best_ask, best_bid from at_prices where timestamp > ? and timestamp <= ? order by timestamp");
        pricesBatchStmt.setLong(1, timestampStart);
        pricesBatchStmt.setLong(2, timestampEnd);
        ResultSet pricesBatchRs = pricesBatchStmt.executeQuery();
        log.info("pricesBatch query executed");

        List<PairPriceSlice> priceList = new ArrayList<>();

        // put price records to array
        while (pricesBatchRs.next()) {
            priceList.add(new PairPriceSlice(
                    pricesBatchRs.getLong(1),
                    pricesBatchRs.getString(2),
                    pricesBatchRs.getBigDecimal(3),
                    pricesBatchRs.getBigDecimal(4)
            ));
        }
        pricesBatchRs.close();
        log.info("priceList size=" + priceList.size());
        return priceList;
    }

    private static void processTimestampBunch(List<PairPriceSlice> pps) {
        log.info("processTsBunch start");
    }

}
