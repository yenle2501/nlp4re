package com.nlp4re.service.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import opennlp.tools.util.Span;

import com.nlp4re.domain.Anchor;
import com.nlp4re.domain.Modal;
import com.nlp4re.service.operations.PatternMatcher;
import com.nlp4re.service.operations.RegexesProvider;
import com.nlp4re.service.operations.SentenceAnalyzer;

/**
 * Test for {@link RequirementLogicImpl_Eng}
 */
@ExtendWith(MockitoExtension.class)
public class RequirementLogicImpl_EngTest {

	@Mock
	private SentenceAnalyzer mockSentenceAnalyzer;
	@Mock
	private PatternMatcher mockPatternMatcher;
	@Mock
	private RegexesProvider mockRegexesProvider;

	@Test
	public void test_getTokensFromSentence_NullPointerException() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.getTokensFromSentence(null));
	}

	@Test
	public void test_getTokensFromSentence() {
		// given

		when(mockSentenceAnalyzer.getTokens(anyString())).thenReturn(new String[] { "return", "tokens" });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		// when
		String[] result = requirementLogic.getTokensFromSentence(anyString());
		// then
		assertThat(result.length, is(2));
		assertThat(result[0], is("return"));
		assertThat(result[1], is("tokens"));
	}

	@Test
	public void test_getTagsFromTokens_NullPointerException() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.getTagsFromTokens(null));
	}

	@Test
	public void test_getTagsFromTokens() {
		// given

		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "VP", "NNS" });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		String[] result = requirementLogic.getTagsFromTokens(new String[] { "tokens" });
		// then
		assertThat(result.length, is(2));
		assertThat(result[0], is("VP"));
		assertThat(result[1], is("NNS"));
	}

	@Test
	public void test_parseModalVp_NullPointerException() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseModalVp(0, null));
	}

	@Test
	public void test_parseModalVp_returnFalse() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				mockRegexesProvider);
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

		when(mockRegexesProvider.getModalRegexes()).thenReturn(List.of(new Modal("should")));
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				mockRegexesProvider);
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

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseSystemName(null, 1, 1));
	}

	@Test
	public void test_parseSystemname_returnTrue() {
		// given

		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "the", "system", "name" }));
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "DT", "NP", "NP" });

		Span mockSpan = new Span(0, 0, "the");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
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

		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "the", "system", "name" }));

		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getSystemName(anyList(), anyInt(), anyInt());
		verify(mockSentenceAnalyzer, times(0)).getPOSTags(any(String[].class));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseSystemname_noSystemname() {
		// given

		// return no system name
		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt())).thenReturn(null);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
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

		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "all", "systems", "of", "the", "something", "have" }));
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "DT", "NP", "NP", "VB" });

		Span mockSpan = new Span(0, 0, "all_some");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(1)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseSystemname_NoVerbAfterSystemname() {
		// given

		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "all", "systems", "of", "the", "something", "have" }));
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class))).thenReturn(new String[] { "DT", "NP", "NP", "MD" });

		Span mockSpan = new Span(0, 0, "all_some");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(1)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseSystemname_ThoseType_returnTrue() {
		// given

		when(mockSentenceAnalyzer.getSystemName(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "all", "systems", "of", "the", "something", "have" }));

		Span mockSpan = new Span(0, 0, "those");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseSystemName(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(0)).getPOSTags(any(String[].class));
	}

	@Test
	public void test_parseCondition_NullPointerException() {
		// given
		SentenceAnalyzer sentenceAnalyzer = mock(SentenceAnalyzer.class);
		PatternMatcher matcher = mock(PatternMatcher.class);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseCondition(null, 0, 0));

	}

	@Test
	public void test_parseCondition_returnTrue_no_condition() {
		// given

		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt())).thenReturn(null);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(0)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_noPattern() {
		// given

		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "the", "system", "name" }));

		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "then" }), 0,
				0);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_returnTrue_If_Then() {
		// given

		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "if", ",", "then" }));

		Span mockSpan = new Span(0, 0, "if");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "then" }), 1,
				0);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_returnFalse_If_Then() {
		// given

		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "if", ",", "ahhaha" }));

		Span mockSpan = new Span(0, 0, "if");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "hahaha" }), 1,
				0);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseCondition_otherConditionTypes() {
		// given

		when(mockSentenceAnalyzer.getConditions(anyList(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(new String[] { "while", "system", "haha" }));

		Span mockSpan = new Span(0, 0, "while_during");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when
		boolean result = requirementLogic.parseCondition(Arrays.asList(new String[] { "haha", "system", "should" }), 0,
				0);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getConditions(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseAnchor_NullPointerException() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseAnchor(null, null, 1, 1));
	}

	@Test
	public void test_parseAnchor_ProvideType_ReturnFalse() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(1);
		Span mockSpan = new Span(0, 0, "provide");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		when(mockSentenceAnalyzer.getChunks(any(String[].class), any(String[].class)))
				.thenReturn(Arrays.asList(new String[] { "MD", "VP" }));
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(new String[] { "the", "system", "should", "have", "with" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "NP" }), 0, 2);
		// then
		// after provide has no Object
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());

	}

	@Test
	public void test_parseAnchor_ProvideType_ReturnFalse_noVerbAfterAbilityTo() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(1);
		Span mockSpan = new Span(0, 0, "provide");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		when(mockSentenceAnalyzer.getChunks(any(String[].class), any(String[].class)))
				.thenReturn(Arrays.asList(new String[] { "MD", "VB" }));
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(
						new String[] { "the", "system", "should", "have", "with", "the", "ability", "to", "system" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "IN", "DT", "AA", "AA", "NP" }), 0, 2);
		// then
		// after provide has no Object
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(1)).getChunks(any(String[].class), any(String[].class));
	}

	@Test
	public void test_parseAnchor_ProvideType_returnTrue() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(0);
		Span mockSpan = new Span(0, 0, "provide");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		when(mockSentenceAnalyzer.getChunks(any(String[].class), any(String[].class)))
				.thenReturn(Arrays.asList(new String[] { "B-NP", "I-NP" }));
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(
						new String[] { "the", "system", "should", "have", "with", "the", "ability", "to", "system" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "IN", "DT", "AA", "AA", "VB" }), -1, 2);
		// then
		// after provide has no Object
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(1)).getChunks(any(String[].class), any(String[].class));
	}

	@Test
	public void test_parseAnchor_BeAbleTo_ReturnFalse() {

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(0);
		Span mockSpan = new Span(0, 0, "be_able_to");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(
						new String[] { "the", "system", "should", "have", "with", "the", "ability", "to", "system" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "IN", "DT", "AA", "AA", "VB" }), -1, 2);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(0)).getChunks(any(String[].class), any(String[].class));
	}

	@Test
	public void test_parseAnchor_BeAbleTo_ReturnTrue() {

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(0);
		Span mockSpan = new Span(0, 0, "be_able_to");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(
						new String[] { "the", "system", "should", "have", "with", "the", "ability", "to", "system" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "IN", "DT", "VB", "AA", "VB" }), -1, 2);
		// then
		// after provide has no Object
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(0)).getChunks(any(String[].class), any(String[].class));
	}

	@Test
	public void test_parseAnchor_ReturnFalse() {

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(0);
		Span mockSpan = new Span(0, 0, "provide");
		Span mockSpan1 = new Span(0, 0, "be_able_to");

		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan, mockSpan1 });
		when(mockRegexesProvider.getAnchorRegexes()).thenReturn(List.of(new Anchor("the", "the .")));
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(
						new String[] { "the", "system", "should", "have", "with", "the", "ability", "to", "system" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "IN", "DT", "VB", "AA", "VB" }), -1, 2);
		// then
		// after provide has no Object
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockSentenceAnalyzer, times(0)).getChunks(any(String[].class), any(String[].class));
	}

	@Test
	public void test_parseAnchor_NoPattern_ReturnTrue() {

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getAnchorStartIndex(anyList(), anyInt(), anyInt())).thenReturn(0);
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);
		// when
		boolean result = requirementLogic.parseAnchor(
				Arrays.asList(
						new String[] { "the", "system", "should", "have", "with", "the", "ability", "to", "system" }),
				Arrays.asList(new String[] { "DT", "NP", "MD", "VB", "IN", "DT", "VB", "AA", "VB" }), -1, 2);
		// then
		// after provide has no Object
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getAnchorStartIndex(anyList(), anyInt(), anyInt());
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
		verify(mockSentenceAnalyzer, times(0)).getChunks(any(String[].class), any(String[].class));
	}

	@Test
	public void test_parseObject_NullPointerException() {

		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseObject(null, null, 1));
	}

	@Test
	public void test_parseObject_returnFalse_noObject() {

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getObjects(anyString(), any(String[].class))).thenReturn(null);
		// when
		boolean result = requirementLogic.parseObject(
				new String[] { "the", "system", "should", "have", "some", "thing" },
				new String[] { "DT", "NP", "MD", "VB", "DT", "NN" }, 4);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getObjects(anyString(), any(String[].class));
		verify(mockPatternMatcher, times(0)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseObject_NoPattern_returnFalse() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getObjects(anyString(), any(String[].class))).thenReturn("some String");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);
		// when
		boolean result = requirementLogic.parseObject(
				new String[] { "the", "system", "should", "have", "some", "thing" },
				new String[] { "DT", "NP", "MD", "VB", "DT", "NN" }, 4);
		// then
		assertThat(result, is(false));
		verify(mockSentenceAnalyzer, times(1)).getObjects(anyString(), any(String[].class));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseObject_returnTrue() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getObjects(anyString(), any(String[].class))).thenReturn("a String");
		Span mockSpan = new Span(0, 0, "all_the");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });
		// when
		boolean result = requirementLogic.parseObject(
				new String[] { "the", "system", "should", "have", "some", "thing" },
				new String[] { "DT", "NP", "MD", "VB", "DT", "NN" }, 4);
		// then
		assertThat(result, is(true));
		verify(mockSentenceAnalyzer, times(1)).getObjects(anyString(), any(String[].class));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseDetails_NullPointerException() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.parseDetails(null, 1));

	}

	@Test
	public void test_parseDetails_returnTrue_withoutCondition() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		// when
		boolean result = requirementLogic.parseDetails(Arrays.asList(new String[] { "the", "if" }), 1);
		// then
		assertThat(result, is(true));
		verify(mockPatternMatcher, times(0)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseDetails_returnTrue_with_condition() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		Span mockSpan = new Span(0, 0, "condition");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		// when
		boolean result = requirementLogic
				.parseDetails(Arrays.asList(new String[] { "the", "if", "and", "only", "if", "thing" }), 1);
		// then
		assertThat(result, is(true));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseDetails_returnTrue_noPattern() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(null);

		// when
		boolean result = requirementLogic
				.parseDetails(Arrays.asList(new String[] { "the", "if", "and", "only", "if", "thing" }), 1);
		// then
		assertThat(result, is(true));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_parseDetails_returnFalse() {

		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		Span mockSpan = new Span(0, 0, "all_the");
		when(mockPatternMatcher.matches(anyMap(), anyString())).thenReturn(new Span[] { mockSpan });

		// when
		boolean result = requirementLogic
				.parseDetails(Arrays.asList(new String[] { "the", "system", "should", "have", "some", "thing" }), 1);
		// then
		assertThat(result, is(false));
		verify(mockPatternMatcher, times(1)).matches(anyMap(), anyString());
	}

	@Test
	public void test_getSentences_NullPointerException() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.getSentences(null));

	}

	@Test
	public void test_getSentences_True() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		// when
		Map<Integer, String> result = requirementLogic.getSentences("some. sentence");
		// then
		assertThat(result.size(), is(2));
		assertThat(result.get(0), is("some."));
	}

	@Test
	public void test_doParse_NullException() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		// when + then
		assertThrows(NullPointerException.class, () -> requirementLogic.doParse(null));
	}

	@Test
	public void test_doParse_nonCompliant() {
		// given
		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);

		when(mockSentenceAnalyzer.getTokens(anyString())).thenReturn(new String[] { "The", "VMS", "system", "will",
				"consume", "as", "any", "units", "of", "energy", "as", "possible", "." });
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class)))
				.thenReturn(new String[] { "DT", "NN", "NN", "MD", "VB", "IN", "IN", "NNS", "IN", "NN", "IN", "NN" });
		when(mockSentenceAnalyzer.getModalIndex(anyList(), anyInt())).thenReturn(1);
		Map<Integer, String> sentences = new HashMap<Integer, String>();
		sentences.put(1, anyString());
		// when
		List<Map<Integer, String>> result = requirementLogic.doParse(sentences);
		// then
		assertThat(result.size(), is(3));
		assertEquals(result.get(1).get(1), "1");
	}

	@Test
	public void test_doParse_Compliant() {
		// given

		RequirementLogicImpl_Eng requirementLogic = new RequirementLogicImpl_Eng(mockSentenceAnalyzer,
				mockPatternMatcher, mockRegexesProvider);
		when(mockSentenceAnalyzer.getTokens(anyString())).thenReturn(new String[] { "The", "VMS", "system", "should",
				"consume", "the", "unit", "of", "energy", "as", "possible", "." });
		when(mockSentenceAnalyzer.getPOSTags(any(String[].class)))
				.thenReturn(new String[] { "DT", "NN", "NN", "MD", "VB", "DT", "NN", "IN", "NN", "IN", "NN" });
		when(mockSentenceAnalyzer.getModalIndex(anyList(), anyInt())).thenReturn(1);

		Map<Integer, String> sentences = new HashMap<Integer, String>();
		sentences.put(1, anyString());
		// when
		List<Map<Integer, String>> result = requirementLogic.doParse(sentences);
		// then
		assertThat(result.size(), is(3));
		assertEquals(result.get(1).get(1), "1");
	}

}
