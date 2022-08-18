package com.sadkoala.arbitrator.atworker.model;

import com.sadkoala.arbitrator.atworker.Global;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {

    private BigInteger id;
    private List<Pair> pairList;
    private List<Token> tokenList;
    private String name;

    public Route(BigInteger id, Token firstToken, Token secondToken, Token thirdToken) {
        this.id = id;

        tokenList = new ArrayList<>();
        tokenList.add(firstToken);
        tokenList.add(secondToken);
        tokenList.add(thirdToken);
        tokenList = Collections.unmodifiableList(tokenList);

        if (tokenList.size() != 3) {
            throw new IllegalStateException("Error creating Route. TokenList size = " + tokenList.size() + ", must be 3");
        }

        pairList = new ArrayList<>();
        pairList.add(Global.findPairByTokens(firstToken, secondToken).get());
        pairList.add(Global.findPairByTokens(secondToken, thirdToken).get());
        pairList.add(Global.findPairByTokens(thirdToken, firstToken).get());
        pairList = Collections.unmodifiableList(pairList);

        if (pairList.size() != 3) {
            throw new IllegalStateException("Error creating Route. PairList size = " + tokenList.size() + ", must be 3");
        }

        name = firstToken.getName() + " - " + secondToken.getName() + " - " + thirdToken.getName();
    }

    public BigInteger getId() {
        return id;
    }

    public List<Pair> getPairList() {
        return pairList;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public String getName() {
        return name;
    }

}
