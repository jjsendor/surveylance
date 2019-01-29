package pl.edu.agh.iosr.surveylance.dao;

import java.util.List;

import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Question;

/**
 * This interface is implemented by AnswerDAO implementations.
 * 
 * @author kornel
 */
public interface AnswerDAO extends GenericDAO<Answer, Long> {

	/**
	 * This method returns list of answers which are answers on question given
	 * as parameter.
	 * 
	 * @param question
	 *            question
	 * @return {@link List} of answers matching to given question
	 */
	public List<Answer> findByQuestion(Question question);

}
