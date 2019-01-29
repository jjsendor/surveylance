package pl.edu.agh.iosr.surveylance.service;

import org.apache.tapestry5.json.JSONObject;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;

/**
 * This service is responsible for restoring survey structure from JSON object
 * and to serialize survey structure to JSON object.
 *
 * @author kuba
 *
 */
public interface SurveyRestorationService {

	/**
	 * Sets {@link SurveyDAO} data access object which is used by this service.
	 * 
	 * @param surveyDAO
	 *            data access object for survey
	 */
	public void setSurveyDAO(SurveyDAO surveyDAO);

	/**
	 * Sets {@link ComponentDAO} data access object which is used by this
	 * service.
	 * 
	 * @param componentDAO
	 *            data access object for component
	 */
	public void setComponentDAO(ComponentDAO componentDAO);

	/**
	 * Sets {@link QuestionDAO} data access object which is used by this
	 * service.
	 * 
	 * @param questionDAO
	 *            data access object for question
	 */
	public void setQuestionDAO(QuestionDAO questionDAO);

	/**
	 * Sets {@link AnswerDAO} data access object which is used by this service.
	 * 
	 * @param answerDAO
	 *            data access object for answer
	 */
	public void setAnswerDAO(AnswerDAO answerDAO);

	/**
	 * Sets {@link ComponentManager} object which is used by this object.
	 * 
	 * @param componentManager
	 *            component manager object
	 */
	public void setComponentManager(ComponentManager componentManager);

	/**
	 * Restores survey structure according to given JSON object.
	 * 
	 * @param survey
	 *            entity of survey which is being restored
	 * @param jsonObject
	 *            JSON object witch updated survey structure
	 * 
	 * @return JSON object witch survey structure filled with id's of created
	 *         components
	 * @deprecated use Dojo Sync actions recording feature
	 */
	public JSONObject restoreSurvey(Survey survey, JSONObject jsonObject);

	/**
	 * Restores survey structure according to given JSON object.
	 * 
	 * @param surveyId
	 *            id of survey which is being restored
	 * @param jsonObject
	 *            JSON object witch updated survey structure
	 * 
	 * @return JSON object witch survey structure filled with id's of created
	 *         components
	 * @deprecated use Dojo Sync actions recording feature
	 */
	public JSONObject restoreSurvey(long surveyId, JSONObject jsonObject);

	/**
	 * Transforms answer to JSON notation.
	 * 
	 * @param answer
	 *            {@link Answer} object
	 * @return {@link JSONObject} with answer
	 */
	public JSONObject answerToJSON(Answer answer);

	/**
	 * Transforms question to JSON notation.
	 * 
	 * @param question
	 *            {@link Question} object
	 * @return {@link JSONObject} with question
	 */
	public JSONObject questionToJSON(Question question);

	/**
	 * Transforms component to JSON notation.
	 * 
	 * @param component
	 *            {@link Component} object
	 * @return {@link JSONObject} with component
	 */
	public JSONObject componentToJSON(Component component);

	/**
	 * Creates JSON object which stores survey structure.
	 * 
	 * @param survey
	 *            survey entity
	 * 
	 * @return JSON object witch survey structure
	 */
	public JSONObject surveyToJSON(Survey survey);

	/**
	 * Creates JSON object which stores survey structure.
	 * 
	 * @param surveyId
	 *            id of survey
	 * 
	 * @return JSON object witch survey structure
	 */
	public JSONObject surveyToJSON(long surveyId);

	/**
	 * Creates JSON object that stores all attributes from given answer entity.
	 *
	 * @param answer	answer entity
	 *
	 * @return JSON object representing answer entity
	 */
	public JSONObject answerJSONObject(Answer answer);

	/**
	 * Creates JSON object that stores all attributes from given question entity.
	 *
	 * @param question	question entity
	 *
	 * @return JSON object representing question entity
	 */
	public JSONObject questionJSONObject(Question question);

	/**
	 * Creates JSON object which stores all attributes from given component
	 * entity.
	 *
	 * @param component	component entity
	 *
	 * @return JSON object representing component entity
	 */
	public JSONObject componentJSONObject(Component component);

	/**
	 * Creates JSON object which stores all attributes from given survey entity.
	 *
	 * @param survey	survey entity
	 *
	 * @return JSON object representing survey entity
	 */
	public JSONObject surveyJSONObject(Survey survey);

}
