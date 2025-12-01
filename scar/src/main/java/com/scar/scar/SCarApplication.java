package com.scar.scar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SCarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SCarApplication.class, args);
	}

}

