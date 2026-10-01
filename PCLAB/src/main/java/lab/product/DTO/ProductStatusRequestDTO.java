package lab.product.DTO;

import lab.product.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusRequestDTO(@NotNull(message = "El estado es obligatorio") ProductStatus status) {
}
