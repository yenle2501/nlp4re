package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.nlp4re.domain.Details;

@Repository
public interface DetailsRepository extends CrudRepository<Details, Integer>{
	
	List<Details> findAll();
}
