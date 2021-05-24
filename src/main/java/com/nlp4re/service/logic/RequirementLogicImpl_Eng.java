package com.nlp4re.service.logic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nlp4re.service.operations.PatternMatcher;
import com.nlp4re.service.operations.RegexesProvider;
import com.nlp4re.service.operations.SentenceAnalyzer;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

@Component
public class RequirementLogicImpl_Eng implements RequirementLogic {

	private String error_logs;
	private int object_start_index;
	private int object_end_index;
	private SentenceAnalyzer sentenceAnalyzer;
	private PatternMatcher matcher;
	private RegexesProvider regexesProvider;

	/**
	 * Constructor
	 */
	public RequirementLogicImpl_Eng(SentenceAnalyzer sentenceAnalyzer, PatternMatcher matcher,
			RegexesProvider regexesProvider) {
		this.sentenceAnalyzer = sentenceAnalyzer;
		this.matcher = matcher;
		this.regexesProvider = regexesProvider;
		this.error_logs = "";
		this.object_start_index = -1;
		this.object_end_index = -1;
	}

	/**
	 * Tokenizes the sentence
	 * 
	 * @param sentence sentence for tokenision
	 * @return List of tokens and tags of sentence
	 */
	public String[] getTokensFromSentence(String sentence) {
		checkNotNull(sentence);
		return sentenceAnalyzer.getTokens(sentence);
	}

	/***
	 * tag each token according to Penn Treebank tag set
	 * 
	 * @param tokens input tokens
	 * @return Array of Tags
	 */
	public String[] getTagsFromTokens(String[] tokens) {
		checkNotNull(tokens);
		return sentenceAnalyzer.getPOSTags(tokens);
	}

	private void setObjectStartIndex(int index) {
		this.object_start_index = index;
	}

	private int getObjectStartIndex() {
		return object_start_index;
	}

	private void setObjectEndIndex(int index) {
		this.object_end_index = index;
	}

	private int getObjectEndIndex() {
		return object_end_index;
	}

	/**
	 * Checks the modal verb
	 * 
	 * @return true : if the sentence contains one of the proposed modal verbs such as shall, should, could, will, must
	 *         false: otherwise
	 */
	public boolean parseModalVp(int modal_index, List<String> list_tokens) {
		checkNotNull(list_tokens);

		if (modal_index == -1) {
			error_logs = "The sentence does not contain any modal verbs. The modal verbs should be SHOULD, SHALL, COULD, WILL, MUST.\n";
			return false;
		} else {

			String modal_vp = list_tokens.get(modal_index);
			List<String> modalverbs = regexesProvider.getModalRegexes().stream().map(m -> m.getKey_name().toUpperCase())
					.collect(Collectors.toList());
			if (modalverbs.contains(modal_vp.toUpperCase())) {
				return true;
			} else {
				error_logs = "The sentence does not contain any valid modal verbs. The modal verbs should be SHOULD, SHALL, COULD, WILL, MUST.\n";
				return false;
			}
		}
	}

	/**
	 * This method has the ability to check system name
	 * 
	 * @return true : if the sentence has a valid name of system 
	 *         false: otherwise
	 */

