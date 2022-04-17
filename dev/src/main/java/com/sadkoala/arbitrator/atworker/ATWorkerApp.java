package com.sadkoala.arbitrator.atworker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ATWorkerApp {

    private static final Logger log = LogManager.getLogger(ATWorkerApp.class);

    public static void main(String[] args) {

        log.info("At-worker started");

        // установление подключения к worker-db
        // установление ro-подключения к monitor-db
        // установление подключения к binance

        // с момента подключения к worker-db логи писать туда

        log.info("At-worker finished");

    }

}
