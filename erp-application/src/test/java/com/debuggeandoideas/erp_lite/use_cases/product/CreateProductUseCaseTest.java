package com.debuggeandoideas.erp_lite.use_cases.product;

import com.debuggeandoideas.erp_lite.TestFixtures;
import com.debuggeandoideas.erp_lite.commands.product.CreateProductCommand;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductImage;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.ports.messages.EventPublisherPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private ImageStorageServicePort imageStorageService;

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private CreateProductUseCase useCase;

    @Test
    void should_createProduct_when_validCommandWithoutImage() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
                "LAPTOP-001", "Gaming Laptop", "A gaming laptop",
                new BigDecimal("1500.00"), "USD", 100,
                "cat-electronics", null, null, "admin"
        );

        ProductRoot savedProduct = TestFixtures.anActiveProduct();
        when(productRepository.findBySku("LAPTOP-001")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(savedProduct);
        doNothing().when(eventPublisher).publish(any());

        // When
        String result = useCase.execute(command);

        // Then
        assertThat(result).isNotBlank();
        verify(productRepository).findBySku("LAPTOP-001");
        verify(productRepository).save(any());
        verify(eventPublisher, atLeastOnce()).publish(any());
        verifyNoInteractions(imageStorageService);
    }

    @Test
    void should_createProduct_when_validCommandWithImage() {
        // Given
        byte[] imageData = "fake-image-data".getBytes();
        CreateProductCommand command = new CreateProductCommand(
                "MOUSE-001", "Wireless Mouse", "A wireless mouse",
                new BigDecimal("25.00"), "USD", 50,
                "cat-electronics", imageData, "mouse.jpg", "admin"
        );

        ProductImage uploadedImage = ProductImage.of("https://s3.amazonaws.com/bucket/mouse.jpg");
        ProductRoot savedProduct = TestFixtures.anActiveProduct();

        when(productRepository.findBySku("MOUSE-001")).thenReturn(Optional.empty());
        when(imageStorageService.upload(anyString(), any())).thenReturn(uploadedImage);
        when(productRepository.save(any())).thenReturn(savedProduct);
        doNothing().when(eventPublisher).publish(any());

        // When
        String result = useCase.execute(command);

        // Then
        assertThat(result).isNotBlank();
        verify(imageStorageService).upload("mouse.jpg", imageData);
        verify(productRepository).save(any());
    }

    @Test
    void should_throwCommandException_when_skuAlreadyExists() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
                "LAPTOP-001", "Gaming Laptop", "desc",
                new BigDecimal("1500.00"), "USD", 100,
                "cat-electronics", null, null, "admin"
        );

        when(productRepository.findBySku("LAPTOP-001")).thenReturn(Optional.of(TestFixtures.anActiveProduct()));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("already exists");
        verifyNoInteractions(imageStorageService, eventPublisher);
        verify(productRepository, never()).save(any());
    }

    @Test
    void should_throwCommandException_when_imageUploadFails() {
        // Given
        byte[] imageData = "fake-image".getBytes();
        CreateProductCommand command = new CreateProductCommand(
                "CAMERA-001", "Digital Camera", "desc",
                new BigDecimal("500.00"), "USD", 20,
                "cat-electronics", imageData, "camera.jpg", "admin"
        );

        when(productRepository.findBySku("CAMERA-001")).thenReturn(Optional.empty());
        when(imageStorageService.upload(anyString(), any())).thenThrow(new RuntimeException("S3 error"));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(CommandException.class)
                .hasMessageContaining("Error uploading image");
        verify(productRepository, never()).save(any());
    }
}
