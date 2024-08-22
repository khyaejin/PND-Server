package com.server.pnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PndApplication {

	public static void main(String[] args) {
		SpringApplication.run(PndApplication.class, args);
	}

}