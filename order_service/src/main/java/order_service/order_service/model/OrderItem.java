package order_service.order_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 64)
    private String productSku;

    @Column(nullable = false, length = 150)
    private String productName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    protected OrderItem() {
    }

    public OrderItem(Long productId, String productSku, String productName, BigDecimal productPrice, Integer quantity) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.subtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductSku() {
        return productSku;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    void setOrder(CustomerOrder order) {
        this.order = order;
    }
}
