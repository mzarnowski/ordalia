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
    public ResponseEntity<Void> closeAggregation(@PathVariable String id) {
        try {
            var product = new ProductCode(id);
            repository.offersOf(product).close();
            return ResponseEntity.ok().build();
        } catch (ProductOffers.QuotaNotReached e) {
            throw new ResponseStatusException(FORBIDDEN, e.getMessage());
        }catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/{id}/offers")
    public ResponseEntity<Void> appendOffer(@PathVariable String id, @RequestBody Price price) {
        try {
            var product = new ProductCode(id);
            repository.offersOf(product).append(product, price);
            return ResponseEntity.ok().build();
        } catch (ProductOffers.AggregationIsClosed e) {
            throw new ResponseStatusException(FORBIDDEN, e.getMessage());
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
