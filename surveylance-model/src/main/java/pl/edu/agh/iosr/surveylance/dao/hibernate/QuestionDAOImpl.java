package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;

/**
 * This class represents Question object DAO implementation which uses Hibernate
 * technology.
 * 
 * @author kornel
 */
public class QuestionDAOImpl extends GenericDAOImpl<Question, Long> implements
		QuestionDAO {

	/**
	 * Public constructor.
	 */
	public QuestionDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public Question findByParent(Component parent) {
		List<Question> questions = super.findByCriteria(Restrictions.eq(
				"parentComponent", parent));
		if (questions.size() == 1)
			return questions.get(0);
		else
			return null;
	}

}
