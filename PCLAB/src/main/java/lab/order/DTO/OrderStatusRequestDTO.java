package lab.order.DTO;

import jakarta.validation.constraints.NotNull;
import lab.order.domain.OrderStatus;

public record OrderStatusRequestDTO(@NotNull OrderStatus status) {
}
