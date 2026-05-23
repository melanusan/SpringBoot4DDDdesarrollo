package com.debuggeandoideas.erp_lite;

import com.debuggeandoideas.erp_lite.domain.entities.customer.CustomerInfo;
import com.debuggeandoideas.erp_lite.domain.entities.order.*;
import com.debuggeandoideas.erp_lite.domain.entities.product.*;
import com.debuggeandoideas.erp_lite.domain.shared.CustomerId;
import com.debuggeandoideas.erp_lite.domain.shared.Money;
import com.debuggeandoideas.erp_lite.domain.shared.Quantity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Domain fixture builder for unit tests.
 */
public class TestFixtures {

    public static final String PRODUCT_ID = UUID.randomUUID().toString();
    public static final String ORDER_ID = UUID.randomUUID().toString();
    public static final Currency USD = Currency.getInstance("USD");

    public static ProductRoot anActiveProduct() {
        return ProductRoot.create(
                SKU.of("LAPTOP-001"),
                ProductName.of("Gaming Laptop"),
                "A high-performance gaming laptop",
                Money.of(new BigDecimal("1500.00"), USD),
                Stock.of(100),
                CategoryReference.of("cat-electronics"),
                null,
                "admin"
        );
    }

    public static CustomerInfo aCustomerInfo() {
        return new CustomerInfo(1L, "John Doe", "john@example.com",
                "555-1234", "123 Main St", "Springfield", "12345", "Acme Corp");
    }

    public static OrderRoot aPendingOrder() {
        ProductRoot product = anActiveProduct();
        Customer customer = Customer.of(CustomerId.of(1L), "John Doe");
        OrderItem item = OrderItem.from(product, Quantity.of(2));
        OrderNumber orderNumber = OrderNumber.generate(42);
        return OrderRoot.create(orderNumber, customer, List.of(item), "admin");
    }

    public static OrderRoot aConfirmedOrder() {
        OrderRoot order = aPendingOrder();
        order.confirm();
        return order;
    }

    public static OrderRoot aShippedOrder() {
        OrderRoot order = aConfirmedOrder();
        order.ship();
        return order;
    }
}
