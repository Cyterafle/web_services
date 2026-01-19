package com.cyterafle.salahsama.claim.processing.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cyterafle.salahsama.claim.processing.entity.Customer;
import com.cyterafle.salahsama.claim.processing.repository.CustomerRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private CustomerRepository customerRepository;


    @GetMapping("/verify")
    public Customer exists(
        @RequestBody Customer customerToVerify
    ) throws Exception {
        Customer existingCustomer = customerRepository.findByMail(customerToVerify.getMail());
        if (null == existingCustomer){
            throw new Exception("Customer does not exist");
        }

        if (! customerToVerify.getName().equals(existingCustomer.getName())){
            throw new Exception("Wrong credentials");
        }

        if (! customerToVerify.getSurname().equals(existingCustomer.getSurname())){
            throw new Exception("Wrong credentials");
        }

        return existingCustomer;
    }

    @PostMapping("/register")
    public Customer register(@RequestBody Customer newCustomer){
        return customerRepository.save(newCustomer);
    }
}
