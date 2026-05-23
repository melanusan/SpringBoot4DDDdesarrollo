package com.debuggeandoideas.erp_lite.use_cases.order;

import com.debuggeandoideas.erp_lite.commands.order.CreateOrderCommand;
import com.debuggeandoideas.erp_lite.domain.entities.order.Customer;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderItem;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderNumber;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderRoot;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductId;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.OrderRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.ports.services.CustomerProviderServicePort;
import com.debuggeandoideas.erp_lite.domain.ports.services.OrderConfirmEmailServicePort;
import com.debuggeandoideas.erp_lite.domain.shared.CustomerId;
import com.debuggeandoideas.erp_lite.domain.shared.Email;
import com.debuggeandoideas.erp_lite.domain.shared.Quantity;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
* JIRA TICKET: ERP-6734
 * Business Rules:
 * - Customer must exist in external system (JSONPlaceholder)
 * - All products must exist and be active
 * - All products must have sufficient stock
 * - Order number is generated automatically
 * Flow:
 * 1. Validate customer exists
 * 2. Validate products exist
 * 3. Create order items from products (snapshot prices)
 * 4. Create order aggregate (domain generates ID)
 * 5. Persist order (write model - PostgreSQL)
 * 6. Publish domain events (for MongoDB sync, email notifications)
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final CustomerProviderServicePort customerProviderService;
    private final OrderConfirmEmailServicePort emailService;

    public String execute(CreateOrderCommand command) {
        log.info("[{}] Starting - customerId={}, items={}", getClass().getSimpleName(),
                command.customerId(), command.items().size());
        MDC.put("userId", String.valueOf(command.customerId()));
        try {
            Customer customer = this.validateAndGet(command.customerId());

            List<OrderItem> items = this.createOrderItems(command.items());

            OrderNumber orderNumber = this.generateOrderNumber();

            OrderRoot orderRoot = OrderRoot.create(
                    orderNumber,
                    customer,
                    items,
                    command.createdBy()
            );

            MDC.put("orderId", orderRoot.getId().value().toString());
            try {
                OrderRoot savedOrder = this.orderRepository.save(orderRoot);

                log.info("[{}] Completed - orderId={}", getClass().getSimpleName(),
                        savedOrder.getId().value());

                this.sendMail(orderRoot, customer);

                return orderRoot.getId().value().toString();

            } finally {
                MDC.clear();
            }

        } catch (IllegalArgumentException iae) {
            log.error("[{}] Invalid data for order creation - message={}", getClass().getSimpleName(),
                    iae.getMessage(), iae);
            throw new CommandException("Error on create order msg: " + iae.getMessage());
        } catch (Exception e) {
            log.error("[{}] Unexpected error creating order", getClass().getSimpleName(), e);
            throw new CommandException(e.getMessage());
        }
    }

    private Customer validateAndGet(Long customerId) {
        log.info("[{}] Validating customer - customerId={}", getClass().getSimpleName(), customerId);
        var customerInfo = this.customerProviderService.findById(customerId)
                .orElseThrow(() -> new CommandException("Customer not found is" + customerId));

        log.info("[{}] Customer validated - customerName={}", getClass().getSimpleName(), customerInfo.name());

        return Customer.of(
                CustomerId.of(customerId),
                customerInfo.name()
        );
    }

    private List<OrderItem> createOrderItems(List<CreateOrderCommand.OrderItemRequest>commandItems) {
        log.info("[{}] Creating order items - count={}", getClass().getSimpleName(), commandItems.size());

        return commandItems.stream()
                .map(this::toOrderItem)
                .toList();
    }

    private OrderItem toOrderItem(CreateOrderCommand.OrderItemRequest commandItem) {
        ProductRoot productRoot = this.productRepository
                .findAllById(ProductId.of(UUID.fromString(commandItem.productId())))
                .orElseThrow(() -> new CommandException("Product not found"));

        Quantity quantity = Quantity.of(commandItem.quantity());

        return OrderItem.from(productRoot, quantity);
    }

    private OrderNumber generateOrderNumber() {
        int sequence = (int) (System.currentTimeMillis() % 1000);
        return OrderNumber.generate(sequence);
    }

    private void publishDomanEvent(OrderRoot order) {
        var events = order.getDomainEvents();
        log.info("[{}] Publishing domain events - count={}", getClass().getSimpleName(), events.size());
        events.forEach(event -> {
            log.debug("[{}] Publishing event - eventType={}", getClass().getSimpleName(), event.getClass().getSimpleName());
            // TODO: send event on queue
        });

        order.clearDomainEvents();
        log.info("[{}] Domain events published successfully", getClass().getSimpleName());
    }

    private void sendMail(OrderRoot order, Customer customer) {
        try {
            log.info("[{}] Sending confirmation email - customerName={}, orderId={}",
                    getClass().getSimpleName(), customer.customerName(), order.getId().value());
            final var mail = Email.of("debuggeandoideas@gmail.com");

            this.emailService.sendMail(
                    mail,
                    order.getId(),
                    order.getOrderNumber().value(),
                    order.getTotalAmount(),
                    customer.customerName(),
                    order.getItems().size()
            );
        } catch (Exception e) {
            log.error("[{}] Error sending confirmation email - orderId={}", getClass().getSimpleName(),
                    order.getId().value(), e);
            throw new CommandException("Error sending mail: " + e.getMessage());
        }
    }
}
