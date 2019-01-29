/**
 * 
 */
package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.SurveyResultDAO;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.SurveyResult;

/**
 * This class represents SurveyResult object DAO implementation which uses
 * Hibernate technology.
 * 
 * @author fibi
 * @author kornel
 */
public class SurveyResultDAOImpl extends GenericDAOImpl<SurveyResult, Long>
		implements SurveyResultDAO {

	/**
	 * Public constructor.
	 */
	public SurveyResultDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public List<SurveyResult> findByQuestion(Question question) {
		return findByCriteria(Restrictions.eq("question", question));
	}

	/** {@inheritDoc} */
	@Override
	public List<SurveyResult> findByQuestionAndForm(Question question, Form form) {
		return findByCriteria(Restrictions.eq("question", question),
				Restrictions.eq("form", form));
	}

}
