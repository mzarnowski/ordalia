package dev.mzarnowski.shopping.product.pricing;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductOffers {
    private final ProductCode productCode;
    private final AtomicInteger remainingQuota;
    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public ProductOffers(ProductCode productCode, int quota) {
        this.productCode = productCode;
        this.remainingQuota = new AtomicInteger(quota);
    }

    public List<Event> close() {
        var remainingQuota = this.remainingQuota.get();
        if(0 < remainingQuota){
            return List.of(new FailedClosingAggregation(productCode, new QuotaNotReached(productCode, remainingQuota)));
        }

        if (isOpen.compareAndSet(true, false)) {
            return List.of(new AggregationClosed(productCode));
        }
        return List.of();
    }

    public List<Event> append(ProductCode productCode, Price price) {
        if (isOpen.get()) {
            remainingQuota.decrementAndGet();
            return List.of(new OfferAppended(productCode, price));
        }

        return List.of(new FailedAppendingOffer(productCode, price, new AggregationIsClosed(productCode)));
    }

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
