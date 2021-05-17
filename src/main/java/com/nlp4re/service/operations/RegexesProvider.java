package com.nlp4re.service.operations;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nlp4re.domain.Anchor;
import com.nlp4re.domain.Conditions;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.Modal;
import com.nlp4re.domain.Object;
import com.nlp4re.domain.SystemName;

@Component
public class RegexesProvider {

	private List<Anchor> anchorRegexes;
	private List<Conditions> conditionsRegexes;
	private List<Details> detailsRegexes;
	private List<Modal> modalRegexes;
	private List<Object> objectRegexes;
	private List<SystemName> systemNameRegexes;

	public RegexesProvider(List<Anchor> anchorRegexes, List<Conditions> conditionsRegexes, List<Details> detailsRegexes,
			List<Modal> modalRegexes, List<Object> objectRegexes, List<SystemName> systemNameRegexes) {

		this.anchorRegexes = anchorRegexes;
		this.conditionsRegexes = conditionsRegexes;
		this.detailsRegexes = detailsRegexes;
		this.modalRegexes = modalRegexes;
		this.objectRegexes = objectRegexes;
		this.systemNameRegexes = systemNameRegexes;
	}

	public void setAnchorRegexes(List<Anchor> anchorRegexes) {
		this.anchorRegexes = anchorRegexes;
	}

	public void setConditionsRegexes(List<Conditions> conditionsRegexes) {
		this.conditionsRegexes = conditionsRegexes;
	}

	public void setDetailsRegexes(List<Details> detailsRegexes) {
		this.detailsRegexes = detailsRegexes;
	}

	public void setModalRegexes(List<Modal> modalRegexes) {
		this.modalRegexes = modalRegexes;
	}

	public void setobjectRegexes(List<Object> objectRegexes) {
		this.objectRegexes = objectRegexes;
	}

	public void setsystemNameRegexes(List<SystemName> systemNameRegexes) {
		this.systemNameRegexes = systemNameRegexes;
	}

	public List<Anchor> getAnchorRegexes() {
		return this.anchorRegexes;
	}

	public List<Conditions> getConditionsRegexes() {
		return this.conditionsRegexes;
	}

	public List<Details> getDetailsRegexes() {
		return this.detailsRegexes;
	}

	public List<Modal> getModalRegexes() {
		return this.modalRegexes;
	}

	public List<Object> getObjectRegexes() {
		return this.objectRegexes;
	}

	public List<SystemName> getSystemNameRegexes() {
		return this.systemNameRegexes;
	}

}
