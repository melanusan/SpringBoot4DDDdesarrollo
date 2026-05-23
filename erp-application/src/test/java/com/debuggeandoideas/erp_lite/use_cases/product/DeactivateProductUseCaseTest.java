package com.debuggeandoideas.erp_lite.use_cases.product;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.product.DeactivateProductCommand;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductRepositoryPort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivateProductUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private DeactivateProductUseCase useCase;

    @Test
    void should_deactivateProduct_when_productIsActive() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot activeProduct = TestFixtures.anActiveProduct();
        DeactivateProductCommand command = new DeactivateProductCommand(productId);

        when(productRepository.findAllById(any())).thenReturn(Optional.of(activeProduct));
        when(productRepository.save(any())).thenReturn(activeProduct);

        // When / Then
        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
        verify(productRepository).findAllById(any());
        verify(productRepository).save(activeProduct);
    }

    @Test
    void should_throwCommandException_when_productNotFound() {
        // Given
        String productId = UUID.randomUUID().toString();
        DeactivateProductCommand command = new DeactivateProductCommand(productId);

        when(productRepository.findAllById(any())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_throwCommandException_when_productAlreadyDeactivated() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct();
        product.deactivate(); // deactivate first
        DeactivateProductCommand command = new DeactivateProductCommand(productId);

        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessage("Product is already deactivated");
        verify(productRepository, never()).save(any());
    }
}
