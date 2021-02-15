package com.nlp4re.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import opennlp.tools.util.Span;

/**
 * Test for {@link PatternMatcher}
 */
public class PatternMatcherTest {

	@Test
	public void testMatchesNullPointerException() {
		// given
		PatternMatcher matcher = new PatternMatcher();
		// when + then
		assertThrows(NullPointerException.class, () -> matcher.matches(null, null));

	}

	@Test
	public void testMatchesNotNull() {
		// given
		PatternMatcher matcher = new PatternMatcher();
		Map<String, String> regexs = new HashMap<String, String>();
		regexs.put("id0", "^THE [\\w\\s]+");
		regexs.put("id1", "SHALL* ");
		String sentence = "The system shall provide functionality to allow truckers to trigger events about their current"
				+ "status while involved in an active job.";
		
		
		// when
		Span[] spans = matcher.matches(regexs, sentence);
		// then
		assertThat(spans[0].getType(), is("id0"));
		assertThat(spans[1].getType(), is("id1"));
	}
}
