package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This class represents Survey object DAO implementation which uses Hibernate
 * technology.
 * 
 * @author kornel
 */
public class SurveyDAOImpl extends GenericDAOImpl<Survey, Long> implements
		SurveyDAO {

	/**
	 * Public constructor.
	 */
	public SurveyDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public List<Survey> findByExpirationDate(Date expirationDate) {
		return super.findByCriteria(Restrictions.eq("expirationDate",
				expirationDate));
	}

	/** {@inheritDoc} */
	@Override
	public List<Survey> findByName(String name) {
		return super.findByCriteria(Restrictions.eq("name", name));
	}

	/** {@inheritDoc} */
	@Override
	public List<Survey> findByOwner(User owner) {
		return super.findByCriteria(Restrictions.eq("owner", owner));
	}

	/** {@inheritDoc} */
	@Override
	public Survey findByRootComponent(Component component) {
		List<Survey> surveys = super.findByCriteria(Restrictions.eq(
				"rootComponent", component));
		if (surveys.size() == 1)
			return surveys.get(0);
		else
			return null;
	}

}
