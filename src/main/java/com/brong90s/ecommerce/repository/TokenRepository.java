package com.brong90s.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.brong90s.ecommerce.entity.Token;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {

    @Query(value = "{'user._id': ObjectId(?0), 'expired': false, 'revoked': false}")
    List<Token> findAllValidTokenByUser(String userId);

    Optional<Token> findByToken(String token);
}
