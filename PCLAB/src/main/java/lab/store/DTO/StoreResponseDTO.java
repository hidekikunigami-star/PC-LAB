package lab.store.DTO;

import lab.store.domain.Store;
import lab.store.domain.StoreStatus;

public record StoreResponseDTO(Long id, String name, Long ownerId, String location, StoreStatus status) {

    public static StoreResponseDTO from(Store store) {
        return new StoreResponseDTO(store.getId(), store.getName(), store.getOwner().getId(),
                store.getLocation(), store.getStatus());
    }
}
