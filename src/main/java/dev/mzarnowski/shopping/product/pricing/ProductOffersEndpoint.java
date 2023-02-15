package dev.mzarnowski.shopping.product.pricing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "/product", produces = "application/json")
class ProductOffersEndpoint {
    private final ProductOfferRepository repository;

    ProductOffersEndpoint(ProductOfferRepository repository) {
        this.repository = repository;
    }

    @PutMapping(path = "/{id}/offers/close")
    public void closeAggregation(@PathVariable String id) {
        try {
            var product = new ProductCode(id);
            var result = repository.offersOf(product).close().orElse(new ProductOffers.AggregationClosed(product));
            if (result instanceof ProductOffers.AggregationClosed) {
                throw new ResponseStatusException(OK);
            } else if (result instanceof ProductOffers.FailedClosingAggregation failure) {
                throw new ResponseStatusException(FORBIDDEN, failure.cause().getMessage());
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/{id}/offers")
    public ResponseEntity<Void> postOffer(@PathVariable String id, @RequestBody Price price) {
        try {
            var product = new ProductCode(id);
            var events = repository.offersOf(product).append(product, price);
            if (events instanceof ProductOffers.FailedAppendingOffer failure) {
                throw new ResponseStatusException(FORBIDDEN, failure.cause().getMessage());
            }
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping(path = "/{id}/offers/aggregation")
    public ResponseEntity<ProductOffers.Aggregation> getOfferAggregation(@PathVariable String id) {
        try {
            var product = new ProductCode(id);
            return repository.offersOf(product).getAggregation()
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Aggregation not available: " + id));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
