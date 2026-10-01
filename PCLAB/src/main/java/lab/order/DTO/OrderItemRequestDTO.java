package lab.order.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {
}
