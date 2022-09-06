package com.sadkoala.arbitrator.atworker;

import java.sql.SQLException;

public class CalcDBRoutesProfitApp {

    public static void main(String[] args) throws SQLException {

        GlobalResources.openWorkerDbConnection("atworker.db", false);

        

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


    }

}
