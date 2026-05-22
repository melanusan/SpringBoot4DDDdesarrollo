package com.debuggeandoideas.erp_lite.controllers.queries;

import com.debuggeandoideas.erp_lite.domain.views.CatalogView;
import com.debuggeandoideas.erp_lite.domain.views.ItemsView;
import com.debuggeandoideas.erp_lite.dtos.BaseResponseWrapper;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import com.debuggeandoideas.erp_lite.exceptions.QueryException;
import com.debuggeandoideas.erp_lite.paths.ApiPaths;
import com.debuggeandoideas.erp_lite.queries.FindCatalogByTypeQuery;
import com.debuggeandoideas.erp_lite.queries.FindCatalogItemByCodeQuery;
import com.debuggeandoideas.erp_lite.queries.FindCatalogItemsByTypeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = ApiPaths.QUERIES_CATALOGS, version = "1")
@RequiredArgsConstructor
@Tag(name = "Query Catalogs", description = "Endpoints de consulta para catálogos e ítems")
public class QueryCatalogControllerV1 {

    private final FindCatalogByTypeQuery findCatalogByTypeQuery;
    private final FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;
    private final FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    @Operation(summary = "Obtener catálogo por tipo", description = "Retorna el catálogo correspondiente al tipo indicado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo encontrado"),
            @ApiResponse(responseCode = "404", description = "Catálogo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/{type}")
    public ResponseEntity<BaseResponseWrapper<CatalogView>> getByType(
            @Parameter(description = "Tipo de catálogo (ej. CATEGORY, CURRENCY)", required = true, example = "CATEGORY")
            @PathVariable String type) {
        log.info("GET catalog by type: {}", type);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        CatalogView response = this.findCatalogByTypeQuery.execute(catalogType)
                .orElseThrow(() -> new QueryException("Catalog with type " + type + " not found"));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener ítems de un catálogo por tipo", description = "Retorna todos los ítems del catálogo del tipo especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ítems del catálogo")
    })
    @GetMapping(path = "/{type}/items")
    public ResponseEntity<BaseResponseWrapper<List<ItemsView>>> getItemsByType(
            @Parameter(description = "Tipo de catálogo (ej. CATEGORY, CURRENCY)", required = true, example = "CATEGORY")
            @PathVariable String type) {
        log.info("GET catalog items by type: {}", type);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        List<ItemsView> response = this.findCatalogItemsByTypeQuery.execute(catalogType);

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener ítem de catálogo por tipo y código", description = "Retorna un ítem específico del catálogo filtrando por tipo y código")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping(path = "/{type}/items", params = "code")
    public ResponseEntity<BaseResponseWrapper<ItemsView>> getItemByTypeAndCode(
            @Parameter(description = "Tipo de catálogo (ej. CATEGORY, CURRENCY)", required = true, example = "CATEGORY")
            @PathVariable String type,
            @Parameter(description = "Código del ítem dentro del catálogo", required = true, example = "ELECTRONICS")
            @RequestParam String code) {
        log.info("GET catalog item by type: {} and code: {}", type, code);

        CatalogType catalogType = CatalogType.valueOf(type.toUpperCase());

        ItemsView response = this.findCatalogItemByCodeQuery.execute(catalogType, code)
                .orElseThrow(() -> new QueryException("Item with code " + code + " not found for type " + type));

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }
}
