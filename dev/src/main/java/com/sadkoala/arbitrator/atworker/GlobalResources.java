package com.sadkoala.arbitrator.atworker;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class GlobalResources {

    private static final Logger log = LogManager.getLogger(ATWorkerApp.class);

    public static final String APPLICATION_PROPERTY_NAME__WORKER_DB_FILE_PATH = "arbitrator_worker_db_file_path";
    public static final String APPLICATION_PROPERTY_NAME__MONITOR_DB_FILE_PATH = "arbitrator_monitor_db_file_path";


    public static final String PARAMS_FILE_PATH = "atworker.properties";
    public static final String ATWORKER_ACTIVE_FILE_PATH = ".atworker_active";
    public static final String ATWORKER_STOP_FILE_PATH = ".atworker_stop";
    private static Optional<Connection> monitorDbConnection = Optional.empty();
    private static Optional<Connection> workerDbConnection = Optional.empty();

    public static Optional<Connection> getMonitorDbConnection() {
        return monitorDbConnection;
    }

    public static Optional<Connection> getWorkerDbConnection() {
        return workerDbConnection;
    }

    public static Connection openMonitorDbConnection(String dbPath, boolean readonly) throws SQLException {
        Connection conn = connectDB(dbPath, readonly);
        monitorDbConnection = Optional.of(conn);
        log.info("Monitor DB connected {" + dbPath + "}");
        return conn;
    }

    public static Connection openWorkerDbConnection(String dbPath, boolean readonly) throws SQLException {
        Connection conn = connectDB(dbPath, readonly);
        workerDbConnection = Optional.of(conn);
        log.info("Worker DB connected {" + dbPath + "}");
        return conn;
    }

    public static void closeMonitorDbConnection() {
        try {
            if (monitorDbConnection.isPresent()) {
                monitorDbConnection.get().close();
                log.info("Worker DB disconnected");
                monitorDbConnection = Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public static void closeWorkerDbConnection() {
        try {
            if (workerDbConnection.isPresent()) {
                workerDbConnection.get().close();
                log.info("Worker DB disconnected");
                workerDbConnection = Optional.empty();
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private static Connection connectDB(String dbPath, boolean readonly) throws SQLException {
        Connection connection;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + dbPath;
            // create a connection to the database
            SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(readonly);
            connection = DriverManager.getConnection(url, config.toProperties());
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            log.error("Error during connection to db " + dbPath);
            throw e;
        }
        return connection;
    }

}
