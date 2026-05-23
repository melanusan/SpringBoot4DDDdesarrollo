package com.debuggeandoideas.erp_lite.use_cases.product;

import com.debuggeandoideas.erp_lite.commands.product.DeactivateProductCommand;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductId;
import com.debuggeandoideas.erp_lite.domain.entities.product.ProductRoot;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductRepositoryPort;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * JIRA TICKET: ERP-6736
 * Use Case: Deactivate a product.
 * 1. Find product by ID
 * 2. Deactivate product (soft delete)
 * 3. Persist changes
 * 4. Publish ProductDeactivated event
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeactivateProductUseCase {

    private final ProductRepositoryPort productRepository;

    public void execute(DeactivateProductCommand command) {
        log.info("[{}] Starting - productId={}", getClass().getSimpleName(), command.productId());

        MDC.put("productId", command.productId());
        try {
            // 1. Find product
            ProductRoot product = findProductById(command.productId());

            log.debug("[{}] Current status - active={}", getClass().getSimpleName(), product.isActive());

            // 2. Deactivate product
            product.deactivate();

            log.debug("[{}] Product deactivated in domain", getClass().getSimpleName());

            // 3. Persist changes
            productRepository.save(product);

            log.info("[{}] Completed - productId={}", getClass().getSimpleName(), command.productId());

        } catch (IllegalStateException ise) {
            log.error("[{}] Product already deactivated - productId={}", getClass().getSimpleName(),
                    command.productId(), ise);
            throw new CommandException("Product is already deactivated");
        } catch (Exception e) {
            log.error("[{}] Unexpected error deactivating product - productId={}", getClass().getSimpleName(),
                    command.productId(), e);
            throw new CommandException("Failed to deactivate product: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    private ProductRoot findProductById(String productId) {
        log.debug("[{}] Finding product by ID - productId={}", getClass().getSimpleName(), productId);

        ProductId productIdVO = ProductId.of(UUID.fromString(productId));

        return productRepository.findAllById(productIdVO)
                .orElseThrow(() -> {
                    log.warn("[{}] Product not found - productId={}", getClass().getSimpleName(), productId);
                    return new CommandException("Product not found with ID: " + productId);
                });
    }
}