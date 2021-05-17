package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Anchor;

@Repository
public interface AnchorRepository extends CrudRepository<Anchor, Integer>{
	
	List<Anchor> findAll();
} 