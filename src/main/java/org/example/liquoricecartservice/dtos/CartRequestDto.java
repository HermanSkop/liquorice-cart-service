package org.example.liquoricecartservice.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class CartRequestDto {
    private String userId;
    private Map<String, Integer> productQuantities = new HashMap<>();
}