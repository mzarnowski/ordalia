package dev.mzarnowski.shopping.product.pricing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PriceTest {
    @Test
    public void price_cannot_be_negative() {
        assertThrows(IllegalArgumentException.class, () -> Price.of(new BigDecimal(-1)));
        assertThrows(IllegalArgumentException.class, () -> Price.of(new BigDecimal(0)));
    }
}