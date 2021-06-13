package com.nlp4re.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nlp4re.domain.Requirement;
import com.nlp4re.domain.Template;
import com.nlp4re.service.RequirementService;

import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/description")
@CrossOrigin(origins = "http://localhost:3000")
public class RequirementController {

	@Autowired
	private RequirementService service;

	/**
	 * check requirements description
	 * @param requirement the requirements description
	 * @return ResponseEntity
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(path = "/check", method = RequestMethod.PUT)
	public ResponseEntity<List<Map<Integer, String>>> checkRequirement(@Valid @RequestBody Requirement requirement)
			throws FileNotFoundException, IOException {
   
		if (requirement == null) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		List<Map<Integer, String>> response = service.checkRequirements(requirement.getDescription());
		if (response == null) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * add rules of the template
	 * 
	 * @param templateRule  contains rules for the template
	 * @return ResponseEntity<httpStatus>
	 */
	@RequestMapping(path = "/addRules", method = RequestMethod.POST)
	public ResponseEntity<HttpStatus> addRules(@Valid @RequestBody Template templateRule)
			throws FileNotFoundException, IOException {

		if (templateRule == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {

			service.saveRules(templateRule);
			return new ResponseEntity<>( HttpStatus.CREATED);
		}
	}
}
