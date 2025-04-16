package org.example.liquoricecartservice.services;

import org.example.liquoricecartservice.config.AbstractMongoDBIntegrationTest;
import org.example.liquoricecartservice.dtos.CartRequestDto;
import org.example.liquoricecartservice.dtos.CartResponseDto;
import org.example.liquoricecartservice.dtos.ProductDto;
import org.example.liquoricecartservice.models.Cart;
import org.example.liquoricecartservice.repositories.CartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(CartServiceIntegrationTest.TestConfiguration.class)
class CartServiceIntegrationTest extends AbstractMongoDBIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductApiService productApiService;

    @Configuration
    static class TestConfiguration {
        @Bean
        @Primary
        public ProductApiService mockProductApiService() {
            return org.mockito.Mockito.mock(ProductApiService.class);
        }
    }

    private final String userId = "test-user-456";
    private CartRequestDto cartRequestDto;
    private List<ProductDto> mockProducts;

    @BeforeEach
    void setUp() {
        cartRequestDto = new CartRequestDto();
        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("prod-1", 2);
        quantities.put("prod-2", 3);
        cartRequestDto.setProductQuantities(quantities);

        mockProducts = Arrays.asList(
            new ProductDto("prod-1", "Product 1", "Description 1", 10.0, Collections.emptyList(), "", 10, true),
            new ProductDto("prod-2", "Product 2", "Description 2", 20.0, Collections.emptyList(), "", 5, true)
        );

        when(productApiService.getProductsByIds(any())).thenReturn(mockProducts);
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveCart() {
        Optional<CartResponseDto> savedCartResponse = cartService.saveCart(cartRequestDto, userId);

        assertThat(savedCartResponse).isPresent();
        CartResponseDto savedCart = savedCartResponse.get();
        assertThat(savedCart.getCartItems()).hasSize(2);

        Optional<CartResponseDto> retrievedCartResponse = cartService.getCart(userId);

        assertThat(retrievedCartResponse).isPresent();
        CartResponseDto retrievedCart = retrievedCartResponse.get();
        assertThat(retrievedCart.getCartItems()).hasSize(2);

        Optional<Cart> storedCart = cartRepository.findById(userId);
        assertThat(storedCart).isPresent();
        assertThat(storedCart.get().getProductQuantities()).hasSize(2);
        assertThat(storedCart.get().getProductQuantities().get("prod-1")).isEqualTo(2);
        assertThat(storedCart.get().getProductQuantities().get("prod-2")).isEqualTo(3);
    }

    @Test
    void shouldReturnEmptyForNonExistentCart() {
        Optional<CartResponseDto> retrievedCartResponse = cartService.getCart("non-existent-user");

        assertThat(retrievedCartResponse).isEmpty();
    }

    @Test
    void shouldUpdateExistingCart() {
        cartService.saveCart(cartRequestDto, userId);

        CartRequestDto updatedCartRequest = new CartRequestDto();
        Map<String, Integer> updatedQuantities = new HashMap<>();
        updatedQuantities.put("prod-1", 5);
        updatedQuantities.put("prod-3", 1);
        updatedCartRequest.setProductQuantities(updatedQuantities);

        List<ProductDto> updatedMockProducts = Arrays.asList(
            new ProductDto("prod-1", "Product 1", "Description 1", 10.0, Collections.emptyList(), "", 10, true),
            new ProductDto("prod-3", "Product 3", "Description 3", 30.0, Collections.emptyList(), "", 8, true)
        );
        when(productApiService.getProductsByIds(any())).thenReturn(updatedMockProducts);

        Optional<CartResponseDto> updatedCartResponse = cartService.saveCart(updatedCartRequest, userId);

        assertThat(updatedCartResponse).isPresent();

        Optional<Cart> storedCart = cartRepository.findById(userId);
        assertThat(storedCart).isPresent();
        assertThat(storedCart.get().getProductQuantities()).hasSize(2);
        assertThat(storedCart.get().getProductQuantities().get("prod-1")).isEqualTo(5);
        assertThat(storedCart.get().getProductQuantities().get("prod-3")).isEqualTo(1);
        assertThat(storedCart.get().getProductQuantities().containsKey("prod-2")).isFalse();
    }
}
