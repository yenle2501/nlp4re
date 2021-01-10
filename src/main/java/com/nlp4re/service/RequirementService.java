package com.nlp4re.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.nlp4re.domain.Requirement;

public interface RequirementService {
	
	Requirement findById(int id);
	
	Map<Integer, String> save(Requirement requirement) throws FileNotFoundException, IOException;
	
	
}
