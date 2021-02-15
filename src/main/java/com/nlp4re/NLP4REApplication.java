package com.nlp4re;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class works as an entry point to launch this application.
 * 
 */
@SpringBootApplication
public class NLP4REApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(NLP4REApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}
