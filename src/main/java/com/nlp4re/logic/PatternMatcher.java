package com.nlp4re.logic;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.util.Span;

/**
 * This class works as a help class to
 * 
 */
public class PatternMatcher {

	/**
	 * match the sentence with definitely regexs
	 * 
	 * @param regexs
	 * @param sentence
	 * @return Array of spans
	 */
	public Span[] matches(Map<String, String> regexs, String sentence) {
		checkNotNull(regexs);
		checkNotNull(sentence);

		Map<String, Pattern[]> regexMap = new HashMap<>();
		regexs.forEach((key, value) -> {
			Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			regexMap.put(key, new Pattern[] { pattern });

		});
		RegexNameFinder finder = new RegexNameFinder(regexMap);
		Span[] spans = finder.find(sentence);
		return spans;
	}
}
