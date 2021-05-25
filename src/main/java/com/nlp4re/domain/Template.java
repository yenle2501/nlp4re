package com.nlp4re.domain;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;


public class Template {


	@Id
	private int id;
	
	@NotNull
	private Anchor anchor;
	
	@NotNull
	private Conditions conditions;
	
	@NotNull
	private Modal modal;
	
	@NotNull
	private Object object;
	
	@NotNull
	private SystemName systemName;
	
	@NotNull
	private Details details;
	
	public Template() {
		
	}
	
	public Template(Anchor anchor, Conditions conditions,Modal modal, Object object,
							SystemName systemName,Details details) {
		this.anchor = anchor;
		this.conditions = conditions;
		this.modal = modal;
		this.object = object;
		this.systemName = systemName;
		this.details = details;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Anchor getAnchor() {
		return this.anchor;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
	}
	
	public Conditions getConditions() {
		return this.conditions;
	}

	public void setConditions(Conditions conditions) {
		this.conditions = conditions;
	}

	public Modal getModal() {
		return this.modal;
	}

	public void setModal(Modal modal) {
		this.modal = modal;
	}
	
	public Object getObject() {
		return this.object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public SystemName getSystemName() {
		return this.systemName;
	}

	public void setSystemName(SystemName systemName) {
		this.systemName = systemName;
	}
	
	public Details getDetails() {
		return this.details;
	}

	public void setDetails(Details details) {
		this.details = details;
	}
}
