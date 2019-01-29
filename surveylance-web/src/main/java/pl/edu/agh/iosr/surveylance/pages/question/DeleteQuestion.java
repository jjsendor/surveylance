package pl.edu.agh.iosr.surveylance.pages.question;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.QuestionManager;

/**
 * Controller class responsible for question deletion.
 * 
 * @author kuba
 */
@ContentType("application/json")
public class DeleteQuestion {

	private static final Logger logger = Logger.getLogger(DeleteQuestion.class);

	@Inject
	private QuestionManager questionManager;

	/**
	 * Dispatches client request. Deletes question and its parent component.
	 * 
	 * @param questionId
	 *            id of question to be deleted
	 * 
	 * @return JSON object response:
	 * 
	 *         <pre>
	 * {
	 *     deleted: {true/false} // was question deleted?
	 * }
	 * </pre>
	 */
	Object onActivate(long questionId) {
		boolean answer = false;

		try {
			questionManager.deleteQuestion(questionId);
			answer = true;
		} catch (RuntimeException e) {
			logger.warn("Question" + questionId + " was not deleted.", e);
		}

		JSONObject response = new JSONObject("{deleted: '" + answer + "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
