package pl.edu.agh.iosr.surveylance.service;

import java.util.List;

import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Survey;

/**
 * This service manages all possible actions for forms.
 * 
 * @author kornel
 */
public interface FormManager {

	/**
	 * Sets form DAO.
	 * 
	 * @param formDAO
	 *            {@link FormDAO} object
	 */
	public void setFormDAO(FormDAO formDAO);

	/**
	 * Returns all forms associated with given survey ID.
	 * 
	 * @param survey
	 *            the {@link Survey}
	 * @return {@link List} of {@link Form} objects
	 */
	public List<Form> getFormsBySurvey(Survey survey);

	/**
	 * Returns {@link Form} with given ID.
	 * 
	 * @param id
	 *            ID of the form
	 * @return the {@link Form} object
	 */
	public Form getFormByID(Long id);

}
