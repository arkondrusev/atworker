package com.sadkoala.arbitrator.atworker;

import java.math.BigDecimal;

public class PairBookTicker {

    private String pairName;

    private long updateTimestamp;
    private BigDecimal bestAskPrice;
    private BigDecimal bestBidPrice;

    public PairBookTicker(String pairName) {
        this.pairName = pairName;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public BigDecimal getBestAskPrice() {
        return bestAskPrice;
    }

    public void setBestAskPrice(BigDecimal bestAskPrice) {
        this.bestAskPrice = bestAskPrice;
    }

    public BigDecimal getBestBidPrice() {
        return bestBidPrice;
    }

    public void setBestBidPrice(BigDecimal bestBidPrice) {
        this.bestBidPrice = bestBidPrice;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
    
}
