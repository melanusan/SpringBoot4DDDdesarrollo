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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCatalogItemByCodeQueryTest {

    @Mock
    private CatalogRepositoryPort catalogRepository;

    @InjectMocks
    private FindCatalogItemByCodeQuery query;

    @Test
    void should_returnItem_when_typeAndCodeExist() {
        // Given
        CatalogType type = CatalogType.PRODUCT_CATEGORIES;
        String code = "ELECTRONICS";
        ItemsView item = new ItemsView(code, "Electronics", "Electronic products", 1);
        when(catalogRepository.findItemByTypeAndCode(type, code)).thenReturn(Optional.of(item));

        // When
        Optional<ItemsView> result = query.execute(type, code);

        // Then
        assertThat(result).isPresent().contains(item);
        verify(catalogRepository).findItemByTypeAndCode(type, code);
    }

    @Test
    void should_returnEmpty_when_itemNotFound() {
        // Given
        CatalogType type = CatalogType.PAYMENT_METHODS;
        String code = "UNKNOWN";
        when(catalogRepository.findItemByTypeAndCode(type, code)).thenReturn(Optional.empty());

        // When
        Optional<ItemsView> result = query.execute(type, code);

        // Then
        assertThat(result).isEmpty();
        verify(catalogRepository).findItemByTypeAndCode(type, code);
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        CatalogType type = CatalogType.SHIPPING_METHODS;
        String code = "EXPRESS";
        when(catalogRepository.findItemByTypeAndCode(type, code)).thenThrow(new RuntimeException("DB error"));

        // When / Then
        assertThatThrownBy(() -> query.execute(type, code))
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing FindCatalogItemByCodeQuery");
        verify(catalogRepository).findItemByTypeAndCode(type, code);
    }
}
