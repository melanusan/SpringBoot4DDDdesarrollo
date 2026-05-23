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
class FindProductByCategoryTest {

    @Mock
    private ProductCatalogRepositoryPort productCatalogRepository;

    @InjectMocks
    private FindProductByCategory query;

    @Test
    void should_returnProducts_when_categoryExists() {
        // Given
        String category = "ELECTRONICS";
        List<ProductView> products = List.of(
                new ProductView("SKU-001", "Laptop", "Gaming laptop", 1500.0, "USD", 10, null, List.of(), Map.of()),
                new ProductView("SKU-002", "Mouse", "Wireless mouse", 25.0, "USD", 50, null, List.of(), Map.of())
        );
        when(productCatalogRepository.findByCategory(category)).thenReturn(products);

        // When
        List<ProductView> result = query.execute(category);

        // Then
        assertThat(result).hasSize(2).isEqualTo(products);
        verify(productCatalogRepository).findByCategory(category);
    }

    @Test
    void should_returnEmptyList_when_noProductsInCategory() {
        // Given
        String category = "EMPTY_CATEGORY";
        when(productCatalogRepository.findByCategory(category)).thenReturn(List.of());

        // When
        List<ProductView> result = query.execute(category);

        // Then
        assertThat(result).isEmpty();
        verify(productCatalogRepository).findByCategory(category);
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        String category = "ELECTRONICS";
        when(productCatalogRepository.findByCategory(category)).thenThrow(new RuntimeException("Mongo error"));

        // When / Then
        assertThatThrownBy(() -> query.execute(category))
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing ProductCatalogRepositoryPort");
        verify(productCatalogRepository).findByCategory(category);
    }
}
