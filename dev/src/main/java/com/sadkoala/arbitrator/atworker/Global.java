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
import java.util.stream.Collectors;

public class Global {

    public static MathContext mathContext = new MathContext(6, RoundingMode.FLOOR);

    private static List<StockExchange> seList = new ArrayList<>();

    public static List<Token> tokenList = new ArrayList<>();
    public static List<Pair> pairList = new ArrayList<>();
    public static List<Route> routeList = new ArrayList<>();

    private static BigInteger tokenIdSeq = BigInteger.ZERO;
    private static BigInteger pairIdSeq = BigInteger.ZERO;
    private static BigInteger routeIdSeq = BigInteger.ZERO;

    static {
        StockExchange binanceSe = new StockExchange(BigInteger.ONE, "BINANCE", new BigDecimal("0.00075"), "/");
        StockExchange okexSe = new StockExchange(BigInteger.TWO, "OKEX", new BigDecimal("0.001"), "-");
        seList.add(binanceSe);
        seList.add(okexSe);

        Token usdt = addToken("USDT");
        binanceSe.addBaseToken(usdt);
        okexSe.addBaseToken(usdt);

        Token btc = addToken("BTC");
        binanceSe.addBaseToken(btc);
        okexSe.addBaseToken(btc);

        Token eth = addToken("ETH");
        binanceSe.addBaseToken(eth);
        okexSe.addBaseToken(eth);

        Token ada = addToken("ADA");
        binanceSe.addToken(ada);
        okexSe.addToken(ada);

        Token ltc = addToken("LTC");
        binanceSe.addToken(ltc);
        okexSe.addToken(ltc);

        Token xrp = addToken("XRP");
        binanceSe.addToken(xrp);
        okexSe.addToken(xrp);

        Token sol = addToken("SOL");
        binanceSe.addToken(sol);
        okexSe.addToken(sol);

        Token atom = addToken("ATOM");
        binanceSe.addToken(atom);
        okexSe.addToken(atom);

        Token doge = addToken("DOGE");
        binanceSe.addToken(doge);
        okexSe.addToken(doge);

        Token matic = addToken("MATIC");
        binanceSe.addToken(matic);
        okexSe.addToken(matic);

        Token dot = addToken("DOT");
        binanceSe.addToken(dot);
        okexSe.addToken(dot);

        Token trx = addToken("TRX");
        binanceSe.addToken(trx);
        okexSe.addToken(trx);

        Token shib = addToken("SHIB");
        binanceSe.addToken(shib);
        okexSe.addToken(shib);



        addPair(btc, usdt);

        addPair(eth, usdt);
        addPair(eth, btc);

        addPair(ada, usdt);
        addPair(ada, btc);
        addPair(ada, eth);

        addPair(ltc, usdt);
        addPair(ltc, btc);
        addPair(ltc, eth);

        addPair(xrp, usdt);
        addPair(xrp, btc);
        addPair(xrp, eth);

        addPair(sol, usdt);
        addPair(sol, btc);
        addPair(sol, eth);

        addPair(atom, usdt);
        addPair(atom, btc);
        addPair(atom, eth);

        addPair(doge, usdt);
        addPair(doge, btc);
        addPair(doge, eth);

        addPair(matic, usdt);
        addPair(matic, btc);
        addPair(matic, eth);

        addPair(dot, usdt);
        addPair(dot, btc);
        addPair(dot, eth);

        addPair(trx, usdt);
        addPair(trx, btc);
        addPair(trx, eth);

        addPair(shib, usdt);
        addPair(shib, btc);
        addPair(shib, eth);

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

        addRoute(usdt, xrp, btc);
        addRoute(usdt, xrp, eth);
        addRoute(btc, xrp, eth);

        addRoute(usdt, sol, btc);
        addRoute(usdt, sol, eth);
        addRoute(btc, sol, eth);

        addRoute(usdt, atom, btc);
        addRoute(usdt, atom, eth);
        addRoute(btc, atom, eth);

        addRoute(usdt, doge, btc);
        addRoute(usdt, doge, eth);
        addRoute(btc, doge, eth);

        addRoute(usdt, matic, btc);
        addRoute(usdt, matic, eth);
        addRoute(btc, matic, eth);

        addRoute(usdt, dot, btc);
        addRoute(usdt, dot, eth);
        addRoute(btc, dot, eth);

        addRoute(usdt, trx, btc);
        addRoute(usdt, trx, eth);
        addRoute(btc, trx, eth);

        addRoute(usdt, shib, btc);
        addRoute(usdt, shib, eth);
        addRoute(btc, shib, eth);

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

    public static Optional<StockExchange> findSeByName(String name) {
        return seList.stream()
                .filter(t -> t.getName().equals(name.toUpperCase()))
                .findFirst();
    }

    public static List<StockExchange> getSeList() {
        return getSeListFull().stream()
                .filter(se -> se.isActive())
                .collect(Collectors.toList());
    }

    public static List<StockExchange> getSeListFull() {
        return seList;
    }

}
