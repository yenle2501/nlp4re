package com.nlp4re.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * Test for {@link SentenceAnalyzer}
 */
public class SentenceAnalyzerTest {

	private String sentence = "The system shall provide functionality to allow truckers to trigger events about their current "
			+ "status while involved in an active job.";

	private String[] tokens = { "The", "system", "shall", " provide", "functionality", "to", "allow", "truckers", "to",
			"trigger", "events", "about", "their", "current", "status", "while", "involved", "in", "an", "active",
			"job", "." };
	private String[] tags = { "DT", "NN", "MD", "VB", "NN", "TO", "VB", "NNS", "TO", "VB", "NNS", "IN", "PRP$", "JJ",
			"NN", "IN", "VBN", "IN", "DT", "JJ", "NN", "." };

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
		TokenizerModel mockTokenModel = mock(TokenizerModel.class);
		TokenizerME mockTokenizerME = mock(TokenizerME.class);

		when(mockTokenizerME.tokenize(sentence)).thenReturn(tokens);
		// when
		String[] tokens = analyzer.getTokens(sentence);
		// then
		assertThat(tokens.length, is(22));
		assertThat(tokens[0], is("The"));
		assertThat(tokens[2], is("shall"));
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
		POSModel mockPOSmodel = mock(POSModel.class);
		POSTaggerME mockPOSTaggerME = mock(POSTaggerME.class);

		when(mockPOSTaggerME.tag(tokens)).thenReturn(tags);
		// when
		String[] POSTags = analyzer.getPOSTags(tokens);
		// then
		assertThat(POSTags[1], is("NP"));
		assertThat(POSTags[2], is("VP"));
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
		ParserModel mockParserModel = mock(ParserModel.class);
		ParserFactory mockParserFactory = mock(ParserFactory.class);
		Parser mockParser = mock(Parser.class);
		ParserTool mockParserTool = mock(ParserTool.class);
		
		when(mockParserFactory.create(mockParserModel)).thenReturn(mockParser);
		// when(mockParserTool.parseLine(sentence, mockParser, 1)).thenReturn(value)
		// when
		Parse[] parses = analyzer.getParses(sentence);

		// then
		assertThat(parses[0].getType(), is("id0"));
		assertThat(parses[1].getType(), is("id1"));
	}

	@Test
	public void test_getChunks_NullPointerException() {
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		assertThrows(NullPointerException.class, () -> analyzer.getChunks(null, null));

	}

	@Test
	public void test_getChunks_NotNull() {
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String[] tokens = analyzer.getTokens(sentence);
		String[] POSTags = analyzer.getPOSTags(tokens);
	}

	@Test
	public void test_getConditions_NullPointerException() {
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		assertThrows(NullPointerException.class, () -> analyzer.getChunks(null, null));

	}

	@Test
	public void test_getConditions_NotNull() {
		SentenceAnalyzer analyzer = new SentenceAnalyzer();
		String[] tokens = analyzer.getTokens(sentence);
		String[] POSTags = analyzer.getPOSTags(tokens);
	}
	
}
