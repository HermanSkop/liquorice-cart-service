package org.example.liquoricecartservice.repositories;

import org.example.liquoricecartservice.config.AbstractMongoDBIntegrationTest;
import org.example.liquoricecartservice.models.Cart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CartRepositoryIntegrationTest extends AbstractMongoDBIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    private Cart testCart;
    private final String userId = "test-user-123";

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setUserId(userId);
        
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 2);
        productQuantities.put("product-2", 1);
        testCart.setProductQuantities(productQuantities);
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveCart() {
        cartRepository.save(testCart);
        
        Optional<Cart> retrievedCartOptional = cartRepository.findById(userId);
        
        assertThat(retrievedCartOptional).isPresent();
        Cart retrievedCart = retrievedCartOptional.get();
        assertThat(retrievedCart.getUserId()).isEqualTo(userId);
        assertThat(retrievedCart.getProductQuantities()).hasSize(2);
        assertThat(retrievedCart.getProductQuantities().get("product-1")).isEqualTo(2);
        assertThat(retrievedCart.getProductQuantities().get("product-2")).isEqualTo(1);
    }

    @Test
    void shouldUpdateExistingCart() {
        cartRepository.save(testCart);
        
        Map<String, Integer> updatedQuantities = new HashMap<>();
        updatedQuantities.put("product-1", 3);
        updatedQuantities.put("product-3", 2);
        
        testCart.setProductQuantities(updatedQuantities);
        cartRepository.save(testCart);
        
        Optional<Cart> retrievedCartOptional = cartRepository.findById(userId);
        
        assertThat(retrievedCartOptional).isPresent();
        Cart retrievedCart = retrievedCartOptional.get();
        assertThat(retrievedCart.getProductQuantities()).hasSize(2);
        assertThat(retrievedCart.getProductQuantities().get("product-1")).isEqualTo(3);
        assertThat(retrievedCart.getProductQuantities().get("product-3")).isEqualTo(2);
        assertThat(retrievedCart.getProductQuantities().containsKey("product-2")).isFalse();
    }

    @Test
    void shouldDeleteCart() {
        cartRepository.save(testCart);

        cartRepository.deleteById(userId);
        
        Optional<Cart> retrievedCartOptional = cartRepository.findById(userId);
        assertThat(retrievedCartOptional).isEmpty();
    }
}
