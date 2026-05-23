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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindProductActiveQueryTest {

    @Mock
    private ProductCatalogRepositoryPort productCatalogRepository;

    @InjectMocks
    private FindProductActiveQuery query;

    @Test
    void should_returnActiveProducts_when_activeProductsExist() {
        // Given
        List<ProductView> activeProducts = List.of(
                new ProductView("SKU-001", "Laptop", "desc", 1500.0, "USD", 10, null, List.of(), Map.of()),
                new ProductView("SKU-002", "Mouse", "desc", 25.0, "USD", 100, null, List.of(), Map.of())
        );
        when(productCatalogRepository.findActive()).thenReturn(activeProducts);

        // When
        List<ProductView> result = query.execute();

        // Then
        assertThat(result).hasSize(2).isEqualTo(activeProducts);
        verify(productCatalogRepository).findActive();
    }

    @Test
    void should_returnEmptyList_when_noActiveProducts() {
        // Given
        when(productCatalogRepository.findActive()).thenReturn(List.of());

        // When
        List<ProductView> result = query.execute();

        // Then
        assertThat(result).isEmpty();
        verify(productCatalogRepository).findActive();
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        when(productCatalogRepository.findActive()).thenThrow(new RuntimeException("Mongo error"));

        // When / Then
        assertThatThrownBy(() -> query.execute())
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing FindProductActiveQuery");
        verify(productCatalogRepository).findActive();
    }
}
