package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;

@IncludeStylesheet("context:css/map.css")
@IncludeJavaScriptLibrary({
	"context:js/dojo/dojo.js",
	"context:js/map.js"})
public class FillSurvey {

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private ComponentManager componentManager;

	@Inject
	private QuestionManager questionManager;

	@Property
	private Survey survey;

	@Property
	private Question question;

	@Property
	private Answer answer;

	@SuppressWarnings("unused")
	@Property
	private Form form;

	@SuppressWarnings("unused")
	@Property
	private String hashCode;

	void onActivate(String hashCode) {
		this.hashCode = hashCode;
		this.form = surveyManager.getInvitationByHash(hashCode);
		this.survey = surveyManager.getSurveyByHash(hashCode);
	}

	public boolean isSurveyExpired() {
		return survey.getExpirationDate().compareTo(
				Calendar.getInstance().getTime()) <= 0;
	}

	public List<Question> getQuestions() {
		return getQuestions(survey.getRootComponent());
	}

	public List<Answer> getAnswers() {
		return questionManager.getAnswers(question);
	}

	public boolean isOpenQuestion() {
		return question.getKind() == QuestionKind.INPUT_TEXT;
	}

	public boolean isQuestionNumeric() {
		return question.getType() == QuestionType.NUMERIC;
	}

	public String getAnswerLiteralValue() {
		if (question.getType() == QuestionType.NUMERIC)
			return ((NumericAnswer) answer).getValue().toString();

		return ((TextAnswer) answer).getValue();
	}

	private List<Question> getQuestions(Component parent) {
		List<Question> questionsList = new LinkedList<Question>();

		List<Component> childComponents = componentManager
				.getComponents(parent);
		Collections.sort(childComponents);

		for (Component child : childComponents) {
			Question question = questionManager.getQuestion(child);

			if (question != null)
				questionsList.add(question);
			else
				questionsList.addAll(getQuestions(child));
		}

		return questionsList;
	}

}
