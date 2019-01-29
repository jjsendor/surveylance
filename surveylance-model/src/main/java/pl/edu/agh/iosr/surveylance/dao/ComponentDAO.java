package pl.edu.agh.iosr.surveylance.dao;

import java.util.List;

import pl.edu.agh.iosr.surveylance.entities.Component;

/**
 * This interface is implemented by ComponentDAO implementations.
 * 
 * @author kornel
 */
public interface ComponentDAO extends GenericDAO<Component, Long> {

	/**
	 * This method returns exists components with given parent.
	 * 
	 * @param parent
	 *            parent component
	 * @return {@link List} of existing components with given parent
	 */
	public List<Component> findByParent(Component parent);

	/**
	 * This method returns root component of component given as parameter.
	 * 
	 * @param component
	 *            component which root is being searched
	 * @return root component of component given as parameter
	 */
	public Component findRootComponent(Component component);

}
