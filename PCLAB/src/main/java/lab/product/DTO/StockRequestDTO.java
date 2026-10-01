package lab.product.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StockRequestDTO(
        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
        Integer stock
) {
}
