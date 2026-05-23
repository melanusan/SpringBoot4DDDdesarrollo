package com.debuggeandoideas.erp_lite.commands.helpers;

import com.debuggeandoideas.erp_lite.domain.entities.order.OrderId;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.OrderRepositoryPort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandHelperTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private CommandHelper commandHelper;

    @Test
    void should_returnOrder_when_orderExists() {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderRoot mockOrder = org.mockito.Mockito.mock(OrderRoot.class);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(mockOrder));

        // When
        OrderRoot result = commandHelper.findOrderById(orderId);

        // Then
        assertThat(result).isNotNull().isEqualTo(mockOrder);
        verify(orderRepository).findById(any(OrderId.class));
    }

    @Test
    void should_throwCommandException_when_orderNotFound() {
        // Given
        String orderId = UUID.randomUUID().toString();
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> commandHelper.findOrderById(orderId))
                .isInstanceOf(CommandException.class)
                .hasMessage("Order not found");
        verify(orderRepository).findById(any(OrderId.class));
    }

    @Test
    void should_throwException_when_orderIdIsInvalidUUID() {
        // Given
        String invalidId = "not-a-valid-uuid";

        // When / Then
        assertThatThrownBy(() -> commandHelper.findOrderById(invalidId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
