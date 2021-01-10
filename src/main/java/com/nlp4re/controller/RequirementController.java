package com.nlp4re.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.nlp4re.domain.Requirement;
import com.nlp4re.service.RequirementService;

@RestController
@RequestMapping("/description")
@CrossOrigin(origins = "http://localhost:3000")
public class RequirementController {

	// private final Logger log = LoggerFactory.getLogger(DescriptionController.class);

	@Autowired
	private RequirementService service;

	// @PutMapping("/test")
	// public ResponseEntity<Requirement> check(@RequestBody Requirement desc) {
	// System.out.println("in PUT mapping: " + desc.getDescription());
	//
	// return new ResponseEntity<>(service.saveText(desc), HttpStatus.OK);
	// }

	@RequestMapping(path = "/test", method = RequestMethod.GET)
	public Requirement getConformedSentences(int id) {
		return service.findById(id);
	}

	@RequestMapping(path = "/check", method = RequestMethod.PUT)
	public ResponseEntity<Requirement> saveCurrentAccount(@Valid @RequestBody Requirement requirement) throws FileNotFoundException, IOException {

		System.out.println("RECEIVED Requirements: " + requirement.getDescription());
		Map<Integer, String> conformance_sentence = service.save(requirement);

		List<String> result = new LinkedList<String>();
		conformance_sentence.forEach((id, sentence) -> {
			result.add(sentence);
		});
		Requirement re = new Requirement();
		re.setDescription(result.toString());

		return new ResponseEntity<>(re, HttpStatus.OK);
	}

	// maybe is not necessary
	// @RequestMapping(path = "/", method = RequestMethod.POST)
	// public Requirement save(@Valid @RequestBody Requirement requirement) {
	// return service.save(requirement);
	// }

}
