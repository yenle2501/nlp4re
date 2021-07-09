package com.nlp4re.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlp4re.domain.Activities;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.ModalVerb;
import com.nlp4re.domain.Objects;
import com.nlp4re.domain.PreCondition;
import com.nlp4re.domain.SystemName;
import com.nlp4re.domain.Template;
import com.nlp4re.repository.ActivitiesRepository;
import com.nlp4re.repository.DetailsRepository;
import com.nlp4re.repository.ModalVerbRepository;
import com.nlp4re.repository.ObjectRepository;
import com.nlp4re.repository.PreConditionRepository;
import com.nlp4re.repository.SystemNameRepository;
import com.nlp4re.service.logic.RequirementLogicImpl_Eng;
import com.nlp4re.service.operations.PatternMatcher;
import com.nlp4re.service.operations.RegexesProvider;
import com.nlp4re.service.operations.SentenceAnalyzer;
import com.nlp4re.service.operations.SentenceOperations;

@Service
public class RequirementService {

	@Autowired
	private ActivitiesRepository anchorRepository;

	@Autowired
	private PreConditionRepository conditionsRepository;

	@Autowired
	private DetailsRepository detailsRepository;

	@Autowired
	private ModalVerbRepository modalRepository;

	@Autowired
	private ObjectRepository objectRepository;

	@Autowired
	private SystemNameRepository systemNameRepository;

	private static Logger logger = LoggerFactory.getLogger(RequirementService.class);
	
	private SentenceOperations sentenceOperations;
	private SentenceAnalyzer sentenceAnalyzer;
	private PatternMatcher matcher;
	private RegexesProvider regexesProvider;

	private List<Activities> anchorRegexes;
	private List<PreCondition> conditionsRegexes;
	private List<Details> detailsRegexes;
	private List<ModalVerb> modalRegexes;
	private List<Objects> objectsRegexes;
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
		this.objectsRegexes = this.objectRepository.findAll();
		this.systemNameRegexes = this.systemNameRepository.findAll();

		this.regexesProvider = new RegexesProvider(anchorRegexes, conditionsRegexes, detailsRegexes, modalRegexes,
				objectsRegexes, systemNameRegexes);
	}

	/**
	 * This method helps to check the requirements description
	 * 
	 * @param desc the requirements description
	 * @return a list of map with key-value-pair 
	 * 			1.Map contains all sentences of requirement 
	 *          2.Map contains all compliant and non-compliant sentences with the order as the keys in 1.Map
	 *          (value 1: for non-compliant, 0: compliant)
	 *          3.Map contains all logs for the non-compliant sentences with the order as the keys in 1.Map
	 */
	public List<Map<Integer, String>> checkRequirements(String desc) {
		if (desc == null || desc.isBlank()) {
			logger.debug("requirements description is empty.");
			return null;
		}

		this.sentenceAnalyzer = new SentenceAnalyzer(this.sentenceOperations);
		this.matcher = new PatternMatcher();
		RequirementLogicImpl_Eng requirementLogicImpl_Eng = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,	regexesProvider);
		
		Map<Integer, String> sentences = requirementLogicImpl_Eng.getSentences(desc);
		List<Map<Integer, String>> result = requirementLogicImpl_Eng.doParse(sentences);
		
		return result;
	}

	/***
	 * save rules/ regexes of template
	 * @param templateRule all rules of template
	 */
	public void saveRules(Template templateRule) {
		checkNotNull(templateRule);
		
		Activities anchor = templateRule.getAnchor();
		PreCondition condition = templateRule.getConditions();
		Details details = templateRule.getDetails();
		ModalVerb modal = templateRule.getModal();
		Objects object = templateRule.getObjects();
		SystemName systemname = templateRule.getSystemName();
		
		if (anchor.getRegex() != null && !anchor.getRegex().isBlank()) {
			anchor.setKey_name(anchor.getKey_name() + this.anchorRepository.count());
			this.anchorRepository.save(anchor);
		}
		if (condition.getRegex() != null && !condition.getRegex().isBlank()) {
			condition.setKey_name(condition.getKey_name() + this.conditionsRepository.count());
			this.conditionsRepository.save(condition);
		}
		if (details.getRegex() != null && !details.getRegex().isBlank()) {
			details.setKey_name(details.getKey_name() + this.detailsRepository.count());
			this.detailsRepository.save(details);
		}
		if (modal.getKey_name() != null && !modal.getKey_name().isBlank()) {
			this.modalRepository.save(modal);
		}
		if (object.getRegex() != null && !object.getRegex().isBlank()) {
			object.setKey_name(object.getKey_name() + this.objectRepository.count());
			this.objectRepository.save(object);
		}
		if (systemname.getRegex() != null && !systemname.getRegex().isBlank()) {
			systemname.setKey_name(systemname.getKey_name() +  this.systemNameRepository.count());
			this.systemNameRepository.save(systemname);
		}
		
		logger.info("saved rules in database");
		// update regexes
		loadRegexes();
	}
	
	/***
	 * save rules/ regexes of template
	 * @param Map<String, List<?>>  all rules of template
	 */
	public Map<String, List<?>> getRules() {
		Map<String, List<?>> result = new HashMap<String, List<?>>();
		
		result.put("anchor",  anchorRegexes);
		result.put("conditions",conditionsRegexes);
		result.put("details",detailsRegexes);
		result.put("modal",modalRegexes);
		result.put("objects",objectsRegexes);
		result.put("systemName",systemNameRegexes);
		
		return result;
	}
}
