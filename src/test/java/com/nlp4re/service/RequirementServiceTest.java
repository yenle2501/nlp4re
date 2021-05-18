package com.nlp4re.service;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.nlp4re.domain.Anchor;
import com.nlp4re.domain.Conditions;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.Modal;
import com.nlp4re.domain.Object;
import com.nlp4re.domain.SystemName;
import com.nlp4re.repository.AnchorRepository;
import com.nlp4re.repository.ConditionsRepository;
import com.nlp4re.repository.DetailsRepository;
import com.nlp4re.repository.ModalRepository;
import com.nlp4re.repository.ObjectRepository;
import com.nlp4re.repository.SystemNameRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RequirementServiceTest {
	
	
	@MockBean
	private AnchorRepository anchorRepository;
	@MockBean
	private ConditionsRepository conditionsRepository;
	@MockBean
	private DetailsRepository detailsRepository;
	@MockBean
	private ModalRepository modalRepository;
	@MockBean
	private ObjectRepository objectRepository;
	@MockBean
	private SystemNameRepository systemNameRepository;

	@SpyBean
	private RequirementService requirementService;
	
	@Test
	public void test_checkRequirements_NullPointerException() {
		// given+ when
		List<Map<Integer, String>> result = requirementService.checkRequirements(null);

		// then
		assertThat(result, is(nullValue()));
	}

	@Test
	public void test_checkRequirements() {
		// given
		when(anchorRepository.findAll()).thenReturn(List.of(new Anchor("be_able_to", "be able to +")));
		when(conditionsRepository.findAll()).thenReturn(List.of(new Conditions("if", "^if+")));
		when(detailsRepository.findAll()).thenReturn(List.of(new Details("the", "some regexes")));
		when(modalRepository.findAll()).thenReturn(List.of(new Modal("should")));
		when(objectRepository.findAll()).thenReturn(List.of(new Object("single_obj", "^a |^an |^the |^one |^each +")));
		when(systemNameRepository.findAll()).thenReturn(List.of(new SystemName("the", "^the [\\w\\s]+")));
		
		// when
		requirementService.loadRegexes();
		List<Map<Integer, String>> result = requirementService
				.checkRequirements("The System should have the object for the test. ");
		// then
		assertThat(result.size(), is(3));
		assertThat(result.get(0).get(0), is("The System should have the object for the test."));
		assertThat(result.get(1).get(0), is("0"));
	}
}
