package com.fa.backend.repos;

import com.fa.backend.domain.Purchase;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {

//    List<com.fa.backend.domain.Order> findByUser(Long id);
}