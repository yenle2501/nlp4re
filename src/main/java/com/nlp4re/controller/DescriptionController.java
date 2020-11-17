package com.nlp4re.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nlp4re.domain.Description;
import com.nlp4re.service.DescriptionService;


@RestController
@RequestMapping("/description")
@CrossOrigin(origins="http://localhost:3000")
public class DescriptionController {
	
	 //private final Logger log = LoggerFactory.getLogger(DescriptionController.class);
	 
	@Autowired
	private DescriptionService service;
	
	
	@PutMapping("/test")
	public ResponseEntity<Description> check(@RequestBody Description desc) {
		System.out.println("in PUT mapping: " + desc.getDescription());
		return new ResponseEntity<>(service.saveText(desc), HttpStatus.OK);
	}
}
