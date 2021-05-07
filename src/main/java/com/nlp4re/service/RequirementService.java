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
	 * @return a list of map with key-value-pair
	 *  1.Map contains all sentences of requirement 
	 *  2.Map contains all compliant and non-compliant sentences with the order as the keys in 1.Map 
	 *  (value 1: for non-compliant, 0: compliant) 
	 *  3.Map contains all logs for the non-compliant sentences with the order as the keys in 1.Map
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

	public static void main(String[] args) {
		String desc = " As soon as the daily activity cycle ends, the Oktupus system must restart all the sensors connected in the home. \r\n"
				+ " The VMS system will consume as few units of energy as possible during the normal operation of the intelligent bracelet.";
		SentenceOperations sentenceOperation = new SentenceOperations();
		SentenceAnalyzer sentenceAnalyzer = new SentenceAnalyzer(sentenceOperation);
		PatternMatcher matcher = new PatternMatcher();

		RequirementLogic logic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		Map<Integer, String> sentences = logic.getSentences(desc);

		List<Map<Integer, String>> result = logic.doParse(sentences);
		result.forEach(action -> {
			action.entrySet().forEach(aa -> {
				System.out.println(aa.getKey() + "-.." + aa.getValue());
			});
		});

	}
}
