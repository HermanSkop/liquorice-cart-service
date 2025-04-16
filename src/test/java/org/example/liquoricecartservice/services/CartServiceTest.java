package org.example.liquoricecartservice.services;

import org.example.liquoricecartservice.dtos.CartRequestDto;
import org.example.liquoricecartservice.dtos.CartResponseDto;
import org.example.liquoricecartservice.dtos.ProductDto;
import org.example.liquoricecartservice.models.Cart;
import org.example.liquoricecartservice.repositories.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductApiService productApiService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartService cartService;

    private final String userId = "user123";
    private Cart testCart;
    private ProductDto testProduct;
    private CartRequestDto testCartRequest;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setUserId(userId);
        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("prod1", 2);
        testCart.setProductQuantities(quantities);

        testProduct = new ProductDto("prod1", "Test Product", "Description", 10.0,
                Collections.emptyList(), "", 5, true);

        testCartRequest = new CartRequestDto();
        testCartRequest.setProductQuantities(quantities);
    }

    @Test
    void getCart_ExistingCart_Success() {
        when(cartRepository.findById(userId)).thenReturn(Optional.of(testCart));
        when(productApiService.getProductsByIds(any())).thenReturn(Collections.singletonList(testProduct));
        when(modelMapper.map(any(), eq(ProductDto.class))).thenReturn(testProduct);

        Optional<CartResponseDto> result = cartService.getCart(userId);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getCartItems().size());
        assertEquals(2, result.get().getCartItems().get(0).getQuantity());
        assertEquals("Test Product", result.get().getCartItems().get(0).getProduct().getName());
    }

    @Test
    void getCart_EmptyCart_ReturnsEmpty() {
        Cart emptyCart = new Cart();
        when(cartRepository.findById(userId)).thenReturn(Optional.of(emptyCart));

        Optional<CartResponseDto> result = cartService.getCart(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void saveCart_Success() {
        when(productApiService.getProductsByIds(any())).thenReturn(Collections.singletonList(testProduct));
        when(cartRepository.findById(userId)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any())).thenReturn(testCart);
        when(modelMapper.map(any(), eq(ProductDto.class))).thenReturn(testProduct);

        Optional<CartResponseDto> result = cartService.saveCart(testCartRequest, userId);

        assertTrue(result.isPresent());
        verify(cartRepository).save(any());
    }

    @Test
    void saveCart_ProductNotAvailable() {
        ProductDto unavailableProduct = new ProductDto("prod1", "Test Product", "Description",
                10.0, Collections.emptyList(), "", 5, false);
        when(productApiService.getProductsByIds(any())).thenReturn(Collections.singletonList(unavailableProduct));

        assertThrows(IllegalArgumentException.class, () -> cartService.saveCart(testCartRequest, userId));
    }

    @Test
    void saveCart_InsufficientQuantity() {
        ProductDto insufficientProduct = new ProductDto("prod1", "Test Product", "Description",
                10.0, Collections.emptyList(), "", 1, true);
        when(productApiService.getProductsByIds(any())).thenReturn(Collections.singletonList(insufficientProduct));

        assertThrows(IllegalArgumentException.class, () -> cartService.saveCart(testCartRequest, userId));
    }
}