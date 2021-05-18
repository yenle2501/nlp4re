package com.nlp4re;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * This class works as an entry point to launch this application.
 * 
 */
@EntityScan("<package with entities>")
@SpringBootApplication
@EnableAutoConfiguration
public class NLP4REApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(NLP4REApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}
