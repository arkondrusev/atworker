package com.sadkoala.arbitrator.atworker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties({"A", "B"})
public class PairBookTicker {

    @JsonProperty("s")
    private String pairName;

    @JsonProperty("u")
    private long updateTimestamp;
    @JsonProperty("a")
    private BigDecimal bestAskPrice;
    @JsonProperty("b")
    private BigDecimal bestBidPrice;

    public PairBookTicker() {

    }

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
