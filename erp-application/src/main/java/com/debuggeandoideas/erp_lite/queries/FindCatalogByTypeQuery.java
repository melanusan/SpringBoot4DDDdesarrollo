package com.debuggeandoideas.erp_lite.queries;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.CatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.CatalogView;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import com.debuggeandoideas.erp_lite.exceptions.QueryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindCatalogByTypeQuery {

    private final CatalogRepositoryPort catalogRepository;

    public Optional<CatalogView> execute(CatalogType catalogType) {
        log.info("Execute FindCatalogByTypeQuery");

        try {
            return this.catalogRepository.findByType(catalogType);
        } catch (RuntimeException e) {
           throw new QueryException("Error executing FindCatalogByTypeQuery");
        }
    }

}
