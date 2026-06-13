package catalog_service.catalog_service.service;

import catalog_service.catalog_service.common.ApiException;
import catalog_service.catalog_service.dto.CreateProductRequest;
import catalog_service.catalog_service.model.Product;
import catalog_service.catalog_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void create_shouldThrowConflict_whenSkuAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest("INDMR", "Indomie Rendang", BigDecimal.valueOf(3500), 5);
        when(productRepository.existsBySkuIgnoreCase("INDMR")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void adjustStock_shouldThrowBadRequest_whenResultingStockIsNegative() {
        Product product = new Product("INDMR", "Indomie Rendang", BigDecimal.valueOf(3500), 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.adjustStock(1L, -10))
                .isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(product.getStock()).isEqualTo(5);
    }
}
