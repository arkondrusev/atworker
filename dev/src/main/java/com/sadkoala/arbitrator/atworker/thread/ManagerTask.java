package com.sadkoala.arbitrator.atworker.thread;

import com.sadkoala.arbitrator.atworker.ATWorkerApp;
import com.sadkoala.arbitrator.atworker.DbHelper;
import com.sadkoala.arbitrator.atworker.PairBookTickersHolder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.sadkoala.arbitrator.atworker.ATWorkerApp.webSocket;

public class ManagerTask implements Runnable {

    private static final Logger log = LogManager.getLogger(ManagerTask.class);

    private static final long WEBSOCKET_INTERVAL = 60000L;
    private long nextWebsocketCheck = 0L;

    private static final long TICKERS_INTERVAL = 120000L;
    private long nextTickersCheck = 0L;

    @Override
    public void run() {
        initCheck();
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                String stack = ExceptionUtils.getStackTrace(e);
                log.error(stack);
                DbHelper.logMessage(stack);
            }

            checkWebsocketIfTime();
            checkPairBookTickersUpdateIfTime();

        }
    }

    private void initCheck() {
        checkWebsocket();
    }

    private void checkWebsocketIfTime() {
        if (System.currentTimeMillis() >= nextWebsocketCheck) {
            checkWebsocket();
        }
    }

    private void checkWebsocket() {
        if (webSocket == null || webSocket.isInputClosed()) {
            String msg = "Websocket not exist or input stream closed";
            DbHelper.logMessage(msg);
            log.info(msg);

            // установление подключения к binance
            webSocket = ATWorkerApp.startSocket();
            nextWebsocketCheck = System.currentTimeMillis() + WEBSOCKET_INTERVAL;
            nextTickersCheck = System.currentTimeMillis() + TICKERS_INTERVAL;
            return;
        }
        nextWebsocketCheck = System.currentTimeMillis() + WEBSOCKET_INTERVAL;
    }

    private void checkPairBookTickersUpdateIfTime() {
        if (System.currentTimeMillis() >= nextTickersCheck) {
            checkPairBookTickersUpdate();
        }
    }

    private void checkPairBookTickersUpdate() {
        long tickersUpdateTimePassed = System.currentTimeMillis() - PairBookTickersHolder.updatedTimestamp;
        if (tickersUpdateTimePassed > TICKERS_INTERVAL) {
            // если давно не было обновлений котировок - рестартуем сокет
            String msg = "No pair tickers updates since " + PairBookTickersHolder.updatedTimestamp + ". Passed " + tickersUpdateTimePassed + " ms.";
            DbHelper.logMessage(msg);
            log.info(msg);

            webSocket.abort();
            msg = "Websocket connection aborted.";
            DbHelper.logMessage(msg);
            log.info(msg);
            webSocket = ATWorkerApp.startSocket();
            nextWebsocketCheck = System.currentTimeMillis() + WEBSOCKET_INTERVAL;
            return;
        }
    }

}
