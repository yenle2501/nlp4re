package com.nlp4re.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlp4re.domain.Description;
import com.nlp4re.repository.DescriptionRespository;

@Service
public class DescriptionService {

//	@Autowired
//    private DescriptionRespository repository;
	
	
	public Description saveText(Description desc) {
		System.out.println("desc service: " + desc.getDescription());
//		repository.save(desc);
		return null;
	}
}
