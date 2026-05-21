package com.debuggeandoideas.erp_lite;

import com.debuggeandoideas.erp_lite.commands.order.CancelOrderCommand;
import com.debuggeandoideas.erp_lite.commands.order.CreateOrderCommand;
import com.debuggeandoideas.erp_lite.commands.order.UpdateOrderStatusCommand;
import com.debuggeandoideas.erp_lite.domain.entities.order.OrderId;
import com.debuggeandoideas.erp_lite.domain.shared.Email;
import com.debuggeandoideas.erp_lite.domain.shared.Money;
import com.debuggeandoideas.erp_lite.persistence.jpa.entities.ProductEntity;
import com.debuggeandoideas.erp_lite.persistence.jpa.repositories.ProductRepository;
import com.debuggeandoideas.erp_lite.persistence.mail.adapter.GmailAdapter;
import com.debuggeandoideas.erp_lite.persistence.mongo.documents.CatalogDocument;
import com.debuggeandoideas.erp_lite.persistence.mongo.repositories.CatalogRepository;
import com.debuggeandoideas.erp_lite.persistence.rest.adapters.JsonPlaceholderCustomerProviderAdapter;
import com.debuggeandoideas.erp_lite.use_cases.order.CancelOrderUseCase;
import com.debuggeandoideas.erp_lite.use_cases.order.CreateOrderUseCase;
import com.debuggeandoideas.erp_lite.use_cases.order.UpdateOrderStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@SpringBootApplication
public class ErpLiteApplication implements CommandLineRunner {

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
    JsonPlaceholderCustomerProviderAdapter jsonPlaceholderCustomerProviderAdapter;

	@Autowired
	private GmailAdapter gmailAdapter;

/*
	private final CreateOrderUseCase createOrderUseCase;
	private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
	private final CancelOrderUseCase cancelOrderUseCase;
*/
	public static void main(String[] args) {
		SpringApplication.run(ErpLiteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//this.catalogRepository.findAll().stream().map(item -> item.getName()).forEach(System.out::println);
		this.catalogRepository.findAll().stream().map(CatalogDocument::getName).forEach(System.out::println);
		this.productRepository.findAll().stream().map(ProductEntity::getName).forEach(System.out::println);

		var r = jsonPlaceholderCustomerProviderAdapter.findById(1L);
		System.out.println(r.get().name());

		Email email =  Email.of("felix.fenix@gmail.com");
		OrderId orderId = OrderId.generate();
		String orderNumber = "2SD-1234-909";
		Money money = Money.of(new BigDecimal("2999.98"), Currency.getInstance("USD"));
		String customerName = "Alejandro Calderon";
		int itemsCount = 10;

		this.gmailAdapter.sendMail(
				email, orderId, orderNumber, money, customerName, itemsCount
		);



			// Test 1: Create Order
			//String createdOrderId = testCreateOrder();
			//log.info("Created with id: {}", createdOrderId);

			// Test 2: Update Order
			//String createdOrderId2 = "a0351376-1903-4f94-88af-afcfc74d575b";
			//testUpdateOrderStatus(createdOrderId2);

			// Test 3: Cancel Order
			//testCancelOrder(createdOrderId2);



	}
/*
	private String testCreateOrder() {


		CreateOrderCommand command = new CreateOrderCommand(
				1L,  // customerId - Leanne Graham from JSONPlaceholder
				List.of(  // items - Lista de productos
						// Laptop Dell XPS 15 - 1 unit - $1,499.99
						new CreateOrderCommand.OrderItemRequest(
								"11111111-1111-1111-1111-111111111111",
								1
						),
						// Mechanical Keyboard RGB - 2 units - $149.99 x 2 = $299.98
						new CreateOrderCommand.OrderItemRequest(
								"66666666-6666-6666-6666-666666666666",
								2
						),
						// Logitech MX Master 3S - 1 unit - $99.99
						new CreateOrderCommand.OrderItemRequest(
								"77777777-7777-7777-7777-777777777777",
								1
						)
				),
				"admin"  // createdBy - Usuario que crea la orden
		);

		return createOrderUseCase.execute(command);
	}

	private void testUpdateOrderStatus(String orderId) {

		UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(
				orderId,
				"CONFIRMED"
		);

		updateOrderStatusUseCase.execute(command);

	}
*/
	/**
	 * Test 3: Cancel an existing order
	 * Using an existing order from seed data: ORD-2025-004 (PENDING)
	 */
	/*
	private void testCancelOrder(String orderId) {

		CancelOrderCommand command = new CancelOrderCommand(
				orderId,
				"Customer requested cancellation - testing use case"
		);

		cancelOrderUseCase.execute(command);

	}
*/
}
