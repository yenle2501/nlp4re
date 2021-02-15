package com.nlp4re.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * This class works as logic class
 */
public class RequirementLogic {

	/**
	 * This method helps to get single sentence from the requirements description
	 * 
	 * @param desc requirements description
	 * @return a String array with sentences
	 * @throws IOException
	 */
	public String[] getSentences(String desc) throws IOException {
		// sentence detector
		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-sent.bin");
		SentenceModel model = new SentenceModel(inputStream);

		SentenceDetector detector = new SentenceDetectorME(model);
		String[] sentences = detector.sentDetect(desc);
		if (sentences.length == 0) {
			return null;
		}
		return sentences;
	}

	/**
	 * This method helps to parse each single sentence with the chosen template.
	 * 
	 * @param sentences list of sentences
	 * @return a list of map with key-value-pair 1.Map contains all sentences of requirement 2.Map contains all
	 *         compliant and non-compliant sentences with the order as the keys in 1.Map 3.Map contains all log for the
	 *         non-compliant sentences with the order as the keys in 1.Map
	 * @throws IOException
	 */
	public List<Map<Integer, String>> doParse(String[] sentences) throws IOException {

		Map<Integer, String> map_sentences = new HashMap<Integer, String>();
		Map<Integer, String> map_compliant_sentences = new HashMap<Integer, String>();
		Map<Integer, String> map_logs_for_non_compliant_sentences = new HashMap<Integer, String>();

		for (int index = 0; index < sentences.length; index++) {
			String sentence = sentences[index];
			map_sentences.put(index, sentence);

			System.out.println("SENTENCE: " + sentence);
			MazoAndJaramilloLogic mazoAndJaramilloLogic = new MazoAndJaramilloLogic();
			mazoAndJaramilloLogic.tokenizeSentence(sentence);
			mazoAndJaramilloLogic.parseModalVp();
			mazoAndJaramilloLogic.parseSystemName();
			mazoAndJaramilloLogic.parseAnchor();
			mazoAndJaramilloLogic.isValidSentence();
			mazoAndJaramilloLogic.parseCondition();
			mazoAndJaramilloLogic.parseObject();
			mazoAndJaramilloLogic.parseConformantSegment();
			mazoAndJaramilloLogic.parseDetails();
			boolean isConformance = mazoAndJaramilloLogic.parseTemplateConformance();
			String error_logs = mazoAndJaramilloLogic.error_logs;

			if (isConformance) {
				map_compliant_sentences.put(index, "0");
			} else {
				map_compliant_sentences.put(index, "1");
				map_logs_for_non_compliant_sentences.put(index, error_logs);
			}
		}
		List<Map<Integer, String>> result = new LinkedList<Map<Integer, String>>();
		result.add(map_sentences);
		result.add(map_compliant_sentences);
		result.add(map_logs_for_non_compliant_sentences);

		return result;

	}

}
