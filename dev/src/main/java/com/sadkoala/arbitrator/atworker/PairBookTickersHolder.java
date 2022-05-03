package com.sadkoala.arbitrator.atworker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PairBookTickersHolder {

    private static ConcurrentMap<String,PairBookTicker> pairBookTickers = new ConcurrentHashMap<>();
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

    private static boolean updated = false;
    public static long updatedTimestamp = 0L;


    public static boolean isUpdated() {
        return updated;
    }

    public static synchronized void update(String pair, PairBookTicker ticker) {
        pairBookTickers.put(pair, ticker);
        updated = true;
        updatedTimestamp = System.currentTimeMillis();
    }

    public static synchronized PairBookTickersSnapshop read() {
        updated = false;
        return new PairBookTickersSnapshop(pairBookTickers, updatedTimestamp);
    }

}
