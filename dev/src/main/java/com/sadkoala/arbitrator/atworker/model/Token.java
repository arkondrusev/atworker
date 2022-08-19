package com.sadkoala.arbitrator.atworker.model;

import java.math.BigInteger;

public class Token {

    private BigInteger id;
    private String name;

    public Token(BigInteger id, String name) {
        this.id = id;
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
