package lab.order.domain;

import lab.User.infrest.UserRepository;
import lab.exception.BadRequestException;
import lab.exception.ConflictException;
import lab.exception.ForbiddenException;
import lab.exception.ResourceNotFoundException;
import lab.order.DTO.OrderItemRequestDTO;
import lab.order.DTO.OrderRequestDTO;
import lab.order.Event.OrderCreatedEvent;
import lab.order.infresct.OrderRepository;
import lab.order.infresct.OrderResponse;
import lab.product.domain.Product;
import lab.product.domain.ProductStatus;
import lab.product.infresct.ProductRepository;
import lab.security.UserPrincipal;
import lab.store.domain.ServiceStore;
import lab.store.domain.Store;
import lab.store.domain.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrder {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ServiceStore serviceStore;
    private final ApplicationEventPublisher events;

    @Transactional
    public OrderResponse create(OrderRequestDTO request, UserPrincipal current) {
        Store store = serviceStore.getStore(request.storeId());
        if (store.getStatus() != StoreStatus.OPEN) {
            throw new ConflictException("El local no esta abierto");
        }

        Order order = new Order();
        order.setCustomer(userRepository.getReferenceById(current.id()));
        order.setStore(store);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequestDTO itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            if (!product.getStore().getId().equals(store.getId())) {
                throw new BadRequestException("El producto no pertenece al local");
            }
            if (product.getStatus() != ProductStatus.AVAILABLE || product.getStock() < itemRequest.quantity()) {
                throw new ConflictException("Stock insuficiente para " + product.getName());
            }
            product.setStock(product.getStock() - itemRequest.quantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }
        order.setTotal(total);
        orderRepository.save(order);

        events.publishEvent(new OrderCreatedEvent(order.getId(), store.getId(), total));
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id, UserPrincipal current) {
        Order order = getOrder(id);
        boolean isCustomer = order.getCustomer().getId().equals(current.id());
        boolean isOwner = order.getStore().getOwner().getId().equals(current.id());
        if (!isCustomer && !isOwner && !current.isAdmin()) {
            throw new ForbiddenException("No tienes acceso a este pedido");
        }
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByStore(Long storeId, UserPrincipal current) {
        ServiceStore.checkOwner(serviceStore.getStore(storeId), current);
        return orderRepository.findByStoreId(storeId).stream().map(OrderResponse::from).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status, UserPrincipal current) {
        Order order = getOrder(id);
        ServiceStore.checkOwner(order.getStore(), current);
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("El pedido ya fue cerrado");
        }
        order.setStatus(status);
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancel(Long id, UserPrincipal current) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }
}
