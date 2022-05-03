package com.sadkoala.arbitrator.atworker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class PairBookTickersSnapshop {

    public long timestamp;
    public Map<String,PairBookTicker> pairBookTickers = new HashMap<>();

    public PairBookTickersSnapshop(ConcurrentMap tickers, long timestamp) {
        this.timestamp = timestamp;
        this.pairBookTickers.putAll(tickers);
    }

}
