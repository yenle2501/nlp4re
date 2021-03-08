package com.nlp4re.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.collections.impl.utility.ListIterate;
import org.springframework.util.StringUtils;
import opennlp.tools.util.Span;

/**
 * This class works as the logic class for check the logic
 * 
 * @TODO
 */
public class MazoAndJaramilloLogic {

	private static final String[] MODALS = { "SHOULD", "SHALL", "COULD", "WILL", "MUST" };
	private static final String[] SYSTEM_NAMES = { "ALL", "SOME", "THOSE", "THE" };

	public String error_logs;

	// public String modal_vp;
	// public String system_name;
	// public String process_vp;
	// public String object;
	// public String details;
	// public String conditions;
	//
	// // private int anchor_start_index;
	// private int anchor_end_index;
	// private int modal_index;
	// private int object_end_index;

	private SentenceAnalyzer sentenceAnalyzer = null;
	private PatternMatcher matcher = null;
	// private String[] tokens;
	// private String[] tags;
	//
	// private List<String> list_tokens;
	// private List<String> list_tags;

	// private int index_comma = -1;

	public MazoAndJaramilloLogic() {
		sentenceAnalyzer = new SentenceAnalyzer();
		matcher = new PatternMatcher();
		// error_logs = "";
	}

	/**
	 * tokenize the sentence
	 * 
	 * @param sentence
	 * @throws IOException
	 */
	public List<String[]> tokenizeSentence(String sentence) {
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
	public boolean parseModalVp(int modal_index, List<String> list_tokens) {
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
	public boolean parseSystemName(List<String> list_tokens, int index_comma, int modal_index) {

		List<String> tokens_systemName = sentenceAnalyzer.getSystemName(list_tokens, index_comma, modal_index);

		if (tokens_systemName != null && !tokens_systemName.isEmpty()
				&& Arrays.asList(SYSTEM_NAMES).contains(tokens_systemName.get(0).toUpperCase())) {

			String systemName = StringUtils.collectionToDelimitedString(tokens_systemName, " ");

			Map<String, String> regexs = new HashMap<String, String>();
			regexs.put("all_some", "^ALL|SOME SYSTEMS OF THE [\\w\\s]+");
			regexs.put("those", "^THOSE SYSTEMS OF THE [\\w\\s]+");
			regexs.put("the", "^THE [\\w\\s]+");

			Span[] spans = matcher.matches(regexs, systemName);

			if (spans == null || spans.length != 1) {
				error_logs = "Name of system should be one of following forms:\n"
						+ "ALL|SOME SYSTEMS OF THE <Product line name>\n"
						+ "THOS SYSTEMS OF THE <Product line name> <Restriction>\n" + "THE <System or part name>\n";
				return false;
			}

			int start_index = spans[0].getType().equals("the") ? 1 : 4;
			String[] a = tokens_systemName.toArray(new String[0]);
			String[] tokens_system_name = Arrays.copyOfRange(a, start_index, tokens_systemName.size());
			List<String> tags_systemName = Arrays.asList(sentenceAnalyzer.getPOSTags(tokens_system_name));

			if (spans[0].getType().equals("the") || spans[0].getType().equals("all_some")) {
				// that contains only noun
				if (!tags_systemName.contains("VB")) {
					return true;
				} else {
					error_logs += "No Verb after system name.\n";
					return false;
				}

			} else if (spans[0].getType().equals("those")) {
				return true;
			}
		}
		error_logs = "Name of system should be one of following forms:\n"
				+ "ALL|SOME SYSTEMS OF THE <Product line name>\n"
				+ "THOS SYSTEMS OF THE <Product line name> <Restriction>\n" + "THE <System or part name>\n";
		return false;

	}

	/**
	 * This method has the ability to check the condition of the sentence
	 * 
	 * @return true :if the sentence has no condition or a valid condition false: otherwise
	 */
	public boolean parseCondition(List<String> list_tokens, int index_comma, int modal_index) {

		List<String> conditions = sentenceAnalyzer.getConditions(list_tokens, modal_index, index_comma);
		if (conditions == null || conditions.isEmpty()) {
			return true;
		}

		String contions_str = StringUtils.collectionToDelimitedString(conditions, " ");
		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("if", "^IF.*");
		regexs.put("while_during", "^WHILE|DURING.* ");
		regexs.put("after", "^AFTER|BEFORE|AS SOON AS.* ");
		regexs.put("incase", "^IN CASE.*IS INCLUDED\t");

		Span[] spans = matcher.matches(regexs, contions_str);
		if (spans == null || spans.length != 1) {
			error_logs += "The condtionals should be one of following forms:\n" + "IF <Condition|Event>, THEN\n"
					+ "WHILE|DURING <Activation state>\n" + "IN CASE <Included feature> IS INCLUDED\n"
					+ "AFTER|BEFORE|AS SOON AS <Bahavior>\n";
			return false;
		} else {
			if (spans[0].getType().equals("if")) {
				if (list_tokens.get(index_comma + 1).equals("then")) {
					return true;
				} else {
					error_logs += "The conditionals should be IF <Condition|Event>, THEN\n";
					return false;
				}
			} else if (spans[0].getType().equals("while_during") || spans[0].getType().equals("after")
					|| spans[0].getType().equals("incase")) {
				return true;
			}
		}
		error_logs += "The condtionals should be one of following forms:\n" + "IF <Condition|Event>,THEN\n"
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
	public List<Integer> parseAnchor(List<String> list_tokens, List<String> list_tags, int index_comma,
			int modal_index) {
		int anchor_start_index = sentenceAnalyzer.getAnchorStartIndex(list_tokens, index_comma, modal_index);
		List<String> anchor_tokens = list_tokens.subList(anchor_start_index, list_tokens.size());
		String anchor = StringUtils.collectionToDelimitedString(anchor_tokens, " ");

		List<Integer> result = new LinkedList<Integer>();
		int erg = -1;
		int anchor_end_index = -1;
		if (list_tags.get(modal_index + 1).equals("VB")) {

			Map<String, String> regex_map = new HashMap<String, String>();
			regex_map.put("provide", "PROVIDE [\\w\\s]+ WITH THE ABILITY TO [\\w\\s]");
			regex_map.put("be_able_to", "BE ABLE TO");

			Span[] spans = matcher.matches(regex_map, anchor);

			if (spans == null || spans.length == 0) {
				// System has a normal verb
				/**
				 * iwie nict richtig, vllt verb hat 2 woerter or in passiv phrase e.x : should be created
				 */
				anchor_end_index = modal_index + 2;
				erg = 0;
				// true
			} else if (spans.length == 1) {

				int index_of_with = anchor_start_index
						+ ListIterate.detectIndex(anchor_tokens, "WITH"::equalsIgnoreCase);

				// System.out.println("index with " + index_of_with );

				// PROVIDE Pattern
				if (spans[0].getType().equals("provide")) {
					// System.out.println("from with: " + list_tokens.subList(modal_index + 2, index_of_with));
					// check who is noun
					List<String> who_chunk = sentenceAnalyzer.getChunks(
							list_tokens.subList(modal_index + 2, index_of_with).toArray(new String[0]),
							list_tags.subList(modal_index + 2, index_of_with).toArray(new String[0]));

					for (String action : who_chunk) {
						// System.out.println("228 action " + action);
						if (!action.contains("-NP")) {
							error_logs += "After PROVIDE an Object should be shown. For example USER| <Name>\n";
							erg = 1;
							break;
							// return false;
						}
					}

					// check verb after "with the ability to"
					/**
					 * TODO muss check chunk
					 */
					// System.out.println("239 anchor tag " + anchor_tags.toString());

					if (list_tags.get(index_of_with + 4).equals("VB")) {
						anchor_end_index = modal_index + (index_of_with - modal_index) + 5;
						// return true;
						erg = 0;
					} else {
						error_logs += "A Verb must be shown after WITH THE ABILITY.\n";
						// return false;
						erg = 1;
					}
				} else if (spans[0].getType().equals("be_able_to")) {
					/**
					 * TODO muss check chunk
					 */
					if (list_tags.get(modal_index + 4).equals("VB")) {
						anchor_end_index = modal_index + 5;
						// return true;
						erg = 0;
					} else {
						// the sentence is not valid, no verb after to
						error_logs += "A Verb must be shown after TO BE ABLE TO.\n";
						erg = 1;
					}
				}
			} else {
				// more than one pattern
				error_logs += "The sentence has more than one Pattern.\n";
				erg = 1;
			}
		}
		// do not have normal verb
//		error_logs += "There is no verb after modal verb\n";
//		erg = 1;

		result.add(erg);
		result.add(anchor_end_index);
		return result;
	}

	/**
	 * This method has the ability to check the objects of sentence
	 * 
	 * @return true: if the sentence has a valid object false: otherwise
	 */
	public boolean parseObject(String[] tokens, String[] tags, int anchor_end_index, int object_end_index) {

		String[] possible_object_tokens = Arrays.copyOfRange(tokens, anchor_end_index, tokens.length);
		String possible_object_string = StringUtils.arrayToDelimitedString(possible_object_tokens, " ");

		String object_string = sentenceAnalyzer.getObjects(possible_object_string, possible_object_tokens);

		if (object_string.equals("") || object_string == null) {
			return true;
		}

		// System.out.println("OBJECT:" + object_string);

		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("single_obj", "^A |^AN |^THE |^ONE |^EACH +");
		regexs.put("between", "^BETWEEN * AND +");
		regexs.put("all_the", "^ALL THE +");

		/***
		 * TODO muss noch angepasst werden, IWIE nicht richtig
		 */
		Span[] spans = matcher.matches(regexs, object_string);

		// maybe do not contains all of them
		if (spans == null || spans.length == 0) {
			// object_end_index = anchor_end_index + object_tokens.length;
			error_logs += object_string + " must be startet with A| AN| THE|ONE| EACH| ALL THE| BETWEEN <A> AND <B>.\n";
			return false;
		}
		// one of the cases
		else if (spans.length == 1) {
			// String[] object_tokens = StringUtils.tokenizeToStringArray(object_string, "
			// ").length;//sentenceAnalyzer.getTokens(object_string);
			object_end_index = anchor_end_index + StringUtils.tokenizeToStringArray(object_string, " ").length - 1;
			return true;
		}

		error_logs += object_string + " must be startet with A| AN| THE|ONE| EACH| ALL THE| BETWEEN <A> AND <B>.\n";
		return false;
	}

	/**
	 *
	 */
	public boolean parseDetails(String[] tokens, int object_end_index) {
		if ((object_end_index + 1) == tokens.length) {
			return true;
		}

		String[] details = Arrays.copyOfRange(tokens, object_end_index + 1, tokens.length);
		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("condition", "IF AND ONLY IF+");
		Span[] spans = matcher.matches(regexs, StringUtils.arrayToDelimitedString(details, " "));

		// detail contains no condition
		if (spans.length == 0) {
		} else if (spans.length == 1) {
			if (spans[0].getType().equals("condition")) {
				System.out.println("DETAILS: " + Arrays.toString(details));
				return true;
			}
		}
		return false;
	}

	/**
	 * parse the complete sentence
	 */
	public boolean parseTemplateConformance(String sentence) {

		error_logs = "";

		List<String[]> tokens_tags = tokenizeSentence(sentence);
		String[] tokens = tokens_tags.get(0);
		String[] tags = tokens_tags.get(1);

		List<String> list_tokens = Arrays.asList(tokens);
		List<String> list_tags = Arrays.asList(tags);

		int index_comma = ListIterate.detectIndex(list_tokens, ","::equals);
		int modal_index = sentenceAnalyzer.getModalIndex(list_tags, index_comma);

		long a = System.currentTimeMillis();
		boolean hasModalVerb = parseModalVp(modal_index, list_tokens);
		long b = System.currentTimeMillis();
		System.out.println("TIME 1: " + (b - a));

		if (!hasModalVerb) {
			// System.out.println(" 395 no modal");
			return false;
		} else {
			long c = System.currentTimeMillis();
			boolean hasSystemName = parseSystemName(list_tokens, index_comma, modal_index);
			long d = System.currentTimeMillis();
			System.out.println("TIME 2: " + (d - c));
			if (!hasSystemName) {
				return false;
			}
			long e = System.currentTimeMillis();
			List<Integer> parAnchor = parseAnchor(list_tokens, list_tags, index_comma, modal_index);

			boolean hasAnchor = parAnchor.get(0) == 0 ? true : false;
			int anchor_index = parAnchor.get(1);

			long f = System.currentTimeMillis();
			System.out.println("TIME 3: " + (f - e));

			long g = System.currentTimeMillis();
			boolean isValidCondition = parseCondition(list_tokens, index_comma, modal_index);
			long h = System.currentTimeMillis();
			System.out.println("TIME 4: " + (h - g));

			if (hasAnchor) {
				// System.out.println(" 402 ancho ");

				long l = System.currentTimeMillis();

				int object_end_index = -1;
				boolean hasObject = parseObject(tokens, tags, anchor_index, object_end_index);

				long k = System.currentTimeMillis();
				System.out.println("TIME 5: " + (k - l));

				boolean isConformantSegment = hasModalVerb && hasAnchor && isValidCondition;

				if (isConformantSegment) {
					// System.out.println(" 407");
					long t = System.currentTimeMillis();
					boolean hasDetails = parseDetails(tokens, object_end_index);
					long r = System.currentTimeMillis();
					System.out.println("TIME 6: " + (r - t));

				}

				if (!hasModalVerb || !hasAnchor || !isValidCondition || !isConformantSegment) {
					// System.out.println(" 412");
					return false;
				} else {
					// System.out.println(" 415");
					return true;
				}
			}
			// System.out.println(" 418");
			return false;
		}
	}

}