package lab.order.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(@NotNull Long storeId, @NotEmpty List<@Valid OrderItemRequestDTO> items) {
}
