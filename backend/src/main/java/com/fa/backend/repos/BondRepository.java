package com.fa.backend.repos;

import com.fa.backend.domain.Bond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BondRepository extends JpaRepository<Bond, Long>, JpaSpecificationExecutor<Bond> {
    // ...
    Optional<Bond> findByTicket(String ticket);
}