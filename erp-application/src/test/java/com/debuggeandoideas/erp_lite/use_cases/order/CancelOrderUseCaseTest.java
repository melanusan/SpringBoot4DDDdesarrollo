package com.debuggeandoideas.erp_lite.use_cases.order;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.helpers.CommandHelper;
import com.debuggeandoideas.erp_lite.commands.order.CancelOrderCommand;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.OrderRepositoryPort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private CommandHelper commandHelper;

    @InjectMocks
    private CancelOrderUseCase useCase;

    @Test
    void should_cancelOrder_when_orderIsPending() {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderRoot pendingOrder = TestFixtures.aPendingOrder();
        CancelOrderCommand command = new CancelOrderCommand(orderId, "Customer requested cancellation of this order");

        when(commandHelper.findOrderById(orderId)).thenReturn(pendingOrder);
        when(orderRepository.save(any())).thenReturn(pendingOrder);

        // When / Then
        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
        verify(commandHelper).findOrderById(orderId);
        verify(orderRepository).save(pendingOrder);
    }

    @Test
    void should_throwCommandException_when_orderNotFound() {
        // Given
        String orderId = UUID.randomUUID().toString();
        CancelOrderCommand command = new CancelOrderCommand(orderId, "Customer requested cancellation of order");

        when(commandHelper.findOrderById(orderId)).thenThrow(new CommandException("Order not found"));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessage("Error on cancel order");
        verifyNoInteractions(orderRepository);
    }

    @Test
    void should_throwCommandException_when_orderIsInFinalState() {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderRoot shippedOrder = TestFixtures.aShippedOrder();
        // SHIPPED → DELIVERED is valid but SHIPPED → CANCELLED is not
        // Advance to DELIVERED (final state)
        shippedOrder.deliver();
        CancelOrderCommand command = new CancelOrderCommand(orderId, "Trying to cancel a delivered order");

        when(commandHelper.findOrderById(orderId)).thenReturn(shippedOrder);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessage("Error on cancel order");
        verifyNoInteractions(orderRepository);
    }
}
