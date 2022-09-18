package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CalcDBRoutesProfitApp {

    private static final Logger log = LogManager.getLogger(CalcDBRoutesProfitApp.class);

    private static Connection conn = null;

    private static final String TABLE_NAME_ROUTE_PROFITS = "at_profit";
    private static final String SQL_CHECK_ROUTE_PROFITS_TABLE_EXISTS = "select name from sqlite_master where type='table' and name='" + TABLE_NAME_ROUTE_PROFITS + "'";
    private static String tableNamePrices = "at_prices";
    private static Integer timestampRangeHours = 3;

    public static void main(String[] args) throws SQLException {

        log.info("CalcDBRoutesProfitApp start");

        conn = GlobalResources.openWorkerDbConnection("db/atworker.db", false);
        checkRouteProfitTableExists();
        Long lastProcessedTimestamp = readLastProcessedTimestamp();

        Long recCount = 0L;

        while (true) {
            Long timestampUntil = lastProcessedTimestamp + timestampRangeHours*3600000;
            List<PairPriceSlice> priceList = readPairPrices(lastProcessedTimestamp, timestampUntil);

            if (priceList.isEmpty()) {
                Long nextMinTs = findNextMinTimestamp(timestampUntil);
                if (nextMinTs == 0) {
                    log.info("no more records. end of processing");
                    break; // exit
                } else {
                    lastProcessedTimestamp = nextMinTs-1;
                }
            } else {
                findPairObjects(priceList);
                processSelectedBunch(priceList);
                lastProcessedTimestamp = timestampUntil;
            }

            recCount = recCount + priceList.size();
            updateLastProcessedTimestamp(lastProcessedTimestamp);
            //break; // for test on one selection
        }

        GlobalResources.closeWorkerDbConnection();
        log.info("records processed = " + recCount);
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
        Long lastProcessedTimestamp = 0L;
        if (readLastProcessedRs.next() && readLastProcessedRs.getString(1) != null) {
            lastProcessedTimestamp = Long.valueOf(readLastProcessedRs.getString(1));
        }
        log.info("lastProcessedTimestamp=" + lastProcessedTimestamp);
        readLastProcessedRs.close();
        return lastProcessedTimestamp;
    }

    private static List<PairPriceSlice> readPairPrices(Long timestampStart, Long timestampEnd) throws SQLException {
        PreparedStatement pricesBatchStmt = conn.prepareStatement("select timestamp, pair, best_ask, best_bid from " + tableNamePrices + " where timestamp > ? and timestamp <= ? order by timestamp");
        pricesBatchStmt.setLong(1, timestampStart);
        pricesBatchStmt.setLong(2, timestampEnd);
        ResultSet pricesBatchRs = pricesBatchStmt.executeQuery();
        log.debug("pricesBatch query executed");

        List<PairPriceSlice> priceList = new ArrayList<>();

        // put price records to array
        while (pricesBatchRs.next()) {
            priceList.add(new PairPriceSlice(
                    pricesBatchRs.getLong(1),
                    pricesBatchRs.getString(2),
                    new BigDecimal2(pricesBatchRs.getString(3)),
                    new BigDecimal2(pricesBatchRs.getString(4))
            ));
        }
        pricesBatchRs.close();
        log.debug("priceList size=" + priceList.size());
        return priceList;
    }

    private static Long findNextMinTimestamp(Long prevTimestamp) throws SQLException {
        log.debug("finding next min timestamp");
        PreparedStatement minTsStmt = conn.prepareStatement("select min(timestamp) from " + tableNamePrices + " where timestamp > ?");
        minTsStmt.setLong(1, prevTimestamp);
        ResultSet minTsRs = minTsStmt.executeQuery();
        Long nextMinTimestamp = minTsRs.next() ? minTsRs.getLong(1) : 0;
        log.debug("next min timestamp found = " + nextMinTimestamp);
        return nextMinTimestamp;
    }

    private static void findPairObjects(List<PairPriceSlice> priceList) {
        // find pair objects for each record
        String pairNameDb;
        for (PairPriceSlice slice : priceList) {
            pairNameDb = slice.getPairNameDb();
            log.debug("pairNameDb="+pairNameDb);
            for (String baseTokenStr : Global.baseTokensStrList) {
                log.debug("baseTokenStr="+baseTokenStr);
                if (pairNameDb.endsWith(baseTokenStr)) {
                    String firstTokenStr = pairNameDb.split(baseTokenStr)[0];
                    log.debug("firstTokenStr="+firstTokenStr);
                    Optional<Pair> pairOpt = Global.findPairByTokenNames(firstTokenStr, baseTokenStr);
                    if (pairOpt.isPresent()) {
                        slice.setPair(pairOpt.get());
                    }
                    break;
                }
            }
            if (slice.getPair() == null) {
                log.debug("timestamp=" + slice.getTimestamp() + " pairNameDb=" + pairNameDb + " pair not found");
            } else {
                log.debug("timestamp=" + slice.getTimestamp() + " pairNameDb=" + pairNameDb + " pair found");
            }
        }
        log.debug("priceList pairs found");
    }

    private static void processSelectedBunch(List<PairPriceSlice> priceList) {
        //separate by timestamp
        List<PairPriceSlice> ppsTs = new ArrayList<>();
        Long prevTs = 0L;
        for (PairPriceSlice pps : priceList) {
            if (prevTs != 0 && !pps.getTimestamp().equals(prevTs)) {
                // process bunch
                processTimestampBunch(ppsTs);
                ppsTs.clear();
                //return; // for testing on one bunch
            }
            ppsTs.add(pps);
            prevTs = pps.getTimestamp();
        }
        processTimestampBunch(ppsTs); // for the last timestamp bunch in the selected bunch
    }

    private static void processTimestampBunch(List<PairPriceSlice> ppsList) {
        log.debug("processTsBunch start");

        List<PairPriceSlice> routePpsList = new ArrayList<>();
        PairPriceSlice routePps;
        routelabel:
        for (Route route : Global.routeList) {
            // find route price slice list
            routePpsList.clear();
            for (Pair pair : route.getPairList()) {
                routePps = null;
                for (PairPriceSlice pps : ppsList) {
                    if (pair.getId().equals(pps.getPair().getId())) {
                        routePps = pps;
                        break;
                    }
                }
                if (routePps == null) {
                    log.debug("not found pps for route " + route.getName() + " pair " + pair.getName() + " timestamp " + ppsList.get(0).getTimestamp());
                    continue routelabel;
                } else {
                    routePpsList.add(routePps);
                }
            }
            log.debug("starting profit calculator for route " + route.getName() + " routePpsList "
                    + routePpsList.get(0).getPairNameDb() + " " + routePpsList.get(1).getPairNameDb() + " " + routePpsList.get(2).getPairNameDb());
            RoutePriceSliceProfit profit = RouteProfitCalculator.calcRouteProfit(Global.stockExchange, route, routePpsList);
            if (profit != null) {
                log.info("profit detected. route: " + route.getName() + " time: " + new Timestamp(profit.getTimestamp()).toString() + " pct: " + profit.getProfitPct().toString());
            }
        }

        log.debug("processTsBunch end");
    }

    private static void updateLastProcessedTimestamp(Long lastProcessedTimestamp) throws SQLException {
        //update last processed timestamp in db
        PreparedStatement updateTsStmt = conn.prepareStatement("insert into worker_state(key, value) values ('last_processed_price_timestamp', ?) on conflict(key) do update set value = ? where key = 'last_processed_price_timestamp';");
        updateTsStmt.setLong(1, lastProcessedTimestamp);
        updateTsStmt.setLong(2, lastProcessedTimestamp);
        updateTsStmt.executeUpdate();
        conn.commit();
        log.debug("lastProcessedTimestamp set to " + lastProcessedTimestamp);
    }

}
