package pl.edu.agh.iosr.surveylance.dao;

import java.util.List;

import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;

/**
 * This interface is implemented by DecisionDAO implementations.
 * 
 * @author kornel
 */
public interface DecisionDAO extends GenericDAO<Decision, Long> {

	/**
	 * This method returns existing decisions with given forkComponent.
	 * 
	 * @param forkComponent
	 *            decision's fork component
	 * @return {@link List} of existing decisions with given forkComponent
	 */
	public List<Decision> findByForkComponent(ForkComponent forkComponent);

	/**
	 * This method returns existing decisions with given forkComponent and
	 * component.
	 * 
	 * @param forkComponent
	 *            decision's fork component
	 * @param component
	 *            decision's component
	 * @return {@link List} of existing decisions with given forkComponent and
	 *         component
	 */
	public List<Decision> findByForkComponentAndComponent(
			ForkComponent forkComponent, Component component);

}
