package dev.mzarnowski.shopping.product;

import org.junit.jupiter.api.Test;

import java.math.MathContext;
import java.util.stream.IntStream;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsTest {
    @Test
    public void aggregates_values() {
        var context = new MathContext(2, HALF_EVEN);
        var statistics = new Statistics(context);

        for (int i = 0; i < 100; i++) {
            statistics.update(valueOf(i));

            var stats = IntStream.rangeClosed(0, i).summaryStatistics();
            assertEquals(statistics.get(Statistics.Quality.MIN), valueOf(stats.getMin()));
            assertEquals(statistics.get(Statistics.Quality.MAX), valueOf(stats.getMax()));
            assertEquals(statistics.get(Statistics.Quality.TOTAL), valueOf(stats.getSum()));
            assertEquals(statistics.get(Statistics.Quality.COUNT), valueOf(stats.getCount()));
            assertEquals(statistics.get(Statistics.Quality.AVERAGE), valueOf(stats.getAverage()).setScale(2, context.getRoundingMode()));
        }
    }
}