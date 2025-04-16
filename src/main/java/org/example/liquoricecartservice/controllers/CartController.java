package org.example.liquoricecartservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.liquoricecartservice.dtos.CartRequestDto;
import org.example.liquoricecartservice.dtos.CartResponseDto;
import org.example.liquoricecartservice.services.CartService;
import org.example.liquoricecartservice.exceptions.NotFoundException;
import org.example.liquoricecartservice.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.example.liquoricecartservice.config.Constants.BASE_PATH;

@Slf4j
@RestController
@RequestMapping(BASE_PATH + "/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(SecurityUtils securityUtils) {
        return cartService.getCart(securityUtils.getCurrentUserId())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Cart is empty"));
    }

   @PostMapping
    public ResponseEntity<CartResponseDto> saveCart(@RequestBody CartRequestDto cart, SecurityUtils securityUtils) {
        return cartService.saveCart(cart, securityUtils.getCurrentUserId())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Cart is empty"));
    }
}
