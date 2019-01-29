package pl.edu.agh.iosr.surveylance.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.SurveyRestorationService;

/**
 * This is implementation of {@link SurveyRestorationService} which provides
 * methods for restoring survey structure from JSON object and for serializing
 * survey structure to JSON object.
 *
 * @author kuba
 *
 */
public class SurveyRestorationServiceImpl implements SurveyRestorationService {

	private static final String ID = "id";
	private static final String MODIFICATIONS = "modifications";
	private static final String POSITION = "position";

	private static final String SURVEY_NAME = "name";
	private static final String SURVEY_DESCRIPTION = "description";
	private static final String SURVEY_EXPIRATION_DATE = "expirationDate";
	private static final String SURVEY_ROOT_COMPONENT = "rootComponent";

	private static final String COMPONENT_COMPONENTS = "components";
	private static final String COMPONENT_QUESTION = "question";

	private static final String QUESTION_TYPE = "type";
	private static final String QUESTION_KIND = "kind";
	private static final String QUESTION_CONTENT = "content";
	private static final String QUESTION_ANSWERS = "answers";

	private static final String ANSWER_VALUE = "value";

	private ComponentManager componentManager;

	private SurveyDAO surveyDAO;
	private ComponentDAO componentDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSurveyDAO(SurveyDAO surveyDAO) {
		this.surveyDAO = surveyDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentDAO(ComponentDAO componentDAO) {
		this.componentDAO = componentDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQuestionDAO(QuestionDAO questionDAO) {
		this.questionDAO = questionDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnswerDAO(AnswerDAO answerDAO) {
		this.answerDAO = answerDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentManager(ComponentManager componentManager) {
		this.componentManager = componentManager;
	}

	/**
	 * Restores single answer.
	 * 
	 * @param question
	 *            {@link Question} object
	 * @param jsonAnswer
	 *            {@link JSONObject} with answer
	 * @return {@link JSONObject} with restored answer
	 */
	private JSONObject restoreAnswer(Question question, JSONObject jsonAnswer) {
		Answer answer = null;

		if (question.getType() == QuestionType.NUMERIC) {
			answer = new NumericAnswer();

			if (!jsonAnswer.isNull(ANSWER_VALUE)) {
				((NumericAnswer) answer).setValue(jsonAnswer
						.getDouble(ANSWER_VALUE));
			}
		} else if (question.getType() == QuestionType.TEXT) {
			answer = new TextAnswer();

			if (!jsonAnswer.isNull(ANSWER_VALUE)) {
				((TextAnswer) answer).setValue(jsonAnswer
						.getString(ANSWER_VALUE));
			}
		}

		answer.setQuestion(question);

		if (!jsonAnswer.isNull(MODIFICATIONS))
			answer.setModifications(jsonAnswer.getInt(MODIFICATIONS));

		if (!jsonAnswer.isNull(POSITION))
			answer.setPosition(jsonAnswer.getInt(POSITION));

		answerDAO.create(answer);

		jsonAnswer.put(ID, answer.getId());

		return jsonAnswer;
	}

	/**
	 * Restores single question.
	 * 
	 * @param parent
	 *            {@link Component} object
	 * @param jsonQuestion
	 *            {@link JSONObject} with question
	 * @return {@link JSONObject} with restored question
	 */
	private JSONObject restoreQuestion(Component parent, JSONObject jsonQuestion) {
		Question question = new Question();
		question.setParentComponent(parent);

		if (!jsonQuestion.isNull(MODIFICATIONS))
			question.setModifications(jsonQuestion.getInt(MODIFICATIONS));

		if (!jsonQuestion.isNull(QUESTION_CONTENT))
			question.setContent(jsonQuestion.getString(QUESTION_CONTENT));

		if (!jsonQuestion.isNull(QUESTION_TYPE)) {
			QuestionType type = null;
			String typeStr = jsonQuestion.getString(QUESTION_TYPE);

			if ("numeric".equals(typeStr))
				type = QuestionType.NUMERIC;
			else if ("text".equals(typeStr))
				type = QuestionType.TEXT;

			question.setType(type);
		}

		if (!jsonQuestion.isNull(QUESTION_KIND)) {
			QuestionKind kind = null;
			String kindStr = jsonQuestion.getString(QUESTION_KIND);

			if ("radio".equals(kindStr))
				kind = QuestionKind.RADIO;
			else if ("checkbox".equals(kindStr))
				kind = QuestionKind.CHECKBOX;
			else if ("input_text".equals(kindStr))
				kind = QuestionKind.INPUT_TEXT;

			question.setKind(kind);
		}

		questionDAO.create(question);

		jsonQuestion.put(ID, question.getId());

		if (!jsonQuestion.isNull(QUESTION_ANSWERS)) {
			JSONArray jsonAnswers = jsonQuestion.getJSONArray(QUESTION_ANSWERS);
			JSONArray jsonAnswers2 = new JSONArray();

			for (int i = 0; i < jsonAnswers.length(); i++) {
				if (!jsonAnswers.isNull(i)) {
					JSONObject jsonAnswer = jsonAnswers.getJSONObject(i);
					jsonAnswers2.put(restoreAnswer(question, jsonAnswer));
				}
			}

			jsonQuestion.put(QUESTION_ANSWERS, jsonAnswers2);
		}

		return jsonQuestion;
	}

	/**
	 * Restores single component.
	 * 
	 * @param parent
	 *            {@link Component} object
	 * @param jsonComponent
	 *            {@link JSONObject} with component
	 * @return {@link JSONObject} with restored component
	 */
	private JSONObject restoreComponent(Component parent,
			JSONObject jsonComponent) {
		Component component = new Component();
		component.setParentComponent(parent);

		if (!jsonComponent.isNull(MODIFICATIONS))
			component.setModifications(jsonComponent.getInt(MODIFICATIONS));

		if (!jsonComponent.isNull(POSITION))
			component.setPosition(jsonComponent.getInt(POSITION));

		componentDAO.create(component);

		jsonComponent.put(ID, component.getId());

		if (!jsonComponent.isNull(COMPONENT_QUESTION)) {
			JSONObject jsonQuestion = jsonComponent
					.getJSONObject(COMPONENT_QUESTION);

			jsonComponent.put(COMPONENT_QUESTION, restoreQuestion(component,
					jsonQuestion));
		} else {
			if (!jsonComponent.isNull(COMPONENT_COMPONENTS)) {
				JSONArray jsonComponents = jsonComponent
						.getJSONArray(COMPONENT_COMPONENTS);
				JSONArray jsonComponents2 = new JSONArray();

				for (int i = 0; i < jsonComponents.length(); i++) {
					if (!jsonComponents.isNull(i)) {
						JSONObject jsonChildComponent = jsonComponents
								.getJSONObject(i);

						jsonComponents2.put(restoreComponent(component,
								jsonChildComponent));
					}
				}

				jsonComponent.put(COMPONENT_COMPONENTS, jsonComponents2);
			}
		}

		return jsonComponent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject restoreSurvey(Survey survey, JSONObject jsonSurvey) {
		// remove current survey structure
		Component rootComponent = survey.getRootComponent();
		survey.setRootComponent(null);
		componentManager.deleteComponent(rootComponent);

		if (!jsonSurvey.isNull(SURVEY_ROOT_COMPONENT)) {
			JSONObject jsonRootComponent = jsonSurvey
					.getJSONObject(SURVEY_ROOT_COMPONENT);

			// create new rootComponent
			rootComponent = new Component();

			if (!jsonRootComponent.isNull(MODIFICATIONS)) {
				rootComponent.setModifications(jsonRootComponent
						.getInt(MODIFICATIONS));
			}

			componentDAO.create(rootComponent);

			jsonRootComponent.put(ID, rootComponent.getId());

			survey.setRootComponent(rootComponent);
			surveyDAO.update(survey);

			if (!jsonRootComponent.isNull(COMPONENT_COMPONENTS)) {
				JSONArray jsonComponents = jsonRootComponent
						.getJSONArray(COMPONENT_COMPONENTS);
				JSONArray jsonComponents2 = new JSONArray();

				for (int i = 0; i < jsonComponents.length(); i++) {
					if (!jsonComponents.isNull(i)) {
						JSONObject jsonComponent = jsonComponents
								.getJSONObject(i);
						jsonComponents2.put(restoreComponent(rootComponent,
								jsonComponent));
					}
				}

				jsonRootComponent.put(COMPONENT_COMPONENTS, jsonComponents2);
			}

			jsonSurvey.put(SURVEY_ROOT_COMPONENT, jsonRootComponent);
		}

		return jsonSurvey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject restoreSurvey(long surveyId, JSONObject jsonSurvey) {
		Survey survey = surveyDAO.findById(surveyId, false);
		return restoreSurvey(survey, jsonSurvey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject answerToJSON(Answer answer) {
		JSONObject jsonAnswer = answerJSONObject(answer);

		return jsonAnswer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject questionToJSON(Question question) {
		JSONObject jsonQuestion = questionJSONObject(question);

		List<Answer> answers = answerDAO.findByQuestion(question);
		JSONArray jsonAnswers = new JSONArray();

		for (Answer answer : answers) {
			JSONObject jsonAnswer = answerToJSON(answer);
			jsonAnswers.put(jsonAnswer);
		}

		jsonQuestion.put(QUESTION_ANSWERS, jsonAnswers);

		return jsonQuestion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject componentToJSON(Component component) {
		JSONObject jsonComponent = componentJSONObject(component);

		Question question = questionDAO.findByParent(component);

		if (question != null) {
			JSONObject jsonQuestion = questionToJSON(question);
			jsonComponent.put(COMPONENT_QUESTION, jsonQuestion);
			jsonComponent.put(COMPONENT_COMPONENTS, JSONObject.NULL);
		}
		else {
			List<Component> components = componentDAO.findByParent(component);
			JSONArray jsonComponents = new JSONArray();

			for (Component child : components) {
				JSONObject jsonChildComponent = componentToJSON(child);
				jsonComponents.put(jsonChildComponent);
			}

			jsonComponent.put(COMPONENT_QUESTION, JSONObject.NULL);
			jsonComponent.put(COMPONENT_COMPONENTS, jsonComponents);
		}

		return jsonComponent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject surveyToJSON(Survey survey) {
		JSONObject jsonSurvey = surveyJSONObject(survey);

		jsonSurvey.put(SURVEY_ROOT_COMPONENT,
				survey.getRootComponent() != null ?
						componentToJSON(survey.getRootComponent())
						: JSONObject.NULL);

		return jsonSurvey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject surveyToJSON(long surveyId) {
		Survey survey = surveyDAO.findById(surveyId, false);
		return surveyToJSON(survey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject answerJSONObject(Answer answer) {
		JSONObject jsonAnswer = new JSONObject();

		Long id = answer.getId();
		Integer modifications = answer.getModifications();
		Integer position = answer.getPosition();

		jsonAnswer.put(ID, id != null ? id : JSONObject.NULL);

		Question question = answer.getQuestion();

		if (question.getType() == QuestionType.NUMERIC)
			jsonAnswer.put(ANSWER_VALUE, ((NumericAnswer) answer).getValue());
		else if (question.getType() == QuestionType.TEXT)
			jsonAnswer.put(ANSWER_VALUE, ((TextAnswer) answer).getValue());

		jsonAnswer.put(MODIFICATIONS,
				modifications != null ? modifications : JSONObject.NULL);
		jsonAnswer.put(POSITION, position != null ? position : JSONObject.NULL);

		return jsonAnswer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject questionJSONObject(Question question) {
		JSONObject jsonQuestion = new JSONObject();

		Long id = question.getId();
		Integer modifications = question.getModifications();
		String content = question.getContent();
		QuestionType type = question.getType();
		QuestionKind kind = question.getKind();

		jsonQuestion.put(ID, id != null ? id : JSONObject.NULL);
		jsonQuestion.put(MODIFICATIONS,
				modifications != null ? modifications : JSONObject.NULL);
		jsonQuestion.put(QUESTION_CONTENT,
				content != null ? content : JSONObject.NULL);
		jsonQuestion.put(QUESTION_TYPE,
				type != null ? type.toString().toLowerCase() : JSONObject.NULL);
		jsonQuestion.put(QUESTION_KIND,
				kind != null ? kind.toString().toLowerCase() : JSONObject.NULL);

		return jsonQuestion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject componentJSONObject(Component component) {
		JSONObject jsonComponent = new JSONObject();

		Long id = component.getId();
		Integer modifications = component.getModifications();
		Integer position = component.getPosition();

		jsonComponent.put(ID, id != null ? id : JSONObject.NULL);
		jsonComponent.put(MODIFICATIONS,
				modifications != null ? modifications : JSONObject.NULL);
		jsonComponent.put(POSITION,
				position != null ? position : JSONObject.NULL);

		return jsonComponent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject surveyJSONObject(Survey survey) {
		JSONObject jsonSurvey = new JSONObject();

		Long id = survey.getId();
		Integer modifications = survey.getModifications();
		String name = survey.getName();
		String description = survey.getDescription();
		Date expirationDate = survey.getExpirationDate();

		jsonSurvey.put(ID, id != null ? id : JSONObject.NULL);
		jsonSurvey.put(MODIFICATIONS,
				modifications != null ? modifications : JSONObject.NULL);
		jsonSurvey.put(SURVEY_NAME, name != null ? name : JSONObject.NULL);
		jsonSurvey.put(SURVEY_DESCRIPTION,
				description != null ? description : JSONObject.NULL);
		jsonSurvey.put(SURVEY_EXPIRATION_DATE,
				expirationDate != null ? expirationDate.toString()
						: JSONObject.NULL);

		return jsonSurvey;
	}

}
