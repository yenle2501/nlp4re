package com.nlp4re.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 * TODO classname should be changed, may a help class
 */
public class SentenceAnalyzer {

	/**
	 * TODO: add logging and comments for class, methods
	 * 
	 */

	private static int system_name_start = 0;

	public String[] getTokens(String sentence) {

		WhitespaceTokenizer whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
		String tokens[] = whitespaceTokenizer.tokenize(sentence);
//		System.out.println("TOKENS: " + Arrays.toString(tokens));
		return tokens;
	}

	public String[] getTags(String[] tokens) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-pos-maxent.bin");
		POSModel model = new POSModel(inputStream);
		POSTaggerME tagger = new POSTaggerME(model);
		String[] tags = tagger.tag(tokens);
//		System.out.println("tags: " + Arrays.toString(tags));
		return tags;
	}

	public List<Parse> getNounChunks(String sentence) throws IOException {

		List<Parse> nounChunks = new LinkedList<Parse>();

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-parser-chunking.bin");
		ParserModel model = new ParserModel(inputStream);
		Parser parser = ParserFactory.create(model);

		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);

		for (Parse p : topParses) {
			System.out.print("PARSE: ");
			p.showCodeTree();
			getNounPhrases(p, nounChunks);
		}

		return nounChunks;
	}

	private void getNounPhrases(Parse p, List<Parse> nounPhrases) {
		if (p.getType().equals("NP")) {
			nounPhrases.add(p);
		}
		for (Parse child : p.getChildren()) {
			getNounPhrases(child, nounPhrases);
		}
	}

	public String[] getChunks(String[] tokens, String[] tags) throws IOException {
		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-chunker.bin");
		ChunkerModel model = new ChunkerModel(inputStream);
		ChunkerME chunker = new ChunkerME(model);
		String tag[] = chunker.chunk(tokens, tags);

//		System.out.println("chunk: " + Arrays.toString(tag));
		return tag;
	}

//	public  Span[] getLocation(String[] tokens) throws IOException {
//		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-ner-location.bin");
//		TokenNameFinderModel location = new TokenNameFinderModel(inputStream);
//		NameFinderME nameFinder = new NameFinderME(location);
//
//		Span[] nameSpans = nameFinder.find(tokens);
//
//		for (Span span : nameSpans) {
////			System.out.println("SPAN: " + span.toString());
//		}
//		return nameSpans;
//	}

	public String getConditions(String[] tokens) {

		String[] conditions = Arrays.copyOfRange(tokens, 0, system_name_start);

		return Arrays.toString(conditions);
	}

	public String getSystemName(List<Parse> nounChunks, String[] tokens, String[] tags) throws IOException {

		int modal_vp_index = Arrays.asList(tags).indexOf("MD");

		for (Parse parse : nounChunks) {
//			System.out.println("NOUN CHUNKS: " + parse.getCoveredText() );
			String coveredText = parse.getCoveredText();
			String[] coveredTextTokens = getTokens(coveredText);

			int end_index = 0;
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals(coveredTextTokens[0])) {
					for (int j = 1; j < coveredTextTokens.length; j++) {
						if (tokens[i + j].equals(coveredTextTokens[j])) {
							system_name_start = i;
							end_index = i + j;
						} else {
							break;
						}
					}

				}
			}

			if (end_index == modal_vp_index - 1) {
				return coveredText;
			}
		}
		return null;
	}

	public List<String> getModalVp(String[] tags, String[] tokens) {
//		Map<Integer, String> modals = new HashMap<Integer, String>();
		List<String> modals = new LinkedList<String>();
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals("MD") || tags[i] == "MD") {
//				modals.put(i, tokens[i]);
				modals.add(tokens[i]);
			}
		}
		return modals;
	}

	public String getAnchor(String[] tokens) {
		return "";
	}

}
