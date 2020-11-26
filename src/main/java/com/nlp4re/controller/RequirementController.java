package com.nlp4re.controller;

import java.io.IOException;

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
@RequestMapping("/requirement")
@CrossOrigin(origins="http://localhost:3000")
public class RequirementController {
	
	 //private final Logger log = LoggerFactory.getLogger(DescriptionController.class);
	 
	@Autowired
	private RequirementService service;
	
	
//	@PutMapping("/test")
//	public ResponseEntity<Requirement> check(@RequestBody Requirement desc) {
//		System.out.println("in PUT mapping: " + desc.getDescription());
//		
//		return new ResponseEntity<>(service.saveText(desc), HttpStatus.OK);
//	}
	
	@RequestMapping(path = "/current", method = RequestMethod.GET)
	public Requirement getCurrentRequirement(int id) {
		return service.findById(id);
	}

	@RequestMapping(path = "/current", method = RequestMethod.PUT)
	public void saveCurrentAccount(@Valid @RequestBody Requirement requirement) {
		 try {
			service.save(requirement);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	maybe is not necessary
//	@RequestMapping(path = "/", method = RequestMethod.POST)
//	public Requirement save(@Valid @RequestBody Requirement requirement) {
//		return service.save(requirement);
//	}
	
}
