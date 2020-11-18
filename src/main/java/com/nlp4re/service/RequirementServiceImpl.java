package com.nlp4re.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlp4re.domain.Requirement;
import com.nlp4re.repository.RequirementRespository;

@Service
public class RequirementServiceImpl implements RequirementService {

	@Autowired
    private RequirementRespository repository;
	
	

	@Override
	public Requirement findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void save(Requirement requirement) {
		
		
	}
	
	
}
