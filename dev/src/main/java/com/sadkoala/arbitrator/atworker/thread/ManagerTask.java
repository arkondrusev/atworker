package com.sadkoala.arbitrator.atworker.thread;

import com.sadkoala.arbitrator.atworker.ATWorkerApp;
import com.sadkoala.arbitrator.atworker.DbHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.sadkoala.arbitrator.atworker.ATWorkerApp.webSocket;

public class ManagerTask implements Runnable {

    private static final Logger log = LogManager.getLogger(ManagerTask.class);

    private static final long WEBSOCKET_INTERVAL = 60000L;
    private long nextWebsocketCheck = 0L;

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
            DbHelper.logMessage("Websocket not working");
            log.info("Websocket not working");

            // установление подключения к binance
            webSocket = ATWorkerApp.startSocket();

            if (webSocket == null) {
                log.error("Could not open websocket connection");
                DbHelper.logMessage("Could not open websocket connection");
            }
            log.info("Websocket connection is open");
            DbHelper.logMessage("Websocket connection is open");
        }

        nextWebsocketCheck = System.currentTimeMillis() + WEBSOCKET_INTERVAL;
    }

}
