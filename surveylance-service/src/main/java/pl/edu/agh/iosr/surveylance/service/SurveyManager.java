package pl.edu.agh.iosr.surveylance.service;

import java.util.List;
import java.util.Locale;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This service manages all possible actions for creating and editing surveys.
 * 
 * @author kuba
 * @author kornel
 */
public interface SurveyManager {

	/**
	 * Sets {@link SurveyDAO} data access object which is used by this service.
	 * 
	 * @param surveyDAO
	 *            data access object for survey
	 */
	public void setSurveyDAO(SurveyDAO surveyDAO);

	/**
	 * Sets {@link ComponentDAO} data access object which is used by this
	 * service.
	 * 
	 * @param componentDAO
	 *            data access object for component
	 */
	public void setComponentDAO(ComponentDAO componentDAO);

	/**
	 * Sets {@link UserDAO} data access object which is used by this service.
	 * 
	 * @param userDAO
	 *            data access object for user
	 */
	public void setUserDAO(UserDAO userDAO);

	/**
	 * Sets {@link FormDAO} data access object which is used by this service.
	 * 
	 * @param formDAO
	 *            data access object for survey invitation
	 */
	public void setFormDAO(FormDAO formDAO);

	/**
	 * Sets {@link SurveyManager} object which is used by this service.
	 * 
	 * @param componentManager
	 */
	public void setComponentManager(ComponentManager componentManager);

	/**
	 * Sets {@link ModificationService} object which is used by this service.
	 * 
	 * @param modificationService
	 */
	public void setModificationService(ModificationService modificationService);

	/**
	 * Looks for the survey entity with specified id.
	 * 
	 * @param surveyId
	 *            id of the survey
	 * 
	 * @return found survey entity or null if it doesn't exist
	 */
	public Survey getSurvey(Long surveyId);

	/**
	 * Looks for the survey entity asociated with invitation containing
	 * specified hash code.
	 * 
	 * @param hashCode
	 *            invitation hash
	 * 
	 * @return found survey entity or <code>null</code> if it doesn't exist
	 */
	public Survey getSurveyByHash(String hashCode);

	/**
	 * Looks for the invitation entity containing specified hash code.
	 * 
	 * @param hashCode
	 *            invitation hash
	 * 
	 * @return found invitation entity or <code>null</code> if it doesn't exist
	 */
	public Form getInvitationByHash(String hashCode);

	/**
	 * Lists all surveys in service.
	 * 
	 * @return list of all found surveys
	 */
	public List<Survey> getSurveys();

	/**
	 * Lists all surveys for given user.
	 * 
	 * @param user
	 *            user entity whose surveys list will be returned
	 * 
	 * @return list of all found surveys for given user
	 */
	public List<Survey> getSurveys(User user);

	/**
	 * Creates and stores given survey in database. Automatically create root
	 * component for new survey.
	 * 
	 * @param survey
	 *            survey entity to be persisted
	 */
	public void createSurvey(Survey survey);

	/**
	 * Deletes given survey from database.
	 * 
	 * @param survey
	 *            survey entity which is deleted
	 */
	public void deleteSurvey(Survey survey);

	/**
	 * Deletes survey with specified id from database.
	 * 
	 * @param surveyId
	 *            id of survey which is deleted
	 */
	public void deleteSurvey(long surveyId);

	/**
	 * Checks if user is owner of survey.
	 * 
	 * @param survey
	 *            survey entity
	 * @param user
	 *            user entity
	 * 
	 * @return <code>true</code> if user is owner of survey
	 */
	public boolean isSurveyOwner(Survey survey, User user);

	/**
	 * Checks if user is owner of survey.
	 * 
	 * @param surveyId
	 *            id of checked survey
	 * @param user
	 *            user entity
	 * 
	 * @return <code>true</code> if user is owner of survey
	 */
	public boolean isSurveyOwner(long surveyId, User user);

	/**
	 * Checks if user is owner of component.
	 * 
	 * @param component
	 *            component entity
	 * @param user
	 *            user entity
	 * 
	 * @return <code>true</code> if user is owner of component
	 */
	public boolean isComponentOwner(Component component, User user);

	/**
	 * Checks if user is owner of component.
	 * 
	 * @param componentId
	 *            id of checked component
	 * @param user
	 *            user entity
	 * 
	 * @return <code>true</code> if user is owner of component
	 */
	public boolean isComponentOwner(long componentId, User user);

	/**
	 * Sends invitation to other users to fill the survey.
	 * 
	 * @param survey
	 *            survey which is sent
	 * @param mails
	 *            users' e-mail addresses
	 * @param context
	 *            URL context for filling survey form
	 * @param locale
	 *            admin's locale
	 */
	public void sendSurvey(Survey survey, List<String> mails, String context,
			Locale locale);

	/**
	 * Invalidates invitation. This method should be called after user submit
	 * survey form.
	 * 
	 * @param form
	 *            invitation which is invalidated
	 */
	public void invalidateInvitation(Form form);

	/**
	 * This method gets form using hash code.
	 * 
	 * @param hash
	 *            form's hash code
	 * 
	 * @return form
	 */
	public Form getFormByHash(String hash);
	
	/**
	 * This method gets survey for question
	 * 
	 * @param question
	 *            question
	 * 
	 * @return survey
	 */	
	public Survey getSurvey(Question question);

}
