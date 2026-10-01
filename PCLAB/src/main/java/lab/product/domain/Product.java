package lab.product.domain;

import jakarta.persistence.*;
import lab.store.domain.Store;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.AVAILABLE;

    public void setStock(Integer stock) {
        this.stock = stock;
        if (status != ProductStatus.INACTIVE) {
            status = stock > 0 ? ProductStatus.AVAILABLE : ProductStatus.SOLD_OUT;
        }
    }
}
