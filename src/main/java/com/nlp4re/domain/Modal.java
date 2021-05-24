package com.nlp4re.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Modal")
public class Modal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	private String key_name;

	@NotNull
	@Column
	private int required;

	public Modal() {

	}

	public Modal(String key_name, int required) {
		this.key_name = key_name;
		this.required = required;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey_name() {
		return key_name;
	}

	public void setKey_name(String key_name) {
		this.key_name = key_name;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}
}
