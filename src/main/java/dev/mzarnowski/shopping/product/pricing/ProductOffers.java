package dev.mzarnowski.shopping.product.pricing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.mzarnowski.shopping.product.pricing.Statistics.Quality.*;

public class ProductOffers {
    private final ProductCode productCode;
    private final AtomicInteger remainingQuota;
    private final Statistics statistics = new Statistics(MathContext.DECIMAL64);
    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public ProductOffers(ProductCode productCode, int quota) {
        this.productCode = productCode;
        this.remainingQuota = new AtomicInteger(quota);
    }

    public Optional<Event> close() {
        var remainingQuota = this.remainingQuota.get();
        if (0 < remainingQuota) {
            return Optional.of(new FailedClosingAggregation(productCode, new QuotaNotReached(productCode, remainingQuota)));
        }

        if (isOpen.compareAndSet(true, false)) {
            return Optional.of(new AggregationClosed(productCode));
        }

        return Optional.empty();
    }

    public Event append(ProductCode productCode, Price price) {
        if (isOpen.get()) {
            remainingQuota.updateAndGet(it -> Math.max(0, it - 1));
            statistics.update(price.value());
            return new OfferAppended(productCode, price);
        }

        return new FailedAppendingOffer(productCode, price, new AggregationIsClosed(productCode));
    }

    public Optional<Aggregation> getAggregation() {
        var count = statistics.get(COUNT).longValue();

        if (count == 0) {
            return Optional.empty();
        }

        var max = statistics.get(MAX);
        var min = statistics.get(MIN);
        var average = statistics.get(AVERAGE);

        var aggregation = new Aggregation(count, min, max, average);
        return Optional.of(aggregation);
    }

    record Aggregation(long count, BigDecimal min, BigDecimal max, BigDecimal average) {}

    public sealed interface Event {}

    public record AggregationClosed(ProductCode productCode) implements Event {}

    public record OfferAppended(ProductCode productCode, Price price) implements Event {}

    public record FailedAppendingOffer(ProductCode productCode, Price price, Exception cause) implements Event {}

    public record FailedClosingAggregation(ProductCode productCode, Exception cause) implements Event {}

    public static final class AggregationIsClosed extends RuntimeException {
        private final ProductCode productCode;

        public AggregationIsClosed(ProductCode productCode) {
            this.productCode = productCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AggregationIsClosed that = (AggregationIsClosed) o;
            return productCode.equals(that.productCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productCode);
        }
    }

    public static final class QuotaNotReached extends RuntimeException {
        private final ProductCode productCode;
        private final int remainingQuota;

        public QuotaNotReached(ProductCode productCode, int remainingQuota) {
            this.productCode = productCode;
            this.remainingQuota = remainingQuota;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QuotaNotReached that = (QuotaNotReached) o;
            return remainingQuota == that.remainingQuota && productCode.equals(that.productCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productCode, remainingQuota);
        }
    }
}
