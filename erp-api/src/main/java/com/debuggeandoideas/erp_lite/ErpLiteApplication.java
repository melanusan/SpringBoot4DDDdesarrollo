package com.debuggeandoideas.erp_lite;

import com.debuggeandoideas.erp_lite.domain.entities.order.OrderId;
import com.debuggeandoideas.erp_lite.domain.shared.Email;
import com.debuggeandoideas.erp_lite.domain.shared.Money;
import com.debuggeandoideas.erp_lite.persistence.jpa.entities.ProductEntity;
import com.debuggeandoideas.erp_lite.persistence.jpa.repositories.ProductRepository;
import com.debuggeandoideas.erp_lite.persistence.mail.adapter.GmailAdapter;
import com.debuggeandoideas.erp_lite.persistence.mongo.documents.CatalogDocument;
import com.debuggeandoideas.erp_lite.persistence.mongo.repositories.CatalogRepository;
import com.debuggeandoideas.erp_lite.persistence.rest.adapters.JsonPlaceholderCustomerProviderAdapter;
import com.debuggeandoideas.erp_lite.enums.CatalogType;
import com.debuggeandoideas.erp_lite.queries.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.math.BigDecimal;
import java.util.Currency;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ErpLiteApplication implements CommandLineRunner {


    @Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
    JsonPlaceholderCustomerProviderAdapter jsonPlaceholderCustomerProviderAdapter;

	@Autowired
	private GmailAdapter gmailAdapter;

    @Autowired
    private FindCatalogByTypeQuery findCatalogByTypeQuery;

    @Autowired
    private FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    @Autowired
    private FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;

    @Autowired
    private FindProductActiveQuery findProductActiveQuery;

    @Autowired
    private FindProductByCategory findProductByCategory;

    @Autowired
    private FindProductByIdQuery findProductByIdQuery;

    @Autowired
    private FindProductBySkuQuery findProductBySkuQuery;

    @Autowired
    private FindProductByTextQuery findProductByTextQuery;

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

        System.out.println(findCatalogByTypeQuery.execute(CatalogType.PRODUCT_CATEGORIES));
        System.out.println("-------------------------");

        System.out.println(findCatalogItemByCodeQuery.execute(CatalogType.PRODUCT_CATEGORIES, "ELECTRONICS"));
        System.out.println("-------------------------");

        System.out.println(findCatalogItemsByTypeQuery.execute(CatalogType.ORDER_STATUSES));
        System.out.println("-------------------------");

        System.out.println(findProductActiveQuery.execute());
        System.out.println("-------------------------");

        System.out.println(findProductByCategory.execute("cat-electronics"));
        System.out.println("-------------------------");

        System.out.println(findProductByIdQuery.execute("11111111-1111-1111-1111-111111111111"));
        System.out.println("-------------------------");

        System.out.println(findProductBySkuQuery.execute("LAPTOP-001"));
        System.out.println("-------------------------");

        System.out.println(findProductByTextQuery.execute("laptop"));
        System.out.println("-------------------------");
    }
}