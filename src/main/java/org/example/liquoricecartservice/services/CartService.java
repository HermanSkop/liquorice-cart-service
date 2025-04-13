package org.example.liquoricecartservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.liquoricecartservice.dtos.CartItemDto;
import org.example.liquoricecartservice.dtos.CartRequestDto;
import org.example.liquoricecartservice.dtos.CartResponseDto;
import org.example.liquoricecartservice.dtos.ProductDto;
import org.example.liquoricecartservice.models.Cart;
import org.example.liquoricecartservice.repositories.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductApiService productApiService;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<CartResponseDto> getCart(String userId) {
        Cart cart = cartRepository.findById(userId).orElse(new Cart());

        if (cart.getProductQuantities().isEmpty())
            return Optional.empty();

        List<String> productIds = new ArrayList<>(cart.getProductQuantities().keySet());
        List<ProductDto> products = productApiService.getProductsByIds(productIds);

        List<CartItemDto> cartItems = products.stream()
                .map(product -> new CartItemDto(
                        modelMapper.map(product, ProductDto.class),
                        cart.getProductQuantities().get(product.getId()))
                )
                .collect(Collectors.toList());

        return Optional.of(new CartResponseDto(cartItems));
    }

    @Transactional
    public Optional<CartResponseDto> saveCart(CartRequestDto cart, String userId) {
        List<String> productIds = new ArrayList<>(cart.getProductQuantities().keySet());
        List<ProductDto> products = productApiService.getProductsByIds(productIds);

        products.forEach(product -> {
            if (product.getAmountLeft() < cart.getProductQuantities().get(product.getId())) {
                log.error("Not enough product available for product ID: {}", product.getId());
                throw new IllegalArgumentException("Not enough product available: " + product.getName());
            } else if (!product.isAvailable()) {
                log.error("Product is not available: {}", product.getId());
                throw new IllegalArgumentException("Product is not available: " + product.getName());
            }
        });

        Cart existingCart = cartRepository.findById(userId).orElse(new Cart());
        existingCart.setUserId(userId);
        existingCart.setProductQuantities(cart.getProductQuantities());
        cartRepository.save(existingCart);

        return getCart(userId);
    }
}
