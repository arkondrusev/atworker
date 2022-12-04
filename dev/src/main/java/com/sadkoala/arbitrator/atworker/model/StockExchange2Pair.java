package com.sadkoala.arbitrator.atworker.model;

import java.math.BigInteger;

public class StockExchange2Pair {

    private BigInteger id;
    private StockExchange se;
    private Pair pair;
    private Boolean active;

    public StockExchange2Pair(BigInteger id, StockExchange se, Pair pair) {
        this.id = id;
        this.se = se;
        this.pair = pair;
    }

    public BigInteger getId() {
        return id;
    }

    public StockExchange getSe() {
        return se;
    }

    public void setSe(StockExchange se) {
        this.se = se;
    }

    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
