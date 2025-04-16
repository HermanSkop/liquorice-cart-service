package org.example.liquoricecartservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "carts")
public class Cart {
    @Id
    private String userId;
    private Map<String, Integer> productQuantities = new HashMap<>();
}