package com.nlp4re.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import opennlp.tools.parser.Parse;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;

/**
 * Test for {@link SentenceAnalyzer}
 */
public class SentenceAnalyzerTest {

	private String sentence = "If the number of products in a warehouse reach the defined minimum limit, then the system shall provide functionality to allow truckers to trigger events about their current "
			+ "status while involved in an active job.";

	private String[] tokens = { "If", "the", "number", "of", "products", "in", "a", "warehouse", "reach", "the",
			"defined", "minimum", "limit", ",", "then", "the", "system", "shall", " provide", "functionality", "to",
			"allow", "truckers", "to", "trigger", "events", "about", "their", "current", "status", "while", "involved",
			"in", "an", "active", "job", "." };
	private String[] tags = { "IN", "DT", "NN", "IN", "NNS", "IN", "DT", "NN", "NN", "DT", "VBN", "NN", "NN", ",", "RB",
			"DT", "NN", "MD", "VB", "NN", "TO", "VB", "NNS", "TO", "VB", "NNS", "IN", "PRP$", "JJ", "NN", "IN", "VBN",
			"IN", "DT", "JJ", "NN", "." };

	@Test
	public void test_getTokens_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// then
		assertThrows(NullPointerException.class, () -> analyzer.getTokens(null));

	}

	@Test
	public void test_getTokens_NotNull() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		TokenizerME mockTokenizerME = mock(TokenizerME.class);

		when(mockTokenizerME.tokenize(sentence)).thenReturn(tokens);
		// when
		String[] tokens = analyzer.getTokens(sentence);
		// then
		assertThat(tokens.length, is(37));
		assertThat(tokens[0], is("If"));
		assertThat(tokens[2], is("number"));
	}

	@Test
	public void test_getPOSTags_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// then
		assertThrows(NullPointerException.class, () -> analyzer.getPOSTags(null));

	}

	@Test
	public void test_getPOSTags_NotNull() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		POSTaggerME mockPOSTaggerME = mock(POSTaggerME.class);

		when(mockPOSTaggerME.tag(tokens)).thenReturn(tags);
		// when
		String[] POSTags = analyzer.getPOSTags(tokens);
		// then
		assertThat(POSTags[1], is("DT"));
		assertThat(POSTags[10], is("VBN"));
	}

	@Test
	public void test_getParses_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getParses(null));
	}

	@Test
	public void test_getParses_NotNull() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		Parse[] result = analyzer.getParses(sentence);

		// then
		assertThat(result[0].getCoveredText(), is(sentence));
		assertThat(result[0].getType(), is("TOP"));
	}

	@Test
	public void test_getChunks_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getChunks(null, null));

	}

	@Test
	public void test_getChunks_NotNull() {

		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		List<String> chunks = analyzer.getChunks(tokens, tags);
		// then
		assertThat(chunks.get(0), is("B-SBAR"));
		assertThat(chunks.get(2), is("I-NP"));
	}

	@Test
	public void test_getConditions_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getConditions(null, 0, 0));

	}

	@Test
	public void test_getConditions_NullValue() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		List<String> list_conditions = analyzer.getConditions(new LinkedList<String>(), 5, -1);
		// then
		assertThat(list_conditions, is(nullValue()));
	}

	@Test
	public void test_getConditions() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		List<String> list_conditions = analyzer.getConditions(Arrays.asList(tokens), 17, 14);
		// then
		assertThat(StringUtils.collectionToDelimitedString(list_conditions, " "),
				is("If the number of products in a warehouse reach the defined minimum limit ,"));

	}

	@Test
	public void test_getSystemName_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getSystemName(null, 0, 0));
	}

	@Test
	public void test_getSystemName_if_then_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		List<String> systemname = analyzer.getSystemName(Arrays.asList(tokens), 14, 17);
		// then
		assertThat(StringUtils.collectionToDelimitedString(systemname, " "), is("the system"));
	}

	@Test
	public void test_getSystemName_with_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String[] senc = new String[] { "While", "the", "payment", "of", "an", "invoice", ",", "the", "Inventory",
				"subsystem", "could", "provide", "the", "warehouse", "manager" };

		// when
		List<String> systemname = analyzer.getSystemName(Arrays.asList(senc), 6, 10);
		// then
		assertThat(StringUtils.collectionToDelimitedString(systemname, " "), is("the Inventory subsystem"));
	}

	@Test
	public void test_getSystemName_no_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String[] senc = new String[] { "The", "Inventory", "subsystem", "could", "provide", "the", "warehouse",
				"manager" };
		// when
		List<String> systemname = analyzer.getSystemName(Arrays.asList(senc), -1, 3);
		// then
		assertThat(StringUtils.collectionToDelimitedString(systemname, " "), is("The Inventory subsystem"));
	}

	@Test
	public void test_getObjects_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getObjects(null, null));
	}

	@Test
	public void test_getObject() {
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String possible_object = "a valid credit card from a branch's dataphone.";
		String[] possible_object_tokens = new String[] { "a", "valid", "credit", "card", "from", "a", "branch's",
				"dataphone", "." };
		// when
		String obj = analyzer.getObjects(possible_object, possible_object_tokens);
		// then
		assertThat(obj, is("a valid credit card"));

	}

	@Test
	public void test_getObject_w_possesive_noun() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String possible_object = "his order .";
		String[] possible_object_tokens = new String[] { "his", "order", "." };
		// when
		String obj = analyzer.getObjects(possible_object, possible_object_tokens);
		// then
		assertThat(obj, is("his order"));
	}

	@Test
	public void test_getModalIndex_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getModalIndex(null, 0));
	}

	@Test
	public void test_getModalIndex_w_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		int modal_index = analyzer.getModalIndex(Arrays.asList(tags), 14);
		// then
		assertThat(modal_index, is(17));
	}

	@Test
	public void test_getModalIndex_without_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		String[] s_tags = { "DT", "NN", "MD", "VB", "NN", "TO", "VB", "NNS", "TO", "VB", "NNS", "IN", "PRP$", "JJ",
				"NN", "IN", "VBN", "IN", "DT", "JJ", "NN", "." };
		int modal_index = analyzer.getModalIndex(Arrays.asList(s_tags), -1);
		// then
		assertThat(modal_index, is(2));
	}

	@Test
	public void test_getAnchorStartIndex_NullPointerException() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when + then
		assertThrows(NullPointerException.class, () -> analyzer.getAnchorStartIndex(null, 0, 0));
	}

	@Test
	public void test_getAnchorStartIndex_if_then_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		// when
		int anchor_index = analyzer.getAnchorStartIndex(Arrays.asList(tokens), 14, 17);
		// then
		assertThat(anchor_index, is(15));
	}

	@Test
	public void test_getAnchorStartIndex_with_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String[] senc = new String[] { "While", "the", "payment", "of", "an", "invoice", ",", "the", "Inventory",
				"subsystem", "could", "provide", "the", "warehouse", "manager" };

		// when
		int anchor_index = analyzer.getAnchorStartIndex(Arrays.asList(senc), 6, 10);
		// then
		assertThat(anchor_index, is(7));
	}

	@Test
	public void test_getAnchorStartIndex_without_condition() {
		// given
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String[] senc = new String[] { "the", "Inventory", "subsystem", "could", "provide", "the", "warehouse",
				"manager" };

		// when
		int anchor_index = analyzer.getAnchorStartIndex(Arrays.asList(senc), -1, 2);
		// then
		assertThat(anchor_index, is(0));
	}

}