package com.nlp4re.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.nlp4re.logic.RequirementLogicImpl_Eng;
import com.nlp4re.logic.SentenceAnalyzer;
import com.nlp4re.logic.PatternMatcher;
import com.nlp4re.logic.RequirementLogic;

/**
 * This class works as a service
 */
@Service
public class RequirementService {

	public List<Map<Integer, String>> check(String desc) throws IOException {
		if (desc == null || desc.isEmpty()) {
			return null;
		}

		SentenceAnalyzer sentenceAnalyzer = new SentenceAnalyzer();
		PatternMatcher matcher = new PatternMatcher();
		RequirementLogic logic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		Map<Integer, String> sentences = logic.getSentences(desc);
		if (sentences == null) {
			return null;
		} else {
			List<Map<Integer, String>> result = logic.doParse(sentences);
			return result;
		}
	}

	public static void main(String[] args) throws IOException {
		String text = "If the number of products in a warehouse reach the defined minimum limit, then some systems of the"
				+ " inventory subsystem should be generated a product replacement alert for that warehouse. " // T

				+ "The information technology tools used in the design of systems performing "
				+ "safety functions shall be assessed for safety implications on the end-product. " // F

				+ "While the payment of an invoice from a customer has not been confirmed, the subsystem must a daily text"
				+ " message to the cell phone number registered by the customer. "// F

				+ "In case the action of comparing text, those systems of the automation framework product line that only "// F
				+ "include the option to enter text shall provide the tester with the ability to configure a text for comparison with another element. "

				+ "The system shall provide the guest with the ability to place his order. "// F

				+ "If the automation framework is web-based, all systems of the Test Automation product line shall provide the tester with "// F
				+ "the ability to select the type of browser where the test will be run (be it Chrome, Firefox or Safari). "

				+ "After reading the products for a particular location, the Inventory subsystem should provide "// T
				+ "the warehouse owner with the ability to close the product count for that location. "

				+ "The point of sale subsystem shall be able to read a valid credit card from a branch's dataphone. "// T

				+ "The system should be able to obtain the information of a client from a branch's dataphone. "// T

				+ "The inventory subsystem could provide the warehouse manager with the ability to "
				+ "eliminate a purchase order, if and only if the purchase order has not been dispatched. "// T

				+ "The system LALALA provide an ATM with the ability to register a sale in a cash register without presenting more than 2 different screens. "// F

				+ "The inventory subsystem LALALA provide the inventory manager with the ability to associate between 1 and 3 bar code reading guns to a cash register. "// F

				+ " As soon as the daily activity cycle ends, the Oktupus system must restart all the sensors connected in the home. ";// T

		// + "The VMS system will consume as few units of energy as possible during the normal operation of the
		// intelligent bracelet.";
		SentenceAnalyzer sentenceAnalyzer = new SentenceAnalyzer();
		PatternMatcher matcher = new PatternMatcher();

		RequirementLogic logic = new RequirementLogicImpl_Eng(sentenceAnalyzer, matcher);
		List<Map<Integer, String>> result = logic.doParse(logic.getSentences(text));
		result.get(1).forEach((index, sentence) -> {
			System.out.println("COMPLIANT" + index + ".." + sentence);
		});
	}

}
