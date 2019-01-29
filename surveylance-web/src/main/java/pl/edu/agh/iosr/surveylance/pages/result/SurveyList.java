package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.List;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

@IncludeStylesheet("context:css/result_survey_list.css")
@IncludeJavaScriptLibrary({
	"context:js/dojo/dojo.js",
	"context:js/result_survey_list.js"
})
public class SurveyList {

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@Inject
	private SurveyResultManager surveyResultManager;

	@SuppressWarnings("unused")
	@Property
	private Survey survey;

	void onActivate() {
	}

	public List<Survey> getSurveys() {
		if (userInfo == null)
			return null;

		List<Survey> surveys =
			surveyResultManager.getSurveys(userInfo.getUser());

		return surveys;
	}

}
