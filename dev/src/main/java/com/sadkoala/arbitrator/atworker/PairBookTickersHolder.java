package com.sadkoala.arbitrator.atworker;

import java.util.ArrayList;
import java.util.List;

public class PairBookTickersHolder {

    public static List pairBookTickers = new ArrayList();
    static {
        pairBookTickers.add(new PairBookTicker("btcusdt"));
        pairBookTickers.add(new PairBookTicker("adabtc"));
        pairBookTickers.add(new PairBookTicker("adausdt"));
    }

}
