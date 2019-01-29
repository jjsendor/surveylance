package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;

/**
 * This class represents Component object DAO implementation which uses
 * Hibernate technology.
 * 
 * @author kornel
 */
public class ComponentDAOImpl extends GenericDAOImpl<Component, Long> implements
		ComponentDAO {

	/**
	 * Public constructor.
	 */
	public ComponentDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public List<Component> findByParent(Component parent) {
		return super.findByCriteria(Restrictions.eq("parentComponent", parent));
	}

	/** {@inheritDoc} */
	@Override
	public Component findRootComponent(Component component) {
		while (component.getParentComponent() != null)
			component = component.getParentComponent();
		return component;
	}

}
