package com.sadkoala.arbitrator.atworker.model;

import com.sadkoala.arbitrator.atworker.Global;

import java.math.BigDecimal;

public class BigDecimal2 extends BigDecimal {

    // Cache of common small BigDecimal values.
    private static final BigDecimal2 ZERO_THROUGH_TEN[] = {
            new BigDecimal2("0"),
            new BigDecimal2("1"),
            new BigDecimal2("2"),
            new BigDecimal2("3"),
            new BigDecimal2("4"),
            new BigDecimal2("5"),
            new BigDecimal2("6"),
            new BigDecimal2("7"),
            new BigDecimal2("8"),
            new BigDecimal2("9"),
            new BigDecimal2("10"),
    };

    public static final BigDecimal2 ZERO = ZERO_THROUGH_TEN[0];
    public static final BigDecimal2 ONE = ZERO_THROUGH_TEN[1];
    public static final BigDecimal2 TWO = ZERO_THROUGH_TEN[2];
    public static final BigDecimal2 THREE = ZERO_THROUGH_TEN[3];
    public static final BigDecimal2 FOUR = ZERO_THROUGH_TEN[4];
    public static final BigDecimal2 FIVE = ZERO_THROUGH_TEN[5];
    public static final BigDecimal2 SIX = ZERO_THROUGH_TEN[6];
    public static final BigDecimal2 SEVEN = ZERO_THROUGH_TEN[7];
    public static final BigDecimal2 EIGHT = ZERO_THROUGH_TEN[8];
    public static final BigDecimal2 NINE = ZERO_THROUGH_TEN[9];
    public static final BigDecimal2 TEN = ZERO_THROUGH_TEN[10];

    public BigDecimal2(String val) {
        super(val);
    }

    @Override
    public BigDecimal2 divide(BigDecimal divisor) {
        return new BigDecimal2(super.divide(divisor, Global.mathContext).toString());
    }

    @Override
    public BigDecimal2 multiply(BigDecimal multiplicand) {
        return new BigDecimal2(super.multiply(multiplicand, Global.mathContext).toString());
    }

    @Override
    public BigDecimal2 subtract(BigDecimal substrahend) {
        return new BigDecimal2(super.subtract(substrahend).toString());
    }

}
