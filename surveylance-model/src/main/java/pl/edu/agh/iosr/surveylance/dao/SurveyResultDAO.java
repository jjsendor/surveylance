package pl.edu.agh.iosr.surveylance.dao;

import java.util.List;

import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.SurveyResult;

/**
 * This interface is implemented by SurveyResultDAO implementations.
 * 
 * @author kornel
 */
public interface SurveyResultDAO extends GenericDAO<SurveyResult, Long> {

	/**
	 * This method returns existing survey results with given question.
	 * 
	 * @param question
	 *            result's question
	 * @return {@link List} of existing results with given question
	 */
	public List<SurveyResult> findByQuestion(Question question);

	/**
	 * This method returns existing survey results with given question anf form.
	 * 
	 * @param question
	 *            result's question
	 * @param form
	 *            result's form
	 * @return {@link List} of existing results with given question
	 */
	public List<SurveyResult> findByQuestionAndForm(Question question, Form form);

}
