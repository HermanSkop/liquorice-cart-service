package org.example.liquoricecartservice.repositories;

import org.example.liquoricecartservice.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
}
