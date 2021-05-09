package com.nlp4re.service;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;


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
		// when
		List<Map<Integer, String>> result = requirementService.checkRequirements("The System should have the object for the test. ");
		
		// then
		assertThat(result.size(), is(3));
		assertThat(result.get(0).get(0), is("The System should have the object for the test."));
		assertThat(result.get(1).get(0), is("0"));
	}
}
