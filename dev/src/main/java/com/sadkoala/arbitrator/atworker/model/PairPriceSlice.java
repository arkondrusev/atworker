package com.sadkoala.arbitrator.atworker.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PairPriceSlice {

    private Long timestamp;
    private Pair pair;
    private String pairNameDb;
    private BigDecimal bestAskPrice;
    private BigInteger bestBidPrice;

    public PairPriceSlice(Long timestamp, String pairNameDb, BigDecimal bestAskPrice, BigInteger bestBidPrice) {
        this.timestamp = timestamp;
        this.pairNameDb = pairNameDb;
        this.bestAskPrice = bestAskPrice;
        this.bestBidPrice = bestBidPrice;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Pair getPair() {
        return pair;
    }

    public String getPairNameDb() {
        return pairNameDb;
    }

    public BigDecimal getBestAskPrice() {
        return bestAskPrice;
    }

    public BigInteger getBestBidPrice() {
        return bestBidPrice;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

}
