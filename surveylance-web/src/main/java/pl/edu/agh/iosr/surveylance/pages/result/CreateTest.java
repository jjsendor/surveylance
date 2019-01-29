package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.Date;
import java.util.Random;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyResultDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.SurveyResult;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.entities.User;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class CreateTest {

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@SuppressWarnings("unused")
	@Property
	private Long numericQuestionId;

	@SuppressWarnings("unused")
	@Property
	private Long textQuestionId;
	
	@SuppressWarnings("unused")
	@Property
	private Long openQuestionId;

	@Inject
	private AnswerDAO answerDAO;

	@Inject
	private QuestionDAO questionDAO;

	@Inject
	private SurveyDAO surveyDAO;

	@Inject
	private ComponentDAO componentDAO;

	@Inject
	private SurveyResultDAO surveyResultDAO;

	void onActivate() {
		User me = userInfo.getUser();

		Component rootComponent = new Component();
		rootComponent.setModifications(1);
		rootComponent.setPosition(1);
		rootComponent.setParentComponent(null);
		componentDAO.create(rootComponent);

		Survey mySurvey = new Survey();
		mySurvey.setDescription("jakis opis ankiety");
		mySurvey.setExpirationDate(new Date());
		mySurvey.setModifications(5);
		mySurvey.setName("ankietka");
		mySurvey.setOwner(me);
		mySurvey.setRootComponent(rootComponent);
		surveyDAO.create(mySurvey);

		Component component1 = new Component();
		component1.setModifications(1);
		component1.setPosition(1);
		component1.setParentComponent(rootComponent);
		componentDAO.create(component1);

		Component component2 = new Component();
		component2.setModifications(1);
		component2.setPosition(1);
		component2.setParentComponent(rootComponent);
		componentDAO.create(component2);
		
		Component component3 = new Component();
		component3.setModifications(1);
		component3.setPosition(1);
		component3.setParentComponent(rootComponent);
		componentDAO.create(component3);

		Question question = new Question();
		question.setContent("Przykladowa tresc pytania");
		question.setType(QuestionType.NUMERIC);
		question.setParentComponent(component1);		
		numericQuestionId = question.getId();

		for (int i = 0; i < 5; i++) {
			NumericAnswer answer = new NumericAnswer();
			answer.setValue(new Double(i + 5));
			answer.setQuestion(question);
			answerDAO.create(answer);

			for (int j = 0; j < i + 3; j++) {
				SurveyResult result = new SurveyResult();
				result.setAnswer(answer);
				result.setQuestion(question);
				surveyResultDAO.create(result);
			}
		}

		Question question2 = new Question();
		question2.setContent("Przykladowa tresc pytania 2");
		question2.setType(QuestionType.TEXT);
		question2.setParentComponent(component2);
		questionDAO.create(question2);
		textQuestionId = question2.getId();

		for (int i = 0; i < 5; i++) {
			TextAnswer answer = new TextAnswer();
			answer.setValue("Odp nr " + i);
			answer.setQuestion(question2);
			answerDAO.create(answer);

			for (int j = 0; j < i + 3; j++) {
				SurveyResult result = new SurveyResult();
				result.setAnswer(answer);
				result.setQuestion(question2);
				surveyResultDAO.create(result);
			}
		}
		
		Question question3 = new Question();
		question3.setContent("Przykladowa tresc pytania 3");
		question3.setType(QuestionType.TEXT);
		question3.setKind(QuestionKind.INPUT_TEXT);
		question3.setParentComponent(component3);
		questionDAO.create(question3);
		openQuestionId = question3.getId();
		
		Random rand = new Random();
		for (int j = 0; j < 100; j++) {
			SurveyResult result = new SurveyResult();
			result.setTextAnswer("Otwarta odpowiedz nr " + rand.nextInt(40));
			result.setQuestion(question3);
			surveyResultDAO.create(result);
		}
		
	}

}
