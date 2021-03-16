package com.nlp4re.logic;

import java.util.List;
import java.util.Map;

/**
 * This class works as a logic class
 */
public interface RequirementLogic {
//
//	/**
//	 * tokenizes the sentence
//	 * 
//	 * @param sentence sentence to tokenize
//	 * @return List of tokens and tags of sentence
//	 */
//	List<String[]> tokenizeSentence(String sentence);
//
//	/**
//	 * Checks the modal verb of the sentence
//	 * 
//	 * @return true: if the sentence contains one of the proposed modal verbs such as shall, should, could false:
//	 *         otherwise
//	 */
//	boolean parseModalVp(int modal_index, List<String> list_tokens);
//
//	/**
//	 * This method has the ability to check system name
//	 * 
//	 * @return true : if the sentence has a valid name of system false: otherwise
//	 */
//	boolean parseSystemName(List<String> list_tokens, int comma_index, int modal_index);
//
//	/**
//	 * This method has the ability to check the condition of the sentence
//	 * 
//	 * @return true :if the sentence has no condition or a valid condition false: otherwise
//	 */
//	boolean parseCondition(List<String> list_tokens, int comma_index, int modal_index);
//
//	/**
//	 * Anchor should contain SYSTEM NAME + MODAL VERB + NORMAL VERB This method has the ability to check the anchor of
//	 * the sentence.
//	 * 
//	 * @return true: if the sentence has a valid anchor false: otherwise
//	 */
//	List<Integer> parseAnchor(List<String> list_tokens, List<String> list_tags, int comma_index, int modal_index);
//
//	/**
//	 * This method has the ability to check the objects of sentence
//	 * 
//	 * @return true: if the sentence has a valid object false: otherwise
//	 */
//	List<Integer> parseObject(String[] tokens, String[] tags, int object_start_index);
//
//	/**
//	 * This method helps to check the details of the sentence
//	 * 
//	 * @param tokens           List of tokens
//	 * @param object_end_index end index of object
//	 * @return true: if the sentence has valid details false: otherwise
//	 */
//	boolean parseDetails(List<String> tokens, int object_end_index);
//
//	/**
//	 * parse the complete sentence
//	 * 
//	 * @param sentence: sentence to check
//	 * @return true: if the sentence does not match with the template false: otherwise
//	 */
//	boolean parseTemplateConformance(String sentence);
//
//	/**
//	 * get error logs for not conformt sentences
//	 * 
//	 * @return error logs
//	 */
//	public String getErrorLogs();

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
