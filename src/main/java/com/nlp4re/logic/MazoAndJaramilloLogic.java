package com.nlp4re.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

public class MazoAndJaramilloLogic {

	private static final String[] MODALS = { "SHOULD", "SHALL", "COULD", "WILL", "MUST" };
	private static final String[] SYSTEM_NAMES = { "ALL", "SOME", "THOSE", "THE" };

	public String modal_vp;
	public String system_name;
	public String process_vp;
	public String object;
	public String details;
	public String conditions;
	public String anchor;

	public boolean isValidSentence;
	public boolean isConformantSegment;
	public boolean isValidObject;
	private boolean isValidCondition;
	private boolean isValidDetails;

	private boolean hasModalVerb;
	private boolean hasSystemName;
	private boolean hasCondition;
	private boolean hasAnchor;
	private boolean hasDetails;

	private int anchor_start_index;
	private int anchor_end_index;
	private int modal_index;
	private int object_end_index;

	private SentenceAnalyzer sentenceAnalyzer = null;
	private PatternMatcher matcher = null;
	private String sentence;
	private String[] tokens;
	private String[] tags;

	public MazoAndJaramilloLogic() {
		sentenceAnalyzer = new SentenceAnalyzer();
		matcher = new PatternMatcher();

		hasModalVerb = false;
		hasSystemName = false;
		hasCondition = false;
		hasAnchor = false;

		isValidCondition = false;
	}

	public void first(String sentence) throws IOException {
		this.sentence = sentence;
		this.tokens = sentenceAnalyzer.getTokens(sentence);
		this.tags = sentenceAnalyzer.getPOSTags(tokens);
	}

