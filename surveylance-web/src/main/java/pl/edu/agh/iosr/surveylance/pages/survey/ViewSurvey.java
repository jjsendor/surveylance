package pl.edu.agh.iosr.surveylance.pages.survey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;

@IncludeStylesheet("context:css/survey_view.css")
public class ViewSurvey {

	@Inject
	private ComponentManager componentManager;

	@Inject
	private QuestionManager questionManager;

	@Persist
	private Survey survey;

	@Property
	private Question question;

	@Property
	private Answer answer;

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	void onActivate(Survey survey) {
		this.survey = survey;
	}

	public boolean isEditable() {
		return !survey.isReadonly();
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

		List<Component> childComponents =
			componentManager.getComponents(parent);
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

	public String getSurveyExpirationDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(survey.getExpirationDate());
	}

}
