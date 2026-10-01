package lab.product.structure;

import jakarta.validation.Valid;
import lab.product.DTO.ProductRequestDTO;
import lab.product.domain.ServiceProduct;
import lab.product.infresct.ProductResponse;
import lab.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ServiceProduct serviceProduct;

    @GetMapping("/stores/{storeId}/products")
    public List<ProductResponse> findByStore(@PathVariable Long storeId) {
        return serviceProduct.findByStore(storeId);
    }

    @GetMapping("/products/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return serviceProduct.findById(id);
    }

    @PostMapping("/stores/{storeId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ProductResponse create(@PathVariable Long storeId, @Valid @RequestBody ProductRequestDTO request,
                                  @AuthenticationPrincipal UserPrincipal current) {
        return serviceProduct.create(storeId, request, current);
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request,
                                  @AuthenticationPrincipal UserPrincipal current) {
        return serviceProduct.update(id, request, current);
    }
}
