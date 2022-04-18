package com.sadkoala.arbitrator.atworker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ATWorkerApp {

    private static final Logger log = LogManager.getLogger(ATWorkerApp.class);

    public static void main(String[] args) {

        log.info("At-worker started");

        // установление подключения к worker-db
        String workerDbPath = "persistent_files\\at-worker.db";
        Connection workerConnection = connectDB(workerDbPath, false);
        if (workerConnection == null) {
            log.error("Could not connect to worker DB at {" + workerDbPath + "}" );
            log.fatal("At-worker app failed");
        }
        log.info("Worker DB connected {" + workerDbPath + "}");
        // установление ro-подключения к monitor-db
        String monitorDbPath = "persistent_files\\at-monitor.db";
        Connection monitorConnection = connectDB(monitorDbPath, true);
        if (monitorConnection == null) {
            log.error("Could not connect to monitor DB at {" + monitorDbPath + "}" );
            log.fatal("At-worker app failed");
        }
        log.info("Monitor DB connected {" + monitorDbPath + "}");
        // установление подключения к binance

        // с момента подключения к worker-db логи писать туда

        log.info("At-worker finished");

    }

    private static Connection connectDB(String dbPath, boolean readonly) {
        Connection connection = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + dbPath;
            // create a connection to the database
            SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(readonly);
            connection = DriverManager.getConnection(url, config.toProperties());
        } catch (SQLException e) {
            log.error(e);
        }
        return connection;
    }

}
