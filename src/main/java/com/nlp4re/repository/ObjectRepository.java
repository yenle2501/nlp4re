package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Objects;

@Repository
public interface ObjectRepository extends CrudRepository<Objects, Integer>{

	List<Objects> findAll();
}
