package org.example.liquoricecartservice.services;

import org.example.liquoricecartservice.dtos.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductApiService productApiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productApiService, "productServiceUrl", "http://test-service");
    }

    @Test
    void getProductsByIds_Success() {
        List<String> productIds = Arrays.asList("1", "2");
        List<ProductDto> expectedProducts = Arrays.asList(
                new ProductDto("1", "Product 1", "Description 1", 10.0, Collections.emptyList(), "", 5, true),
                new ProductDto("2", "Product 2", "Description 2", 20.0, Collections.emptyList(), "", 3, true)
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class),
                anyMap()
        )).thenReturn(new ResponseEntity<>(expectedProducts, HttpStatus.OK));

        List<ProductDto> result = productApiService.getProductsByIds(productIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
    }

    @Test
    void getProductsByIds_EmptyResponse() {
        List<String> productIds = Arrays.asList("1", "2");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class),
                anyMap()
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        List<ProductDto> result = productApiService.getProductsByIds(productIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductsByIds_Exception() {
        List<String> productIds = Arrays.asList("1", "2");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class),
                anyMap()
        )).thenThrow(new RuntimeException("Network error"));

        List<ProductDto> result = productApiService.getProductsByIds(productIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}