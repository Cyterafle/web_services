package com.cyterafle.salahsama.claim.processing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cyterafle.salahsama.claim.processing.entity.Customer;

@EnableJpaRepositories
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public Customer findByMail(String mail);
}
