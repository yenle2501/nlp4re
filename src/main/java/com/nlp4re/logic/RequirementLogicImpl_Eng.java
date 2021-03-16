package com.nlp4re.logic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.util.StringUtils;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

/**
 * This class works as the logic class for check the logic
 */
public class RequirementLogicImpl_Eng implements RequirementLogic {

	private static final String[] MODALS = { "SHOULD", "SHALL", "COULD", "WILL", "MUST" };
	private static final String[] SYSTEM_NAMES = { "ALL", "SOME", "THOSE", "THE" };

	private String error_logs;
	private SentenceAnalyzer sentenceAnalyzer;
	private PatternMatcher matcher;

	public RequirementLogicImpl_Eng(SentenceAnalyzer sentenceAnalyzer, PatternMatcher matcher) {
		// this.sentenceAnalyzer = new SentenceAnalyzer();
		// this.matcher = new PatternMatcher();
		this.error_logs = "";
		this.sentenceAnalyzer = sentenceAnalyzer;
		this.matcher = matcher;
	}

	/**
	 * tokenizes the sentence
	 * 
	 * @param sentence sentence to tokenize
	 * @return List of tokens and tags of sentence
	 */

	private List<String[]> tokenizeSentence(String sentence) {
		checkNotNull(sentence);

		String[] tokens = sentenceAnalyzer.getTokens(sentence);
		String[] tags = sentenceAnalyzer.getPOSTags(tokens);
		List<String[]> result = new LinkedList<String[]>();
		result.add(tokens);
		result.add(tags);
		return result;
	}

