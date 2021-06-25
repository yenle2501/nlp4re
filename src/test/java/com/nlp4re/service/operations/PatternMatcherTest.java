package com.nlp4re.service.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
		Map<String, String> mockRegexs = new HashMap<String, String>();
		mockRegexs.put("id0", "^THE [\\w\\s]+");

		// when
		Span[] spans = matcher.matches(mockRegexs, "The system shall");

		// then
		assertEquals(spans.length, 1);
		assertEquals(spans[0].getType(),"id0");
	}
}
