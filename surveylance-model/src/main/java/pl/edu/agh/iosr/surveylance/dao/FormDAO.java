package pl.edu.agh.iosr.surveylance.dao;

import java.util.List;

import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Survey;

/**
 * This interface is implemented by FormDAO implementations.
 * 
 * @author fibi
 * @author kornel
 */
public interface FormDAO extends GenericDAO<Form, Long> {

	/**
	 * This method returns form with given hash.
	 * 
	 * @param hash
	 *            form's hash
	 * @return form with given hash
	 */
	public Form findByHash(String hash);

	/**
	 * Returns all forms associated with given survey ID.
	 * 
	 * @param survey
	 *            the {@link Survey} object
	 * @return {@link List} of the {@link Form} objects
	 */
	public List<Form> findBySurvey(Survey survey);

}
