package com.nlp4re.service.logic;

import java.util.List;
import java.util.Map;

/**
 * This class works as a logic class
 */
public interface RequirementLogic {

	/**
	 * This method helps to get single sentence from the requirements description
	 * 
	 * @param desc requirements description
	 * @return a String array with sentences
	 * @throws IOException
	 */
	public Map<Integer, String> getSentences(String desc);

	/**
	 * This method helps to parse each single sentence with the chosen template.
	 * 
	 * @param sentences list of sentences
	 * @return a list of map with key-value-pair 1.Map contains all sentences of requirement 2.Map contains all
	 *         compliant and non-compliant sentences with the order as the keys in 1.Map 3.Map contains all logs for the
	 *         non-compliant sentences with the order as the keys in 1.Map
	 */
	public List<Map<Integer, String>> doParse(Map<Integer, String> sentences);
}
