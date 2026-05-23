package com.debuggeandoideas.erp_lite.use_cases.product;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.product.UpdateProductCommand;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductImage;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.ports.services.ImageStorageServicePort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private ImageStorageServicePort imageStorageService;

    @InjectMocks
    private UpdateProductUseCase useCase;

    @Test
    void should_updateProduct_when_validCommandWithoutImage() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct();
        UpdateProductCommand command = new UpdateProductCommand(
                productId, "Updated Laptop Name", "Updated description",
                new BigDecimal("1800.00"), null, null, null
        );

        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        // When / Then
        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
        verify(productRepository).findAllById(any());
        verify(productRepository).save(product);
        verifyNoInteractions(imageStorageService);
    }

    @Test
    void should_updateProduct_when_validCommandWithNewImage() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct();
        byte[] imageData = "new-image".getBytes();
        UpdateProductCommand command = new UpdateProductCommand(
                productId, "Updated Laptop", null,
                null, null, imageData, "new-laptop.jpg"
        );

        ProductImage newImage = ProductImage.of("https://s3.amazonaws.com/bucket/new-laptop.jpg");
        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(imageStorageService.upload(anyString(), any())).thenReturn(newImage);
        when(productRepository.save(any())).thenReturn(product);

        // When / Then
        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
        verify(imageStorageService).upload("new-laptop.jpg", imageData);
        verify(productRepository).save(product);
    }

    @Test
    void should_throwCommandException_when_productNotFound() {
        // Given
        String productId = UUID.randomUUID().toString();
        UpdateProductCommand command = new UpdateProductCommand(
                productId, "Updated Name", null, null, null, null, null
        );

        when(productRepository.findAllById(any())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("Product not found");
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_throwCommandException_when_imageUploadFails() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductRoot product = TestFixtures.anActiveProduct();
        byte[] imageData = "broken-image".getBytes();
        UpdateProductCommand command = new UpdateProductCommand(
                productId, null, null, null, null, imageData, "broken.jpg"
        );

        when(productRepository.findAllById(any())).thenReturn(Optional.of(product));
        when(imageStorageService.upload(anyString(), any())).thenThrow(new RuntimeException("S3 unavailable"));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("Failed to upload product image");
        verify(productRepository, never()).save(any());
    }
}
