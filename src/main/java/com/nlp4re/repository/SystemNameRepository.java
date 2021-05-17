package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.SystemName;

@Repository
public interface SystemNameRepository extends CrudRepository<SystemName, Integer>{

	List<SystemName> findAll();
}
