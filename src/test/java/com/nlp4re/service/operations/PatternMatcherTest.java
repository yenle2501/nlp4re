package com.nlp4re.service.operations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import com.nlp4re.service.operations.PatternMatcher;

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
		assertThat(spans.length, is(1));
		assertThat(spans[0].getType(), is("id0"));
	}
}
