package com.nlp4re.service;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
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
import com.nlp4re.domain.Template;
import com.nlp4re.repository.AnchorRepository;
import com.nlp4re.repository.ConditionsRepository;
import com.nlp4re.repository.DetailsRepository;
import com.nlp4re.repository.ModalRepository;
import com.nlp4re.repository.ObjectRepository;
import com.nlp4re.repository.SystemNameRepository;
import com.nlp4re.service.logic.RequirementLogicImpl_Eng;

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
	@MockBean
	private Template template;
	@MockBean 
	private RequirementLogicImpl_Eng requirementLogicImpl_Eng;
	
	@SpyBean
	private RequirementService requirementService;

	@Test
	public void test_checkRequirements_returnNull() {
		// given+ when
		List<Map<Integer, String>> result = requirementService.checkRequirements(null);
		List<Map<Integer, String>> result1 = requirementService.checkRequirements("");
		
		// then
		assertThat(result, is(nullValue()));
		assertThat(result1, is(nullValue()));
	}
	
	@Test
	public void test_checkRequirements() {
		// given
		when(anchorRepository.findAll()).thenReturn(List.of(new Anchor("be_able_to", "be able to +", 1)));
		when(conditionsRepository.findAll()).thenReturn(List.of(new Conditions("if", "^if+", 1)));
		when(detailsRepository.findAll()).thenReturn(List.of(new Details("the", "some regexes", 1)));
		when(modalRepository.findAll()).thenReturn(List.of(new Modal("should", 1)));
		when(objectRepository.findAll())
				.thenReturn(List.of(new Object("single_obj", "^a |^an |^the |^one |^each +", 1)));
		when(systemNameRepository.findAll()).thenReturn(List.of(new SystemName("the", "^the [\\w\\s]+", 1)));

		// when
		requirementService.loadRegexes();
		List<Map<Integer, String>> result = requirementService
				.checkRequirements("The System should have the object for the test. ");
		// then
		assertThat(result.size(), is(3));
		assertThat(result.get(0).get(0), is("The System should have the object for the test."));
		assertThat(result.get(1).get(0), is("0"));
	}

	@Test
	public void test_saveRules_NullPointerException() {
		// given+ when + then
		assertThrows(NullPointerException.class, () -> requirementService.saveRules(null));

	}

	@Test
	public void test_saveRules() {
		// given

		when(template.getAnchor()).thenReturn(new Anchor("key", "regex", 1));
		when(template.getConditions()).thenReturn(new Conditions("key", "regex", 1));
		when(template.getDetails()).thenReturn(new Details("key", "regex", 1));
		when(template.getModal()).thenReturn(new Modal("key", 1));
		when(template.getObject()).thenReturn(new Object("key", "regex", 1));
		when(template.getSystemName()).thenReturn(new SystemName("key", "regex", 1));

		// when
		requirementService.saveRules(template);
		
		// then
		verify(template, times(1)).getAnchor();
		verify(template, times(1)).getConditions();
		verify(template, times(1)).getDetails();
		verify(template, times(1)).getModal();
		verify(template, times(1)).getObject();
		verify(template, times(1)).getSystemName();
		

		verify(anchorRepository, times(1)).save(any(Anchor.class));
		verify(conditionsRepository, times(1)).save(any(Conditions.class));
		verify(detailsRepository, times(1)).save(any(Details.class));
		verify(modalRepository, times(1)).save(any(Modal.class));
		verify(objectRepository, times(1)).save(any(Object.class));
		verify(systemNameRepository, times(1)).save(any(SystemName.class));
	}
	
	@Test
	public void test_saveRules_EmptyFields() {
		// given

		when(template.getAnchor()).thenReturn(new Anchor("key", null, 1));
		when(template.getConditions()).thenReturn(new Conditions("key", "", 1));
		when(template.getDetails()).thenReturn(new Details("key", null, 1));
		when(template.getModal()).thenReturn(new Modal("", 1));
		when(template.getObject()).thenReturn(new Object("key", null, 1));
		when(template.getSystemName()).thenReturn(new SystemName("key", null, 1));

		// when
		requirementService.saveRules(template);
		
		// then
		verify(template, times(1)).getAnchor();
		verify(template, times(1)).getConditions();
		verify(template, times(1)).getDetails();
		verify(template, times(1)).getModal();
		verify(template, times(1)).getObject();
		verify(template, times(1)).getSystemName();
		
		verify(anchorRepository, times(0)).save(any(Anchor.class));
		verify(conditionsRepository, times(0)).save(any(Conditions.class));
		verify(detailsRepository, times(0)).save(any(Details.class));
		verify(modalRepository, times(0)).save(any(Modal.class));
		verify(objectRepository, times(0)).save(any(Object.class));
		verify(systemNameRepository, times(0)).save(any(SystemName.class));
	}
	
	
}
