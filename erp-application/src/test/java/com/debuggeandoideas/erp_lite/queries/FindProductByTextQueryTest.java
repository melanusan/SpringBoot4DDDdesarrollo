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
class FindProductByTextQueryTest {

    @Mock
    private ProductCatalogRepositoryPort productCatalogRepository;

    @InjectMocks
    private FindProductByTextQuery query;

    @Test
    void should_returnProducts_when_textMatches() {
        // Given
        String text = "laptop";
        List<ProductView> products = List.of(
                new ProductView("SKU-001", "Gaming Laptop", "desc", 1500.0, "USD", 5, null, List.of(), Map.of())
        );
        when(productCatalogRepository.findByText(text)).thenReturn(products);

        // When
        List<ProductView> result = query.execute(text);

        // Then
        assertThat(result).hasSize(1).isEqualTo(products);
        verify(productCatalogRepository).findByText(text);
    }

    @Test
    void should_returnEmptyList_when_noProductsMatchText() {
        // Given
        String text = "xyznotexist";
        when(productCatalogRepository.findByText(text)).thenReturn(List.of());

        // When
        List<ProductView> result = query.execute(text);

        // Then
        assertThat(result).isEmpty();
        verify(productCatalogRepository).findByText(text);
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        String text = "laptop";
        when(productCatalogRepository.findByText(text)).thenThrow(new RuntimeException("Mongo error"));

        // When / Then
        assertThatThrownBy(() -> query.execute(text))
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing FindProductByTextQuery");
        verify(productCatalogRepository).findByText(text);
    }
}
