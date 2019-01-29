package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.List;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.CalendarManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

@IncludeJavaScriptLibrary({
	"context:js/gears/gears_init.js",
	"context:js/gears/gears.js",
	"context:js/gears/survey.js",
	"context:js/gears/component.js",
	"context:js/jstorm/JStORM.js",
	"context:js/jstorm/JStORM.Query.js",
	"context:js/jstorm/JStORM.Gears.js",
	"context:js/jstorm/JStORM.Sql.js",
	"context:js/jstorm/JStORM.Field.js",
	"context:js/jstorm/JStORM.ModelMetaData.js",
	"context:js/jstorm/JStORM.Model.js",
	"context:js/jstorm/JStORM.Events.js",
	"context:js/jstorm/JStORM.SQLite.Introspection.js",
	"context:js/jstorm/gears_model.js",
	"context:js/model.js"
})
public class SurveyList {

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private CalendarManager calendarManager;

	@Property
	private Survey survey;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@Persist
	private long deletedSurveyId;

	@Persist
	private boolean surveyDeleted;

	public List<Survey> getSurveys() throws Exception {
		if (userInfo == null)
			return null;

		return surveyManager.getSurveys(userInfo.getUser());
	}

	void onActionFromDelete(long surveyId) {
		surveyManager.deleteSurvey(surveyId);
		deletedSurveyId = surveyId;
		surveyDeleted = true;

		// remove survey from calendar
		calendarManager.setSessionToken(userInfo.getSessionToken());
		calendarManager.removeEvent(surveyId);
	}

	public long getDeletedSurveyId() {
		return deletedSurveyId;
	}

	public boolean isEditable() {
		return !survey.isReadonly();
	}

	public boolean isDeleted() {
		if (surveyDeleted) {
			surveyDeleted = false;
			return true;
		}

		return false;
	}
}
