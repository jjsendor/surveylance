package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.Calendar;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.CalendarManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class CreateSurvey {

	@Component(id = "survey")
	private BeanEditForm form;

	@Persist
	@Property
	private Survey survey;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private CalendarManager calendarManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@InjectPage
	private SurveyCreated surveyCreated;

	@Component(id = "expirationDate")
	private DateField expirationDate;

	@Inject
	private Messages messages;

	Object onSuccess() {
		if (survey.getExpirationDate().compareTo(
				Calendar.getInstance().getTime()) <= 0) {
			form.recordError(expirationDate,
					messages.get("expirationDate-range-message"));
			return null;
		}

		survey.setOwner(userInfo.getUser());
		surveyManager.createSurvey(survey);

		calendarManager.setSessionToken(userInfo.getSessionToken());
		calendarManager.addEvent(survey);

		surveyCreated.setSurvey(survey);
		surveyCreated.setCreated(true);
		return surveyCreated;
	}

}
