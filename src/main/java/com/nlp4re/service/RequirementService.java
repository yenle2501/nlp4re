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
	private AnchorRepository anchorRepository;

	@Autowired
	private ConditionsRepository conditionsRepository;

	@Autowired
	private DetailsRepository detailsRepository;

	@Autowired
	private ModalRepository modalRepository;

	@Autowired
	private ObjectRepository objectRepository;

	@Autowired
	private SystemNameRepository systemNameRepository;

	private SentenceOperations sentenceOperations;
	private SentenceAnalyzer sentenceAnalyzer;
	private PatternMatcher matcher;
	private RegexesProvider regexesProvider;

	private List<Anchor> anchorRegexes;
	private List<Conditions> conditionsRegexes;
	private List<Details> detailsRegexes;
	private List<Modal> modalRegexes;
	private List<Object> objectRegexes;
	private List<SystemName> systemNameRegexes;

	/**
	 * Constructor
	 */
	public RequirementService() {
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
	 * This method helps to check the requirements description
	 * 
	 * @param desc the requirements description
	 * @return a list of map with key-value-pair 
	 * 				1.Map contains all sentences of requirement 
	 * 				2.Map contains all compliant and non-compliant sentences with the order as the keys in 1.Map
	 * 						 (value 1: for non-compliant, 0: compliant)
	 * 				3.Map contains all logs for the non-compliant sentences with the order as the keys in 1.Map
	 * @throws IOException
	 */
	public List<Map<Integer, String>> checkRequirements(String desc) {
		if (desc == null || desc.isEmpty()) {
			return null;
		}

		this.sentenceAnalyzer = new SentenceAnalyzer(this.sentenceOperations);
		this.matcher = new PatternMatcher();
		RequirementLogicImpl_Eng requirementServiceImpl_Eng = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				regexesProvider);
		Map<Integer, String> sentences = requirementServiceImpl_Eng.getSentences(desc);
		if (sentences == null) {
			return null;
		} else {
			List<Map<Integer, String>> result = requirementServiceImpl_Eng.doParse(sentences);
			return result;
		}
	}
}
