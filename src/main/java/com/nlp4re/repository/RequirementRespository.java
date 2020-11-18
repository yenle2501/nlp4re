package com.nlp4re.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Requirement;

@Repository
public interface  RequirementRespository extends PagingAndSortingRepository<Requirement, Long> {

	
	Requirement findById(int id);
}
