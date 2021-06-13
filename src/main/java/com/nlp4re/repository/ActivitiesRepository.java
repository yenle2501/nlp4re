package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Activities;

@Repository
public interface ActivitiesRepository extends CrudRepository<Activities, Integer>{
	
	List<Activities> findAll();
	
} 