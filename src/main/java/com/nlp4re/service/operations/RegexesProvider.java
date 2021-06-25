package com.nlp4re.service.operations;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nlp4re.domain.Activities;
import com.nlp4re.domain.PreCondition;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.ModalVerb;
import com.nlp4re.domain.Objects;
import com.nlp4re.domain.SystemName;

@Component
public class RegexesProvider {

	private List<Activities> activitiesRegexes;
	private List<PreCondition> preConditionsRegexes;
	private List<Details> detailsRegexes;
	private List<ModalVerb> modalVerbRegexes;
	private List<Objects> objectRegexes;
	private List<SystemName> systemNameRegexes;

	public RegexesProvider(List<Activities> anchorRegexes, List<PreCondition> conditionsRegexes, List<Details> detailsRegexes,
			List<ModalVerb> modalRegexes, List<Objects> objectRegexes, List<SystemName> systemNameRegexes) {

		this.activitiesRegexes = anchorRegexes;
		this.preConditionsRegexes = conditionsRegexes;
		this.detailsRegexes = detailsRegexes;
		this.modalVerbRegexes = modalRegexes;
		this.objectRegexes = objectRegexes;
		this.systemNameRegexes = systemNameRegexes;
	}

	public void setAnchorRegexes(List<Activities> anchorRegexes) {
		this.activitiesRegexes = anchorRegexes;
	}

	public void setConditionsRegexes(List<PreCondition> conditionsRegexes) {
		this.preConditionsRegexes = conditionsRegexes;
	}

	public void setDetailsRegexes(List<Details> detailsRegexes) {
		this.detailsRegexes = detailsRegexes;
	}

	public void setModalRegexes(List<ModalVerb> modalRegexes) {
		this.modalVerbRegexes = modalRegexes;
	}

	public void setobjectRegexes(List<Objects> objectRegexes) {
		this.objectRegexes = objectRegexes;
	}

	public void setsystemNameRegexes(List<SystemName> systemNameRegexes) {
		this.systemNameRegexes = systemNameRegexes;
	}

	public List<Activities> getAnchorRegexes() {
		return this.activitiesRegexes;
	}

	public List<PreCondition> getConditionsRegexes() {
		return this.preConditionsRegexes;
	}

	public List<Details> getDetailsRegexes() {
		return this.detailsRegexes;
	}

	public List<ModalVerb> getModalRegexes() {
		return this.modalVerbRegexes;
	}

	public List<Objects> getObjectRegexes() {
		return this.objectRegexes;
	}

	public List<SystemName> getSystemNameRegexes() {
		return this.systemNameRegexes;
	}

}
