package com.sadkoala.arbitrator.atworker.thread;

import com.sadkoala.arbitrator.atworker.*;
import com.sadkoala.arbitrator.atworker.model.StockExchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.sadkoala.arbitrator.atworker.GlobalResources.webSocket;

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

                checkWebsocketIfTime();
                checkPairBookTickersUpdateIfTime();

                if (ATWorkerApp.fileExists(GlobalResources.ATWORKER_STOP_FILE_PATH)) {
                    break;
                }
            } catch (InterruptedException e) {
                String stack = ExceptionUtils.getStackTrace(e);
                log.error(stack);
                DbHelper.logMessage(stack);
            }
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
        List<StockExchange> seList = Global.seList;

        if (webSocket == null || webSocket.isInputClosed()) {
            String msg = "Websocket not exist or input stream closed";
            DbHelper.logMessage(msg);
            log.info(msg);

            // установление подключения к binance
            webSocket = ATWorkerApp.startSocket();
            nextTickersCheck = System.currentTimeMillis() + TICKERS_INTERVAL;
        }
        nextWebsocketCheck = System.currentTimeMillis() + WEBSOCKET_INTERVAL;
    }

    private void checkPairBookTickersUpdateIfTime() {
        if (System.currentTimeMillis() >= nextTickersCheck) {
            checkPairBookTickersUpdate();
        }
        nextTickersCheck = System.currentTimeMillis() + TICKERS_INTERVAL;
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
        }
    }

}