	public boolean parseSystemName(List<String> list_tokens, int comma_index, int modal_index) {
		checkNotNull(list_tokens);

		List<String> tokens_possible_systemName = sentenceAnalyzer.getSystemName(list_tokens, comma_index, modal_index);

		if (tokens_possible_systemName != null && !tokens_possible_systemName.isEmpty()) {

			Map<String, String> regexs = new HashMap<String, String>();
			regexesProvider.getSystemNameRegexes().forEach(systemname -> {
				regexs.put(systemname.getKey_name(), systemname.getRegex());
				regexs.put(systemname.getKey_name(), systemname.getRegex().toUpperCase());
			});

			String systemName = StringUtils.collectionToDelimitedString(tokens_possible_systemName, " ");

			Span[] spans = matcher.matches(regexs, systemName);
			// No patterns are matched
			if (spans == null || spans.length != 1) {
				error_logs = "System name should be one of the following forms:\r\n"
						+ "ALL|SOME SYSTEMS OF THE <Product line name>\r\n"
						+ "THOSE SYSTEMS OF THE <Product line name> <Restriction>\r\n"
						+ "THE <System or part name>\r\n";
				return false;
			}
			// one of the patterns is matched
			else if (spans.length == 1) {
				if (spans[0].getType().equals("the") || spans[0].getType().equals("all_some")) {
					// if it is 'the' pattern, the start index of system name is one, otherwise it is 4 (for example:
					// 'some systems of the')
					int start_index = spans[0].getType().equals("the") ? 1 : 4;
					String[] tokens_system_name = Arrays.copyOfRange(tokens_possible_systemName.toArray(new String[0]),
							start_index, tokens_possible_systemName.size());
					List<String> tags_system_name = Arrays.asList(sentenceAnalyzer.getPOSTags(tokens_system_name));

					// that contains only noun
					if (!tags_system_name.contains("VB")) {
						return true;
					} else {
						error_logs += "No Verb after system name.\n";
						return false;
					}

				} // another type of regex
				else {
					return true;
				}
			}
		}
		error_logs = "System name should be one of the following forms:\r\n"
				+ "ALL|SOME SYSTEMS OF THE <Product line name>\r\n"
				+ "THOSE SYSTEMS OF THE <Product line name> <Restriction>\r\n" + "THE <System or part name>\r\n";
		return false;

	}

	/**
	 * This method has the ability to check the precondition of the sentence
	 * 
	 * @return true :if the sentence has no precondition or a valid condition 
	 *         false: otherwise
	 */

	public boolean parseCondition(List<String> list_tokens, int comma_index, int modal_index) {
		checkNotNull(list_tokens);

		List<String> token_conditions = sentenceAnalyzer.getConditions(list_tokens, modal_index, comma_index);
		// it is not required that the sentence has a condition
		if (token_conditions == null || token_conditions.isEmpty()) {
			return true;
		}

		Map<String, String> regexes = new HashMap<String, String>();
		regexesProvider.getConditionsRegexes().forEach(condition -> {
			regexes.put(condition.getKey_name(), condition.getRegex());
			regexes.put(condition.getKey_name(), condition.getRegex().toUpperCase());
		});

		String condition = StringUtils.collectionToDelimitedString(token_conditions, " ");
		Span[] spans = matcher.matches(regexes, condition);

		if (spans != null && spans.length == 1) {
			if (spans[0].getType().equals("if")) {
				if (list_tokens.get(comma_index + 1).equals("then")) {
					return true;
				} else {
					error_logs += "The condition should be IF <Condition|Event>, THEN\n";
					return false;
				}
			} else if (spans[0].getType().equals("while_during") || spans[0].getType().equals("after")
					|| spans[0].getType().equals("incase")) {
				return true;
			}
		}
		error_logs += "The condtions should be one of following forms:\r\n" + "IF <Condition|Event>,THEN \r\n"
				+ "WHILE|DURING <Activation state> \n" + "IN CASE <Included feature> IS INCLUDED \r\n"
				+ "AFTER|BEFORE| AS SOON AS <Bahavior> \r\n";
		return false;

	}

	/**
	 * Anchor should contain SYSTEM NAME + MODAL VERB + NORMAL VERB This method has the ability to check the anchor of
	 * the sentence.
	 * 
	 * @return List of value(1: false, 0: true) and start index of object 0: if the sentence has a valid anchor 1:
	 *         otherwise
	 */

