package org.example.liquoricecartservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.liquoricecartservice.dtos.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductApiService {

    private final RestTemplate restTemplate;

    @Value("${service.product.url}")
    private String productServiceUrl;

    public List<ProductDto> getProductsByIds(List<String> productIds) {
        try {
            log.debug("Fetching products with IDs: {}", productIds);
            Map<String, String> params = new HashMap<>();
            params.put("productIds", String.join(",", productIds));

            ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                    productServiceUrl + "/api/v1/products/batch?productIds={productIds}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    params
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.debug("Successfully retrieved {} products", response.getBody().size());
                return response.getBody();
            } else {
                log.warn("Failed to retrieve products with IDs: {}", productIds);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Error fetching products with IDs: {}", productIds, e);
            return Collections.emptyList();
        }
    }
}
