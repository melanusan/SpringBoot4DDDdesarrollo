package com.debuggeandoideas.erp_lite.controllers.queries;

import com.debuggeandoideas.erp_lite.domain.views.ProductView;
import com.debuggeandoideas.erp_lite.dtos.BaseResponseWrapper;
import com.debuggeandoideas.erp_lite.exceptions.QueryException;
import com.debuggeandoideas.erp_lite.paths.ApiPaths;
import com.debuggeandoideas.erp_lite.queries.*;
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
@RequestMapping(path = ApiPaths.QUERIES_PRODUCTS, version = "1")
@RequiredArgsConstructor
@Tag(name = "Query Products", description = "Endpoints de consulta para productos")
public class QueryProductsControllersV1 {

    private final FindProductByIdQuery findProductByIdQuery;
    private final FindProductBySkuQuery findProductBySkuQuery;
    private final FindProductActiveQuery findProductActiveQuery;
    private final FindProductByTextQuery findProductByTextQuery;
    private final FindProductByCategory findProductByCategory;

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto dado su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<BaseResponseWrapper<ProductView>> getById(
            @Parameter(description = "Identificador único del producto", required = true, example = "abc123")
            @PathVariable String id) {
        log.info("[{}] Request received - productId={}", getClass().getSimpleName(), id);

        ProductView response = this.findProductByIdQuery.execute(id)
                .orElseThrow(() -> new QueryException("Product with id " + id + " not found"));

        log.info("[{}] Response sent - status=200, productId={}", getClass().getSimpleName(), id);

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener producto por SKU", description = "Retorna un producto dado su código SKU")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(params = "sku")
    public ResponseEntity<BaseResponseWrapper<ProductView>> getBySku(
            @Parameter(description = "Código SKU del producto", required = true, example = "SKU-001")
            @RequestParam String sku) {
        log.info("[{}] Request received - sku={}", getClass().getSimpleName(), sku);

        ProductView response = this.findProductBySkuQuery.execute(sku)
                .orElseThrow(() -> new QueryException("Product with sku " + sku + " not found"));

        log.info("[{}] Response sent - status=200, sku={}", getClass().getSimpleName(), sku);

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Obtener productos activos", description = "Retorna la lista de todos los productos activos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos activos"),
            @ApiResponse(responseCode = "204", description = "No hay productos activos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(path = "/active")
    public ResponseEntity<BaseResponseWrapper<List<ProductView>>> getActive() {
        log.info("[{}] Request received - endpoint=active", getClass().getSimpleName());

        List<ProductView> response = this.findProductActiveQuery.execute();

        if (response.isEmpty()) {
            log.info("[{}] Response sent - status=204, result=no active products found",
                    getClass().getSimpleName());
            return ResponseEntity.noContent().build();
        }

        log.info("[{}] Response sent - status=200, count={}", getClass().getSimpleName(), response.size());

        return ResponseEntity.ok(BaseResponseWrapper.of(response));
    }

    @Operation(summary = "Buscar productos por texto", description = "Retorna productos cuyo nombre o descripción coincida con el texto de búsqueda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<BaseResponseWrapper<List<ProductView>>> search(
            @Parameter(description = "Texto a buscar en nombre o descripción del producto", required = true, example = "laptop")
            @RequestParam String text) {
        log.info("[{}] Request received - text={}", getClass().getSimpleName(), text);

        List<ProductView> products = this.findProductByTextQuery.execute(text);

        log.info("[{}] Response sent - status=200, count={}", getClass().getSimpleName(), products.size());

        return ResponseEntity.ok(BaseResponseWrapper.of(products));
    }

    @Operation(summary = "Obtener productos por categoría", description = "Retorna todos los productos que pertenecen a la categoría indicada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos por categoría"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(params = "category")
    public ResponseEntity<BaseResponseWrapper<List<ProductView>>> findByCategory(
            @Parameter(description = "Identificador de la categoría a filtrar", required = true, example = "ELECTRONICS")
            @RequestParam String category) {
        log.info("[{}] Request received - category={}", getClass().getSimpleName(), category);

        List<ProductView> products = this.findProductByCategory.execute(category);

        log.info("[{}] Response sent - status=200, category={}, count={}", getClass().getSimpleName(),
                category, products.size());

        return ResponseEntity.ok(BaseResponseWrapper.of(products));
    }
}
