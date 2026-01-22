package com.cyterafle.salahsama.claim.processing.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cyterafle.salahsama.claim.processing.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByMail(String mail);
    Optional<Customer> findByNameAndSurname(String name, String surname);
}
