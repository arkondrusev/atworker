package com.sadkoala.arbitrator.atworker.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StockExchange {

    private BigInteger id;
    private String name;
    private BigDecimal tradeFee;
    private List<Token> tokens = new ArrayList<>();
    private List<Pair> pairs = new ArrayList<>();

    public StockExchange(BigInteger id, String name, BigDecimal tradeFee) {
        this.id = id;
        this.name = name;
        this.tradeFee = tradeFee;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

}
