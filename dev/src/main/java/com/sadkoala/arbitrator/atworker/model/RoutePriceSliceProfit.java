package com.sadkoala.arbitrator.atworker.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class RoutePriceSliceProfit {

    private List<PairPriceSlice> priceSliceList;
    private boolean directWay;
    private StockExchange stockExchange;
    private BigDecimal profitPct;

    public RoutePriceSliceProfit(List<PairPriceSlice> priceSliceList, boolean directWay, StockExchange stockExchange, BigDecimal profitPct) {
        this.priceSliceList = Collections.unmodifiableList(priceSliceList);
        this.directWay = directWay;
        this.stockExchange = stockExchange;
        this.profitPct = profitPct;
    }

    public Long getTimestamp() {
        return priceSliceList.get(0).getTimestamp();
    }

    public List<PairPriceSlice> getPriceSliceList() {
        return priceSliceList;
    }

    public boolean isDirectWay() {
        return directWay;
    }

    public StockExchange getStockExchange() {
        return stockExchange;
    }

    public BigDecimal getTradeFee() {
        return stockExchange.getTradeFee();
    }

    public BigDecimal getProfitPct() {
        return profitPct;
    }

}
