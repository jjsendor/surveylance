package pl.edu.agh.iosr.surveylance.pages.answer;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.QuestionManager;

/**
 * Controller class responsible for updating answer's information.
 * 
 * @author kuba
 */
public class UpdateAnswer {

	private static final Logger logger = Logger.getLogger(UpdateAnswer.class);

	@Inject
	private Request request;

	@Inject
	private QuestionManager questionManager;

	/**
	 * Dispatches client request. Updates answer using question manager.
	 * Parameters from request:
	 * <dl>
	 *   <dt>value</dt>
	 *   <dd>new value of answer</dd>
	 * </dl>
	 *
	 * @param	answerId	id of answer which is being updated
	 *
	 * @return	JSON object response in form of:
	 * <pre>
	 * {
	 *     updated: true/false
	 * }
	 * </pre>
	 * Value of <code>update</code> determines if answer was updated correctly
	 * or there was a problem (e.g. answer with given <code>answerId</code> does
	 * not exist in database).
	 */
	Object onActivate(long answerId) {
		Boolean updated = false;

		try {
			String value = request.getParameter("value");
			questionManager.setAnswer(answerId, value);
			updated = true;
		} catch (RuntimeException e) {
			logger.warn("Answer " + answerId + " was not updated.", e);
		}

		JSONObject response = new JSONObject("{updated: '" + updated + "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
