package dev.mzarnowski.shopping.product;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.EnumMap;
import java.util.Map;

public class Statistics {
    private final MathContext context;
    private final Map<Quality, BigDecimal> statistics = new EnumMap<>(Quality.class);

    public Statistics(MathContext context) {
        this.context = context;
        statistics.put(Quality.COUNT, BigDecimal.ZERO);
    }

    public void update(BigDecimal price) {
        statistics.merge(Quality.MIN, price, BigDecimal::min);
        statistics.merge(Quality.MAX, price, BigDecimal::max);

        var total = statistics.merge(Quality.TOTAL, price, BigDecimal::add);
        var count = statistics.merge(Quality.COUNT, BigDecimal.ONE, BigDecimal::add);
        statistics.put(Quality.AVERAGE, total.divide(count, 2, context.getRoundingMode()));
    }

    public BigDecimal get(Quality key) {
        return statistics.get(key);
    }

    public enum Quality {
        MIN, MAX, TOTAL, COUNT, AVERAGE;
    }
}
