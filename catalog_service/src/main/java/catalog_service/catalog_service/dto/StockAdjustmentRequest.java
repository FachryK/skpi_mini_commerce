package catalog_service.catalog_service.dto;

import jakarta.validation.constraints.NotNull;

public record StockAdjustmentRequest(@NotNull Integer quantityChange) {
}
