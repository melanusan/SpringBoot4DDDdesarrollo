package com.debuggeandoideas.erp_lite;

import com.debuggeandoideas.erp_lite.domain.order.OrderId;
import com.debuggeandoideas.erp_lite.domain.shared.Email;
import com.debuggeandoideas.erp_lite.domain.shared.Money;
import com.debuggeandoideas.erp_lite.persistence.jpa.entities.ProductEntity;
import com.debuggeandoideas.erp_lite.persistence.jpa.repositories.ProductRepository;
import com.debuggeandoideas.erp_lite.persistence.mail.adapter.GmailAdapter;
import com.debuggeandoideas.erp_lite.persistence.mongo.documents.CatalogDocument;
import com.debuggeandoideas.erp_lite.persistence.mongo.repositories.CatalogRepository;
import com.debuggeandoideas.erp_lite.persistence.rest.adapters.JsonPlaceholderCustomerProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Currency;

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



	}
}
