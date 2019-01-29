package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Survey;

/**
 * This class represents Form object DAO implementation which uses Hibernate
 * technology.
 * 
 * @author fibi
 * @author kornel
 */
public class FormDAOImpl extends GenericDAOImpl<Form, Long> implements FormDAO {

	/**
	 * Public constructor.
	 */
	public FormDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public Form findByHash(String hash) {
		List<Form> forms = super.findByCriteria(Restrictions.eq("hash", hash));
		if (forms.size() == 1)
			return forms.get(0);
		else
			return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<Form> findBySurvey(Survey surveyID) {
		return super.findByCriteria(Restrictions.eq("survey", surveyID));
	}

}
