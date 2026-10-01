package lab.product.domain;

import lab.exception.ResourceNotFoundException;
import lab.product.DTO.ProductRequestDTO;
import lab.product.infresct.ProductRepository;
import lab.product.infresct.ProductResponse;
import lab.security.UserPrincipal;
import lab.store.domain.ServiceStore;
import lab.store.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceProduct {

    private final ProductRepository productRepository;
    private final ServiceStore serviceStore;

    @Transactional
    public ProductResponse create(Long storeId, ProductRequestDTO request, UserPrincipal current) {
        Store store = serviceStore.getStore(storeId);
        ServiceStore.checkOwner(store, current);
        Product product = new Product();
        product.setStore(store);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> findByStore(Long storeId) {
        return productRepository.findByStoreId(storeId).stream().map(ProductResponse::from).toList();
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequestDTO request, UserPrincipal current) {
        Product product = getProduct(id);
        ServiceStore.checkOwner(product.getStore(), current);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return ProductResponse.from(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
}
