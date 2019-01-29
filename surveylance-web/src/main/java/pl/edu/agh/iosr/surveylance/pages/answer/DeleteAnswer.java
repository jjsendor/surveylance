package pl.edu.agh.iosr.surveylance.pages.answer;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.QuestionManager;

/**
 * Controller class responsible for deleting answer.
 * 
 * @author kuba
 */
public class DeleteAnswer {

	private static final Logger logger = Logger.getLogger(DeleteAnswer.class);

	@Inject
	private QuestionManager questionManager;

	/**
	 * Dispatches client request. Deletes answer using question manager.
	 *
	 * @param	answerId	id of answer which is being deleted
	 *
	 * @return	JSON object response in form of:
	 * <pre>
	 * {
	 *     deleted: true/false
	 * }
	 * </pre>
	 * Value of <code>deleted</code> determines if answer was deleted correctly
	 * or there was a problem with that (e.g. answer with specified answerId
	 * does not exist).
	 */
	Object onActivate(long answerId) {
		Boolean deleted = false;

		try {
			questionManager.deleteAnswer(answerId);
			deleted = true;
		} catch (RuntimeException e) {
			logger.warn("Answer " + answerId + " was not deleted.", e);
		}

		JSONObject response = new JSONObject("{deleted: '" + deleted + "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
