package order_service.order_service.catalog;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        Integer stock,
        ProductStatus status
) {
}
