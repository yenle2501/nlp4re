package com.nlp4re.service;

import static org.junit.Assert.assertEquals;
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
import com.nlp4re.domain.Activities;
import com.nlp4re.domain.PreCondition;
import com.nlp4re.domain.Details;
import com.nlp4re.domain.ModalVerb;
import com.nlp4re.domain.Objects;
import com.nlp4re.domain.SystemName;
import com.nlp4re.domain.Template;
import com.nlp4re.repository.ActivitiesRepository;
import com.nlp4re.repository.PreConditionRepository;
import com.nlp4re.repository.DetailsRepository;
import com.nlp4re.repository.ModalVerbRepository;
import com.nlp4re.repository.ObjectRepository;
import com.nlp4re.repository.SystemNameRepository;
import com.nlp4re.service.logic.RequirementLogicImpl_Eng;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RequirementServiceTest {

	@MockBean
	private ActivitiesRepository anchorRepository;
	@MockBean
	private PreConditionRepository conditionsRepository;
	@MockBean
	private DetailsRepository detailsRepository;
	@MockBean
	private ModalVerbRepository modalRepository;
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
		assertEquals(result, null);
		assertEquals(result1, null);
	}
	
	@Test
	public void test_checkRequirements() {
		// given
		when(anchorRepository.findAll()).thenReturn(List.of(new Activities("be_able_to", "be able to +", 1)));
		when(conditionsRepository.findAll()).thenReturn(List.of(new PreCondition("if", "^if+", 1)));
		when(detailsRepository.findAll()).thenReturn(List.of(new Details("the", "some regexes", 1)));
		when(modalRepository.findAll()).thenReturn(List.of(new ModalVerb("should", 1)));
		when(objectRepository.findAll())
				.thenReturn(List.of(new Objects("single_obj", "^a |^an |^the |^one |^each +", 1)));
		when(systemNameRepository.findAll()).thenReturn(List.of(new SystemName("the", "^the [\\w\\s]+", 1)));

		// when
		requirementService.loadRegexes();
		List<Map<Integer, String>> result = requirementService
				.checkRequirements("The System should have the object for the test. ");
		// then
		assertEquals(result.size(), 3);
		assertEquals(result.get(0).get(0), "The System should have the object for the test.");
		assertEquals(result.get(1).get(0), "0");
	}

	@Test
	public void test_saveRules_NullPointerException() {
		// given+ when + then
		assertThrows(NullPointerException.class, () -> requirementService.saveRules(null));

	}
	
	@Test
	public void test_saveRules() {
		// given

		when(template.getAnchor()).thenReturn(new Activities("key", "regex", 1));
		when(template.getConditions()).thenReturn(new PreCondition("key", "regex", 1));
		when(template.getDetails()).thenReturn(new Details("key", "regex", 1));
		when(template.getModal()).thenReturn(new ModalVerb("key", 1));
		when(template.getObjects()).thenReturn(new Objects("key", "regex", 1));
		when(template.getSystemName()).thenReturn(new SystemName("key", "regex", 1));

		// when
		requirementService.saveRules(template);
		
		// then
		verify(template, times(1)).getAnchor();
		verify(template, times(1)).getConditions();
		verify(template, times(1)).getDetails();
		verify(template, times(1)).getModal();
		verify(template, times(1)).getObjects();
		verify(template, times(1)).getSystemName();
		

		verify(anchorRepository, times(1)).save(any(Activities.class));
		verify(conditionsRepository, times(1)).save(any(PreCondition.class));
		verify(detailsRepository, times(1)).save(any(Details.class));
		verify(modalRepository, times(1)).save(any(ModalVerb.class));
		verify(objectRepository, times(1)).save(any(Objects.class));
		verify(systemNameRepository, times(1)).save(any(SystemName.class));
	}
	
	@Test
	public void test_saveRules_EmptyFields() {
		// given

		when(template.getAnchor()).thenReturn(new Activities("key", "", 1));
		when(template.getConditions()).thenReturn(new PreCondition("key", "", 1));
		when(template.getDetails()).thenReturn(new Details("key", "", 1));
		when(template.getModal()).thenReturn(new ModalVerb("", 1));
		when(template.getObjects()).thenReturn(new Objects("key", "", 1));
		when(template.getSystemName()).thenReturn(new SystemName("key", "", 1));

		// when
		requirementService.saveRules(template);
		
		// then
		verify(template, times(1)).getAnchor();
		verify(template, times(1)).getConditions();
		verify(template, times(1)).getDetails();
		verify(template, times(1)).getModal();
		verify(template, times(1)).getObjects();
		verify(template, times(1)).getSystemName();
		
		verify(anchorRepository, times(0)).save(any(Activities.class));
		verify(conditionsRepository, times(0)).save(any(PreCondition.class));
		verify(detailsRepository, times(0)).save(any(Details.class));
		verify(modalRepository, times(0)).save(any(ModalVerb.class));
		verify(objectRepository, times(0)).save(any(Objects.class));
		verify(systemNameRepository, times(0)).save(any(SystemName.class));
	}
	
	@Test
	public void test_saveRules_NullFields() {
		// given

		when(template.getAnchor()).thenReturn(new Activities("key", null, 1));
		when(template.getConditions()).thenReturn(new PreCondition("key", null, 1));
		when(template.getDetails()).thenReturn(new Details("key", null, 1));
		when(template.getModal()).thenReturn(new ModalVerb(null, 1));
		when(template.getObjects()).thenReturn(new Objects("key", null, 1));
		when(template.getSystemName()).thenReturn(new SystemName("key",null, 1));

		// when
		requirementService.saveRules(template);
		
		// then
		verify(template, times(1)).getAnchor();
		verify(template, times(1)).getConditions();
		verify(template, times(1)).getDetails();
		verify(template, times(1)).getModal();
		verify(template, times(1)).getObjects();
		verify(template, times(1)).getSystemName();
		
		verify(anchorRepository, times(0)).save(any(Activities.class));
		verify(conditionsRepository, times(0)).save(any(PreCondition.class));
		verify(detailsRepository, times(0)).save(any(Details.class));
		verify(modalRepository, times(0)).save(any(ModalVerb.class));
		verify(objectRepository, times(0)).save(any(Objects.class));
		verify(systemNameRepository, times(0)).save(any(SystemName.class));
	}
	
}
