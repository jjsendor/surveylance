package pl.edu.agh.iosr.surveylance.dao.hibernate;

import pl.edu.agh.iosr.surveylance.dao.TextAnswerDAO;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;

/**
 * This class represents TextAnswer object DAO implementation which uses
 * Hibernate technology.
 * 
 * @author kornel
 */
public class TextAnswerDAOImpl extends GenericDAOImpl<TextAnswer, Long>
		implements TextAnswerDAO {

	/**
	 * Public constructor.
	 */
	public TextAnswerDAOImpl() {
		super();
	}

}
