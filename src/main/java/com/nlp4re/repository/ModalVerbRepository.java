package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.ModalVerb;

@Repository
public interface ModalVerbRepository extends CrudRepository<ModalVerb, Integer>{

	 List<ModalVerb> findAll();
}
