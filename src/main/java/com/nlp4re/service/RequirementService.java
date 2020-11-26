package com.nlp4re.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.nlp4re.domain.Requirement;

public interface RequirementService {
	
	Requirement findById(int id);
	
	void save(Requirement requirement) throws FileNotFoundException, IOException;
	
	
}
