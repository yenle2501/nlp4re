package com.nlp4re.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

	public void doParse(String[] sentences) throws IOException {
		MazoAndJaramilloLogic mazoAndJaramilloLogic = new MazoAndJaramilloLogic();

		for (String sentence : sentences) {
			System.out.println();
			System.out.println("SENTENCE: " + sentence);

			mazoAndJaramilloLogic.first(sentence);

			boolean hasModalVerb = mazoAndJaramilloLogic.parseModalVp();
			boolean hasSystemName = mazoAndJaramilloLogic.parseSystemName();
			boolean hasCondition = mazoAndJaramilloLogic.parseCondition();			
			/** if all rights, then return true
			 * if any 
			 * if all false, check that it is not re
			 * */
		}
	}
}
