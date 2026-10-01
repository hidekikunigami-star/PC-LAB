package lab.order.infresct;

import lab.order.domain.Order;
import lab.order.domain.OrderItem;
import lab.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, Long customerId, Long storeId, OrderStatus status,
                            BigDecimal total, List<Item> items, LocalDateTime createdAt) {

    public record Item(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {

        static Item from(OrderItem item) {
            return new Item(item.getProduct().getId(), item.getProduct().getName(),
                    item.getQuantity(), item.getUnitPrice());
        }
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getCustomer().getId(), order.getStore().getId(),
                order.getStatus(), order.getTotal(), order.getItems().stream().map(Item::from).toList(),
                order.getCreatedAt());
    }
}
