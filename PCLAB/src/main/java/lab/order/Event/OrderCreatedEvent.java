package lab.order.Event;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, Long storeId, BigDecimal total) {
}
