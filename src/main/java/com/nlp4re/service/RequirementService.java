package com.nlp4re.service;

import com.nlp4re.domain.Requirement;

public interface RequirementService {
	
	Requirement findById(int id);
	
	void save(Requirement requirement);
	
	
}
