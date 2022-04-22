package com.sadkoala.arbitrator.atworker;

import java.util.HashMap;
import java.util.Map;

public class PairBookTickersHolder {

    public static Map pairBookTickers = new HashMap();
    static {
        pairBookTickers.put("btcusdt", new PairBookTicker("btcusdt"));
        pairBookTickers.put("adabtc", new PairBookTicker("adabtc"));
        pairBookTickers.put("adausdt", new PairBookTicker("adausdt"));
    }

}
