package catalog_service.catalog_service.controller;

import catalog_service.catalog_service.dto.CreateProductRequest;
import catalog_service.catalog_service.dto.ProductResponse;
import catalog_service.catalog_service.dto.StatusUpdateRequest;
import catalog_service.catalog_service.dto.StockAdjustmentRequest;
import catalog_service.catalog_service.dto.StockUpdateRequest;
import catalog_service.catalog_service.model.ProductStatus;
import catalog_service.catalog_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @GetMapping
    public Page<ProductResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProductStatus status,
            Pageable pageable
    ) {
        return productService.findAll(search, status, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PatchMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return productService.updateStock(id, request.stock());
    }

    @PatchMapping("/{id}/stock-adjustments")
    public ProductResponse adjustStock(@PathVariable Long id, @Valid @RequestBody StockAdjustmentRequest request) {
        return productService.adjustStock(id, request.quantityChange());
    }

    @PatchMapping("/{id}/status")
    public ProductResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return productService.updateStatus(id, request.status());
    }
}
