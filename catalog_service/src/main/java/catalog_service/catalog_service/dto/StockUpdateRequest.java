package catalog_service.catalog_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockUpdateRequest(@NotNull @Min(0) Integer stock) {
}
