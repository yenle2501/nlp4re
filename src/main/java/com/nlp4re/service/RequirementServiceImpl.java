package com.nlp4re.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlp4re.domain.Requirement;
import com.nlp4re.logic.RequirementLogic;
import com.nlp4re.repository.RequirementRespository;

@Service
public class RequirementServiceImpl implements RequirementService {

	@Autowired
	private RequirementRespository repository;

	@Override
	public Requirement findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Requirement requirement) {
		RequirementLogic logic = new RequirementLogic();

	}

	public static void main(String[] args) throws IOException {
		String text =
//				"If the number of products in a warehouse reach the defined minimum limit then, the inventory subsystem "
//				+ "should generate a product replacement alert for that warehouse. While the payment of an invoice from a customer has not been confirmed, the subsystem must send a daily"
//				+ " text message to the cell phone number registered by the customer. "
//				+
				"In case the action of comparing text is included, those systems of the automation framework product line that only "
				+ "include the option to enter text shall provide the tester with the ability to configure a text for comparison with another element.";

		RequirementLogic logic = new RequirementLogic();

		logic.doParse(logic.getSentences(text));

	}

}
