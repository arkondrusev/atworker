package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.BigDecimal2;
import com.sadkoala.arbitrator.atworker.model.PairPriceSlice;
import com.sadkoala.arbitrator.atworker.model.RoutePriceSliceProfit;
import com.sadkoala.arbitrator.atworker.model.Token;

import java.util.ArrayList;
import java.util.List;

public class RouteProfitCalculatorTest {

    //@Test
    public void calcRouteProfitTest() {
        String token1 = "USDT";
        String token2 = "BTC";
        String token3 = "ETH";
        List<PairPriceSlice> priceSliceList = new ArrayList<>();
        PairPriceSlice pairPriceSlice;
        pairPriceSlice = new PairPriceSlice(1659702901492l, "ethusdt", new BigDecimal2("1.1"), new BigDecimal2("0.9"));
        pairPriceSlice.setPair(Global.findPairByTokenNames("ETH", "USDT").get());
        priceSliceList.add(pairPriceSlice);
        pairPriceSlice = new PairPriceSlice(1659702901492l, "ethbtc", new BigDecimal2("1.1"), new BigDecimal2("0.9"));
        pairPriceSlice.setPair(Global.findPairByTokenNames("ETH", "BTC").get());
        priceSliceList.add(pairPriceSlice);
        pairPriceSlice = new PairPriceSlice(1659702901492l, "btcusdt", new BigDecimal2("1.1"), new BigDecimal2("0.9"));
        pairPriceSlice.setPair(Global.findPairByTokenNames("BTC", "USDT").get());
        priceSliceList.add(pairPriceSlice);
        RoutePriceSliceProfit routePriceSliceProfit = RouteProfitCalculator.calcRouteProfit(Global.stockExchange,
                Global.findRouteByTokenNames(token1, token2, token3).get(),
                priceSliceList);
        System.out.println(routePriceSliceProfit.getProfitPct());
    }

    //@Test
    public void doTradeTest() {
        Token usdt = Global.findTokenByName("usdt").get();
        Token eth = Global.findTokenByName("eth").get();
        BigDecimal2 sourceValue = BigDecimal2.ONE;
        BigDecimal2 tradeFeeMultiplier = BigDecimal2.ONE.subtract(Global.stockExchange.getTradeFee());
        PairPriceSlice pairPriceSlice = new PairPriceSlice(1659702901492l, "ethusdt", new BigDecimal2("1.1"), new BigDecimal2("0.9"));
        pairPriceSlice.setPair(Global.findPairByTokens(usdt, eth).get());
        BigDecimal2 targetValue = RouteProfitCalculator.doTrade(usdt, eth, sourceValue, tradeFeeMultiplier, pairPriceSlice);
        System.out.println(targetValue);
    }

}
