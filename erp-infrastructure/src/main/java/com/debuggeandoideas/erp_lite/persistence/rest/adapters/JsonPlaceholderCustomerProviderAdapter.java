package com.debuggeandoideas.erp_lite.persistence.rest.adapters;

import com.debuggeandoideas.erp_lite.domain.entities.customer.CustomerInfo;
import com.debuggeandoideas.erp_lite.domain.ports.services.CustomerProviderServicePort;
import com.debuggeandoideas.erp_lite.persistence.rest.mappers.CustomerMapper;
import com.debuggeandoideas.erp_lite.persistence.rest.dtos.UserDTO;
import com.debuggeandoideas.erp_lite.persistence.rest.models.JsonplaceholderConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Service
@Slf4j
public class JsonPlaceholderCustomerProviderAdapter implements CustomerProviderServicePort {

    private final RestClient jsonClient;
    private final CustomerMapper customerMapper;
    private final String endpoint;

    public JsonPlaceholderCustomerProviderAdapter(
            @Qualifier("jsonplaceholder") RestClient restClient,
            CustomerMapper customerMapper,
            JsonplaceholderConfigModel jsonConfig) {

        this.jsonClient = restClient;
        this.customerMapper = customerMapper;
        this.endpoint = jsonConfig.usersEndpoint();
    }

    @Override
    public Optional<CustomerInfo> findById(Long id) {
        log.debug("[{}] Executing operation - customerId={}", getClass().getSimpleName(), id);

        try {
            final UserDTO response = this.jsonClient
                    .get()
                    .uri(endpoint, id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.warn("[{}] External provider 4xx response - customerId={}", getClass().getSimpleName(), id);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("[{}] External integration 5xx failure - customerId={}", getClass().getSimpleName(), id);
                    })
                    .body(UserDTO.class);

            if (response == null) {
                log.warn("[{}] Customer not found in external provider - customerId={}", getClass().getSimpleName(), id);
                return Optional.empty();
            }

            log.info("[{}] Operation successful - customerId={}, customerName={}", getClass().getSimpleName(),
                    id, response.name());

            return Optional.of(this.customerMapper.toCustomerInfo(response));

        } catch (RestClientException rce) {
            log.error("[{}] External integration failure fetching customer - customerId={}", getClass().getSimpleName(),
                    id, rce);
            return Optional.empty();
        } catch (Exception e) {
            log.error("[{}] Unexpected error fetching customer - customerId={}", getClass().getSimpleName(), id, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("[{}] Executing operation - customerId={}", getClass().getSimpleName(), id);
        return false;
    }
}
