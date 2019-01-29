package pl.edu.agh.iosr.surveylance.pages.question;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.exceptions.UnknownQuestionKindException;
import pl.edu.agh.iosr.surveylance.exceptions.UnknownQuestionTypeException;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.exceptions.ComponentDoesNotExistException;

/**
 * Controller class responsible for question creation.
 * 
 * @author kuba
 */
@ContentType("application/json")
public class CreateQuestion {

	private static final Logger logger = Logger.getLogger(CreateQuestion.class);
	
	private static final String CREATION_ERROR_RESPONSE =
		"{component: { id: -1 }, question: { id: -1 }}";

	@Inject
	private Request request;

	@Inject
	private ForkManager hierarchyManager;
	
	@Inject
	private QuestionManager questionManager;

	/**
	 * Dispatches client request. Creates question using question manager.
	 * Parameters from request:
	 * <dl>
	 *   <dt>position</dt>
	 *   <dd>position on which question parent component is added</dd>
	 *
	 *   <dt>type</dt>
	 *   <dd>question type (numeric/text)</dd>
	 *
	 *   <dt>kind</dt>
	 *   <dd>question kind (radio/checkbox/input_text)</dd>
	 * </dl>
	 *
	 * @param parentId
	 *            parent component id of question component
	 *
	 * @return JSON object response in form of:
	 *
	 * <pre>
	 * {
	 *     component: {
	 *         id: component.id,
	 *         modifications: component.modifications,
	 *         position: component.position,
	 *         parentId: component.parentComponent.id
	 *     },
	 *     question: {
	 *         id: question.id,
	 *         modifications: question.modifications,
	 *         type: question.type,
	 *         kind: question.kind
	 *     }
	 * }
	 * </pre>
	 */
	Object onActivate(long parentId) {
		Component parent = hierarchyManager.getComponentById(parentId);
		JSONObject response;

		try {
			if (parent == null) {
				throw new ComponentDoesNotExistException("Component with id "
						+ parentId + " does not exist in database");
			}

			String positionStr = request.getParameter("position");
			String typeStr = request.getParameter("type");
			String kindStr = request.getParameter("kind");

			Integer position = Integer.valueOf(positionStr);

			QuestionType type = null;

			if ("numeric".equals(typeStr))
				type = QuestionType.NUMERIC;
			else if ("text".equals(typeStr))
				type = QuestionType.TEXT;
			else {
				throw new UnknownQuestionTypeException(
						"Unknown question type: \"" + typeStr + "\".");
			}

			QuestionKind kind = null;

			if ("radio".equals(kindStr))
				kind = QuestionKind.RADIO;
			else if ("checkbox".equals(kindStr))
				kind = QuestionKind.CHECKBOX;
			else if ("input_text".equals(kindStr))
				kind = QuestionKind.INPUT_TEXT;
			else {
				throw new UnknownQuestionKindException(
						"Unknown question kind: \"" + kindStr + "\".");
			}

			Question question = new Question();
			question.setType(type);
			question.setKind(kind);

			questionManager.createQuestion(question, parent, position);
			Component component = question.getParentComponent();

			response = new JSONObject("{component: {" + "id: "
					+ component.getId() + "," + "modifications: "
					+ component.getModifications() + "," + "position: "
					+ component.getPosition() + "," + "parentId: "
					+ component.getParentComponent().getId()
					+ "}, question: {" + "id: " + question.getId() + ","
					+ "modifications: " + question.getModifications() + ","
					+ "type: "
					+ question.getType().toString().toLowerCase() + ","
					+ "kind: "
					+ question.getKind().toString().toLowerCase() + "}}");
		} catch (RuntimeException e) {
			logger.warn("Question was not created.", e);
			response = new JSONObject(CREATION_ERROR_RESPONSE);
		}

		return new TextStreamResponse("application/json", response.toString());
	}

}
