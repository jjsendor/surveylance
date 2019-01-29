package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class CreateTest2 {

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@SuppressWarnings("unused")
	@Property
	private Long surveyID;

	@Property
	private List<Long> surveysIDs;

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

		surveysIDs = new ArrayList<Long>(); 
		for(int q=0 ; q<3 ; q++){
			Component rootComponent = new Component();
			rootComponent.setModifications(1);
			rootComponent.setParentComponent(null);
			componentDAO.create(rootComponent);
	
			Survey mySurvey = new Survey();
			mySurvey.setDescription("jakis opis ankiety");
			mySurvey.setExpirationDate(new Date());
			mySurvey.setModifications(5);
			mySurvey.setName("ankietka " + q);
			mySurvey.setOwner(me);
			mySurvey.setRootComponent(rootComponent);
			surveyDAO.create(mySurvey);
			surveysIDs.add(mySurvey.getId());
			
			Component component1 = new Component();
			component1.setModifications(1);
			component1.setParentComponent(rootComponent);
			componentDAO.create(component1);
	
			Component component2 = new Component();
			component2.setModifications(1);
			component2.setParentComponent(rootComponent);
			componentDAO.create(component2);
			
			Component component3 = new Component();
			component3.setModifications(1);
			component3.setParentComponent(rootComponent);
			componentDAO.create(component3);
	
			Question question = new Question();
			question.setContent("Pytanie do wspolnej analizy");
			question.setType(QuestionType.NUMERIC);
			question.setKind(QuestionKind.RADIO);
			question.setParentComponent(component1);	
			questionDAO.create(question);
	
			for (int i = 0; i < 5; i++) {
				NumericAnswer answer = new NumericAnswer();
				answer.setValue(new Double(i + 5));
				answer.setQuestion(question);
				answerDAO.create(answer);
	
				for (int j = 0; j < (q+1)%3*i + 3; j++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answer);
					result.setQuestion(question);
					surveyResultDAO.create(result);
				}
			}
	
			Question question2 = new Question();
			question2.setContent("Pytanie do oddzielnej analizy " + (q+1));
			question2.setType(QuestionType.TEXT);
			question2.setKind(QuestionKind.RADIO);
			question2.setParentComponent(component2);
			questionDAO.create(question2);
	
			for (int i = 0; i < 5; i++) {
				TextAnswer answer = new TextAnswer();
				answer.setValue("Pyt " + (q+1) + " odp nr " + i);
				answer.setQuestion(question2);
				answerDAO.create(answer);
	
				for (int j = 0; j < q*i + 3; j++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answer);
					result.setQuestion(question2);
					surveyResultDAO.create(result);
				}
			}
			
			Question question3 = new Question();
			question3.setContent("Pytanie do oddzielnej analizy " + (q+4));
			question3.setType(QuestionType.TEXT);
			question3.setKind(QuestionKind.INPUT_TEXT);
			question3.setParentComponent(component3);
			questionDAO.create(question3);
			
			Random rand = new Random();
			for (int j = 0; j < 100; j++) {
				SurveyResult result = new SurveyResult();
				result.setTextAnswer("Pyt " + (q+4) + " Otwarta odpowiedz nr " + rand.nextInt(40));
				result.setQuestion(question3);
				surveyResultDAO.create(result);
			}
			
			Question question4 = new Question();
			question4.setContent("Pytanie do wspolnej analizy " + 2);
			question4.setType(QuestionType.TEXT);
			question4.setKind(QuestionKind.INPUT_TEXT);
			question4.setParentComponent(component3);
			questionDAO.create(question4);
			
			for (int j = 0; j < 100; j++) {
				SurveyResult result = new SurveyResult();
				result.setTextAnswer("Otwarta odpowiedz nr " + rand.nextInt(40));
				result.setQuestion(question4);
				surveyResultDAO.create(result);
			}			
		}
	}

}