	public boolean parseCondition() throws IOException {

		String[] conditions = sentenceAnalyzer.getConditions(tokens, tags);
		if (conditions == null || conditions.length <= 0) {
			hasCondition = false;
			isValidCondition = true;
			return false;
		}
		String contions_str = StringUtils.arrayToDelimitedString(conditions, " ");
		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("if", "^IF.*");
		regexs.put("while_during", "^WHILE|DURING.* ");
		regexs.put("after", "^AFTER|BEFORE|AS SOON AS.* ");
		regexs.put("incase", "^IN CASE.*IS INCLUDED\t");

		Span[] spans = matcher.matches(regexs, contions_str);

//		System.out.println("CONDITION : " + contions_str);

		if (spans == null || spans.length != 1) {
			isValidCondition = false;
			hasCondition = false;
			return false;
		} else {
			// String[] tags_systemName = sentenceAnalyzer.getPOSTags(conditions);

			if (spans[0].getType().equals("if")) {
				int token_index = Arrays.asList(tokens).indexOf(",");

				if (tokens[token_index + 1].equals("then")) {
					isValidCondition = true;
					hasCondition = true;
					return true;
				} else {
					// after if should have then
					isValidCondition = false;
					hasCondition = false;
					return false;
				}
			} else if (spans[0].getType().equals("while_during") || spans[0].getType().equals("after")
					|| spans[0].getType().equals("incase")) {
				isValidCondition = true;
				hasCondition = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * parse system name
	 * 
	 * @return true: false:
	 */
	public boolean parseSystemName() throws IOException {

		String[] tokens_systemName = sentenceAnalyzer.getSystemName(tokens, tags);

		if (tokens_systemName == null || tokens_systemName.length == 0) {
			/**
			 * TODO: Log: Sentence does not have systemname
			 */
			return false;
		} else {

			if (Arrays.asList(SYSTEM_NAMES).contains(tokens_systemName[0].toUpperCase())) {

				String systemName = StringUtils.arrayToDelimitedString(tokens_systemName, " ");

				Map<String, String> regexs = new HashMap<String, String>();
				regexs.put("all_some", "^ALL|SOME SYSTEMS OF THE [\\w\\s]+");
				regexs.put("those", "^THOSE SYSTEMS OF THE [\\w\\s]+");
				regexs.put("the", "^THE [\\w\\s]+");

				Span[] spans = matcher.matches(regexs, systemName);

				// no sugession pattern or more than one pattern
				if (spans == null || spans.length != 1) {
					return false;
				}

				int start_index = spans[0].getType().equals("the") ? 1 : 4;
				String[] tokens_system_name = Arrays.copyOfRange(tokens_systemName, start_index,
						tokens_systemName.length);
				String[] tags_systemName = sentenceAnalyzer.getPOSTags(tokens_system_name);

				if (spans[0].getType().equals("the") || spans[0].getType().equals("all_some")) {
					// that contains only noun
					if (!Arrays.asList(tags_systemName).contains("VB")) {
//						System.out.println("SYSTEM NAME : " + systemName);
						return true;
					} else {
						return false;
					}

				} else if (spans[0].getType().equals("those")) {
					// must contains verb because of restrictions
					if (Arrays.asList(tags_systemName).contains("VB")) {
//						System.out.println("SYSTEM NAME : " + systemName);
						return true;
					} else {
						return false;
					}
				}

			} else {
				 /** TODO: Log, do not contain any proposed name
				 */
				return false;
			}
		}
		return false;

	}

	/**
	 * parse modal verbs
	 * 
	 * @return true: if the sentence contains one of the proposed modal verbs such as shall, should, could false:
	 *         otherwise
	 */
	public boolean parseModalVp() {
		List<String> modals = sentenceAnalyzer.getModalVp(tags, tokens);

		if (modals.size() < 1) {
			return false;
		}

		String modal_vp = modals.get(0);
		if (Arrays.asList(MODALS).contains(modal_vp.toUpperCase())) {
//			System.out.println("MODAL VERB: " + modal_vp);
			this.modal_vp = modal_vp;
			this.hasModalVerb = true;
			this.modal_index = Arrays.asList(tags).indexOf("MD");
			return true;
		}

		return false;
	}

	/**
	 * parse anchor
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean parseAnchor() throws IOException {
		assert this.hasModalVerb;

		this.anchor_start_index = sentenceAnalyzer.getAnchorStartIndex(tags, tokens);
		String[] anchor_tokens = Arrays.copyOfRange(tokens, anchor_start_index, tokens.length);
		String[] anchor_tags = sentenceAnalyzer.getPOSTags(anchor_tokens);
		anchor = StringUtils.arrayToDelimitedString(anchor_tokens, " ");

		if (tags[modal_index + 1].equals("VB")) {

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

//				System.out.println("ANCHOR:" + StringUtils
//						.arrayToDelimitedString(Arrays.copyOfRange(tokens, anchor_start_index, anchor_end_index), " "));
				hasAnchor = true;
				return true;
			} else if (spans.length == 1) {
				List<String> list_anchor_tags = Arrays.asList(anchor_tags);

				int index_of_modal = list_anchor_tags.indexOf("MD");
				int index_of_with = sentenceAnalyzer.getTokenIndex(anchor_tags, anchor_tokens, "IN", "WITH",
						StringUtils.tokenizeToStringArray("WITH THE ABILITY TO", " "), false);

				// PROVIDE Pattern
				if (spans[0].getType().equals("provide")) {
					// check who is noun
					String[] who_chunk = sentenceAnalyzer.getChunks(
							Arrays.copyOfRange(anchor_tokens, index_of_modal + 2, index_of_with),
							Arrays.copyOfRange(anchor_tags, index_of_modal + 2, index_of_with));

					for (int i = 0; i < who_chunk.length; i++) {
						if (!who_chunk[i].contains("-NP")) {
							hasAnchor = false;
							return false;
						}
					}
					// check verb after "with the ability to"
					/**
					 * TODO muss check chunk
					 */
					if (anchor_tags[index_of_with + 4].equals("VB")) {
						anchor_end_index = modal_index + (index_of_with - index_of_modal) + 5;
//						System.out.println("ANCHOR PROVIDE:" + StringUtils.arrayToDelimitedString(
//								Arrays.copyOfRange(tokens, anchor_start_index, anchor_end_index), " "));
						hasAnchor = true;
						return true;
					} else {
						return false;
					}
				} else if (spans[0].getType().equals("be_able_to")) {
					/**
					 * TODO muss check chunk
					 */
					if (tags[modal_index + 4].equals("VB")) {

						// if (tokens[modal_index + 4].equals("FROM") || tokens[modal_index + 4].equals("TOWARDS")) {
						//
						// }
						// // String[] anchor = Arrays.copyOfRange(tokens,);
						anchor_end_index = modal_index + 5;
//						System.out.println("ANCHOR:" + StringUtils.arrayToDelimitedString(
//								Arrays.copyOfRange(tokens, anchor_start_index, anchor_end_index), " "));
						hasAnchor = true;
						return true;
					} else {
						// the sentence is not valid, no verb after to

						hasAnchor = false;
						return false;
					}
				}
			} else {
				// more than one pattern
				hasAnchor = false;
				return false;
			}
		}
		// do not have normal verb
		hasAnchor = false;
		return false;
	}

	public boolean isValidSentence() {
		if (hasAnchor) {
			isValidSentence = true;
			return true;
		}
		isValidSentence = false;
		return false;

	}

	public boolean parseObject() throws IOException {
		assert hasAnchor == true;

		String[] possible_object_tokens = Arrays.copyOfRange(tokens, anchor_end_index, tokens.length);
		String possible_object_string = StringUtils.arrayToDelimitedString(possible_object_tokens, " ");

		String object_string = sentenceAnalyzer.getObjects(possible_object_string);

		if (object_string.equals("") || object_string == null) {
			isValidObject = true;
		}

		String[] object_tokens = sentenceAnalyzer.getTokens(object_string);

//		System.out.println("OBJECT:" + object_string);

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

			object_end_index = anchor_end_index + object_tokens.length;
			isValidObject = true;
		}
		// one of the cases
		else if (spans.length == 1) {
			object_end_index = anchor_end_index + object_tokens.length - 1;
			isValidObject = true;

			return true;
		}

		isValidObject = false;
		return false;
	}

	/**
	 * this method checks for the validation of segment that should check whether the sentence has anchor, modal verb
	 * and valid condition ( or without condition)
	 */
	public boolean parseConformantSegment() {
		if (hasModalVerb && hasAnchor && isValidCondition) {
			isConformantSegment = true;
			return true;
		}
		return false;
	}

	/**
	 *
	 */
	public boolean parseDetails() {
		assert isConformantSegment;

		if ((object_end_index + 1) == tokens.length) {
			hasDetails = false;
			isValidDetails = true;
			return true;
		}

		String[] details = Arrays.copyOfRange(tokens, object_end_index + 1, tokens.length);

//		System.out.println("DETAILS: " + Arrays.toString(details));

		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("condition", "IF AND ONLY IF+");
		Span[] spans = matcher.matches(regexs, StringUtils.arrayToDelimitedString(details, " "));
		
		// detail contains no condition
		if(spans.length == 0) {
			
		}
		else if (spans.length == 1) {
			if(spans[0].getType().equals("condition")) {
//				System.out.println("DETAILS: " + Arrays.toString(details));
				hasDetails = true;
				isValidDetails = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * parse the complete sentence
	 */
	public boolean parseTemplateConformance() {
		if (!hasModalVerb || !hasAnchor || !isValidCondition || !isConformantSegment) {
			return false;
		}
		return true;
	}

}
