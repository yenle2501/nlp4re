package com.nlp4re.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

	@Autowired
	private RequirementService service;

	@RequestMapping(path = "/check", method = RequestMethod.PUT)
	public ResponseEntity<List<Map<Integer, String>>> saveCurrentAccount(@Valid @RequestBody Requirement requirement)
			throws FileNotFoundException, IOException {

		System.out.println("RECEIVED Requirements: " + requirement.getDescription());
		List<Map<Integer, String>> response = service.save(requirement);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
