package com.nlp4re.logic;

import static com.google.common.base.Preconditions.checkNotNull;

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
	public Map<Integer, String> getSentences(String desc) {
		checkNotNull(desc);

		// sentence detector
		SentenceModel model = null;
		try {
			InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-sent.bin");
			model = new SentenceModel(inputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}

		SentenceDetector detector = new SentenceDetectorME(model);
		String[] sentences = detector.sentDetect(desc);
		Map<Integer, String> map_sentences = new HashMap<Integer, String>();
		for (int index = 0; index < sentences.length; index++) {
			map_sentences.put(index, sentences[index]);
		}
		return map_sentences;
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
	public List<Map<Integer, String>> doParse(Map<Integer, String> sentences) {
		checkNotNull(sentences);

		Map<Integer, String> map_compliant_sentences = new HashMap<Integer, String>();
		Map<Integer, String> map_logs_for_non_compliant_sentences = new HashMap<Integer, String>();

		long timestart = System.currentTimeMillis();

		MazoAndJaramilloLogic mazoAndJaramilloLogic = new MazoAndJaramilloLogic();
		
		sentences.entrySet().stream().forEach(e -> {
			Integer index = e.getKey();
			String sentence = e.getValue();
			help(mazoAndJaramilloLogic, map_compliant_sentences, map_logs_for_non_compliant_sentences, index, sentence);
		});

		long timeend = System.currentTimeMillis();
		System.out.println("TIME: " + (timeend - timestart)); 
		List<Map<Integer, String>> result = new LinkedList<Map<Integer, String>>();
		result.add(sentences);
		result.add(map_compliant_sentences);
		result.add(map_logs_for_non_compliant_sentences);

		return result;

	}

	private void help(MazoAndJaramilloLogic mazoAndJaramilloLogic, Map<Integer, String> map_compliant_sentences,
			Map<Integer, String> map_logs_for_non_compliant_sentences, int index, String sentence) {
		// Integer index = e.getKey();
		// String sentence = e.getValue();

//		System.out.println("SENTENCE: " + sentence);
		boolean isConformance = mazoAndJaramilloLogic.parseTemplateConformance(sentence);
		String error_logs = mazoAndJaramilloLogic.error_logs;

//		 System.out.println("LOGS:" + error_logs);

		if (isConformance) {
			map_compliant_sentences.put(index, "0");
		} else {
			map_compliant_sentences.put(index, "1");
			map_logs_for_non_compliant_sentences.put(index, error_logs);
		}
	}
}
