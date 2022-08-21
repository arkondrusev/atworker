package com.sadkoala.arbitrator.atworker.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class RoutePriceSliceProfit {

    private Long timestamp;
    private List<PairPriceSlice> priceSliceList;
    private boolean direction;  // 0 - direct, 1 - reverse
    private StockExchange stockExchange;
    private BigDecimal tradeFee;
    private BigDecimal profitPct;

    public RoutePriceSliceProfit(Long timestamp, List<PairPriceSlice> priceSliceList, boolean direction, StockExchange stockExchange, BigDecimal tradeFee, BigDecimal profitPct) {
        this.timestamp = timestamp;
        this.priceSliceList = Collections.unmodifiableList(priceSliceList);
        this.direction = direction;
        this.stockExchange = stockExchange;
        this.tradeFee = tradeFee;
        this.profitPct = profitPct;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public List<PairPriceSlice> getPriceSliceList() {
        return priceSliceList;
    }

    public boolean isDirection() {
        return direction;
    }

    public StockExchange getStockExchange() {
        return stockExchange;
    }

    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    public BigDecimal getProfitPct() {
        return profitPct;
    }

}
