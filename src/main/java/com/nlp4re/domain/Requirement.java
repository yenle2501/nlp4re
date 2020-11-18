package com.nlp4re.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Requirement {

	@Id
	@NotNull
	private String id;

	@NotNull
	private String description;

	public Requirement() {
	}

	public Requirement( String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
