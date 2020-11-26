package com.nlp4re.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import opennlp.tools.parser.Parse;

public class MazoAndJaramilloLogic {

	private static final String[] MODALS = { "SHOULD", "SHALL", "COULD" };
	private static final String[] SYSTEM_NAMES = { "ALL", "SOME", "THOSE", "THE" };

	public String modal_vp;
	public int modal_vp_index;
	public String system_name;
	public String process_vp;
	public String object;
	public String details;
	public String conditions;

	public boolean is_multiple_modals;
	public boolean is_valid_sentence;

	private SentenceAnalyzer sentenceAnalyzer = null;
	private String sentence;
	private String[] tokens;
	private String[] tags;

	public MazoAndJaramilloLogic() {

		sentenceAnalyzer = new SentenceAnalyzer();
	}

	public void first(String sentence) throws IOException {
		this.sentence = sentence;
		this.tokens = sentenceAnalyzer.getTokens(sentence);
		this.tags = sentenceAnalyzer.getTags(tokens);
	}

	public boolean parseCondition(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseSystemName() throws IOException {
//		if (!parseModalVp()) {
//			return false;
//		}

		List<Parse> nounChunks = sentenceAnalyzer.getNounChunks(this.sentence);
		String systemName = sentenceAnalyzer.getSystemName(nounChunks, tokens, tags);

		if (systemName == null) {
			/**
			 * TODO: Log: Sentence does not have systemname
			 */
		} else {
			String[] systemNameTokens = sentenceAnalyzer.getTokens(systemName);
			if (Arrays.asList(SYSTEM_NAMES).contains(systemNameTokens[0].toUpperCase())) {
				
			}
			else {
				/** TODO:
				 *  Log, do not contain any suggest name
				 * */
			}
		}
		System.out.println("SYSTEM NAME: " + systemName);
		return false;
	}

	public boolean parseModalVp() {
//		Map<Integer, String> 
		List<String> modals = sentenceAnalyzer.getModalVp(tags, tokens);

		if (modals.size() < 1) {

			return false;
		}
		if (modals.size() > 1) {

			return false;
		}
		String modal_vp = modals.get(0);
		if (Arrays.asList(MODALS).contains(modal_vp.toUpperCase())) {
			System.out.println("MODAL VERB: " + modal_vp);
			return true;
		}

		return false;
	}

	public boolean parseActivitie(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseObject(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseDetails(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseConditionalDetails(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean parseTemplateConformance(String sentence) {
		// TODO Auto-generated method stub
		return false;
	}

}
