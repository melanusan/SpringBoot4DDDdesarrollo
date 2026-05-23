package com.debuggeandoideas.erp_lite.persistence.mongo.adapters;

import com.debuggeandoideas.erp_lite.domain.ports.repositories.CatalogRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.views.CatalogView;
import com.debuggeandoideas.erp_lite.domain.views.ItemsView;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import com.debuggeandoideas.erp_lite.persistence.mongo.mappers.CatalogMapper;
import com.debuggeandoideas.erp_lite.persistence.mongo.repositories.CatalogRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static com.debuggeandoideas.erp_lite.constants.CacheConstants.CACHE_CATALOGS_BY_TYPE;

@Repository
@Slf4j
@AllArgsConstructor
public class CatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;
    private final RedisTemplate<String, Object> redisTemplate; //NUEVA LINEA
    private final ObjectMapper objectMapper; //NUEVA LINEA

    @Override
    public Optional<CatalogView> findByType(CatalogType type) {
        log.debug("[{}] Executing operation - type={}", getClass().getSimpleName(), type);

        Object raw = redisTemplate.opsForValue().get(CACHE_CATALOGS_BY_TYPE + type.name()); //NUEVA LINEA
        if (raw != null) {
            CatalogView cached = objectMapper.convertValue(raw, CatalogView.class); //NUEVA LINEA
            log.debug("[{}] Catalog found in cache - type={}", getClass().getSimpleName(), type);
            return Optional.of(cached);
        }

        return catalogRepository.findByCatalogType(type)
                .map(catalogMapper::toView);
    }

    @Override
    public List<ItemsView> findItemsByType(CatalogType type) {
        log.debug("[{}] Executing operation - type={}", getClass().getSimpleName(), type);

        Object raw = this.redisTemplate.opsForValue().get(CACHE_CATALOGS_BY_TYPE + type.name()); //NUEVA LINEA

        if (raw != null) {
            CatalogView cached = objectMapper.convertValue(raw, CatalogView.class); //NUEVA LINEA

            log.debug("[{}] Catalog items found in cache - type={}, total={}", getClass().getSimpleName(),
                    type, cached.items().size());
            return cached.items();
        }

        return catalogRepository.findByCatalogType(type)
                .map(doc -> doc.getItems()
                        .stream()
                        .map(catalogMapper::toItemView)
                        .toList())
                .orElse(List.of());
    }

    @Override
    public Optional<ItemsView> findItemByTypeAndCode(CatalogType type, String code) {
        log.debug("[{}] Executing operation - type={}, code={}", getClass().getSimpleName(), type, code);

        return catalogRepository.findByCatalogType(type)
                .flatMap(doc -> doc.getItems()
                        .stream()
                        .filter(item -> item.code().equals(code))
                        .findFirst()
                        .map(catalogMapper::toItemView));
    }
}