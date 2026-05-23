package com.debuggeandoideas.erp_lite.queries;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.ProductView;
import com.debuggeandoideas.erp_lite.exceptions.QueryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindProductByIdQueryTest {

    @Mock
    private ProductCatalogRepositoryPort productCatalogRepository;

    @InjectMocks
    private FindProductByIdQuery query;

    @Test
    void should_returnProduct_when_idExists() {
        // Given
        String id = "prod-123";
        ProductView product = new ProductView("SKU-001", "Laptop", "desc", 1500.0, "USD", 10, null, List.of(), Map.of());
        when(productCatalogRepository.findById(id)).thenReturn(Optional.of(product));

        // When
        Optional<ProductView> result = query.execute(id);

        // Then
        assertThat(result).isPresent().contains(product);
        verify(productCatalogRepository).findById(id);
    }

    @Test
    void should_returnEmpty_when_productNotFound() {
        // Given
        String id = "unknown-id";
        when(productCatalogRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<ProductView> result = query.execute(id);

        // Then
        assertThat(result).isEmpty();
        verify(productCatalogRepository).findById(id);
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        String id = "prod-123";
        when(productCatalogRepository.findById(id)).thenThrow(new RuntimeException("Mongo error"));

        // When / Then
        assertThatThrownBy(() -> query.execute(id))
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing FindProductByIdQuery");
        verify(productCatalogRepository).findById(id);
    }
}
