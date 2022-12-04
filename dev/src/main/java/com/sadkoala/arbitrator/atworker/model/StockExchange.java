package com.sadkoala.arbitrator.atworker.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;

public class StockExchange {

    private BigInteger id;
    private String name;
    private BigDecimal tradeFee;

    private static BigInteger tokenIdSeq = BigInteger.ZERO;
    private static BigInteger pairIdSeq = BigInteger.ZERO;
    private static BigInteger routeIdSeq = BigInteger.ZERO;

    private List<StockExchange2Token> se2tokens = new ArrayList<>();
    private List<StockExchange2Pair> se2pairs = new ArrayList<>();
    private WebSocket websocket;
    private Boolean active = true;
    private String pairTokensSeparator;

    public StockExchange(BigInteger id, String name, BigDecimal tradeFee, String pairTokensSeparator) {
        this.id = id;
        this.name = name;
        this.tradeFee = tradeFee;
        this.pairTokensSeparator = pairTokensSeparator;
    }

    public List<StockExchange2Token> getSe2Tokens() {
        return se2tokens;
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

    public List<StockExchange2Pair> getSe2Pairs() {
        return se2pairs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    public WebSocket getWebsocket() {
        return websocket;
    }

    public void setWebsocket(WebSocket websocket) {
        this.websocket = websocket;
    }

    public String getPairTokensSeparator() {
        return pairTokensSeparator;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    private synchronized void addToken1(Token token, boolean base) {
        tokenIdSeq.add(BigInteger.ONE);
        if (base) {
            se2tokens.add(new StockExchange2Token(tokenIdSeq, this, token, true));
        } else {
            se2tokens.add(new StockExchange2Token(tokenIdSeq, this, token, false));
        }
    }

    public void addBaseToken(Token token) {
        addToken1(token, true);
    }

    public void addToken(Token token) {
        addToken1(token, false);
    }

    public synchronized void addPair(Pair pair) {
        pairIdSeq.add(BigInteger.ONE);
        se2pairs.add(new StockExchange2Pair(pairIdSeq, this, pair));
    }

}
