package com.nlp4re.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Description;

@Repository
public interface  DescriptionRespository extends PagingAndSortingRepository<Description, Long> {

	
	Description findById(int id);
}
