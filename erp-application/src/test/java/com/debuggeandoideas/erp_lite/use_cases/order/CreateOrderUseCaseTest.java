package com.debuggeandoideas.erp_lite.use_cases.order;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.order.CreateOrderCommand;
import com.debuggeandoideas.erp_lite.domain.entities.customer.CustomerInfo;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.OrderRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.ports.services.CustomerProviderServicePort;
import com.debuggeandoideas.erp_lite.domain.ports.services.OrderConfirmEmailServicePort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private CustomerProviderServicePort customerProviderService;

    @Mock
    private OrderConfirmEmailServicePort emailService;

    @InjectMocks
    private CreateOrderUseCase useCase;

    @Test
    void should_createOrder_when_validDataProvided() {
        // Given
        ProductRoot product = TestFixtures.anActiveProduct();
        CustomerInfo customer = TestFixtures.aCustomerInfo();
        String productId = UUID.randomUUID().toString();

        CreateOrderCommand command = new CreateOrderCommand(
                1L,
                List.of(new CreateOrderCommand.OrderItemRequest(productId, 2)),
                "admin"
        );

        when(customerProviderService.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(emailService).sendMail(any(), any(), any(), any(), any(), anyInt());

        // When
        String result = useCase.execute(command);

        // Then
        assertThat(result).isNotBlank();
        verify(customerProviderService).findById(1L);
        verify(productRepository).findAllById(any());
        verify(orderRepository).save(any());
        verify(emailService).sendMail(any(), any(), any(), any(), any(), anyInt());
    }

    @Test
    void should_throwCommandException_when_customerNotFound() {
        // Given
        String productId = UUID.randomUUID().toString();
        CreateOrderCommand command = new CreateOrderCommand(
                99L,
                List.of(new CreateOrderCommand.OrderItemRequest(productId, 1)),
                "admin"
        );

        when(customerProviderService.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class);
        verify(customerProviderService).findById(99L);
        verifyNoInteractions(productRepository, orderRepository, emailService);
    }

    @Test
    void should_throwCommandException_when_productNotFound() {
        // Given
        String productId = UUID.randomUUID().toString();
        CustomerInfo customer = TestFixtures.aCustomerInfo();
        CreateOrderCommand command = new CreateOrderCommand(
                1L,
                List.of(new CreateOrderCommand.OrderItemRequest(productId, 1)),
                "admin"
        );

        when(customerProviderService.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(any())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class);
        verify(productRepository).findAllById(any());
        verifyNoInteractions(orderRepository, emailService);
    }

    @Test
    void should_throwCommandException_when_emailServiceFails() {
        // Given
        ProductRoot product = TestFixtures.anActiveProduct();
        CustomerInfo customer = TestFixtures.aCustomerInfo();
        String productId = UUID.randomUUID().toString();

        CreateOrderCommand command = new CreateOrderCommand(
                1L,
                List.of(new CreateOrderCommand.OrderItemRequest(productId, 2)),
                "admin"
        );

        when(customerProviderService.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).sendMail(any(), any(), any(), any(), any(), anyInt());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("Error sending mail");
    }
}
