package com.nlp4re.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RequirementService {
	
	List<Map<Integer, String>> check(String desc) throws FileNotFoundException, IOException;
	
	
}
