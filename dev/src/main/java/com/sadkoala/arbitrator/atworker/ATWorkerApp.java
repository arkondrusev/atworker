package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.listeners.BinanceSocketListener;
import com.sadkoala.arbitrator.atworker.thread.ManagerTask;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ATWorkerApp {

    private static final Logger log = LogManager.getLogger(ATWorkerApp.class);

    public static Map<String, Object> appParams = new HashMap<>();
    static {
        appParams.put(GlobalResources.APPLICATION_PROPERTY_NAME__WORKER_DB_FILE_PATH, "atworker.db");
        appParams.put(GlobalResources.APPLICATION_PROPERTY_NAME__MONITOR_DB_FILE_PATH, "atmonitor.db");
    }

    /*
     * on start:
     *   check if file .atworker_active exists
     *     yes - finish program
     *     no - proceed execution
     *   watch if file .atworker_stop exists
     *     yes - finish program
     *     no - continue watching
     *
     * finishing program:
     *   finish all necessary tasks
     *   delete file .atworker_stop if exists
     *   delete file .atworker_active if exists
     *
     * load param file, override default params
     *  if no param file - proceed with default params
     * connect monitor DB
     *  if couldn't connect (no file or other problem) - finish program
     * connect worker DB
     *  if couldn't connect (no file or other problem) - finish program
     *
     *
     */

    public static void main(String[] args) {
        log.info("At-worker started");

        try {
            startProgram();
            mainRun();
            finishProgram();
        } catch (Exception e) {
            log.fatal(ExceptionUtils.getStackTrace(e));
            log.fatal("Application terminated with error");
        } finally {
            try {
                finalizeProgram();
            } catch (Exception e) {
                log.fatal(ExceptionUtils.getStackTrace(e));
                log.fatal("Error during finalizing application");
            }
        }

        log.info("At-worker finished");
    }

    private static void finalizeProgram() {
        log.info("Finalize atworker begin");

        GlobalResources.closeWorkerDbConnection();
        GlobalResources.closeMonitorDbConnection();
        DbHelper.releaseStatements();

        log.info("Finalize atworker end");
    }

    private static void mainRun () {
        log.info("main part started");

        Thread managerThread = new Thread(new ManagerTask());
        managerThread.start();

        // start save prices to db thread
        Thread savePricesThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
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

        GlobalResources.webSocket.abort();
        DbHelper.logMessage("Websocket connection closed");
        log.info("Websocket connection closed");

        log.info("main part finished");
    }

    private static void startProgram() throws IOException, SQLException {
        log.info("Init atworker begin");

        if (fileExists(GlobalResources.ATWORKER_ACTIVE_FILE_PATH)) {
            throw new IllegalStateException("Atworker already started (" + GlobalResources.ATWORKER_ACTIVE_FILE_PATH + " is present)");
        }

        File atworkerActiveFile = new File(GlobalResources.ATWORKER_ACTIVE_FILE_PATH);
        if (!atworkerActiveFile.createNewFile()) {
            throw new IOException("Not able to create atworker lock file (" + GlobalResources.ATWORKER_ACTIVE_FILE_PATH + ")");
        }

        loadParamFile();
        connectMonitorDB();
        connectWorkerDB();
        DbHelper.initStatements();

        log.info("Init atworker end");
    }

    private static void finishProgram() {
        log.info("Finish atworker begin");

        File atworkerActiveFile = new File(GlobalResources.ATWORKER_ACTIVE_FILE_PATH);
        if (atworkerActiveFile.exists() && !atworkerActiveFile.delete()) {
            log.warn("Could not delete " + GlobalResources.ATWORKER_ACTIVE_FILE_PATH + " or file not exists");
        }
        File atworkerStopFile = new File(GlobalResources.ATWORKER_STOP_FILE_PATH);
        if (atworkerStopFile.exists() && !atworkerStopFile.delete()) {
            log.warn("Could not delete " + GlobalResources.ATWORKER_STOP_FILE_PATH + " or file not exists");
        }

        log.info("Finish atworker end");
    }

    public static boolean fileExists(String path) {
        File f = new File(path);
        return (f.exists() && !f.isDirectory());
    }

    private static void loadParamFile() {
        log.info("loading param file " + GlobalResources.PARAMS_FILE_PATH);
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(GlobalResources.PARAMS_FILE_PATH));
            for (Map.Entry<Object,Object> entry : prop.entrySet()) {
                appParams.put((String) entry.getKey(), entry.getValue());
            }
            log.info("param file loaded");
        } catch (Exception e) {
            log.warn("Worker parameter file not loaded. " + e.getMessage());
        }
    }

    private static void connectMonitorDB() throws SQLException {
        // ???????????????????????? ro-?????????????????????? ?? monitor-db
        String monitorDbPath = appParams.get(GlobalResources.APPLICATION_PROPERTY_NAME__MONITOR_DB_FILE_PATH).toString();
        GlobalResources.openMonitorDbConnection(monitorDbPath, true);
    }

    private static void connectWorkerDB() throws SQLException {
        String workerDbPath = appParams.get(GlobalResources.APPLICATION_PROPERTY_NAME__WORKER_DB_FILE_PATH).toString();
        GlobalResources.openWorkerDbConnection(workerDbPath, false);
    }

    public static WebSocket startSocket() {

        log.info("Opening new websocket");

        WebSocket.Listener wsListener = new BinanceSocketListener();

        String wsUri = "wss://stream.binance.com:9443/stream?streams=" +
                "btcusdt@bookTicker/adausdt@bookTicker/adabtc@bookTicker/" +
                "ethusdt@bookTicker/ethbtc@bookTicker/adaeth@bookTicker/" +
                "ltcusdt@bookTicker/ltcbtc@bookTicker/ltceth@bookTicker/" +
                "xrpusdt@bookTicker/xrpbtc@bookTicker/xrpeth@bookTicker/" +
                "solusdt@bookTicker/solbtc@bookTicker/soleth@bookTicker/" +
                "atomusdt@bookTicker/atombtc@bookTicker/atometh@bookTicker/" +

                "dogeusdt@bookTicker/dogebtc@bookTicker/dogemeth@bookTicker/" +
                "maticusdt@bookTicker/maticbtc@bookTicker/maticmeth@bookTicker/" +
                "dotusdt@bookTicker/dotbtc@bookTicker/doteth@bookTicker/" +
                "trxusdt@bookTicker/trxbtc@bookTicker/trxeth@bookTicker/" +
                "shibusdt@bookTicker/shibbtc@bookTicker/shibeth@bookTicker";

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
