package lab.store.domain;

import lab.User.infrest.UserRepository;
import lab.exception.ConflictException;
import lab.exception.ForbiddenException;
import lab.exception.ResourceNotFoundException;
import lab.security.UserPrincipal;
import lab.store.DTO.StoreRequestDTO;
import lab.store.DTO.StoreResponseDTO;
import lab.store.infresct.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceStore {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoreResponseDTO create(StoreRequestDTO request, UserPrincipal current) {
        if (storeRepository.existsByName(request.name())) {
            throw new ConflictException("Ya existe un local con ese nombre");
        }
        Store store = new Store();
        store.setName(request.name());
        store.setLocation(request.location());
        store.setOwner(userRepository.getReferenceById(current.id()));
        return StoreResponseDTO.from(storeRepository.save(store));
    }

    public List<StoreResponseDTO> findAll() {
        return storeRepository.findAll().stream().map(StoreResponseDTO::from).toList();
    }

    public StoreResponseDTO findById(Long id) {
        return StoreResponseDTO.from(getStore(id));
    }

    @Transactional
    public StoreResponseDTO update(Long id, StoreRequestDTO request, UserPrincipal current) {
        Store store = getStore(id);
        checkOwner(store, current);
        if (!store.getName().equals(request.name()) && storeRepository.existsByName(request.name())) {
            throw new ConflictException("Ya existe un local con ese nombre");
        }
        store.setName(request.name());
        store.setLocation(request.location());
        return StoreResponseDTO.from(store);
    }

    @Transactional
    public StoreResponseDTO changeStatus(Long id, StoreStatus status, UserPrincipal current) {
        Store store = getStore(id);
        checkOwner(store, current);
        store.setStatus(status);
        return StoreResponseDTO.from(store);
    }

    public Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local no encontrado"));
    }

    public static void checkOwner(Store store, UserPrincipal current) {
        if (!current.isAdmin() && !store.getOwner().getId().equals(current.id())) {
            throw new ForbiddenException("No eres el duenio del local");
        }
    }
}
