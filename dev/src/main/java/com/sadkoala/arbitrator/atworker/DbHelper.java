package com.sadkoala.arbitrator.atworker;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbHelper {

    private static final Logger log = LogManager.getLogger(DbHelper.class);

    private static final String SQL_INSERT_LOG_MESSAGE = "insert into worker_log (timestamp, tag, msg) values (?, ?, ?)";
    private static PreparedStatement insertLogMessagePrepared = null;

    private static final String SQL_INSERT_PAIR_PRICE = "insert into at_prices (timestamp, pair, best_ask, best_bid) values (?, ?, ?, ?)";
    private static PreparedStatement insertPairPricePrepared = null;

    public static void initStatements() {
        try {
            insertLogMessagePrepared = ATWorkerApp.workerDbConnection.prepareStatement(SQL_INSERT_LOG_MESSAGE);
            insertPairPricePrepared = ATWorkerApp.workerDbConnection.prepareStatement(SQL_INSERT_PAIR_PRICE);
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void releaseStatements() {
        releaseStatement(insertLogMessagePrepared);
        releaseStatement(insertPairPricePrepared);
    }

    private static void releaseStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public static void logMessage(String msg, String tags, long timestamp) {
        try {
            insertLogMessagePrepared.setLong(1, timestamp);
            insertLogMessagePrepared.setString(2, tags);
            insertLogMessagePrepared.setString(3, msg);
            insertLogMessagePrepared.executeUpdate();
            ATWorkerApp.workerDbConnection.commit();
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void logMessage(String msg, String tags) {
        logMessage(msg, tags, System.currentTimeMillis());
    }

    public static void logMessage(String msg) {
        logMessage(msg, null);
    }

    public static void logMessage(String msg, long timestamp) {
        logMessage(msg, null, timestamp);
    }

    public static void insertPairPrice(long timestamp, String pair, BigDecimal bestAsk, BigDecimal bestBid) {
        try {
            insertPairPricePrepared.setLong(1, timestamp);
            insertPairPricePrepared.setString(2, pair);
            insertPairPricePrepared.setBigDecimal(3, bestAsk);
            insertPairPricePrepared.setBigDecimal(4, bestBid);
            insertPairPricePrepared.executeUpdate();
            ATWorkerApp.workerDbConnection.commit();
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
