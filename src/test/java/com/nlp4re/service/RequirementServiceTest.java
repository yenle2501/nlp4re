package com.nlp4re.service;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.nlp4re.domain.Anchor;
import com.nlp4re.domain.Conditions;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.Modal;
import com.nlp4re.domain.Object;
import com.nlp4re.domain.SystemName;

public class RequirementServiceTest {

	@Test
	public void test_checkRequirements_NullPointerException() {
		// given
		// when
		RequirementService requirementService = new RequirementService();
		List<Map<Integer, String>> result = requirementService.checkRequirements(null);

		// then
		assertThat(result, is(nullValue()));
	}

	@Test
	public void test_checkRequirements() {
		// given
		RequirementService requirementService = new RequirementService();

		when(requirementService.anchorRepository.findAll()).thenReturn(List.of(new Anchor("the", "some regexes")));
		when(requirementService.conditionsRepository.findAll())
				.thenReturn(List.of(new Conditions("the", "some regexes")));
		when(requirementService.detailsRepository.findAll()).thenReturn(List.of(new Details("the", "some regexes")));
		when(requirementService.modalRepository.findAll()).thenReturn(List.of(new Modal("the")));
		when(requirementService.objectRepository.findAll())
				.thenReturn(List.of(new Object("the", "some regexes")));
		when(requirementService.systemNameRepository.findAll()).thenReturn(List.of(new SystemName("the", "some regexes")));
		// when
		requirementService.loadServiceOperations();
		requirementService.loadRegexes();
		List<Map<Integer, String>> result = requirementService
				.checkRequirements("The System should have the object for the test. ");

		// then
		assertThat(result.size(), is(3));
		assertThat(result.get(0).get(0), is("The System should have the object for the test."));
		assertThat(result.get(1).get(0), is("0"));
	}
}
