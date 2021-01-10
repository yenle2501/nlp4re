package com.nlp4re.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class RequirementLogic {

	public RequirementLogic() {

	}

	public String[] getSentences(String requirement) throws IOException {
		// sentence detector
		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-sent.bin");
		SentenceModel model = new SentenceModel(inputStream);

		SentenceDetector detector = new SentenceDetectorME(model);
		String[] sentences = detector.sentDetect(requirement);

		return sentences;
	}

	public Map<Integer, String> doParse(String[] sentences) throws IOException {

		List<String> requirement_list = new LinkedList<String>();
		// List<String> conformance_list = new LinkedList<String>();
		Map<Integer, String> conformance_list = new HashMap<Integer, String>();

		// for (String sentence : sentences) {

		// Arrays.asList().stream().forEach(sentence -> {
		for (int index = 0; index < sentences.length; index++) {
			// System.out.println();
			String sentence = sentences[index];
			System.out.println("SENTENCE: " + sentence);

			try {
				MazoAndJaramilloLogic mazoAndJaramilloLogic = new MazoAndJaramilloLogic();
				mazoAndJaramilloLogic.first(sentence);
				mazoAndJaramilloLogic.parseModalVp();
				mazoAndJaramilloLogic.parseSystemName();
				mazoAndJaramilloLogic.parseAnchor();
				mazoAndJaramilloLogic.isValidSentence();
				mazoAndJaramilloLogic.parseCondition();
				mazoAndJaramilloLogic.parseObject();
				mazoAndJaramilloLogic.parseConformantSegment();
				mazoAndJaramilloLogic.parseDetails();
				boolean isConformance = mazoAndJaramilloLogic.parseTemplateConformance();

				requirement_list.add(sentence);
				if (isConformance) {
					// System.out.println(">>>>>>>>>>SENTENCE CONFORMED <<<<<<<<<<<< ");
					conformance_list.put(index, sentence);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/**
			 * if all rights, then return true if any if all false, check that it is not re
			 */
		}
		return conformance_list;

	}

}
