package lab.order.structure;

import jakarta.validation.Valid;
import lab.order.DTO.OrderRequestDTO;
import lab.order.DTO.OrderStatusRequestDTO;
import lab.order.domain.ServiceOrder;
import lab.order.infresct.OrderResponse;
import lab.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final ServiceOrder serviceOrder;

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderResponse create(@Valid @RequestBody OrderRequestDTO request,
                                @AuthenticationPrincipal UserPrincipal current) {
        return serviceOrder.create(request, current);
    }

    @GetMapping("/orders/me")
    public List<OrderResponse> myOrders(@AuthenticationPrincipal UserPrincipal current) {
        return serviceOrder.findByCustomer(current.id());
    }

    @GetMapping("/orders/{id}")
    public OrderResponse findById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal current) {
        return serviceOrder.findById(id, current);
    }

    @GetMapping("/stores/{storeId}/orders")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public List<OrderResponse> findByStore(@PathVariable Long storeId,
                                           @AuthenticationPrincipal UserPrincipal current) {
        return serviceOrder.findByStore(storeId, current);
    }

    @PatchMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequestDTO request,
                                      @AuthenticationPrincipal UserPrincipal current) {
        return serviceOrder.updateStatus(id, request.status(), current);
    }

    @PostMapping("/orders/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal current) {
        return serviceOrder.cancel(id, current);
    }
}
