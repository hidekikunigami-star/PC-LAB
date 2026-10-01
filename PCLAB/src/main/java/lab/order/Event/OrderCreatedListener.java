package lab.order.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderCreatedListener {

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Nuevo pedido #{} en el local {} por S/ {}", event.orderId(), event.storeId(), event.total());
    }
}
