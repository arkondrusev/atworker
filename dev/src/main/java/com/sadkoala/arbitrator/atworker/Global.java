package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.Pair;
import com.sadkoala.arbitrator.atworker.model.Route;
import com.sadkoala.arbitrator.atworker.model.StockExchange;
import com.sadkoala.arbitrator.atworker.model.Token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Global {

    public static MathContext mathContext = new MathContext(6, RoundingMode.FLOOR);

    public static StockExchange stockExchange = new StockExchange(BigInteger.ONE, "Binance", new BigDecimal("0.00075"));
    public static List<Token> tokenList = new ArrayList<>();
    public static List<Pair> pairList = new ArrayList<>();
    public static List<Route> routeList = new ArrayList<>();

    private static BigInteger tokenIdSeq = BigInteger.ZERO;
    private static BigInteger pairIdSeq = BigInteger.ZERO;
    private static BigInteger routeIdSeq = BigInteger.ZERO;

    public static List<String> baseTokensStrList = new ArrayList<>();

    static {
        baseTokensStrList.add("USDT");
        baseTokensStrList.add("BTC");
        baseTokensStrList.add("ETH");

        Token usdt = addToken("USDT");
        Token btc = addToken("BTC");
        Token eth = addToken("ETH");
        Token ada = addToken("ADA");
        Token ltc = addToken("LTC");

        addPair(btc, usdt);
        addPair(ada, btc);
        addPair(ada, usdt);
        addPair(eth, usdt);
        addPair(eth, btc);
        addPair(ada, eth);
        addPair(ltc, usdt);
        addPair(ltc, btc);
        addPair(ltc, eth);

        addRoute(usdt, eth, btc);
        addRoute(usdt, btc, eth);
        addRoute(usdt, ada, btc);
        addRoute(usdt, btc, ada);
        addRoute(usdt, ltc, btc);
        addRoute(usdt, btc, ltc);
        addRoute(btc, ada, eth);
        addRoute(btc, eth, ada);
        addRoute(btc, ltc, eth);
        addRoute(btc, eth, ltc);
    }

    public static synchronized Token addToken(String name) {
        tokenIdSeq = tokenIdSeq.add(BigInteger.ONE);
        Token newToken = new Token(tokenIdSeq, name);
        tokenList.add(newToken);
        return newToken;
    }

    public static synchronized Pair addPair(Token upper, Token lower) {
        pairIdSeq = pairIdSeq.add(BigInteger.ONE);
        Pair newPair = new Pair(pairIdSeq, upper, lower);
        pairList.add(newPair);
        return newPair;
    }

    public static synchronized Route addRoute(Token firstToken, Token secondToken, Token thirdToken) {
        routeIdSeq = routeIdSeq.add(BigInteger.ONE);
        Route newRoute = new Route(routeIdSeq, firstToken, secondToken, thirdToken);
        routeList.add(newRoute);
        return newRoute;
    }

    public static Optional<Token> findTokenByName(String name) {
        return tokenList.stream()
                .filter(t -> t.getName().equals(name.toUpperCase()))
                .findFirst();
    }

    public static Optional<Pair> findPairByTokens(Token firstToken, Token secondToken, boolean firstOnlyUpper) {
        Optional<Pair> pairSearch = pairList.stream()
                .filter(pair -> pair.getUpperToken().equals(firstToken)
                        && pair.getBottomToken().equals(secondToken))
                .findFirst();
        if (pairSearch.isEmpty() && !firstOnlyUpper) {
            pairSearch = pairList.stream()
                    .filter(pair -> pair.getBottomToken().equals(firstToken)
                            && pair.getUpperToken().equals(secondToken))
                    .findFirst();
        }
        return pairSearch;
    }

    public static Optional<Pair> findPairByTokens(Token firstToken, Token secondToken) {
        return findPairByTokens(firstToken, secondToken, false);
    }

    public static Optional<Pair> findPairByTokenNames(String firstToken, String secondToken, boolean firstOnlyUpper) {
        return findPairByTokens(findTokenByName(firstToken).get(), findTokenByName(secondToken).get(), firstOnlyUpper);
    }

    public static Optional<Pair> findPairByTokenNames(String firstToken, String secondToken) {
        return findPairByTokenNames(firstToken, secondToken, false);
    }

    public static Optional<Route> findRouteByTokenNames(String firstToken, String secondToken, String thirdToken) {
        try {
            List<Token> tokens = new ArrayList<>();
            tokens.add(findTokenByName(firstToken).get());
            tokens.add(findTokenByName(secondToken).get());
            tokens.add(findTokenByName(thirdToken).get());
            return routeList.stream()
                    .filter(r -> r.getTokenList().containsAll(tokens))
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
