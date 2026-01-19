package com.cyterafle.salahsama.claim.processing.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cyterafle.salahsama.claim.processing.entity.Claim;
import com.cyterafle.salahsama.claim.processing.repository.ClaimRepository;

@RestController
@RequestMapping("/rest/claims")
public class ClaimController {

    @Autowired
    ClaimRepository claimRepository;

    @PostMapping
    public Claim createClaim(@RequestBody Claim claim){
        return claimRepository.save(claim);
    }
}
