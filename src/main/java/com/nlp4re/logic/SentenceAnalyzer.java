package com.nlp4re.logic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.util.StringUtils;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * This class works as a
 */
public class SentenceAnalyzer {
	TokenizerME tokenizer;
	POSTaggerME tagger;
	Parser parser;
	ChunkerME chunker;

	public SentenceAnalyzer() {
		tokenizer = getTokens();
		tagger = getPOSTags();
		parser = getParses();
		chunker = getChunks();
	}

	/**
	 * Get Tokens from given sentence
	 * 
	 * @param sentence Sentence for conversion to tokens
	 * @return list of tokens from sentence
	 */
	public TokenizerME getTokens() {
		// checkNotNull(sentence);

		TokenizerModel tokenModel = null;
		try {
			InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-token.bin");
			tokenModel = new TokenizerModel(inputStream);

		} catch (IOException e) {
			e.printStackTrace();
		}
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		return tokenizer;
		// String tokens[] = tokenizer.tokenize(sentence);
		// return tokens;
	}

	public String[] getTokens(String sentence) {
		return tokenizer.tokenize(sentence);
	}

	/**
	 * Get Part of Speech Tags from array of tokens
	 * 
	 * @param tokens List of Tokens
	 * @return An array of POSTags
	 * 
	 */
	public POSTaggerME getPOSTags() {
		// checkNotNull(tokens);

		POSModel model = null;
		try {
			InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-pos-maxent.bin");
			model = new POSModel(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		POSTaggerME tagger = new POSTaggerME(model);
		return tagger;
		// String[] tags = tagger.tag(tokens);
		// return tags;
	}

	public String[] getPOSTags(String[] tokens) {
		return tagger.tag(tokens);
	}

	/**
	 * Determines Parses from given sentence
	 * 
	 * @param sentence Sentence for conversion to parse
	 * @return An Array of Parses from sentence
	 * 
	 */
	public Parser getParses() {
		// checkNotNull(sentence);

		ParserModel model = null;
		try {
			InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-parser-chunking.bin");
			model = new ParserModel(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Parser parser = ParserFactory.create(model);
		return parser;
	}
	// Parse[] parses;
	// synchronized (parser) {
	// parses = ParserTool.parseLine(sentence, parser, 1);
	// }
	//
	// return parses;
	// }

	/**
	 * Determines chunks from given tags and tokens
	 * 
	 * @param tokens: array of tokens
	 * @param tags:   array of tags
	 * @return List of Chunks
	 * @throws IOException
	 */
	public ChunkerME getChunks() {// (String[] tokens, String[] tags) {
		// checkNotNull(tokens);
		// checkNotNull(tags);

		ChunkerModel model = null;
		try {
			InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-chunker.bin");
			model = new ChunkerModel(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChunkerME chunker = new ChunkerME(model);
		return chunker;

		// String[] chunks = chunker.chunk(tokens, tags);
		// return Arrays.asList(chunks);
	}

	public List<String> getChunks(String[] tokens, String[] tags) {
		checkNotNull(tokens);
		checkNotNull(tags);
		return Arrays.asList(chunker.chunk(tokens, tags));
	}

	/**
	 * Determines the condition in the sentence from given tokens, index of comma and modal verb
	 * 
	 * @param token:       List of tokens
	 * @param comma_index: index of comma in sentence
	 * @param modal_index: index of modal verb in sentence
	 * @return condition of sentence in form of a list
	 */
	public List<String> getConditions(List<String> tokens, int modal_index, int comma_index) {
		checkNotNull(tokens);

		if (comma_index == -1 || comma_index > modal_index) {
			return null;
		}
		return tokens.subList(0, comma_index);
	}

	/**
	 * Determines the system name from given tokens
	 * 
	 * @param tokens:      List of tokens
	 * @param comma_index: index of comma in sentence
	 * @param modal_index: index of modal verb in sentence
	 * @return system name in form of a list
	 * 
	 */
	public List<String> getSystemName(List<String> tokens, int comma_index, int modal_index) {
		checkNotNull(tokens);

		List<String> systemName = new LinkedList<String>();

		if (comma_index != -1 && comma_index < modal_index) {
			// cause if..., then
			if (tokens.get(comma_index + 1).equals("then")) {
				systemName = tokens.subList(comma_index + 2, modal_index);
			} else {
				systemName = tokens.subList(comma_index + 1, modal_index);
			}
		} else {
			systemName = tokens.subList(0, modal_index);
		}
		return systemName;
	}

	/**
	 * Determines object of sentence
	 * 
	 * @param sentence Sentence to check
	 * @return Object of the sentence
	 */
	public String getObjects(String sentence, String[] possible_object_tokens) {
		checkNotNull(sentence);
		checkNotNull(possible_object_tokens);

		
		
		Parse[] parses = new Parse[] {};
		parses = ParserTool.parseLine(sentence, parser, 1);

		LinkedList<Parse> list_objects = new LinkedList<Parse>();

		Arrays.asList(parses).stream().forEach(parse -> {
			getNounChunk(parse, list_objects);
		});

		Stream<Parse> new_ob = list_objects.stream().filter(parse -> !parse.getCoveredText().equals(sentence));
		String object = new_ob.findFirst().get().getCoveredText();

		String[] object_tokens = new String[] {};
		object_tokens = tokenizer.tokenize(object);

		// because Possessive pronoun such as his/her.. or between are not nouns
		if (!object_tokens[0].equalsIgnoreCase(possible_object_tokens[0])) {
			int index = Arrays.asList(possible_object_tokens).indexOf(object_tokens[0]);

			String[] missed_tokens = Arrays.copyOfRange(possible_object_tokens, 0, index);
			object = StringUtils.arrayToDelimitedString(missed_tokens, " ") + " " + object; // 2840
		}

		return object;
	}

	/**
	 * Determines noun chunk from Parse and save it in list_object
	 * 
	 * @param parse        Parse for determination of noun chunks
	 * @param list_objects list of objects
	 */
	public void getNounChunk(Parse parse, List<Parse> list_objects) {
		checkNotNull(parse);
		checkNotNull(list_objects);

		if (parse.getType().equals("NP")) {
			list_objects.add(parse);

		}
		// for (Parse child : parse.getChildren()) {
		Arrays.asList(parse.getChildren()).stream().forEach(child -> {
			getNounChunk(child, list_objects);
		});
	}

	/**
	 * Get modal verb index from given tags
	 * 
	 * @param tags:        List of tags
	 * @param index_comma: index of comma in sentence
	 * @return index of modal verb in the sentence
	 * 
	 */
	public int getModalIndex(List<String> tags, int index_comma) {
		checkNotNull(tags);

		int modal_index = ListIterate.detectIndex(tags, "MD"::equals);
		// if the sentence contains condition
		if (index_comma != -1 && index_comma <= modal_index) {
			int x = ListIterate.detectIndex(tags.subList(index_comma, tags.size()), "MD"::equals);
			return x + index_comma;
		}
		return modal_index;
	}

	/**
	 * Get start index of anchor in sentence from given tokens
	 * 
	 * @param tokens:      List of tokens
	 * @param comma_index: index of comma in sentence
	 * @param modal_index: index of modal verb in sentence
	 * @return start index of anchor
	 * 
	 */
	public int getAnchorStartIndex(List<String> tokens, int comma_index, int modal_index) {
		checkNotNull(tokens);

		int start_index_anchor = 0;
		// in case the sentence looks like : if ..., then ...
		if (comma_index < modal_index && comma_index != -1 && !tokens.get(comma_index + 1).equals("then")) {
			start_index_anchor = comma_index + 1;
		} else if (comma_index < modal_index && comma_index != -1 && tokens.get(comma_index + 1).equals("then")) {
			start_index_anchor = comma_index + 2;
		}
		return start_index_anchor;
	}
}
