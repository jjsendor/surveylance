package pl.edu.agh.iosr.surveylance.pages.question;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.QuestionManager;

/**
 * Controller class responsible for updating question content.
 * 
 * @author kuba
 */
@ContentType("application/json")
public class UpdateQuestion {

	private static final Logger logger = Logger.getLogger(UpdateQuestion.class);

	@Inject
	private Request request;

	@Inject
	private QuestionManager questionManager;

	/**
	 * Dispatches client request. Updates question content using question
	 * manager. Parameters from request:
	 * <dl>
	 * <dt>content</dt>
	 * <dd>new question's content</dd>
	 * </dl>
	 * 
	 * @param questionId
	 *            id of question which content is updated
	 * 
	 * @return JSON object response:
	 * 
	 *         <pre>
	 * {
	 *     updated: {true/false} // was question updated?
	 * }
	 * </pre>
	 */
	Object onActivate(long questionId) {
		boolean updated = false;

		try {
			String content = request.getParameter("content");
			questionManager.setQuestionContent(questionId, content);
			updated = true;
		} catch (RuntimeException e) {
			logger.warn("Question " + questionId + " was not updated.", e);
		}

		JSONObject response = new JSONObject("{updated: " + updated + "}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
