package com.sadkoala.arbitrator.atworker;

import com.sadkoala.arbitrator.atworker.model.PairPriceSlice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RouteProfitCalculatorTest {

    @Test
    public void calcRouteProfit() {
        String token1 = "USDT";
        String token2 = "BTC";
        String token3 = "ETH";
        List<PairPriceSlice> priceSliceList = new ArrayList<>();
        PairPriceSlice pairPriceSlice = new PairPriceSlice(1659702901492l, "btcusdt", new BigDecimal("23184.28"), new BigDecimal("23182.34"));
        pairPriceSlice.setPair(Global.findPairByTokenNames("BTC", "USDT").get());
        priceSliceList.add(pairPriceSlice);
        pairPriceSlice = new PairPriceSlice(1659702901492l, "ethbtc", new BigDecimal("0.073114"), new BigDecimal("0.073109"));
        pairPriceSlice.setPair(Global.findPairByTokenNames("ETH", "BTC").get());
        priceSliceList.add(pairPriceSlice);
        pairPriceSlice = new PairPriceSlice(1659702901492l, "ethusdt", new BigDecimal("1695.22"), new BigDecimal("1694.93"));
        pairPriceSlice.setPair(Global.findPairByTokenNames("ETH", "USDT").get());
        priceSliceList.add(pairPriceSlice);
        RouteProfitCalculator.calcRouteProfit(Global.stockExchange,
                Global.findRouteByTokenNames(token1, token2, token3).get(),
                priceSliceList);
    }

}
