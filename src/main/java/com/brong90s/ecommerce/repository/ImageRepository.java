package com.brong90s.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brong90s.ecommerce.entity.Image;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
}
