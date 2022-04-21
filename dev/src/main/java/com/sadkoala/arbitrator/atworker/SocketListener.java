package com.sadkoala.arbitrator.atworker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class SocketListener implements WebSocket.Listener {

    private static final Logger log = LogManager.getLogger(SocketListener.class);

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        log.info(data);
        webSocket.request(1);
        return null;
    }

}
