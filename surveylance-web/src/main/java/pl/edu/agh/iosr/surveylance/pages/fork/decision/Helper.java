package pl.edu.agh.iosr.surveylance.pages.fork.decision;

import org.apache.tapestry5.json.JSONObject;

import pl.edu.agh.iosr.surveylance.entities.Decision;

public class Helper {

	static JSONObject createJSONFromDecision(Decision decision) {
		JSONObject answer = null;

		if (decision != null) {
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append("{id: '");
			responseBuffer.append(decision.getId());

			if (decision.getForkComponent() != null) {
				responseBuffer.append("', forkId: '");
				responseBuffer.append(decision.getForkComponent().getId());
			}

			if (decision.getComponent() != null) {
				responseBuffer.append("', choiceId: '");
				responseBuffer.append(decision.getComponent().getId());
			}

			if (decision.getAnswer() != null) {
				responseBuffer.append("', answerId: '");
				responseBuffer.append(decision.getAnswer().getId());
			}

			responseBuffer.append("'}");

			answer = new JSONObject(responseBuffer.toString());
		} else
			answer = new JSONObject();

		return answer;
	}

}
