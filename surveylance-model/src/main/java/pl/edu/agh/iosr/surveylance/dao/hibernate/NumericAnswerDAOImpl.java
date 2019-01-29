package pl.edu.agh.iosr.surveylance.dao.hibernate;

import pl.edu.agh.iosr.surveylance.dao.NumericAnswerDAO;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;

/**
 * This class represents NumericAnswer object DAO implementation which uses
 * Hibernate technology.
 * 
 * @author kornel
 */
public class NumericAnswerDAOImpl extends GenericDAOImpl<NumericAnswer, Long>
		implements NumericAnswerDAO {

	/**
	 * Public constructor.
	 */
	public NumericAnswerDAOImpl() {
		super();
	}

}
