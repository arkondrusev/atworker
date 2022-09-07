package com.sadkoala.arbitrator.atworker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CalcDBRoutesProfitApp {

    private static final Logger log = LogManager.getLogger(CalcDBRoutesProfitApp.class);

    private static final String TABLE_NAME_ROUTE_PROFITS = "at_profit";

    private static final String SQL_CHECK_ROUTE_PROFITS_TABLE_EXISTS = "select name from sqlite_master where type='table' and name='" + TABLE_NAME_ROUTE_PROFITS + "'";

    public static void main(String[] args) throws SQLException {

        log.info("CalcDBRoutesProfitApp start");

        Connection conn = GlobalResources.openWorkerDbConnection("db/atworker.db", false);

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

        // read property "last processed timestamp
        Statement readLastProcessed = conn.createStatement();


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

}
