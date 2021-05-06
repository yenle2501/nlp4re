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
	public void test_tokenizeSentence_NullPointerException() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.tokenizeSentence(null));
	}

	@Test
	public void test_tokenizeSentence() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(sentenceAnalyzer.getTokens(anyString())).thenReturn(new String[] { "return", "tokens" });
		when(sentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "VP", "NNS" });

		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when
		List<String[]> result = requirementLogic.tokenizeSentence(anyString());
		// then
		assertThat(result.size(), is(2));
		assertThat(result.get(0)[0], is("return"));
		assertThat(result.get(1)[0], is("VP"));
	}

	@Test
	public void test_parseModalVp_NullPointerException() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseModalVp(0, null));
	}

	@Test
	public void test_parseModalVp_returnFalse() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when
		boolean result = requirementLogic.parseModalVp(-1, Arrays.asList(new String[] { "return", "tokens" }));
		// then
		assertThat(result, is(false));
	}

	@Test
	public void test_parseModalVp_returnTrue() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when
		boolean result = requirementLogic.parseModalVp(2, Arrays.asList(new String[] { "some", "tokens", "should" }));
		// then
		assertThat(result, is(true));
	}

	@Test
	public void test_parseSystemname_NullPointerException() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseSystemName(null, 0, 0));

	}

	@Test
	public void test_parseSystemname_returnTrue() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "the", "system", "name" }));
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "DT", "NP", "NP" });

		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		Span mockSpan = new Span(0, 0, "the");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getSystemName(anyList(), anyInt(), anyInt());
		verify(mockSentenceAnalyzer, times(1)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseSystemname_noPattern() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "the", "system", "name" }));

		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getSystemName(anyList(), anyInt(), anyInt());
		verify(mockSentenceAnalyzer, times(0)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseSystemname_noSystemname() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt())).thenReturn(null);
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockPatternMatcher, times(0)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(0)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseSystemname_VerbAfterSystemname() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "all", "systems", "of", "the", "something", "have" }));
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "DT", "NP", "NP", "VB" });
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		Span mockSpan = new Span(0, 0, "all_some");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(1)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseCondition_NullPointerException() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseCondition(null, 0, 0));

	}

	@Test
	public void test_parseCondition_returnTrue_no_condition() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt())).thenReturn(null);

		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
	}

	@Test
	public void test_parseCondition_returnTrue() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "if", "system", "then" }));

		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		Span mockSpan = new Span(0, 0, "if");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "then" }), 1,
				0);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_noPattern() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "the", "system", "name" }));
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "then" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_noThenAfterIf() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "if", "system", "haha" }));
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		Span mockSpan = new Span(0, 0, "if");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_otherPatterns() {
		// given
		SentenceAnalyzer mockSentenceAnalyzer = mock(SentenceAnalyzer.class);
		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "while", "system", "haha" }));
		PatternMatcher mockPatternMatcher = mock(PatternMatcher.class);
		Span mockSpan = new Span(0, 0, "while_during");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
	}


}