	public boolean parseAnchor(List<String> list_tokens, List<String> list_tags, int comma_index, int modal_index) {
		checkNotNull(list_tokens);
		checkNotNull(list_tags);

		int global_object_start_index = -1;

		int anchor_start_index = sentenceAnalyzer.getAnchorStartIndex(list_tokens, comma_index, modal_index);
		List<String> anchor_tokens = list_tokens.subList(anchor_start_index, list_tokens.size());

		if (list_tags.get(modal_index + 1).equals("VB")) {
			List<String> tags_after_modal_verb = list_tags.subList(modal_index, list_tags.size());

			Map<String, String> regex_map = new HashMap<String, String>();
			regexesProvider.getAnchorRegexes().forEach(anchor -> {
				regex_map.put(anchor.getKey_name(), anchor.getRegex());
				regex_map.put(anchor.getKey_name(), anchor.getRegex().toUpperCase());
			});

			String anchor = StringUtils.collectionToDelimitedString(anchor_tokens, " ");
			Span[] spans = matcher.matches(regex_map, anchor);

			// System has a normal verb
			if (spans == null || spans.length == 0) {
				// in passiv form
				int object_start_index = tags_after_modal_verb.indexOf("DT");
				// singular noun
				// if object_start_index <= 4, sentence is "should be provided the blaba"
				if (object_start_index == -1 || object_start_index > 4) {
					object_start_index = tags_after_modal_verb.indexOf("NN");
					// plural noun
					if (object_start_index == -1 || object_start_index > 4) {
						object_start_index = tags_after_modal_verb.indexOf("NNS");
					}
				}
				global_object_start_index = modal_index + object_start_index;
				setObjectStartIndex(global_object_start_index);
				return true;
			}
			// System has one of the provided patterns
			else if (spans.length == 1) {
				// PROVIDE Pattern
				if (spans[0].getType().equals("provide")) {

					// check who is noun
					int index_of_with = anchor_start_index
							+ ListIterate.detectIndex(anchor_tokens, "WITH"::equalsIgnoreCase);
					List<String> who_chunk = sentenceAnalyzer.getChunks(
							list_tokens.subList(modal_index + 2, index_of_with).toArray(new String[0]),
							list_tags.subList(modal_index + 2, index_of_with).toArray(new String[0]));

					for (String chunk : who_chunk) {
						if (!chunk.contains("-NP")) {
							error_logs += "After PROVIDE an Object should be shown. For example USER| <Name>";
							setObjectStartIndex(global_object_start_index);
							return false;
						}
					}

					if (list_tags.get(index_of_with + 4).equals("VB")) {
						// the ability to
						global_object_start_index = index_of_with + 5;
						setObjectStartIndex(global_object_start_index);
						return true;
					} else {
						error_logs += "A Verb must be shown after WITH THE ABILITY TO";
						setObjectStartIndex(global_object_start_index);
						return false;
					}
				} else if (spans[0].getType().equals("be_able_to")) {
					// be able to
					if (list_tags.get(modal_index + 4).equals("VB")) {
						global_object_start_index = modal_index + 5;
						setObjectStartIndex(global_object_start_index);
						return true;
					} else {
						// the sentence is not valid, no verb after to
						error_logs += "A Verb must be shown after BE ABLE TO";
						setObjectStartIndex(global_object_start_index);
						return false;
					}
				}
				// another types of regex and is already in database
				else {
					return true;
				}
			} else {
				// more than one pattern
				error_logs += "The sentence has more than one Pattern";
				setObjectStartIndex(global_object_start_index);
				return false;
			}
		} else {
			error_logs += "No verb after modal verb";
			setObjectStartIndex(global_object_start_index);
			return false;
		}
	}

	/**
	 * This method has the ability to check the objects of sentence
	 * 
	 * @return true: if the sentence has a valid object false: otherwise
	 */

	public boolean parseObject(String[] tokens, String[] tags, int object_start_index) {
		checkNotNull(tokens);
		checkNotNull(tags);

		int object_end_index = -1;
		String[] possible_object_tokens = Arrays.copyOfRange(tokens, object_start_index, tokens.length);
		String possible_object_string = StringUtils.arrayToDelimitedString(possible_object_tokens, " ");
		String object_string = sentenceAnalyzer.getObjects(possible_object_string, possible_object_tokens);

		if (object_string == null || object_string.isEmpty() || object_string.isBlank()) {
			error_logs += "The sentence does not contain any Object.\n";
			return false;
		} else {
			Map<String, String> regexs = new HashMap<String, String>();
			regexesProvider.getObjectRegexes().forEach(object -> {
				regexs.put(object.getKey_name(), object.getRegex());
				regexs.put(object.getKey_name(), object.getRegex().toUpperCase());
			});

			Span[] spans = matcher.matches(regexs, object_string);
			// maybe do not contains all of them
			if (spans == null || spans.length == 0) {
				error_logs += "'" + object_string + "'"
						+ " must be startet with A| AN| THE|ONE| EACH| ALL THE| BETWEEN <A> AND <B>";
				return false;
			}
			// one of the cases
			else if (spans.length == 1) {
				object_end_index = object_start_index + StringUtils.tokenizeToStringArray(object_string, " ").length
						- 1;
				setObjectEndIndex(object_end_index);
				return true;
			}
		}
		return false;
	}

