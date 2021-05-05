package com.nlp4re.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.nlp4re.operations.PatternMatcher;
import com.nlp4re.operations.SentenceAnalyzer;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class RequirementLogicImpl_EngTest {

	@Test
	public void test_getSentences_NullPointerException() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.getSentences(null));

	}

	@Test
	public void test_getSentences() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);

		SentenceModel mockSentenceModel = mock(SentenceModel.class);
		SentenceDetectorME mockSentenceDetectorME = mock(SentenceDetectorME.class);
		SentenceDetector mockSentenceDetector = mock(SentenceDetector.class);
		// when
		when(mockSentenceDetector.sentDetect(anyString())).thenReturn(new String[] { "some.", "test" });
		// then
		Map<Integer, String> result = requirementLogic.getSentences("some. test");
		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(2));
		assertThat(result.get(0), is("some."));
	}

	@Test
	public void test_doParse_NullException() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);

		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.doParse(null));
	}

}
