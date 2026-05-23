package com.debuggeandoideas.erp_lite.queries;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.CatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.CatalogView;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import com.debuggeandoideas.erp_lite.exceptions.QueryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCatalogByTypeQueryTest {

    @Mock
    private CatalogRepositoryPort catalogRepository;

    @InjectMocks
    private FindCatalogByTypeQuery query;

    @Test
    void should_returnCatalog_when_typeExists() {
        // Given
        CatalogType type = CatalogType.PRODUCT_CATEGORIES;
        CatalogView catalog = new CatalogView(true, "Categories", "desc", type, Instant.now(), Instant.now(), List.of());
        when(catalogRepository.findByType(type)).thenReturn(Optional.of(catalog));

        // When
        Optional<CatalogView> result = query.execute(type);

        // Then
        assertThat(result).isPresent().contains(catalog);
        verify(catalogRepository).findByType(type);
    }

    @Test
    void should_returnEmpty_when_catalogNotFound() {
        // Given
        CatalogType type = CatalogType.CURRENCIES;
        when(catalogRepository.findByType(type)).thenReturn(Optional.empty());

        // When
        Optional<CatalogView> result = query.execute(type);

        // Then
        assertThat(result).isEmpty();
        verify(catalogRepository).findByType(type);
    }

    @Test
    void should_throwQueryException_when_repositoryThrowsException() {
        // Given
        CatalogType type = CatalogType.COUNTRIES;
        when(catalogRepository.findByType(type)).thenThrow(new RuntimeException("DB error"));

        // When / Then
        assertThatThrownBy(() -> query.execute(type))
                .isInstanceOf(QueryException.class)
                .hasMessage("Error executing FindCatalogByTypeQuery");
        verify(catalogRepository).findByType(type);
    }
}
