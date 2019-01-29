package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.FormManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;

@IncludeStylesheet("context:css/map.css")
@IncludeJavaScriptLibrary( { "context:js/dojo/dojo.js", "context:js/map.js" })
public class Map {

	@Inject
	private FormManager formManager;

	@Inject
	private SurveyManager surveyManager;

	@SuppressWarnings("unused")
	@Property
	private Form form;

	@Property
	private Survey survey;

	void onActivate(long surveyID) {
		this.survey = surveyManager.getSurvey(surveyID);
	}

	public List<Form> getForms() {
		List<Form> forms = this.formManager.getFormsBySurvey(survey);
		List<Form> resultForms = new ArrayList<Form>();
		for (Form form : forms)
			if (form.getX() != null && form.getY() != null)
				resultForms.add(form);
		return resultForms;
	}

}
