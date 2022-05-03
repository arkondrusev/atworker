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

    private static ObjectMapper mapper = new ObjectMapper();

    private StringBuilder message = new StringBuilder();

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);

        message.append(data);

        if (last) {
            try {
                JsonNode tree = mapper.readTree(message.toString());
                String pair = tree.get("stream").textValue().split("@")[0];
                PairBookTicker ticker = mapper.readValue(tree.get("data").toString(), PairBookTicker.class);
                PairBookTickersHolder.update(pair, ticker);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
            message = new StringBuilder();
        }
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket,
                                       int statusCode,
                                       String reason) {
        log.info("Websocket close message received. Status code: [" + statusCode + "]. Reason: [" + reason + "]");
        message = new StringBuilder();
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.info("Websocket error received. " + error);
        message = new StringBuilder();
    }

}
