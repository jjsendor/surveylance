package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import pl.edu.agh.iosr.surveylance.entities.Contact;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.ContactManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class SendSurvey {

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@Inject
	private Request request;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private ContactManager contactManager;

	@SuppressWarnings("unused")
	@Property
	private Contact contact;

	@Persist
	@Property
	private Survey survey;

	@SuppressWarnings("unused")
	@Property
	private String mail;

	@Property
	private List<String> addedMails = new ArrayList<String>();

	public boolean isMailAdded() {
		return addedMails.size() > 0;
	}

	void onActivate(long surveyId) {
		contactManager.setSessionToken(userInfo.getSessionToken());

		this.survey = surveyManager.getSurvey(surveyId);
	}

	public List<Contact> getContacts() {
		return contactManager.getContacts();
	}

	void onActionFromDelete(String mail) {
		addedMails.remove(mail);
	}

	void onActionFromAdd(String mail) {
		addedMails.add(mail);
	}

	Object onActionFromSend() {
		String host = request.getHeader("Host");
		String contextPath = request.getContextPath();
		String context = "http://" + host + contextPath + "/survey/fill";

		surveyManager.sendSurvey(survey, addedMails, context, request
				.getLocale());

		return SurveySendConfirmation.class;
	}

}