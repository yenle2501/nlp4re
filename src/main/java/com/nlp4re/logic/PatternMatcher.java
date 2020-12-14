package com.nlp4re.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.util.Span;

public class PatternMatcher {
	
	public PatternMatcher() {

	}

	public Span[] matches(Map<String, String> regexs, String sentence) {
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
