package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.PreCondition;

@Repository
public interface PreConditionRepository extends CrudRepository<PreCondition, Integer>{

	List<PreCondition> findAll();
}
