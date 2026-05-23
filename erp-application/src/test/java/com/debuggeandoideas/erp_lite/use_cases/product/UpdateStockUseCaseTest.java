package com.debuggeandoideas.erp_lite.use_cases.product;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.product.UpdateStockCommand;
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
class UpdateStockUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private UpdateStockUseCase useCase;

    @Test
    void should_incrementStock_when_positiveQuantity() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct(); // stock = 100
        UpdateStockCommand command = new UpdateStockCommand(productId, 10, "PURCHASE");

        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        // When / Then
        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
        verify(productRepository).findAllById(any());
        verify(productRepository).save(product);
    }

    @Test
    void should_decrementStock_when_negativeQuantity() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct(); // stock = 100
        UpdateStockCommand command = new UpdateStockCommand(productId, -5, "SALE");

        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        // When / Then
        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
        verify(productRepository).save(product);
    }

    @Test
    void should_throwCommandException_when_productNotFound() {
        // Given
        String productId = UUID.randomUUID().toString();
        UpdateStockCommand command = new UpdateStockCommand(productId, 5, "PURCHASE");

        when(productRepository.findAllById(any())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_throwCommandException_when_insufficientStock() {
        // Given — product has stock=100, try to decrement 200
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct();
        UpdateStockCommand command = new UpdateStockCommand(productId, -200, "SALE");

        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class);
        verify(productRepository, never()).save(any());
    }
}
