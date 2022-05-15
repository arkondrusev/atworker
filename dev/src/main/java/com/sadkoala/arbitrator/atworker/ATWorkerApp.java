package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.thread.ManagerTask;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class ATWorkerApp {

    private static final Logger log = LogManager.getLogger(ATWorkerApp.class);

    public static Connection monitorDbConnection = null;
    public static Connection workerDbConnection = null;
    public static WebSocket webSocket = null;

    public static void main(String[] args) {

        log.info("At-worker started");

        connectMonitorDB();
        if (monitorDbConnection == null) {
            return;
        }

        connectWorkerDB();
        if (workerDbConnection == null) {
            return;
        }
        DbHelper.initStatements();

        DbHelper.logMessage("At-worker started");

        Thread managerThread = new Thread(new ManagerTask());
        managerThread.start();

        // start save prices to db thread
        Thread savePricesThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }

                if (PairBookTickersHolder.isUpdated()) {
                    PairBookTickersSnapshop snapshot = PairBookTickersHolder.read();
                    for (PairBookTicker value : snapshot.pairBookTickers.values()) {
                        DbHelper.insertPairPrice(snapshot.timestamp, value.getPairName(), value.getBestAskPrice(), value.getBestBidPrice());
                    }
                }
            }
        });
        savePricesThread.start();

        try {
            managerThread.join();
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        webSocket.abort();
        DbHelper.logMessage("Websocket connection closed");
        log.info("Websocket connection closed");

        DbHelper.logMessage("At-worker going to finish. Disconnecting from databases");
        disconnectWorkerDB();
        disconnectMonitorDB();
        DbHelper.releaseStatements();

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
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return connection;
    }

    private static void connectMonitorDB() {
        // установление ro-подключения к monitor-db
        String monitorDbPath = "persistent_files\\at-monitor.db";
        monitorDbConnection = connectDB(monitorDbPath, true);
        if (monitorDbConnection == null) {
            log.error("Could not connect to monitor DB at {" + monitorDbPath + "}" );
            log.fatal("At-worker app failed");
        } else {
            log.info("Monitor DB connected {" + monitorDbPath + "}");
        }
    }

    private static void connectWorkerDB() {
        String workerDbPath = "persistent_files\\at-worker.db";
        workerDbConnection = connectDB(workerDbPath, false);
        if (workerDbConnection == null) {
            log.error("Could not connect to worker DB at {" + workerDbPath + "}" );
            log.fatal("At-worker app failed");
        } else {
            log.info("Worker DB connected {" + workerDbPath + "}");
        }
    }

    private static void disconnectWorkerDB() {
        // закрыть соединение с бд воркер
        try {
            workerDbConnection.close();
        } catch (SQLException e) {
            log.error(e);
        }
        log.info("Worker DB disconnected");
    }

    private static void disconnectMonitorDB() {
        // закрыть соединение с бд монитор
        try {
            monitorDbConnection.close();
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        log.info("Monitor DB disconnected");
    }

    public static WebSocket startSocket() {

        WebSocket.Listener wsListener = new SocketListener();

        String wsUri = "wss://stream.binance.com:9443/stream?streams=btcusdt@bookTicker/adausdt@bookTicker/adabtc@bookTicker/ethusdt@bookTicker/ethbtc@bookTicker/adaeth@bookTicker/ltcusdt@bookTicker/ltcbtc@bookTicker/ltceth@bookTicker";

        WebSocket webSocket = null;
        try {
            webSocket = HttpClient.newHttpClient()
                    .newWebSocketBuilder().buildAsync(URI.create(wsUri), wsListener)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        if (webSocket == null) {
            log.error("Could not open websocket connection");
            DbHelper.logMessage("Could not open websocket connection");
        } else {
            log.info("Websocket connection is open");
            DbHelper.logMessage("Websocket connection is open");
        }

        return webSocket;
    }

}
