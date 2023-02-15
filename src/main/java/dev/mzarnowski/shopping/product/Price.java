package dev.mzarnowski.shopping.product;

import java.math.BigDecimal;

public class Price {
    private final BigDecimal value;

    public static Price of(BigDecimal value) {
        if (isNegative(value)) {
            throw new IllegalArgumentException("Price cannot be negative: " + value);
        }
        return new Price(value);
    }

    private Price(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal value() {
        return value;
    }

    private static boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }
}
