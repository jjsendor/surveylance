package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;

@IncludeStylesheet("context:css/survey_edit.css")
@IncludeJavaScriptLibrary({
	"context:js/dojo/dojo.js",
	"context:js/gears/gears_init.js",
	"context:js/gears/gears.js",
	"context:js/gears/survey.js",
	"context:js/gears/component.js",
	"context:js/gears/question.js",
	"context:js/gears/answer.js",
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
	"context:js/model.js",
	"context:js/survey_edit.js",
	"context:js/gears/synchronization.js",
	"context:js/bandoneon.js"
})
public class EditSurvey {

	@Property
	private Survey survey;

	@Property
	private Component rootComponent;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private ComponentManager componentManager;

	@Inject
	private QuestionManager questionManager;

	@Property
	private Component component;

	private Question question;

	@Property
	private Answer answer;

	void onActivate(long surveyId) {
		// TODO check if survey exists, and user has access to survey,
		// otherwise redirect to survey/list
		survey = surveyManager.getSurvey(surveyId);
		rootComponent = survey.getRootComponent();
	}

	public List<Component> getComponents() {
		return getComponents(rootComponent);
	}

	private List<Component> getComponents(Component parent) {
		List<Component> componentsList = new LinkedList<Component>();
		componentsList.add(parent);

		List<Component> childComponents = componentManager
				.getComponents(parent);
		Collections.sort(childComponents);

		for (Component child : childComponents)
			componentsList.addAll(getComponents(child));

		return componentsList;
	}

	public Question getQuestion() {
		question = questionManager.getQuestion(component);
		return question;
	}

	public List<Answer> getAnswers() {
		return questionManager.getAnswers(question);
	}

	public boolean isQuestionNumeric() {
		return question.getType() == QuestionType.NUMERIC;
	}

	public NumericAnswer getNumericAnswer() {
		return (NumericAnswer) answer;
	}

	public TextAnswer getTextAnswer() {
		return (TextAnswer) answer;
	}

}
