package pl.edu.agh.iosr.surveylance.dao;

import java.util.Date;
import java.util.List;

import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This interface is implemented by SurveyDAO implementations.
 * 
 * @author kornel
 */
public interface SurveyDAO extends GenericDAO<Survey, Long> {

	/**
	 * This method returns exists surveys with given expiration date.
	 * 
	 * @param expirationDate
	 *            expiration date
	 * @return {@link List} of existing surveys objects with given expiration
	 *         date
	 */
	public List<Survey> findByExpirationDate(Date expirationDate);

	/**
	 * This method returns exists surveys with given name.
	 * 
	 * @param name
	 *            survey's name
	 * @return {@link List} of existing surveys objects with given name
	 */
	public List<Survey> findByName(String name);

	/**
	 * This method returns exists surveys with given owner id.
	 * 
	 * @param owner
	 *            survey's owner
	 * @return {@link List} of existing surveys objects with given owner id
	 */
	public List<Survey> findByOwner(User owner);

	/**
	 * This method returns existing survey with given root component.
	 * 
	 * @param component
	 *            survey's root component
	 * @return survey with given root component
	 */
	public Survey findByRootComponent(Component component);

}
