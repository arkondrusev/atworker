package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.*;

import java.util.List;

public class RouteProfitCalculator {

    public static RoutePriceSliceProfit calcRouteProfit(StockExchange stockExchange, Route route, List<PairPriceSlice> priceSliceList) {
        Token sourceToken;
        Token destToken;
        BigDecimal2 tradeFeeMultiplier = BigDecimal2.ONE.subtract(stockExchange.getTradeFee());
        BigDecimal2 moneyStart = BigDecimal2.ONE;
        BigDecimal2 money = moneyStart;
        RoutePriceSliceProfit routeProfit;

        List<Token> routeTokenList = route.getTokenList();
        for (int i = 0; i < routeTokenList.size(); i++) {
            sourceToken = routeTokenList.get(i);
            destToken = (i == routeTokenList.size()-1) ? routeTokenList.get(0) : routeTokenList.get(i+1);
            money = doTrade(sourceToken, destToken, money, tradeFeeMultiplier, priceSliceList.get(i));
        }

        routeProfit = null;
        if (moneyStart.compareTo(money) < 0) {
            BigDecimal2 profitPct = money.subtract(moneyStart).divide(moneyStart);
            routeProfit = new RoutePriceSliceProfit(priceSliceList, true, stockExchange, profitPct);
        }
        return routeProfit;
    }

    private static boolean isBuyTrade(Token sourceToken, Token destToken, Pair pair) {
        if (pair.getUpperToken().equals(sourceToken)
                && pair.getBottomToken().equals(destToken)) {
            return false;
        } else if (pair.getUpperToken().equals(destToken)
                && pair.getBottomToken().equals(sourceToken)) {
            return true;
        } else {
            throw new IllegalStateException("Pair doesn't match tokens");
        }
    }

    protected static BigDecimal2 doTrade(Token sourceToken, Token destToken, BigDecimal2 sourceValue, BigDecimal2 tradeFeeMultiplier, PairPriceSlice priceSlice) {
        boolean buyTrade = isBuyTrade(sourceToken, destToken, priceSlice.getPair());

        BigDecimal2 intermediateValue;
        if (buyTrade) {
            intermediateValue = sourceValue.divide(priceSlice.getBestAskPrice());
        } else {
            intermediateValue = sourceValue.multiply(priceSlice.getBestBidPrice());
        }

        return intermediateValue.multiply(tradeFeeMultiplier);
    }

}
