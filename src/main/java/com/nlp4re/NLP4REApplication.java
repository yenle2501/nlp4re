package com.nlp4re;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nlp4re.domain.Description;

@SpringBootApplication
public class NLP4REApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(NLP4REApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("APP RUN");
		//Description desc = new Description();
		//desc.setDescription("test app run");
		
	}

}
