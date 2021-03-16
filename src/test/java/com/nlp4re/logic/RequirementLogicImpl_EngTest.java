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

	@Test
	public void test_doParse_false() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getTokens(anyString())).thenReturn(new String[] { "return", "," ,"then", "tokens","should","provide" });
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "VP",",", "PP", "NNS", "MD", "VB" });
		when(mockSentenceAnalyzer.getModalIndex(anyList(), anyInt())).thenReturn(5);
		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(),anyInt())).thenReturn(1);
		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt())).thenReturn(Arrays.asList(new String[] { "the", "system"}));
		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt())).thenReturn(Arrays.asList(new String[] { "if", "system", "then" }));
		when(mockSentenceAnalyzer.getChunks(any(String[].class), any(String[].class))).thenReturn(Arrays.asList(new String[] { "SP" }));
		when(mockSentenceAnalyzer.getObjects( anyString(),any(String[].class))).thenReturn("one object");
		
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		Span mockSpan = new Span(0, 0, "if");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		
		Map<Integer, String> sentences = new HashMap<Integer, String>();
		sentences.put(0, "some String");//"if the sentence has something, then the system should provide the user with the ability to do one chance");
		// when
		List<Map<Integer, String>> result = requirementLogic.doParse(sentences);
		// then
		assertThat(!result.isEmpty(), is(true));
		assertThat(result.get(0).get(0), is("some String"));
		assertThat(result.get(1).get(0), is("1"));
		verify(mockSentenceAnalyzer, times(1)).getTokens(anyString());
	}
}
