package com.brong90s.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brong90s.ecommerce.entity.CartItem;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {
}
