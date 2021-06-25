package com.nlp4re.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "objects")
public class Objects {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	private String key_name;

	@NotNull
	private String regex;

	@NotNull
	@Column
	private int required;

	public Objects() {

	}

	public Objects(String key_name, String regex, int required) {
		this.key_name = key_name;
		this.regex = regex;
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

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}
}
