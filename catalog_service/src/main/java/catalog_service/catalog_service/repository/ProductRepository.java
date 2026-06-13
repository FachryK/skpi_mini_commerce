package catalog_service.catalog_service.repository;

import catalog_service.catalog_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySkuIgnoreCase(String sku);
}
