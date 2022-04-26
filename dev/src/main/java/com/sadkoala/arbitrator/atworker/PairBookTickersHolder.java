package com.sadkoala.arbitrator.atworker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PairBookTickersHolder {

    public static ConcurrentMap<String,PairBookTicker> pairBookTickers = new ConcurrentHashMap<>();
    static {
        pairBookTickers.put("btcusdt", new PairBookTicker("btcusdt"));
        pairBookTickers.put("adabtc", new PairBookTicker("adabtc"));
        pairBookTickers.put("adausdt", new PairBookTicker("adausdt"));
    }

}
