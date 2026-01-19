package com.cyterafle.salahsama.claim.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan({"com.cyterafle.salahsama.claim.processing.entity","com.cyterafle.salahsama.claim.processing.rest.controller","com.cyterafle.salahsama.claim.processing.repository"})
public class ProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessingApplication.class, args);
	}

}
