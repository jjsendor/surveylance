package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Question;

/**
 * This class represents Answer object DAO implementation which uses Hibernate
 * technology.
 * 
 * @author kornel
 */
public class AnswerDAOImpl extends GenericDAOImpl<Answer, Long> implements
		AnswerDAO {

	/**
	 * Public constructor.
	 */
	public AnswerDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public List<Answer> findByQuestion(Question question) {
		return super.findByCriteria(Restrictions.eq("question", question));
	}

}
