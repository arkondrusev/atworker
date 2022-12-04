package com.sadkoala.arbitrator.atworker.model;

import java.math.BigInteger;

public class Pair {

    private BigInteger id;
    private Token upperToken;
    private Token bottomToken;
    private String name; // calc
    private Boolean active = true;

    public Pair(BigInteger id, Token upperToken, Token bottomToken) {
        this.id = id;
        this.upperToken = upperToken;
        this.bottomToken = bottomToken;

        name = upperToken.getName().toUpperCase() + "/" + bottomToken.getName().toUpperCase();
    }

    public BigInteger getId() {
        return id;
    }

    public Token getUpperToken() {
        return upperToken;
    }

    public Token getBottomToken() {
        return bottomToken;
    }

    public String getName() {
        return name;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
