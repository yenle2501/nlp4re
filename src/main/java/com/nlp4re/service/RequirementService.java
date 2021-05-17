package com.nlp4re.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlp4re.domain.Anchor;
import com.nlp4re.domain.Conditions;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.Modal;
import com.nlp4re.domain.Object;
import com.nlp4re.domain.SystemName;
import com.nlp4re.repository.AnchorRepository;
import com.nlp4re.repository.ConditionsRepository;
import com.nlp4re.repository.DetailsRepository;
import com.nlp4re.repository.ModalRepository;
import com.nlp4re.repository.ObjectRepository;
import com.nlp4re.repository.SystemNameRepository;
import com.nlp4re.service.logic.RequirementLogicImpl_Eng;
import com.nlp4re.service.operations.PatternMatcher;
import com.nlp4re.service.operations.RegexesProvider;
import com.nlp4re.service.operations.SentenceAnalyzer;
import com.nlp4re.service.operations.SentenceOperations;

/**
 * This class works as a service
 */
@Service
public class RequirementService {

	@Autowired
	protected AnchorRepository anchorRepository;

	@Autowired
	protected ConditionsRepository conditionsRepository;

	@Autowired
	protected DetailsRepository detailsRepository;

	@Autowired
	protected ModalRepository modalRepository;

	@Autowired
	protected ObjectRepository objectRepository;

	@Autowired
	protected SystemNameRepository systemNameRepository;

	protected SentenceOperations sentenceOperations;
	protected SentenceAnalyzer sentenceAnalyzer;
	protected PatternMatcher matcher;
	private RegexesProvider regexesProvider;

	protected List<Anchor> anchorRegexes;
	protected List<Conditions> conditionsRegexes;
	protected List<Details> detailsRegexes;
	protected List<Modal> modalRegexes;
	protected List<Object> objectRegexes;
	protected List<SystemName> systemNameRegexes;

	/**
	 * Constructor
	 */
	public RequirementService() {
	}

	@PostConstruct
	protected void loadServiceOperations() {
		this.sentenceOperations = new SentenceOperations();
	}

	@PostConstruct
	protected void loadRegexes() {
		this.anchorRegexes = this.anchorRepository.findAll();
		this.conditionsRegexes = this.conditionsRepository.findAll();
		this.detailsRegexes = this.detailsRepository.findAll();
		this.modalRegexes = this.modalRepository.findAll();
		this.objectRegexes = this.objectRepository.findAll();
		this.systemNameRegexes = this.systemNameRepository.findAll();

		this.regexesProvider = new RegexesProvider(anchorRegexes, conditionsRegexes, detailsRegexes, modalRegexes,
				objectRegexes, systemNameRegexes);
	}

	/**
	 * This method helps to check the requirements
	 * 
	 * @param desc the requirements description
	 * @return a list of map with key-value-pair 1.Map contains all sentences of requirement 2.Map contains all
	 *         compliant and non-compliant sentences with the order as the keys in 1.Map (value 1: for non-compliant, 0:
	 *         compliant) 3.Map contains all logs for the non-compliant sentences with the order as the keys in 1.Map
	 * @throws IOException
	 */
	public List<Map<Integer, String>> checkRequirements(String desc) {
		if (desc == null || desc.isEmpty()) {
			return null;
		}

		this.sentenceAnalyzer = new SentenceAnalyzer(this.sentenceOperations);
		this.matcher = new PatternMatcher();
		RequirementLogicImpl_Eng requirementServiceImpl_Eng = new RequirementLogicImpl_Eng(sentenceAnalyzer,
				matcher, regexesProvider);
		Map<Integer, String> sentences = requirementServiceImpl_Eng.getSentences(desc);
		if (sentences == null) {
			return null;
		} else {
			List<Map<Integer, String>> result = requirementServiceImpl_Eng.doParse(sentences);
			return result;
		}
	}
	/**
	 * This method helps to get single sentence from the requirements description
	 * 
	 * @param desc requirements description
	 * @return a String array with sentences
	 * @throws IOException
	 */
	// public Map<Integer, String> getSentences(String desc);
	//
	// /**
	// * This method helps to parse each single sentence with the chosen template.
	// *
	// * @param sentences list of sentences
	// * @return a list of map with key-value-pair 1.Map contains all sentences of requirement 2.Map contains all
	// * compliant and non-compliant sentences with the order as the keys in 1.Map 3.Map contains all logs for the
	// * non-compliant sentences with the order as the keys in 1.Map
	// */
	// public List<Map<Integer, String>> doParse(Map<Integer, String> sentences);
}
