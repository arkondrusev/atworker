package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.*;

import java.math.BigDecimal;
import java.util.List;

public class RouteProfitCalculator {

    public static RoutePriceSliceProfit calcRouteProfit(BigDecimal tradeFee, Route route, List<PairPriceSlice> priceSlices) {
        Token firstToken = route.getTokenList().get(0);
        Token secondToken = route.getTokenList().get(1);
        Token thirdToken = route.getTokenList().get(2);

        PairPriceSlice priceSlice = priceSlices.get(0);
        Pair secondPair = priceSlices.get(1).getPair();
        Pair thirdPair = priceSlices.get(2).getPair();

        BigDecimal money = new BigDecimal("1");

        return null;
    }

    private static BigDecimal getTradePrice(PairPriceSlice priceSlice, boolean buyTrade) {
        return buyTrade ? priceSlice.getBestBidPrice() : priceSlice.getBestAskPrice();
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

    private static BigDecimal doTrade(Token sourceToken, Token destToken, BigDecimal sourceValue, BigDecimal tradeFeeMultiplier, PairPriceSlice priceSlice) {
        BigDecimal intermediateValue = sourceValue.divide(priceSlice.getBestBidPrice());
        BigDecimal destValue = intermediateValue.multiply(tradeFeeMultiplier);

        return destValue;
    }

}
