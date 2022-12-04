package com.sadkoala.arbitrator.atworker.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OkexSocketListener extends AbstractSocketListener {

    private static final Logger log = LogManager.getLogger(OkexSocketListener.class);

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void processMessage(String message) {
        System.out.println(message);
    }

}
