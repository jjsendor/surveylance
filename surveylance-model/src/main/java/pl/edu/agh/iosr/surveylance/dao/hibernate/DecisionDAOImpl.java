package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.DecisionDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;

/**
 * This class represents Decision object DAO implementation which uses Hibernate
 * technology.
 * 
 * @author fibi
 */
public class DecisionDAOImpl extends GenericDAOImpl<Decision, Long> implements
		DecisionDAO {

	/**
	 * Public constructor.
	 */
	public DecisionDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public List<Decision> findByForkComponent(ForkComponent forkComponent) {
		return super.findByCriteria(Restrictions.eq("forkComponent",
				forkComponent));
	}

	/** {@inheritDoc} */
	@Override
	public List<Decision> findByForkComponentAndComponent(
			ForkComponent forkComponent, Component component) {
		return super.findByCriteria(Restrictions.eq("forkComponent",
				forkComponent), Restrictions.eq("component", component));
	}

}
