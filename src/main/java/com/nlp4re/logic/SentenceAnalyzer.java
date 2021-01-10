package com.nlp4re.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import opennlp.tools.util.Span;

/**
 * TODO classname should be changed, may a help class
 */
public class SentenceAnalyzer {

	/**
	 * Get Tokens from given sentence
	 * 
	 * @param sentence Sentence for conversion to tokens
	 * @return list of tokens from sentence
	 */
	public String[] getTokens(String sentence) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-token.bin");
		TokenizerModel tokenModel = new TokenizerModel(inputStream);
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		String tokens[] = tokenizer.tokenize(sentence);
		// System.out.println("TOKENS: " + Arrays.toString(tokens));
		return tokens;
	}

	/**
	 * Get Part of Speech Tags from list of tokens
	 * 
	 * @param tokens List of Tokens
	 * @return list of POSTags
	 * 
	 */
	public String[] getPOSTags(String[] tokens) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-pos-maxent.bin");
		POSModel model = new POSModel(inputStream);
		POSTaggerME tagger = new POSTaggerME(model);
		String[] tags = tagger.tag(tokens);
		// System.out.println("tags: " + Arrays.toString(tags));
		return tags;
	}

	/**
	 * Get Parse from given sentence
	 * 
	 * @param sentence Sentence for conversion to parse
	 * @return list of Parses from sentence
	 * 
	 */
	public Parse[] getParses(String sentence) throws IOException {

		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-parser-chunking.bin");
		ParserModel model = new ParserModel(inputStream);
		Parser parser = ParserFactory.create(model);

		Parse[] parses = ParserTool.parseLine(sentence, parser, 1);

		return parses;
	}

	/**
	 * Get chunks from given tags and tokens
	 * 
	 * @param tokens:
	 * @param tags:
	 * @return list of chunks
	 * 
	 */
	public String[] getChunks(String[] tokens, String[] tags) throws IOException {
		InputStream inputStream = new FileInputStream(".\\src\\main\\resources\\models\\en-chunker.bin");
		ChunkerModel model = new ChunkerModel(inputStream);
		ChunkerME chunker = new ChunkerME(model);
		String[] chunks = chunker.chunk(tokens, tags);
		return chunks;
	}

	/**
	 * Get Tokens from given sentence
	 * 
	 * @param sentence Sentence for conversion to tokens
	 * @return list of tokens from sentence
	 * 
	 */
	public String[] getConditions(String[] tokens, String[] tags) {
		int token_index = Arrays.asList(tokens).indexOf(",");
		int index_of_modal = Arrays.asList(tags).indexOf("MD");

		if (token_index == -1 || token_index > index_of_modal) {
			return null;
		}
		String[] conditions = Arrays.copyOfRange(tokens, 0, Arrays.asList(tokens).indexOf(","));
		return conditions;
	}

	/**
	 * Get Tokens from given sentence
	 * 
	 * @param sentence Sentence for conversion to tokens
	 * @return list of tokens from sentence
	 * 
	 */
	public String[] getSystemName(String[] tokens, String[] tags) {

		int index_of_comma = Arrays.asList(tokens).indexOf(",");
		int index_of_modal = Arrays.asList(tags).indexOf("MD");
		String[] systemName = new String[] {};

		if (index_of_comma != -1 && index_of_comma < index_of_modal) {
			// cause if..., then
			if (tokens[index_of_comma + 1].equals("then")) {
				systemName = Arrays.copyOfRange(tokens, index_of_comma + 2, index_of_modal);
			} else {
				systemName = Arrays.copyOfRange(tokens, index_of_comma + 1, index_of_modal);
			}
		} else {
			systemName = Arrays.copyOfRange(tokens, 0, index_of_modal);
		}
		return systemName;
	}

	/**
	 * Get modal verb from given tokens and tags
	 * 
	 * @param tokens:
	 * @param tags:
	 * @return list of tokens from sentence
	 * 
	 */
	public List<String> getModalVp(String[] tags, String[] tokens) {
//		System.out.println("TOKENS: " + Arrays.toString(tags));
		List<String> modals = new LinkedList<String>();
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals("MD") || tags[i] == "MD") {
				modals.add(tokens[i]);
			}
		}
		return modals;
	}

	public String getObjects(String sentence) throws IOException {

		Parse[] a = getParses(sentence);
		LinkedList<Parse> list_objects = new LinkedList<Parse>();
		Arrays.asList(a).forEach(parse -> {
			getNounChunk(parse, list_objects);
		});

		LinkedList<Parse> list_objects_cpy = new LinkedList<Parse>();
		list_objects_cpy.addAll(list_objects);

		list_objects_cpy.stream().sequential().forEach(parse -> {
			for (Parse object : list_objects) {
				if (Arrays.asList(object.getChildren()).contains(parse)) {
					list_objects.remove(object);
					break;
				}
			}
		});

		String object = list_objects.get(0).getCoveredText();
		String[] object_tokens = getTokens(object);
		String[] possible_object_tokens = getTokens(sentence);

		// because Possessive pronoun  such as his/her.. or between are not nouns
		if (!object_tokens[0].equalsIgnoreCase(possible_object_tokens[0])) {
			int index = Arrays.asList(possible_object_tokens).indexOf(object_tokens[0]);

			String[] missed_tokens = Arrays.copyOfRange(possible_object_tokens, 0, index);
//			System.out.println("missed tokens:" + Arrays.toString(missed_tokens));
			object = StringUtils.arrayToDelimitedString(missed_tokens, " ") + " " + object;
		}

		return object;
	}

	/**
	 * Get Tokens from given sentence
	 * 
	 * @param sentence Sentence for conversion to tokens
	 * @return list of tokens from sentence
	 * 
	 */
	public void getNounChunk(Parse parse, List<Parse> list_objects) {

		if (parse.getType().equals("NP")) {
			list_objects.add(parse);

		}
		for (Parse child : parse.getChildren()) {
			getNounChunk(child, list_objects);
		}
	}

	/**
	 * Get anchor from given tokens and tags
	 * 
	 * @param tokens:
	 * @param tags:
	 * @return anchor from tokens and tags
	 * 
	 */
	public int getAnchorStartIndex(String[] tags, String[] tokens) {
		int start_index_anchor = 0;
		int index_of_comma = Arrays.asList(tokens).indexOf(",");
		int index_of_modal = Arrays.asList(tags).indexOf("MD");

		if (index_of_comma < index_of_modal && index_of_comma != -1 && !tokens[index_of_comma + 1].equals("then")) {
			start_index_anchor = index_of_comma + 1;
		} else if (index_of_comma < index_of_modal && index_of_comma != -1
				&& tokens[index_of_comma + 1].equals("then")) {
			start_index_anchor = index_of_comma + 2;
		}

		// String[] anchor = Arrays.copyOfRange(tokens, start_index_anchor,
		// index_of_modal + 2);
		return start_index_anchor;
	}

	/**
	 * Get anchor from given tokens and tags
	 * 
	 * @param tokens:
	 * @param tags:
	 * @return anchor from tokens and tags
	 * 
	 */
	public int getTokenIndex(String[] tags, String[] tokens, String tagname, String tokenname,
			String[] condition_tokens, boolean isPre) {

		int index = -1;
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equalsIgnoreCase(tagname) && tokens[i].equalsIgnoreCase(tokenname)) {
				String[] b = isPre ? Arrays.copyOfRange(tokens, i - condition_tokens.length, i)
						: Arrays.copyOfRange(tokens, i, i + condition_tokens.length);

				String a = StringUtils.arrayToDelimitedString(b, " ");
				String condition_string = StringUtils.arrayToDelimitedString(condition_tokens, " ");
				if (a.equalsIgnoreCase(condition_string)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
}
