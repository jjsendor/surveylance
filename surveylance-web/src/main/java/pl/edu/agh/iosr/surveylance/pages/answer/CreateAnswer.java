package pl.edu.agh.iosr.surveylance.pages.answer;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.exceptions.QuestionDoesNotExistException;

/**
 * Controller class responsible for creating new answer for existing question.
 *
 * @author kuba
 */
public class CreateAnswer {

	private static final Logger logger = Logger.getLogger(CreateAnswer.class);

	@Inject
	private Request request;

	@Inject
	private QuestionManager questionManager;

	/**
	 * Dispatches client request. Creates answer using question manager.
	 *
	 * @param	questionId	id of question for which answer is being created
	 *
	 * @return	JSON object response in form of:
	 * <pre>
	 * {
	 *     id: answer.id,
	 *     position: answer.position
	 * }
	 * </pre>
	 * If answer cannot be created (question with given <code>questionId</code>
	 * does not exist in database) both <code>id</code> and
	 * <code>position</code> are set to -1.
	 */
	Object onActivate(long questionId) {
		long answerId = -1;
		int answerPosition = -1;
		Question question = questionManager.getQuestion(questionId);
		
		try {
			if (question == null) {
				throw new QuestionDoesNotExistException("Question with id "
						+ questionId + " does not exist in database");
			}

			Answer answer = null;

			String positionStr = request.getParameter("position");

			try {
				int position = Integer.parseInt(positionStr);
				answer = questionManager.createAnswer(question, position);
			} catch (NumberFormatException e) {
				logger.warn("Cannot parse position parameter: " + positionStr,
						e);
				answer = questionManager.createAnswer(question);
			}
			
			answerId = answer.getId();
			answerPosition = answer.getPosition();
		} catch (RuntimeException e) {
			logger.warn("Answer was not created.", e);
		}

		JSONObject response = new JSONObject("{" + "id: " + answerId
				+ "," + "position: " + answerPosition + "}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
