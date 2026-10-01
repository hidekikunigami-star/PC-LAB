package lab.order.Event;

import lab.order.domain.OrderStatus;

public record OrderStatusChangedEvent(Long orderId, Long customerId, Long storeId,
                                      OrderStatus previousStatus, OrderStatus newStatus, String changedBy) {
}
