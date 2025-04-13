package org.example.liquoricecartservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDto {
    ProductDto product;
    int quantity;
}