	/**
	 * This method helps to check the details of the sentence
	 * 
	 * @param tokens           List of tokens
	 * @param object_end_index end index of object
	 * @return true: if the sentence has valid details false: otherwise
	 */

	public boolean parseDetails(List<String> tokens, int object_end_index) {
		checkNotNull(tokens);

		if ((object_end_index + 1) >= tokens.size()) {
			return true;
		}

		List<String> details = tokens.subList(object_end_index + 1, tokens.size());
		Map<String, String> regexs = new HashMap<String, String>();
		regexesProvider.getDetailsRegexes().forEach(detail -> {
			regexs.put(detail.getKey_name(), detail.getRegex());
			regexs.put(detail.getKey_name(), detail.getRegex().toUpperCase());
		});
		Span[] spans = matcher.matches(regexs, StringUtils.collectionToDelimitedString(details, " "));

		// detail contains no condition
		if (spans == null || spans.length == 0) {
			return true;
		} else if (spans != null && spans.length == 1) {
			return true;
		}
		return false;
	}

	/**
	 * parse the complete sentence
	 * 
	 * @param sentence: sentence to check
	 * @return true: if the sentence matches with the template false: otherwise
	 */
	private boolean parseTemplateConformance(String sentence) {
		checkNotNull(sentence);

		this.error_logs = "";
		String[] tokens = getTokensFromSentence(sentence);
		String[] tags = getTagsFromTokens(tokens);
		List<String> list_tokens = Arrays.asList(tokens);
		List<String> list_tags = Arrays.asList(tags);

		int comma_index = ListIterate.detectIndex(list_tokens, ","::equals);
		int modal_index = sentenceAnalyzer.getModalIndex(list_tags, comma_index);

		boolean hasModalVerb = parseModalVp(modal_index, list_tokens);
		if (!hasModalVerb) {
			return false;
		} else {
			boolean hasSystemName = parseSystemName(list_tokens, comma_index, modal_index);
			if (!hasSystemName) {
				return false;
			}
			boolean hasAnchor = parseAnchor(list_tokens, list_tags, comma_index, modal_index);
			if (hasAnchor) {
				boolean hasValidCondition = parseCondition(list_tokens, comma_index, modal_index);

				int object_start_index = getObjectStartIndex();
				boolean hasObject = parseObject(tokens, tags, object_start_index);
				int object_end_index = getObjectEndIndex();
				boolean hasDetails = parseDetails(list_tokens, object_end_index);

				if (hasSystemName && hasAnchor && hasValidCondition && hasObject && hasDetails) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
	}

	/**
	 * This method helps to get single sentence from the requirements description
	 * 
	 * @param desc requirements description
	 * @return a String array with sentences
	 * @throws IOException
	 */
	@Override
	public Map<Integer, String> getSentences(String desc) {
		checkNotNull(desc);

		SentenceModel model = null;
		try {
			InputStream inputStream = new FileInputStream("./src/main/resources/models/en-sent.bin");
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
	 * @param sentences map of sentences with indexes
	 * @return a list of map with key-value-pair 1.Map contains all sentences of requirement 2.Map contains all
	 *         compliant and non-compliant sentences with the order as the keys in 1.Map 3.Map contains all logs for the
	 *         non-compliant sentences with the order as the keys in 1.Map
	 */
	@Override
	public List<Map<Integer, String>> doParse(Map<Integer, String> sentences) {
		checkNotNull(sentences);

		Map<Integer, String> map_compliant_sentences = new HashMap<Integer, String>();
		Map<Integer, String> map_logs_for_non_compliant_sentences = new HashMap<Integer, String>();

		sentences.entrySet().forEach(entry -> {
			Integer index = entry.getKey();
			String sentence = entry.getValue();

			boolean isConformance = parseTemplateConformance(sentence);
			String error_logs = this.error_logs;
			if (isConformance) {
				map_compliant_sentences.put(index, "0");
			} else {
				map_compliant_sentences.put(index, "1");
				map_logs_for_non_compliant_sentences.put(index, error_logs);
			}
		});
		List<Map<Integer, String>> result = new LinkedList<Map<Integer, String>>();
		result.add(sentences);
		result.add(map_compliant_sentences);
		result.add(map_logs_for_non_compliant_sentences);

		return result;

	}
}