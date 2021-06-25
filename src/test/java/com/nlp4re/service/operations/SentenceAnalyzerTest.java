package com.nlp4re.service.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;

/**
 * Test for {@link SentenceAnalyzer}
 */
public class SentenceAnalyzerTest {

	@Test
	public void test_getTokens_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// then
		assertThrows(NullPointerException.class, () -> analyzer.getTokens(null));
	}

	@Test
	public void test_getTokens_NotNull() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		TokenizerME mockTokenizerME = mock(TokenizerME.class);
		when(mockSentenceOperations.getTokenizerME()).thenReturn(mockTokenizerME);
		when(mockTokenizerME.tokenize(anyString())).thenReturn(new String[] { "If", "the" });

		// when
		String[] tokens = analyzer.getTokens("If the");

		// then
		assertEquals(tokens.length, 2);
		assertEquals(tokens[0], "If");
		assertEquals(tokens[1], "the");
	}

	@Test
	public void test_getPOSTags_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// then
		assertThrows(NullPointerException.class, () -> analyzer.getPOSTags(null));

	}

	@Test
	public void test_getPOSTags_NotNull() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		POSTaggerME mockPOSTaggerME = mock(POSTaggerME.class);
		when(mockSentenceOperations.getPOSTaggerME()).thenReturn(mockPOSTaggerME);
		when(mockPOSTaggerME.tag(any(String[].class))).thenReturn(new String[] { "IN", "DT" });
		//
		// when
		String[] POSTags = analyzer.getPOSTags(new String[] { "If", "the" });
		// then
		assertEquals(POSTags.length, 2);
		assertEquals(POSTags[0], "IN");
		assertEquals(POSTags[1], "DT");
	}

	@Test
	public void test_getParses_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getParses(null));
	}

	@Test
	public void test_getParses_NotNull() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		Parser mockParser = mock(Parser.class);
		Parse mockParse = mock(Parse.class);
		when(mockSentenceOperations.getParser()).thenReturn(mockParser);
		when(ParserTool.parseLine(anyString(), mockParser, anyInt())).thenReturn(new Parse[] { mockParse });
		// when
		Parse[] result = analyzer.getParses("some string is here");

		// then
		assertEquals(result.length, 1);
	}

	@Test
	public void test_getChunks_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getChunks(null, null));

	}

	@Test
	public void test_getChunks_NotNull() {

		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		ChunkerME mockChunkerME = mock(ChunkerME.class);
		when(mockSentenceOperations.getChunkerME()).thenReturn(mockChunkerME);
		when(mockChunkerME.chunk(any(String[].class), any(String[].class)))
				.thenReturn(new String[] { "B-SBAR", "I-NP" });

		// when
		List<String> chunks = analyzer.getChunks(new String[] { "If", "the" }, new String[] { "IN", "DT" });
		// then
		assertEquals(chunks.size(), 2);
		assertEquals(chunks.get(0), "B-SBAR");
		assertEquals(chunks.get(1), "I-NP");
	}

	@Test
	public void test_getConditions_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getConditions(null, 0, 0));

	}

	@Test
	public void test_getConditions_NullValue() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when
		List<String> list_conditions1 = analyzer.getConditions(new LinkedList<String>(), 5, -1);
		List<String> list_conditions2 = analyzer.getConditions(new LinkedList<String>(), 5, 8);
		// then
		assertEquals(list_conditions1, null);
		assertEquals(list_conditions2, null);
	}

	@Test
	public void test_getConditions() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when
		List<String> list_conditions = analyzer.getConditions(Arrays.asList(new String[] { "If", "the", "system" }), 3, 1);

		// then
		assertEquals(list_conditions.size(), 1);
		assertEquals(list_conditions.get(0), "If");

	}

	@Test
	public void test_getSystemName_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getSystemName(null, 0, 0));
	}

	@Test
	public void test_getSystemName_if_then_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when
		List<String> systemname = analyzer
				.getSystemName(Arrays.asList(new String[] { "If", ",", "then", "the", "system", "should" }), 1, 5);
		// then
		assertEquals(systemname.size(), 2);
		assertEquals(systemname.get(0), "the");
		assertEquals(StringUtils.collectionToDelimitedString(systemname, " "), "the system");
	}

	@Test
	public void test_getSystemName_with_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when
		List<String> systemname = analyzer
				.getSystemName(Arrays.asList(new String[] { "while", ",", "the", "system", "should" }), 1, 4);
		// then
		assertEquals(systemname.size(), 2);
		assertEquals(systemname.get(0), "the");
		assertEquals(StringUtils.collectionToDelimitedString(systemname, " "), "the system");
	}

	@Test
	public void test_getSystemName_no_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when
		List<String> systemname = analyzer.getSystemName(Arrays.asList(new String[] { "the", "system", "should" }), -1,
				2);
		// then
		assertEquals(systemname.size(), 2);
		assertEquals(systemname.get(0), "the");
		assertEquals(StringUtils.collectionToDelimitedString(systemname, " "), "the system");
	}

	@Test
	public void test_getObjects_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getObjects(null, null));
	}

	@Test
	public void test_getObject() {
		// given
		SentenceOperations mockSentenceOperations = new SentenceOperations();
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when
		String obj = analyzer.getObjects("some objects to apply", new String[] { "some", "objects", "to", "apply" });
		// then
		assertEquals(obj, "some objects");

	}

	@Test
	public void test_getObject_w_possesive_noun() {
		// given
		SentenceOperations mockSentenceOperations = new SentenceOperations();
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		String possible_object = "his order .";
		String[] possible_object_tokens = new String[] { "his", "order", "." };
		// when
		String obj = analyzer.getObjects(possible_object, possible_object_tokens);
		// then
		assertEquals(obj, "his order");
	}

	@Test
	public void test_getModalIndex_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getModalIndex(null, 0));
	}

	@Test
	public void test_getModalIndex_w_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when
		int modal_index = analyzer.getModalIndex(Arrays.asList("NP", ",", "PP", "MD", "VP"), 1);
		// then
		assertEquals(modal_index, 3);
	}

	@Test
	public void test_getModalIndex_without_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when
		int modal_index = analyzer.getModalIndex(Arrays.asList("NP", "PP", "MD", "VP"), -1);
		// then
		assertEquals(modal_index, 2);
	}

	@Test
	public void test_getAnchorStartIndex_NullPointerException() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getAnchorStartIndex(null, 0, 0));
	}

	@Test
	public void test_getAnchorStartIndex_if_then_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when
		int anchor_index = analyzer.getAnchorStartIndex(Arrays.asList("if", "some", ",", "then", "any", "should"), 2,
				5);
		// then
		assertEquals(anchor_index, 4);
	}

	@Test
	public void test_getAnchorStartIndex_with_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);
		// when
		int anchor_index = analyzer.getAnchorStartIndex(Arrays.asList("while", "some", ",", "any", "should"), 2, 4);
		// then
		assertEquals(anchor_index, 3);
	}

	@Test
	public void test_getAnchorStartIndex_without_condition() {
		// given
		SentenceOperations mockSentenceOperations = mock(SentenceOperations.class);
		SentenceAnalyzer analyzer = new SentenceAnalyzer(mockSentenceOperations);

		// when
		int anchor_index = analyzer.getAnchorStartIndex(Arrays.asList("any", "should"), -1, 1);
		// then
		assertEquals(anchor_index, 0);
	}

}