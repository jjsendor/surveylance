package pl.edu.agh.iosr.surveylance.service;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;

/**
 * Updates surveys' components modifications values, from bottom to top
 * components.
 *
 * @author kuba
 *
 */
public interface ModificationService {

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
	 * Mark survey as modified (increments its <code>modifications</code>
	 * attribute.
	 *
	 * @param	survey	survey entity that is mark as modified
	 */
	public void markModified(Survey survey);

	/**
	 * Mark component as modified (increments its <code>modifications</code>
	 * attribute and marks all its parent components (up to survey) as
	 * modified.
	 *
	 * @param	survey	survey entity that is mark as modified
	 */
	public void markModified(Component component);

	/**
	 * Mark question as modified (increments its <code>modifications</code>
	 * attribute and marks all its parent components (up to survey) as
	 * modified.
	 *
	 * @param	question	question entity that is mark as modified
	 */
	public void markModified(Question question);

	/**
	 * Mark answer as modified (increments its <code>modifications</code>
	 * attribute and marks all its parent components (question and then
	 * components, up to survey) as modified.
	 *
	 * @param	answer	answer entity that is mark as modified
	 */
	public void markModified(Answer answer);

}
