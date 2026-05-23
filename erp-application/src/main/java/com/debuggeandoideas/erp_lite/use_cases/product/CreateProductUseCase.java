package com.debuggeandoideas.erp_lite.use_cases.product;

import com.debuggeandoideas.erp_lite.commands.product.CreateProductCommand;
import com.debuggeandoideas.erp_lite.domain.entities.product.*;
import com.debuggeandoideas.erp_lite.domain.ports.messages.EventPublisherPort;
import com.debuggeandoideas.erp_lite.domain.ports.repositories.ProductRepositoryPort;
import com.debuggeandoideas.erp_lite.domain.ports.services.ImageStorageServicePort;
import com.debuggeandoideas.erp_lite.domain.shared.Money;
import com.debuggeandoideas.erp_lite.exceptions.CommandException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

/**
 * Use Case: Create a new product.
 * JIRA TICKET: ERP-6735
 * 1. Validate SKU uniqueness
 * 2. Upload image to S3 (if provided)
 * 3. Create ProductRoot aggregate
 * 4. Persist to PostgreSQL
 * 5. Publish ProductCreated event (CQRS sync to MongoDB)
 */
@Slf4j
@Service
@Transactional(noRollbackFor = RuntimeException.class)
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final ImageStorageServicePort imageStorageService;
    private final EventPublisherPort eventPublisher;


    public String execute(CreateProductCommand command) {
        log.info("[{}] Starting - sku={}, createdBy={}", getClass().getSimpleName(),
                command.sku(), command.createdBy());

        try {
            validateSkuUniqueness(command.sku());

            SKU sku = SKU.of(command.sku());
            ProductName name = ProductName.of(command.name());
            Money price = Money.of(command.price(), Currency.getInstance(command.currency()));
            Stock stock = Stock.of(command.stock());
            CategoryReference category = CategoryReference.of(command.categoryId());

            ProductImage img = this.uploadImg(command);

            ProductRoot product = ProductRoot.create(
                    sku,
                    name,
                    command.description(),
                    price,
                    stock,
                    category,
                    img,
                    command.createdBy()
            );

            log.debug("[{}] Product created in domain - productId={}", getClass().getSimpleName(),
                    product.getId().value());

            MDC.put("productId", product.getId().value().toString());
            try {
                ProductRoot savedProduct = productRepository.save(product);

                log.info("[{}] Completed - productId={}", getClass().getSimpleName(),
                        savedProduct.getId().value());

                this.sendEventMessage(product);

                return savedProduct.getId().value().toString();
            } finally {
                MDC.clear();
            }

        } catch (IllegalArgumentException iae) {
            log.error("[{}] Invalid data for product creation - sku={}", getClass().getSimpleName(),
                    command.sku(), iae);
            throw new CommandException("Error creating product: " + iae.getMessage());
        } catch (Exception e) {
            log.error("[{}] Unexpected error creating product - sku={}", getClass().getSimpleName(),
                    command.sku(), e);
            throw new CommandException("Failed to create product: " + e.getMessage());
        }
    }

    private void validateSkuUniqueness(String sku) {
        log.debug("Validating SKU uniqueness: {}", sku);

        if (productRepository.findBySku(sku).isPresent()) {
            log.warn("SKU already exists: {}", sku);
            throw new CommandException("Product with SKU '" + sku + "' already exists");
        }
    }

    private ProductImage uploadImg(CreateProductCommand command) {
        if (!command.hasImage()) {
            log.info("[{}] No image provided - sku={}", getClass().getSimpleName(), command.sku());
            return null;
        }

        log.info("[{}] Uploading image - sku={}", getClass().getSimpleName(), command.sku());

        try {
            return this.imageStorageService.upload(
                    command.imageName(),
                    command.imageData()
            );
        } catch (Exception e) {
            log.error("[{}] Unexpected error uploading image - sku={}", getClass().getSimpleName(),
                    command.sku(), e);
            throw new CommandException("Error uploading image with SKU: " + e.getMessage());
        }
    }

    private void sendEventMessage(ProductRoot product) {
        product.getDomainEvents()
                .forEach(eventPublisher::publish);

        product.clearDomainEvents();

        log.info("[{}] Event published successfully - productId={}", getClass().getSimpleName(),
                product.getId().value());
    }
}