package com.sadkoala.arbitrator.atworker.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadkoala.arbitrator.atworker.PairBookTicker;
import com.sadkoala.arbitrator.atworker.PairBookTickersHolder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BinanceSocketListener extends AbstractSocketListener {

    private static final Logger log = LogManager.getLogger(BinanceSocketListener.class);

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void processMessage(String message) {
        try {
            JsonNode tree = mapper.readTree(message);
            String pair = tree.get("stream").textValue().split("@")[0];
            PairBookTicker ticker = mapper.readValue(tree.get("data").toString(), PairBookTicker.class);
            PairBookTickersHolder.update(pair, ticker);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
