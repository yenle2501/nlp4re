package com.nlp4re.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class RequirementLogic {

	private static void findSystemName(String text) {
		SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		String[] tokens = tokenizer.tokenize(text);
		// Loading the Tokenizer model
		InputStream inputStream;
			
		TokenNameFinderModel model = null;
		try {
			inputStream = new FileInputStream("C:\\Bildung\\Master\\MasterArbeit\\nlp4re\\src\\main\\resources\\models\\en-ner-person.bin");
			model = new TokenNameFinderModel(inputStream);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		NameFinderME nameFinder = new NameFinderME(model);
		Span nameSpans[] = nameFinder.find(tokens);
		// do something with the names
		System.out.println("Found entity: " + Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
	}

	public static void main(String[] args) {
		String text = "John is 26 years old. His best friend's name is Leonard. He has a sister named Penny.";
//				"the surveillance and tracking module shall provide the system administrator with the ability to monitor system configuration changes posted to the database";

		findSystemName(text);
	}

}
