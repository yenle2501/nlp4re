package com.nlp4re.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nlp4re.domain.Requirement;
import com.nlp4re.domain.Template;
import com.nlp4re.service.RequirementService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RequirementControllerTest {

	@MockBean
	private RequirementService requirementService;

	@MockBean
	private Requirement requirement;

	@MockBean
	private Template template;

	@Autowired
	private RequirementController requirementController;

	@Test
	void testCheckRequirement_returnNull() throws Exception {
		// given + when
		ResponseEntity<List<Map<Integer, String>>> result = requirementController.checkRequirement(null);
		// then
		assertEquals(result.getBody(), null);
		assertEquals(result.getStatusCode(), HttpStatus.NO_CONTENT);
	}

	@Test
	void testCheckRequirement_ServiceReturnNull() throws Exception {
		// given
		when(requirementService.checkRequirements(anyString())).thenReturn(null);
		when(requirement.getDescription()).thenReturn("some string");
		// when
		ResponseEntity<List<Map<Integer, String>>> result = requirementController.checkRequirement(requirement);
		// then
		assertEquals(result.getBody(), null);
		assertEquals(result.getStatusCode(), HttpStatus.NO_CONTENT);
	}

	@Test
	void testCheckRequirement() throws Exception {
		// given
		List<Map<Integer, String>> response = new LinkedList<Map<Integer, String>>();
		Map<Integer, String> map1 = new HashMap<Integer, String>();
		map1.put(1, "key1");
		Map<Integer, String> map2 = new HashMap<Integer, String>();
		map2.put(1, "key2");
		Map<Integer, String> map3 = new HashMap<Integer, String>();
		map3.put(1, "key3");
		response.add(map1);
		response.add(map2);
		response.add(map3);

		when(requirementService.checkRequirements(anyString())).thenReturn(response);
		when(requirement.getDescription()).thenReturn("some string");
		// when
		ResponseEntity<List<Map<Integer, String>>> result = requirementController.checkRequirement(requirement);
		// then

		assertEquals(result.getBody().size(), 3);
		assertEquals(result.getBody().get(0).get(1), "key1");
		assertEquals(result.getBody().get(1).get(1), "key2");
		assertEquals(result.getBody().get(2).get(1), "key3");
		assertEquals(result.getStatusCode(), HttpStatus.OK);
	}

	@Test
	void testChangeRules_returnNull() throws Exception {
		// given + when
		ResponseEntity<HttpStatus> result = requirementController.addRules(null);
		// then
		assertEquals(result.getBody(), null);
		assertEquals(result.getStatusCode(), HttpStatus.NO_CONTENT);
	}

	@Test
	void testChangeRules() throws Exception {
		// given + when
		ResponseEntity<HttpStatus> result = requirementController.addRules(template);
		// then

		verify(requirementService, times(1)).saveRules(template);
		assertEquals(result.getStatusCode(), HttpStatus.CREATED);
	}

}
