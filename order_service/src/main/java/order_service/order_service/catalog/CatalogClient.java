package order_service.order_service.catalog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalogClient", url = "${catalog.service.url}", configuration = CatalogClientConfig.class)
public interface CatalogClient {
    @GetMapping("/api/products/{id}")
    ProductResponse getProduct(@PathVariable Long id);

    @PatchMapping("/api/products/{id}/stock-adjustments")
    ProductResponse adjustStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request);
}
