package pl.edu.agh.iosr.surveylance.service.impl;

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
import pl.edu.agh.iosr.surveylance.service.MailService;
import pl.edu.agh.iosr.surveylance.service.ModificationService;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;

/**
 * Implementation of {@link SurveyManager} interface.
 * 
 * @author kuba
 */
public class SurveyManagerImpl implements SurveyManager {

	private ComponentManager componentManager;
	private ModificationService modificationService;

	private SurveyDAO surveyDAO;
	private ComponentDAO componentDAO;
	private UserDAO userDAO;
	private FormDAO formDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSurveyDAO(SurveyDAO surveyDAO) {
		this.surveyDAO = surveyDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentDAO(ComponentDAO componentDAO) {
		this.componentDAO = componentDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFormDAO(FormDAO formDAO) {
		this.formDAO = formDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentManager(ComponentManager componentManager) {
		this.componentManager = componentManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setModificationService(
			ModificationService modificationService) {
		this.modificationService = modificationService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Survey getSurvey(Long surveyId) {
		return surveyDAO.findById(surveyId, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Survey getSurveyByHash(String hashCode) {
		Form form = formDAO.findByHash(hashCode);

		if (form != null)
			return form.getSurvey();

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Form getInvitationByHash(String hashCode) {
		return formDAO.findByHash(hashCode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Survey> getSurveys() {
		return surveyDAO.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Survey> getSurveys(User user) {
		return surveyDAO.findByOwner(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createSurvey(Survey survey) {
		Component rootComponent = new Component();
		componentDAO.create(rootComponent);

		survey.setRootComponent(rootComponent);
		survey.setModifications(0);
		surveyDAO.create(survey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSurvey(Survey survey) {
		Component rootComponent = survey.getRootComponent();

		surveyDAO.delete(survey);
		if (rootComponent != null) {
			survey.setRootComponent(null);
			// delete rootComponent and all components, questions ans answers
			// underneath it
			componentManager.deleteComponent(rootComponent);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSurvey(long surveyId) {
		Survey survey = surveyDAO.findById(surveyId, false);
		deleteSurvey(survey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSurveyOwner(Survey survey, User user) {
		if (survey.getOwner().getId().equals(user.getId()))
			return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSurveyOwner(long surveyId, User user) {
		Survey survey = surveyDAO.findById(surveyId, false);

		return isSurveyOwner(survey, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComponentOwner(Component component, User user) {
		Component root = componentDAO.findRootComponent(component);
		Survey survey = surveyDAO.findByRootComponent(root);

		return isSurveyOwner(survey, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComponentOwner(long componentId, User user) {
		Component comp = componentDAO.findById(componentId, false);

		return isComponentOwner(comp, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendSurvey(Survey survey, List<String> mails, String context,
			Locale locale) {
		String localeStr = locale.getCountry() == "pl" ? "pl" : null;
		MailService mailService = new MailServiceImpl(localeStr);

		for (String mail : mails) {
			User user = userDAO.findByGoogleId(mail);

			if (user == null) {
				user = new User();
				user.setGoogleId(mail);
				userDAO.create(user);
			}

			String hashCode = String.valueOf(survey.hashCode())
					+ String.valueOf(user.hashCode())
					+ String.valueOf((int) (Math.random() * 1000));

			Form form = new Form();
			form.setSubmitted(false);
			form.setSurvey(survey);
			form.setUser(user);
			form.setHash(hashCode);
			formDAO.create(form);

			survey.setReadonlyFlag();
			surveyDAO.update(survey);

			mailService.sendInvitation(context + "/" + hashCode, mail);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidateInvitation(Form form) {
		form.setSubmitted(true);
		formDAO.update(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Form getFormByHash(String hash) {
		return formDAO.findByHash(hash);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Survey getSurvey(Question question){
		return surveyDAO.findByRootComponent(
				componentManager.getRoot(question.getParentComponent()));
	}
	
}