	/**
	 * Checks the modal verb of the sentence
	 * 
	 * @return true: if the sentence contains one of the proposed modal verbs such as shall, should, could false:
	 *         otherwise
	 */
	private boolean parseModalVp(int modal_index, List<String> list_tokens) {
		checkNotNull(list_tokens);

		if (modal_index == -1) {
			error_logs = "The sentence does not contain any modal verbs. The modal verbs should be SHOULD, SHALL, COULD, WILL, MUST.\n";
			return false;
		} else {
			String modal_vp = list_tokens.get(modal_index);

			if (Arrays.asList(MODALS).contains(modal_vp.toUpperCase())) {
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
	 * @return true : if the sentence has a valid name of system false: otherwise
	 */

	private boolean parseSystemName(List<String> list_tokens, int comma_index, int modal_index) {
		checkNotNull(list_tokens);

		List<String> tokens_possible_systemName = sentenceAnalyzer.getSystemName(list_tokens, comma_index, modal_index);

		if (tokens_possible_systemName != null && !tokens_possible_systemName.isEmpty()
				&& Arrays.asList(SYSTEM_NAMES).contains(tokens_possible_systemName.get(0).toUpperCase())) {

			Map<String, String> regexs = new HashMap<String, String>();
			regexs.put("all_some", "^ALL|SOME SYSTEMS OF THE[\\w\\s]+");
			regexs.put("those", "^THOSE SYSTEMS OF THE[\\w\\s]+");
			regexs.put("the", "^THE[\\w\\s]+");
			regexs.put("all_some", "^all|some systems of the [\\w\\s]+");
			regexs.put("those", "^those systems of the [\\w\\s]+");
			regexs.put("the", "^the [\\w\\s]+");

			String systemName = StringUtils.collectionToDelimitedString(tokens_possible_systemName, " ");

			Span[] spans = matcher.matches(regexs, systemName);
			// No patterns are matched
			if (spans == null || spans.length != 1) {
				error_logs = "System name should be one of the following forms:\n"
						+ "ALL|SOME SYSTEMS OF THE <Product line name>\n"
						+ "THOSE SYSTEMS OF THE <Product line name> <Restriction>\n" + "THE <System or part name>\n";
				return false;
			}
			// one of the patterns is matched
			else if (spans.length == 1) {
				// if it is 'the' pattern, the start index of system name is one, otherwise it is 4 (for example: 'some
				// systems of the')
				int start_index = spans[0].getType().equals("the") ? 1 : 4;
				String[] a = tokens_possible_systemName.toArray(new String[0]);
				String[] tokens_system_name = Arrays.copyOfRange(a, start_index, tokens_possible_systemName.size());
				List<String> tags_system_name = Arrays.asList(sentenceAnalyzer.getPOSTags(tokens_system_name));

				if (spans[0].getType().equals("the") || spans[0].getType().equals("all_some")) {
					// that contains only noun
					if (!tags_system_name.contains("VB")) {
						return true;
					} else {
						error_logs += "No Verb after system name.\n";
						return false;
					}

				} else if (spans[0].getType().equals("those")) {
					// cause of restriction, the sentence can contain verb
					return true;
				}
			}
		}
		error_logs = "System name should be one of the following forms:\n"
				+ "ALL|SOME SYSTEMS OF THE <Product line name>\n"
				+ "THOSE SYSTEMS OF THE <Product line name> <Restriction>\n" + "THE <System or part name>\n";
		return false;

	}

	/**
	 * This method has the ability to check the condition of the sentence
	 * 
	 * @return true :if the sentence has no condition or a valid condition false: otherwise
	 */

	private boolean parseCondition(List<String> list_tokens, int comma_index, int modal_index) {
		checkNotNull(list_tokens);

		List<String> token_conditions = sentenceAnalyzer.getConditions(list_tokens, modal_index, comma_index);
		// it is not required that the sentence has a condition
		if (token_conditions == null || token_conditions.isEmpty()) {
			return true;
		}

		Map<String, String> regexes = new HashMap<String, String>();
		regexes.put("if", "^IF+");
		regexes.put("while_during", "^WHILE|DURING+ ");
		regexes.put("after", "^AFTER|BEFORE|AS SOON AS+ ");
		regexes.put("incase", "^IN CASE [:alpha:] IS INCLUDED+");
		regexes.put("if", "^if+");
		regexes.put("while_during", "^while|during+ ");
		regexes.put("after", "^after|before|as soon as+ ");
		regexes.put("incase", "^in case [:alpha:] is included+");

		String condition = StringUtils.collectionToDelimitedString(token_conditions, " ");
		Span[] spans = matcher.matches(regexes, condition);

		if (spans == null || spans.length != 1) {
			error_logs += "The condtions should be one of the following forms:\n" + "IF <Condition|Event>, THEN\n"
					+ "WHILE|DURING <Activation state>\n" + "IN CASE <Included feature> IS INCLUDED\n"
					+ "AFTER|BEFORE|AS SOON AS <Bahavior>\n";
			return false;
		} else {
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
		error_logs += "The condtions should be one of following forms:\n" + "IF <Condition|Event>,THEN\n"
				+ "WHILE|DURING <Activation state>\n" + "IN CASE <Included feature> IS INCLUDED\n"
				+ "AFTER|BEFORE| AS SOON AS <Bahavior>\n";
		return false;
	}

	/**
	 * Anchor should contain SYSTEM NAME + MODAL VERB + NORMAL VERB This method has the ability to check the anchor of
	 * the sentence.
	 * 
	 * @return true: if the sentence has a valid anchor false: otherwise
	 */

	private List<Integer> parseAnchor(List<String> list_tokens, List<String> list_tags, int comma_index,
			int modal_index) {
		checkNotNull(list_tokens);
		checkNotNull(list_tags);

		List<Integer> result = new LinkedList<Integer>();
		int erg = -1;
		int global_object_start_index = -1;

		int anchor_start_index = sentenceAnalyzer.getAnchorStartIndex(list_tokens, comma_index, modal_index);
		List<String> anchor_tokens = list_tokens.subList(anchor_start_index, list_tokens.size());

		if (list_tags.get(modal_index + 1).equals("VB")) {
			List<String> tags_after_modal_verb = list_tags.subList(modal_index, list_tags.size());

			Map<String, String> regex_map = new HashMap<String, String>();
			regex_map.put("provide", "PROVIDE [\\w\\s]+ WITH THE ABILITY TO [\\w\\s]");
			regex_map.put("be_able_to", "BE ABLE TO+");
			regex_map.put("provide", "provide [\\w\\s]+ with the ability to [\\w\\s]");
			regex_map.put("be_able_to", "be able to +");

			String anchor = StringUtils.collectionToDelimitedString(anchor_tokens, " ");
			Span[] spans = matcher.matches(regex_map, anchor);

			// System has a normal verb
			if (spans == null || spans.length == 0) {
				// in passiv form
				int object_start_index = tags_after_modal_verb.indexOf("DT");
				if (object_start_index == -1 || object_start_index > 4) {
					object_start_index = tags_after_modal_verb.indexOf("NN");
					if (object_start_index == -1 || object_start_index > 4) {
						object_start_index = tags_after_modal_verb.indexOf("NNS");
					}
				}
				global_object_start_index = modal_index + object_start_index;
				erg = 0;
			} else if (spans.length == 1) {
				// PROVIDE Pattern
				if (spans[0].getType().equals("provide")) {
					// check who is noun
					int index_of_with = anchor_start_index
							+ ListIterate.detectIndex(anchor_tokens, "WITH"::equalsIgnoreCase);
					List<String> who_chunk = sentenceAnalyzer.getChunks(
							list_tokens.subList(modal_index + 2, index_of_with).toArray(new String[0]),
							list_tags.subList(modal_index + 2, index_of_with).toArray(new String[0]));

					for (String action : who_chunk) {
						if (!action.contains("-NP")) {
							error_logs += "After PROVIDE an Object should be shown. For example USER| <Name>\n";
							erg = 1;
							break;
						}
					}
					if (list_tags.get(index_of_with + 4).equals("VB")) {
						// the ability to
						global_object_start_index = index_of_with + 5;
						erg = 0;
					} else {
						error_logs += "A Verb must be shown after WITH THE ABILITY TO.\n";
						erg = 1;
					}
				} else if (spans[0].getType().equals("be_able_to")) {
					// be able to
					if (list_tags.get(modal_index + 4).equals("VB")) {
						global_object_start_index = modal_index + 5;
						erg = 0;
					} else {
						// the sentence is not valid, no verb after to
						error_logs += "A Verb must be shown after BE ABLE TO.\n";
						erg = 1;
					}
				}
			} else {
				// more than one pattern
				error_logs += "The sentence has more than one Pattern.\n";
				erg = 1;
			}
		} else {
			error_logs += "No verb after modal verb.\n";
			erg = 1;
		}
		// do not have normal verb
		result.add(erg);
		result.add(global_object_start_index);

		return result;
	}

	/**
	 * This method has the ability to check the objects of sentence
	 * 
	 * @return true: if the sentence has a valid object false: otherwise
	 */

	private List<Integer> parseObject(String[] tokens, String[] tags, int object_start_index) {
		checkNotNull(tokens);
		checkNotNull(tags);

		int erg = -1;
		int object_end_index = -1;
		String[] possible_object_tokens = Arrays.copyOfRange(tokens, object_start_index, tokens.length);
		String possible_object_string = StringUtils.arrayToDelimitedString(possible_object_tokens, " ");
		String object_string = sentenceAnalyzer.getObjects(possible_object_string, possible_object_tokens);

		if (object_string == null || object_string.isEmpty() || object_string.isBlank()) {
			error_logs += "The sentence does not contain any Object.\n";
			erg = 1;
		} else {
			Map<String, String> regexs = new HashMap<String, String>();
			regexs.put("single_obj", "^A |^AN |^THE |^ONE |^EACH +");
			regexs.put("between", "^BETWEEN  [:alpha:] AND +");
			regexs.put("all_the", "^ALL THE +");
			regexs.put("single_obj", "^a |^an |^the |^one |^each +");
			regexs.put("between", "^between [:alpha:] and +");
			regexs.put("all_the", "^all the +");

			Span[] spans = matcher.matches(regexs, object_string);
			// maybe do not contains all of them
			if (spans == null || spans.length == 0) {
				error_logs += "'" + object_string + "'"
						+ " must be startet with A| AN| THE|ONE| EACH| ALL THE| BETWEEN <A> AND <B>.\n";
				erg = 1;
			}
			// one of the cases
			else if (spans.length == 1) {
				object_end_index = object_start_index + StringUtils.tokenizeToStringArray(object_string, " ").length
						- 1;
				erg = 0;
			}
		}
		List<Integer> result = new LinkedList<Integer>();
		result.add(erg);
		result.add(object_end_index);

		return result;
	}

	/**
	 * This method helps to check the details of the sentence
	 * 
	 * @param tokens           List of tokens
	 * @param object_end_index end index of object
	 * @return true: if the sentence has valid details false: otherwise
	 */

	private boolean parseDetails(List<String> tokens, int object_end_index) {
		checkNotNull(tokens);

		if ((object_end_index + 1) == tokens.size()) {
			return true;
		}

		List<String> details = tokens.subList(object_end_index + 1, tokens.size());

		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("condition", "IF AND ONLY IF+");
		regexs.put("condition", "if and only if+");
		Span[] spans = matcher.matches(regexs, StringUtils.collectionToDelimitedString(details, " "));

		// detail contains no condition
		if (spans.length == 0) {
			return true;
		} else if (spans.length == 1) {
			if (spans[0].getType().equals("condition")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * parse the complete sentence
	 * 
	 * @param sentence: sentence to check
	 * @return true: if the sentence does not match with the template false: otherwise
	 */
	private boolean parseTemplateConformance(String sentence) {
		checkNotNull(sentence);

		this.error_logs = "";

		List<String[]> tokens_tags = tokenizeSentence(sentence);
		String[] tokens = tokens_tags.get(0);
		String[] tags = tokens_tags.get(1);
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
			List<Integer> parAnchor = parseAnchor(list_tokens, list_tags, comma_index, modal_index);
			boolean hasAnchor = parAnchor.get(0) == 0 ? true : false;
			if (hasAnchor) {
				int object_start_index = parAnchor.get(1);
				boolean hasValidCondition = parseCondition(list_tokens, comma_index, modal_index);

				List<Integer> parObject = parseObject(tokens, tags, object_start_index);
				boolean hasObject = parObject.get(0) == 0 ? true : false;
				int object_end_index = parObject.get(1);

				boolean hasDetails = parseDetails(list_tokens, object_end_index);

				if (hasModalVerb && hasSystemName && hasAnchor && hasValidCondition && hasObject && hasDetails) {
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
	 *         compliant and non-compliant sentences with the order as the keys in 1.Map 3.Map contains all logs for the
	 *         non-compliant sentences with the order as the keys in 1.Map
	 */
	@Override
	public List<Map<Integer, String>> doParse(Map<Integer, String> sentences) {
		checkNotNull(sentences);

		Map<Integer, String> map_compliant_sentences = new HashMap<Integer, String>();
		Map<Integer, String> map_logs_for_non_compliant_sentences = new HashMap<Integer, String>();

		sentences.entrySet().stream().forEach(entry -> {
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