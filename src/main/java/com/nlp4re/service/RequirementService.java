package com.nlp4re.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.nlp4re.logic.RequirementLogicImpl_Eng;
import com.nlp4re.operations.SentenceAnalyzer;
import com.nlp4re.operations.SentenceOperations;
import com.nlp4re.operations.PatternMatcher;
import com.nlp4re.logic.RequirementLogic;

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
	 * @return list of conform and not conform requirements
	 * @throws IOException
	 */
	public List<Map<Integer, String>> checkRequirements(String desc) throws IOException {
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
