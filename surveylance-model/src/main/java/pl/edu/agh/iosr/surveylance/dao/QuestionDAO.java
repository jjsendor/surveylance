package pl.edu.agh.iosr.surveylance.dao;

import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;

/**
 * This interface is implemented by QuestionDAO implementations.
 * 
 * @author kornel
 */
public interface QuestionDAO extends GenericDAO<Question, Long> {

	/**
	 * This method returns Question object which parent is object given as
	 * parameter.
	 * 
	 * @param parent
	 *            searching object's parent Component object
	 * @return question, which parent is object given as parameter\
	 */
	public Question findByParent(Component parent);

}
