package lab.store.DTO;

import jakarta.validation.constraints.NotBlank;

public record StoreRequestDTO(@NotBlank String name, String location) {
}
