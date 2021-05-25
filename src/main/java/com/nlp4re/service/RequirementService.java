package com.nlp4re.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import com.nlp4re.domain.Template;
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
	 * 			1.Map contains all sentences of requirement 
	 *          2.Map contains all compliant and non-compliant sentences with the order as the keys in 1.Map
	 *          (value 1: for non-compliant, 0: compliant)
	 *          3.Map contains all logs for the non-compliant sentences with the order as the keys in 1.Map
	 * @throws IOException
	 */
	public List<Map<Integer, String>> checkRequirements(String desc) {
		if (desc == null || desc.isBlank()) {
			return null;
		}

		this.sentenceAnalyzer = new SentenceAnalyzer(this.sentenceOperations);
		this.matcher = new PatternMatcher();
		RequirementLogicImpl_Eng requirementLogicImpl_Eng = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher,
				regexesProvider);
		Map<Integer, String> sentences = requirementLogicImpl_Eng.getSentences(desc);
		List<Map<Integer, String>> result = requirementLogicImpl_Eng.doParse(sentences);
		
		return result;
	}

	/***
	 * change rules/ regexes of temlate
	 * @param templateRule all rules of template
	 */
	public void saveRules(Template templateRule) {
		checkNotNull(templateRule);
		
		Anchor anchor = templateRule.getAnchor();
		Conditions condition = templateRule.getConditions();
		Details details = templateRule.getDetails();
		Modal modal = templateRule.getModal();
		Object object = templateRule.getObject();
		SystemName systemname = templateRule.getSystemName();
		
		
		String query = "";
		
		if (anchor.getRegex() != null && !anchor.getRegex().isBlank()) {
			query += "\r\nINSERT INTO ANCHOR (KEY_NAME, REGEX, REQUIRED) VALUES ('" + anchor.getKey_name() + this.anchorRepository.count()+ "','" + anchor.getRegex()+ "'," + anchor.getRequired() +"); \r\n";
			// for unique key name 
			anchor.setKey_name(anchor.getKey_name() + this.anchorRepository.count());
			this.anchorRepository.save(anchor);
		}
		if (condition.getRegex() != null && !condition.getRegex().isBlank()) {
			query += "\r\nINSERT INTO CONDITIONS (KEY_NAME, REGEX, REQUIRED) VALUES ('" + condition.getKey_name() + this.conditionsRepository.count() +  "','" + condition.getRegex() + "'," + condition.getRequired()+ "); \r\n";
			condition.setKey_name(condition.getKey_name() + this.conditionsRepository.count());
			this.conditionsRepository.save(condition);
		}
		if (details.getRegex() != null && !details.getRegex().isBlank()) {
			query += "\r\nINSERT INTO DETAILS (KEY_NAME, REGEX, REQUIRED) VALUES ('" + details.getKey_name() + this.detailsRepository.count() + "','" + details.getRegex() + "'," + details.getRequired() +"); \r\n";
			details.setKey_name(details.getKey_name() + this.detailsRepository.count());
			this.detailsRepository.save(details);
		}
		if (modal.getKey_name() != null && !modal.getKey_name().isBlank()) {
			query += "\r\nINSERT INTO MODAL (KEY_NAME, REQUIRED) VALUES ('" + modal.getKey_name() +"'," + modal.getRequired() +"); \r\n";
			this.modalRepository.save(modal);
		}
		if (object.getRegex() != null && !object.getRegex().isBlank()) {
			query += "\r\nINSERT INTO OBJECT (KEY_NAME, REGEX, REQUIRED) VALUES ('" + object.getKey_name() + this.objectRepository.count() + "','" + object.getRegex() + "'," + object.getRequired() +"); \r\n";
			object.setKey_name(object.getKey_name() + this.objectRepository.count());
			this.objectRepository.save(object);
		}
		if (systemname.getRegex() != null && !systemname.getRegex().isBlank()) {
			query += "\r\nINSERT INTO SYSTEMNAME (KEY_NAME, REGEX, REQUIRED) VALUES ('" + systemname.getKey_name() +  this.systemNameRepository.count() +"','" + systemname.getRegex() + "'," + systemname.getRequired() +");\r\n";
			systemname.setKey_name(systemname.getKey_name() +  this.systemNameRepository.count());
			this.systemNameRepository.save(systemname);
		}
		
		// update regexes
		loadRegexes();
		
		// write the query in data.sql for a persistent saving
		Writer output;
		try {
			output = new BufferedWriter(new FileWriter("./src/main/resources/db/data.sql", true));
			output.append(query);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
