package dev.mzarnowski.shopping.product.pricing;

import java.math.BigDecimal;

public record Price(BigDecimal value) {
    public static Price of(BigDecimal value) {
        return new Price(value);
    }
}
