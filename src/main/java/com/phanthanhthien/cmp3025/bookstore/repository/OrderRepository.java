package com.phanthanhthien.cmp3025.bookstore.repository;

import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Order Repository
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Order> findByMomoRequestId(String momoRequestId);
}
