package com.debuggeandoideas.erp_lite.use_cases.order;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.helpers.CommandHelper;
import com.debuggeandoideas.erp_lite.commands.order.UpdateOrderStatusCommand;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderRoot;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderStatus;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.OrderRepositoryPort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderStatusUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private CommandHelper commandHelper;

    @InjectMocks
    private UpdateOrderStatusUseCase useCase;

    @Test
    void should_updateStatusToConfirmed_when_orderIsPending() {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderRoot pendingOrder = TestFixtures.aPendingOrder();
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, "CONFIRMED");

        when(commandHelper.findOrderById(orderId)).thenReturn(pendingOrder);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        String result = useCase.execute(command);

        // Then
        assertThat(result).contains(OrderStatus.CONFIRMED);
        verify(commandHelper).findOrderById(orderId);
        verify(orderRepository).save(pendingOrder);
    }

    @Test
    void should_updateStatusToShipped_when_orderIsConfirmed() {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderRoot confirmedOrder = TestFixtures.aConfirmedOrder();
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, "SHIPPED");

        when(commandHelper.findOrderById(orderId)).thenReturn(confirmedOrder);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        String result = useCase.execute(command);

        // Then
        assertThat(result).contains(OrderStatus.SHIPPED);
        verify(orderRepository).save(confirmedOrder);
    }

    @Test
    void should_updateStatusToDelivered_when_orderIsShipped() {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderRoot shippedOrder = TestFixtures.aShippedOrder();
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, "DELIVERED");

        when(commandHelper.findOrderById(orderId)).thenReturn(shippedOrder);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        String result = useCase.execute(command);

        // Then
        assertThat(result).contains(OrderStatus.DELIVERED);
        verify(orderRepository).save(shippedOrder);
    }

    @Test
    void should_throwCommandException_when_orderNotFound() {
        // Given
        String orderId = UUID.randomUUID().toString();
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, "CONFIRMED");

        when(commandHelper.findOrderById(orderId)).thenThrow(new CommandException("Order not found"));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessage("Unexpected error updating order status");
        verifyNoInteractions(orderRepository);
    }

    @Test
    void should_throwCommandException_when_invalidStatusTransition() {
        // Given — PENDING → SHIPPED is invalid (must go through CONFIRMED first)
        String orderId = UUID.randomUUID().toString();
        OrderRoot pendingOrder = TestFixtures.aPendingOrder();
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, "SHIPPED");

        when(commandHelper.findOrderById(orderId)).thenReturn(pendingOrder);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessage("Error updating order status");
        verifyNoInteractions(orderRepository);
    }
}
