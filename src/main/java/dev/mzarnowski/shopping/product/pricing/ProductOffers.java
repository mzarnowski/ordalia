package dev.mzarnowski.shopping.product.pricing;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductOffers {
    private final ProductCode productCode;
    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public ProductOffers(ProductCode productCode) {
        this.productCode = productCode;
    }

    public List<Event> close() {
        if (isOpen.compareAndSet(true, false)) {
            return List.of(new AggregationClosed(productCode));
        }
        return List.of();
    }

    public List<Event> append(ProductCode productCode, Price price) {
        if (isOpen.get()) {
            return List.of(new OfferAppended(productCode, price));
        }

        return List.of(new FailedAppendingOffer(productCode, price, new AggregationIsClosed(productCode)));
    }

    public sealed interface Event {}

    public record AggregationClosed(ProductCode productCode) implements Event {}

    public record OfferAppended(ProductCode productCode, Price price) implements Event {}

    public record FailedAppendingOffer(ProductCode productCode, Price price, Exception cause) implements Event {}

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
}
