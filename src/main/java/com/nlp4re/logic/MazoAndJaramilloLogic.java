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

	private static final String[] MODALS = { "SHOULD", "SHALL", "COULD" };
	private static final String[] CONDITIONS = { "IF", "WHILE", "DURING", "IN CASE", "AFTER", "BEFORE", "AS SOON AS" };
	private static final String[] SYSTEM_NAMES = { "ALL", "SOME", "THOSE", "THE" };

	public String modal_vp;
	public int modal_vp_index;
	public String system_name;
	public String process_vp;
	public String object;
	public String details;
	public String conditions;

	public boolean is_multiple_modals;
	public boolean is_valid_sentence;

	private SentenceAnalyzer sentenceAnalyzer = null;
	private String sentence;
	private String[] tokens;
	private String[] tags;

	public MazoAndJaramilloLogic() {

		sentenceAnalyzer = new SentenceAnalyzer();
	}

	public void first(String sentence) throws IOException {
		this.sentence = sentence;
		this.tokens = sentenceAnalyzer.getTokens(sentence);
		this.tags = sentenceAnalyzer.getPOSTags(tokens);
	}

	public boolean parseCondition() throws IOException {

		String[] conditions = sentenceAnalyzer.getConditions(tokens);
		if (conditions == null || conditions.length <= 0) {
			return true;
		}
		String contions_str = StringUtils.arrayToDelimitedString(conditions, " ");
		Pattern if_pattern = Pattern.compile("^IF.*", Pattern.CASE_INSENSITIVE);
		Pattern while_during_pattern = Pattern.compile("^WHILE|DURING.* ", Pattern.CASE_INSENSITIVE);
		Pattern incase_pattern = Pattern.compile("^IN CASE.*IS INCLUDED\t", Pattern.CASE_INSENSITIVE);
		Pattern after_assoonas_before_pattern = Pattern.compile("^AFTER|BEFORE|AS SOON AS.* ",
				Pattern.CASE_INSENSITIVE);

		Pattern[] while_during_after_before_assoonas = new Pattern[] { while_during_pattern,
				after_assoonas_before_pattern };
		Pattern[] if_ps = new Pattern[] { if_pattern };
		Pattern[] in_case = new Pattern[] { incase_pattern };

		Map<String, Pattern[]> regexMap = new HashMap<>();
		regexMap.put("while_during_after", while_during_after_before_assoonas);
		regexMap.put("if", if_ps);
		regexMap.put("incase", in_case);

		System.out.println("condition : " + contions_str);

		RegexNameFinder finder = new RegexNameFinder(regexMap);
		Span[] span = finder.find(contions_str);

		if (span == null || span.length != 1) {
			return false;
		} else {
			String[] tags_systemName = sentenceAnalyzer.getPOSTags(conditions);
			System.out.println("FOUND CONDITION. " + span[0].toString());
			return true;
		}
	}

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

				Pattern all_some = Pattern.compile("^ALL|SOME SYSTEMS OF THE.{1,1}", Pattern.CASE_INSENSITIVE);
				Pattern those = Pattern.compile("^THOSE SYSTEMS OF THE.{1,1}", Pattern.CASE_INSENSITIVE);
				Pattern the = Pattern.compile("^THE.{1,1}", Pattern.CASE_INSENSITIVE);

				Pattern[] all_some_pattern = new Pattern[] { all_some };
				Pattern[] those_pattern = new Pattern[] { those };
				Pattern[] the_pattern = new Pattern[] { the };

				Map<String, Pattern[]> regexMap = new HashMap<>();
				regexMap.put("all_some", all_some_pattern);
				regexMap.put("those", those_pattern);
				regexMap.put("the", the_pattern);

				RegexNameFinder finder = new RegexNameFinder(regexMap);
				Span[] spans = finder.find(systemName);

				// no sugession pattern or more than one pattern
				if (spans == null || spans.length != 1) {
					return false;
				}

				int start_index = spans[0].getType().equals("the") ? 1 : 4;
				String[] tokens_actual_name = Arrays.copyOfRange(tokens_systemName, start_index,
						tokens_systemName.length);
				String[] tags_systemName = sentenceAnalyzer.getPOSTags(tokens_actual_name);

				if (spans[0].getType().equals("the") || spans[0].getType().equals("all_some")) {
					// that contains only noun
					if (!Arrays.asList(tags_systemName).contains("VB")) {
						System.out.println("SYSTEM NAME : " + systemName);
						return true;
					} else {
						return false;
					}

				} else if (spans[0].getType().equals("those")) {
					// must contains verb because of restrictions
					if (Arrays.asList(tags_systemName).contains("VB")) {
						System.out.println("SYSTEM NAME : " + systemName);
						return true;
					} else {
						return false;
					}
				}

			} else {
				/**
				 * TODO: Log, do not contain any suggest name
				 */

				System.out.println("invalid");
				return false;
			}
		}
		return false;

	}

	public boolean parseModalVp() {
		List<String> modals = sentenceAnalyzer.getModalVp(tags, tokens);

		if (modals.size() < 1) {

			return false;
		}

		String modal_vp = modals.get(0);
		if (Arrays.asList(MODALS).contains(modal_vp.toUpperCase())) {
			System.out.println("MODAL VERB: " + modal_vp);
			return true;
		}

		return false;
	}

	public boolean parseActivitie(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseObject(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseDetails(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseConditionalDetails(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseTemplateConformance(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

}
