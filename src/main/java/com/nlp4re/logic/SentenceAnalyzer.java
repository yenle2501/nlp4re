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
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
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
	public String[] getTokens(String sentence) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-token.bin");
		TokenizerModel tokenModel = new TokenizerModel(inputStream);
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		String tokens[] = tokenizer.tokenize(sentence);
//		System.out.println("TOKENS: " + Arrays.toString(tokens));
		return tokens;
	}

	public String[] getPOSTags(String[] tokens) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-pos-maxent.bin");
		POSModel model = new POSModel(inputStream);
		POSTaggerME tagger = new POSTaggerME(model);
		String[] tags = tagger.tag(tokens);
//		System.out.println("tags: " + Arrays.toString(tags));
		return tags;
	}

	public Parse[] getParses(String sentence) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-parser-chunking.bin");
		ParserModel model = new ParserModel(inputStream);
		Parser parser = ParserFactory.create(model);

		Parse[] parses = ParserTool.parseLine(sentence, parser, 1);

		return parses;
	}

	public String[] getChunks(String[] tokens, String[] tags) throws IOException {
		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-chunker.bin");
		ChunkerModel model = new ChunkerModel(inputStream);
		ChunkerME chunker = new ChunkerME(model);
		String[] chunks = chunker.chunk(tokens, tags);
		return chunks;
	}

	public void getNounPhrases(Parse p, List<Parse> nounPhrases) {
		if (p.getType().equals("NP")) {
			nounPhrases.add(p);
		}
		for (Parse child : p.getChildren()) {
			getNounPhrases(child, nounPhrases);
		}
	}

	public String[] getConditions(String[] tokens) {
		int token_index = Arrays.asList(tokens).indexOf(",");
		if (token_index == -1) {
			return null;
		}
		String[] conditions = Arrays.copyOfRange(tokens, 0, Arrays.asList(tokens).indexOf(","));
		return conditions;
	}

	public String[] getSystemName(String[] tokens, String[] tags) {

		int index_of_comma = Arrays.asList(tokens).indexOf(",");
		int index_of_modal = Arrays.asList(tags).indexOf("MD");
		String[] systemName = new String[] {};

		if (index_of_comma != -1) {
			systemName = Arrays.copyOfRange(tokens, index_of_comma + 1, index_of_modal);
		} else {
			systemName = Arrays.copyOfRange(tokens, 0, index_of_modal);
		}
		return systemName;
	}

	public List<String> getModalVp(String[] tags, String[] tokens) {
		System.out.println("TOKENS: " + Arrays.toString(tags));
		List<String> modals = new LinkedList<String>();
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals("MD") || tags[i] == "MD") {
				modals.add(tokens[i]);
			}
		}
		return modals;
	}

	public String getAnchor(String[] tokens) {
		return "";
	}

}
