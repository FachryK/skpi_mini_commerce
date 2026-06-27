package catalog_service.catalog_service.repository;

import catalog_service.catalog_service.model.Product;
import catalog_service.catalog_service.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySkuIgnoreCase(String sku);

    @Query("""
            SELECT p FROM Product p
            WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Product> search(@Param("search") String search, @Param("status") ProductStatus status, Pageable pageable);
}
