package com.nlp4re.service.operations;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

@Component
public class SentenceOperations {

	private TokenizerME tokenizer;
	private POSTaggerME tagger;
	private Parser parser;
	private ChunkerME chunker;

	public SentenceOperations() {
		loadModels();
	}

	/***
	 * Load the required modules provided by OpenNLP library
	 */
	private void loadModels() {
		try {
			InputStream tokenMEInputStream = new FileInputStream("./src/main/resources/models/en-token.bin");
			InputStream posMEInputStream = new FileInputStream("./src/main/resources/models/en-pos-maxent.bin");
			InputStream parserInputStream = new FileInputStream("./src/main/resources/models/en-parser-chunking.bin");
			InputStream chunkerModelInputStream = new FileInputStream("./src/main/resources/models/en-chunker.bin");

			TokenizerModel tokenModel = new TokenizerModel(tokenMEInputStream);
			POSModel posModel = new POSModel(posMEInputStream);
			ParserModel parserModel = new ParserModel(parserInputStream);
			ChunkerModel chunkerModel = new ChunkerModel(chunkerModelInputStream);

			TokenizerME tokenizer = new TokenizerME(tokenModel);
			POSTaggerME tagger = new POSTaggerME(posModel);
			Parser parser = ParserFactory.create(parserModel);
			ChunkerME chunker = new ChunkerME(chunkerModel);

			setTokenizerME(tokenizer);
			setPOSTaggerME(tagger);
			setParser(parser);
			setChunkerME(chunker);

			tokenMEInputStream.close();
			posMEInputStream.close();
			parserInputStream.close();
			chunkerModelInputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setTokenizerME(TokenizerME tokenizer) {
		this.tokenizer = tokenizer;
	}

	public TokenizerME getTokenizerME() {
		return this.tokenizer;
	}

	public void setPOSTaggerME(POSTaggerME tagger) {
		this.tagger = tagger;
	}

	public POSTaggerME getPOSTaggerME() {
		return this.tagger;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public Parser getParser() {
		return this.parser;
	}

	public void setChunkerME(ChunkerME chunker) {
		this.chunker = chunker;
	}

	public ChunkerME getChunkerME() {
		return this.chunker;
	}
}
