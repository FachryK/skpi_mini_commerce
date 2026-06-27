package catalog_service.catalog_service.service;

import catalog_service.catalog_service.common.ApiException;
import catalog_service.catalog_service.dto.CreateProductRequest;
import catalog_service.catalog_service.dto.ProductResponse;
import catalog_service.catalog_service.model.Product;
import catalog_service.catalog_service.model.ProductStatus;
import catalog_service.catalog_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        String sku = request.sku().trim();
        if (productRepository.existsBySkuIgnoreCase(sku)) {
            throw new ApiException(HttpStatus.CONFLICT, "SKU already exists");
        }

        Product product = new Product(
                sku,
                request.name().trim(),
                request.price(),
                request.stock()
        );
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String search, ProductStatus status, Pageable pageable) {
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        return productRepository.search(normalizedSearch, status, pageable).map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    @Transactional
    public ProductResponse updateStock(Long id, Integer stock) {
        Product product = getProduct(id);
        product.setStock(stock);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse adjustStock(Long id, Integer quantityChange) {
        Product product = getProduct(id);
        int nextStock = product.getStock() + quantityChange;
        if (nextStock < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }
        product.setStock(nextStock);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateStatus(Long id, ProductStatus status) {
        Product product = getProduct(id);
        product.setStatus(status);
        return ProductResponse.from(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
