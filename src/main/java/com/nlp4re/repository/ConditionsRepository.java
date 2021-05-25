package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Conditions;

@Repository
public interface ConditionsRepository extends CrudRepository<Conditions, Integer>{

	List<Conditions> findAll();
}
