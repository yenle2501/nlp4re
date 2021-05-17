package com.nlp4re.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nlp4re.domain.Modal;

@Repository
public interface ModalRepository extends CrudRepository<Modal, Integer>{

	 List<Modal> findAll();
}
