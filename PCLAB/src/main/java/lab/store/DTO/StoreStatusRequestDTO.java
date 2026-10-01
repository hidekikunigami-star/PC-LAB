package lab.store.DTO;

import jakarta.validation.constraints.NotNull;
import lab.store.domain.StoreStatus;

public record StoreStatusRequestDTO(@NotNull StoreStatus status) {
}
