package com.nlp4re.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.nlp4re.logic.RequirementLogic;
import com.nlp4re.logic.RequirementLogicImpl_Eng;
import com.nlp4re.operations.PatternMatcher;
import com.nlp4re.operations.SentenceAnalyzer;
import com.nlp4re.operations.SentenceOperations;


/**
 * This class works as a service
 */
@Service
public class RequirementService {

	private SentenceOperations sentenceOperations;

	public RequirementService() {
		this.sentenceOperations = new SentenceOperations();
	}
	
	/**
	 * This method helps to check the requirements
	 * 
	 * @param desc the requirements description
	 * @return a list of map with key-value-pair
	 *  1.Map contains all sentences of requirement 
	 *  2.Map contains all compliant and non-compliant sentences with the order as the keys in 1.Map 
	 *  (value 1: for non-compliant, 0: compliant) 
	 *  3.Map contains all logs for the non-compliant sentences with the order as the keys in 1.Map
	 * @throws IOException
	 */
	public List<Map<Integer, String>> checkRequirements(String desc){
		if (desc == null || desc.isEmpty()) {
			return null;
		}

		SentenceAnalyzer sentenceAnalyzer = new SentenceAnalyzer(this.sentenceOperations);
		PatternMatcher matcher = new PatternMatcher();

		RequirementLogic logic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		Map<Integer, String> sentences = logic.getSentences(desc);

		if (sentences == null) {
			return null;
		} else {
			List<Map<Integer, String>> result = logic.doParse(sentences);
			return result;
		}
	}
}
