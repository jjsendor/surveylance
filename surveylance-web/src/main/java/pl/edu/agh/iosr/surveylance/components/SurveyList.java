package pl.edu.agh.iosr.surveylance.components;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class SurveyList {

	private Iterator<Survey> surveysIterator;

	@Property
	private Survey survey;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	@Property
	private UserSessionInfo userInfo;

	public List<Survey> getSurveys() {
		return surveyManager.getSurveys(userInfo.getUser());
	}

}
