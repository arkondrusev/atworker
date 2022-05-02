package com.sadkoala.arbitrator.atworker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PairBookTickersHolder {

    public static ConcurrentMap<String,PairBookTicker> pairBookTickers = new ConcurrentHashMap<>();
    static {
        pairBookTickers.put("btcusdt", new PairBookTicker("btcusdt"));
        pairBookTickers.put("adabtc", new PairBookTicker("adabtc"));
        pairBookTickers.put("adausdt", new PairBookTicker("adausdt"));
        pairBookTickers.put("ethusdt", new PairBookTicker("ethusdt"));
        pairBookTickers.put("ethbtc", new PairBookTicker("ethbtc"));
        pairBookTickers.put("adaeth", new PairBookTicker("adaeth"));
        pairBookTickers.put("ltcusdt", new PairBookTicker("ltcusdt"));
        pairBookTickers.put("ltcbtc", new PairBookTicker("ltcbtc"));
        pairBookTickers.put("ltceth", new PairBookTicker("ltceth"));
    }

}
