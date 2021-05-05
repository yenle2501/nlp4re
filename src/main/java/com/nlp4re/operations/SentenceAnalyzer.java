package com.nlp4re.operations;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.util.StringUtils;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;

/**
 * This class helps to analyze the sentence and converts the sentence into tokens, tags, Chunks, Parsers
 */
public class SentenceAnalyzer {

	private SentenceOperations sentenceOperations;

	public SentenceAnalyzer(SentenceOperations sentenceOperations) {
		this.sentenceOperations = sentenceOperations;
	}

	/**
	 * Get Tokens from given sentence
	 * 
	 * @param sentence Sentence for conversion to tokens
	 * @return list of tokens from sentence
	 */
	public String[] getTokens(String sentence) {
		checkNotNull(sentence);
	
		TokenizerME tokenizer = sentenceOperations.getTokenizerME();
		String[] tokens = new String[] {};
		synchronized (tokenizer) {
			tokens = tokenizer.tokenize(sentence);
		}
		return tokens;
	}

	/**
	 * Get Part of Speech Tags from array of tokens
	 * 
	 * @param tokens List of Tokens
	 * @return An array of POSTags
	 * 
	 */
	public String[] getPOSTags(String[] tokens) {
		checkNotNull(tokens);
		POSTaggerME tagger = sentenceOperations.getPOSTaggerME();
		String[] tags = new String[] {};
		synchronized (tagger) {
			tags = tagger.tag(tokens);
		}
		
		return tags;
	}

	/**
	 * Get Parses from sentence
	 * 
	 * @return Parser
	 * 
	 */
	public Parse[] getParses(String sentence) {
		Parse[] parses = new Parse[] {};
		Parser parser = sentenceOperations.getParser();
		synchronized (parser) {
			parses = ParserTool.parseLine(sentence, parser, 1);
		}
		return parses;
	}

	
	/**
	 * Get Chunks from given tokens and tags
	 * 
	 * @return List of chunks
	 */
	public List<String> getChunks(String[] tokens, String[] tags) {
		checkNotNull(tokens);
		checkNotNull(tags);
		ChunkerME chunker = sentenceOperations.getChunkerME();
		List<String> chunks = new LinkedList<String>();
		synchronized (chunker) {
			chunks = Arrays.asList(chunker.chunk(tokens, tags));
		}
		
		return chunks;

	}

	/**
	 * Determines the condition in the sentence from given tokens, index of comma and modal verb
	 * 
	 * @param tokens:      List of tokens
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
	 * @return list tokens of system name
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
	 * Determines noun chunk from Parse and save it in list_object
	 * 
	 * @param parse:        Parse for determination of noun chunks
	 * @param list_objects: list of objects
	 */
	private void getNounChunk(Parse parse, List<Parse> list_objects) {
		checkNotNull(parse);
		checkNotNull(list_objects);

		if (parse.getType().equals("NP")) {
			list_objects.add(parse);

		}
		Arrays.asList(parse.getChildren()).stream().forEach(child -> {
			getNounChunk(child, list_objects);
		});
	}

	/**
	 * Determines object of sentence
	 * 
	 * @param possible_object_string: Sentence to check
	 * @param possible_object_tokens: tokens of possible object
	 * @return Object of the sentence
	 */
	public String getObjects(String possible_object_string, String[] possible_object_tokens) {
		checkNotNull(possible_object_string);
		checkNotNull(possible_object_tokens);

		Parse[] parses = getParses(possible_object_string);
		LinkedList<Parse> list_objects = new LinkedList<Parse>();
		Arrays.asList(parses).stream().forEach(parse -> {
			getNounChunk(parse, list_objects);
		});

		Stream<Parse> parses_without_original_sentence = list_objects.stream()
				.filter(parse -> !parse.getCoveredText().equals(possible_object_string));
		// first parse is first noun chunk from sentence
		String object = parses_without_original_sentence.findFirst().get().getCoveredText();
		String[] object_tokens = new String[] {};
		TokenizerME tokenizer = sentenceOperations.getTokenizerME();
		synchronized (tokenizer) {
			object_tokens = tokenizer.tokenize(object);
		}
		// because Possessive pronoun such as his/her.. or between are not nouns
		if (!object_tokens[0].equalsIgnoreCase(possible_object_tokens[0])) {
			int index = Arrays.asList(possible_object_tokens).indexOf(object_tokens[0]);
			String[] missed_tokens = Arrays.copyOfRange(possible_object_tokens, 0, index);
			object = StringUtils.arrayToDelimitedString(missed_tokens, " ") + " " + object;
		}
		return object;
	}

	/**
	 * Get modal verb index from given tags
	 * 
	 * @param tags:        List of tags
	 * @param index_comma: index of comma in sentence
	 * @return index of modal verb in the sentence
	 * 
	 */
	public int getModalIndex(List<String> tags, int comma_index) {
		checkNotNull(tags);

		int modal_index = ListIterate.detectIndex(tags, "MD"::equals);
		// if the sentence contains condition and the condition has a modal verb
		if (comma_index != -1 && comma_index <= modal_index) {
			// modal index after comma
			int x = ListIterate.detectIndex(tags.subList(comma_index, tags.size()), "MD"::equals);
			return x + comma_index;
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

		int anchor_start_index = 0;
		// in case the sentence has condition
		if (comma_index < modal_index && comma_index != -1 && !tokens.get(comma_index + 1).equals("then")) {
			anchor_start_index = comma_index + 1;
		}
		// in case the sentence looks like : if ..., then ...
		else if (comma_index < modal_index && comma_index != -1 && tokens.get(comma_index + 1).equals("then")) {
			anchor_start_index = comma_index + 2;
		}
		return anchor_start_index;
	}
}
