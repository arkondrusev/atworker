package com.sadkoala.arbitrator.atworker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class SocketListener implements WebSocket.Listener {

    private static final Logger log = LogManager.getLogger(SocketListener.class);

    public static ObjectMapper mapper = new ObjectMapper();

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);
        log.info(data);
        try {
            JsonNode jsonNode = mapper.readTree(data.toString());
            PairBookTicker ticker = mapper.readValue(jsonNode.get("data").toString(), PairBookTicker.class);
            log.info(ticker.getPairName() + " " + ticker.getUpdateTimestamp());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket,
                                       int statusCode,
                                       String reason) {
        log.info("Websocket close message received. Status code: [" + statusCode + "]. Reason: [" + reason + "]");
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.info("Websocket error received. " + error);
    }

}
