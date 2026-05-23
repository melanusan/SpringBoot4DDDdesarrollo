package com.debuggeandoideas.erp_lite.queries;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.CatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.ItemsView;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import com.debuggeandoideas.erp_lite.exceptions.QueryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCatalogItemsByTypeQueryTest {

    @Mock
    private CatalogRepositoryPort catalogRepository;

    @InjectMocks
    private FindCatalogItemsByTypeQuery query;

    @Test
    void should_returnItems_when_typeExists() {
        // Given
        CatalogType type = CatalogType.ORDER_STATUSES;
        List<ItemsView> items = List.of(
                new ItemsView("PENDING", "Pending", "Order pending", 1),
                new ItemsView("CONFIRMED", "Confirmed", "Order confirmed", 2)
        );
        when(catalogRepository.findItemsByType(type)).thenReturn(items);

        // When
        List<ItemsView> result = query.execute(type);

        // Then
        assertThat(result).hasSize(2).isEqualTo(items);
        verify(catalogRepository).findItemsByType(type);
    }

    @Test
    void should_returnEmptyList_when_noItemsFound() {
        // Given
        CatalogType type = CatalogType.CURRENCIES;
        when(catalogRepository.findItemsByType(type)).thenReturn(List.of());

        // When
        List<ItemsView> result = query.execute(type);

        // Then
        assertThat(result).isEmpty();
        verify(catalogRepository).findItemsByType(type);
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        CatalogType type = CatalogType.COUNTRIES;
        when(catalogRepository.findItemsByType(type)).thenThrow(new RuntimeException("DB error"));

        // When / Then
        assertThatThrownBy(() -> query.execute(type))
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing FindCatalogItemsByTypeQuery");
        verify(catalogRepository).findItemsByType(type);
    }
}
