package com.debuggeandoideas.erp_lite.queries;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.ItemsView;
import com.debuggeandoideas.erp_lite.domain.views.ProductView;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindProductActiveQuery {

    private final ProductCatalogRepositoryPort productCatalogRepository;

    public List<ProductView> execute() {
        log.info("Execute FindProductActiveQuery");

        return this.productCatalogRepository.findActive();
    }
}
