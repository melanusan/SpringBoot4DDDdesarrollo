package com.debuggeandoideas.erp_lite.queries;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.CatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.CatalogView;
import com.debuggeandoideas.erp_lite.domain.views.ItemsView;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindCatalogItemsByTypeQuery {

    private final CatalogRepositoryPort catalogRepository;

    public List<ItemsView> execute(CatalogType catalogType) {
        log.info("Execute FindCatalogItemsByTypeQuery");

        return this.catalogRepository.findItemsByType(catalogType);
    }

}
