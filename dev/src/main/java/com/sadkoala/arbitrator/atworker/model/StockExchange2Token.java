package com.sadkoala.arbitrator.atworker.model;

import java.math.BigInteger;

public class StockExchange2Token {

    private BigInteger id;
    private StockExchange se;
    private Token token;
    // name token in se, if different from default
    private String seTokenName = null;
    private Boolean baseToken = false;

    public StockExchange2Token(BigInteger id, StockExchange se, Token token, Boolean baseToken, String seTokenName) {
        this.id = id;
        this.se = se;
        this.token = token;
        this.seTokenName = seTokenName;
        this.baseToken = baseToken;
    }

    public StockExchange2Token(BigInteger id, StockExchange se, Token token, Boolean baseToken) {
        this.id = id;
        this.se = se;
        this.token = token;
        this.baseToken = baseToken;
    }

    public StockExchange2Token(BigInteger id, StockExchange se, Token token) {
        this.id = id;
        this.se = se;
        this.token = token;
    }

    public BigInteger getId() {
        return id;
    }

    public StockExchange getSe() {
        return se;
    }

    public Token getToken() {
        return token;
    }

    public String getSeTokenName() {
        return seTokenName;
    }

    public Boolean getBaseToken() {
        return baseToken;
    }

}
