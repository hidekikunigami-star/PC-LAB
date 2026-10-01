package lab.store.structure;

import jakarta.validation.Valid;
import lab.security.UserPrincipal;
import lab.store.DTO.StoreRequestDTO;
import lab.store.DTO.StoreResponseDTO;
import lab.store.DTO.StoreStatusRequestDTO;
import lab.store.domain.ServiceStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final ServiceStore serviceStore;

    @GetMapping
    public List<StoreResponseDTO> findAll() {
        return serviceStore.findAll();
    }

    @GetMapping("/{id}")
    public StoreResponseDTO findById(@PathVariable Long id) {
        return serviceStore.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public StoreResponseDTO create(@Valid @RequestBody StoreRequestDTO request,
                                   @AuthenticationPrincipal UserPrincipal current) {
        return serviceStore.create(request, current);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public StoreResponseDTO update(@PathVariable Long id, @Valid @RequestBody StoreRequestDTO request,
                                   @AuthenticationPrincipal UserPrincipal current) {
        return serviceStore.update(id, request, current);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public StoreResponseDTO changeStatus(@PathVariable Long id, @Valid @RequestBody StoreStatusRequestDTO request,
                                         @AuthenticationPrincipal UserPrincipal current) {
        return serviceStore.changeStatus(id, request.status(), current);
    }
}
