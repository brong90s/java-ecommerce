package com.brong90s.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brong90s.ecommerce.entity.Cart;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Cart findByUserId(String id);
}
