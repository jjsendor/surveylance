package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
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
import pl.edu.agh.iosr.surveylance.service.SurveyRestorationService;

public class SurveyRestorationServiceTest {

	private static ApplicationContext applicationContext;

	private SurveyRestorationService restorationService;
	private SurveyManager surveyManager;
	private ComponentManager componentManager;
	private QuestionManager questionManager;

	private ComponentDAO componentDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;

	/**
	 * Sets up testing environment. That means opening hibernate session factory
	 * and opening new hibernate session which is used in tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] paths = { "applicationContextDao.xml",
				"applicationContext.xml" };
		applicationContext = new ClassPathXmlApplicationContext(paths);
	}

	/**
	 * Tears down testing environment. Closes hibernate session.
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets up testing environment for single test: creates DAO for question and
	 * manager service for question.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		questionDAO = (QuestionDAO) applicationContext.getBean("questionDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");

		restorationService = (SurveyRestorationService) applicationContext
				.getBean("surveyRestorationService");
		surveyManager = (SurveyManager) applicationContext
				.getBean("surveyManager");
		componentManager = (ComponentManager) applicationContext
				.getBean("componentManager");
		questionManager = (QuestionManager) applicationContext
				.getBean("questionManager");
	}

	/**
	 * Tears down testing environment after single test.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		restorationService = null;
		componentDAO = null;
		questionDAO = null;
		answerDAO = null;
	}

	@Test
	public void testRestoreSurvey() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Survey survey = new Survey();
		survey.setName("restored survey");
		survey.setDescription("restored survey description");
		surveyManager.createSurvey(survey);

		int componentsSize = componentDAO.findAll().size();
		int questionsSize = questionDAO.findAll().size();
		int answersSize = answerDAO.findAll().size();

		int rootComponentModifications = 2009;

		int component0Modifications = 2010;
		int component0Position = 0;

		int component1Modifications = 2011;
		int component1Position = 1;

		int component1_0Modifications = 2012;
		int component1_0Position = 0;

		int question1_0Modifications = 2013;
		QuestionType question1_0Type = QuestionType.TEXT;
		QuestionKind question1_0Kind = QuestionKind.CHECKBOX;
		String question1_0Content = "What is your favourite color?";

		int answer1_0_0Modifications = 2014;
		int answer1_0_0Position = 0;
		String answer1_0_0Value = "red";

		int answer1_0_1Modifications = 2015;
		int answer1_0_1Position = 1;
		String answer1_0_1Value = "yellow";

		int answer1_0_2Modifications = 2016;
		int answer1_0_2Position = 2;
		String answer1_0_2Value = "blue";

		int component1_1Modifications = 2017;
		int component1_1Position = 1;

		int question1_1Modifications = 2018;
		QuestionType question1_1Type = QuestionType.NUMERIC;
		QuestionKind question1_1Kind = QuestionKind.RADIO;
		String question1_1Content = "2 + 2 =";

		int answer1_1_0Modifications = 2019;
		int answer1_1_0Position = 0;
		double answer1_1_0Value = 3.0;

		int answer1_1_1Modifications = 2020;
		int answer1_1_1Position = 1;
		double answer1_1_1Value = 4.0;

		int answer1_1_2Modifications = 2021;
		int answer1_1_2Position = 2;
		double answer1_1_2Value = 5.0;

		JSONObject jsonSurvey = new JSONObject("{" + "id: 1,"
				+ "name: \"restored survey\"," + "modifications: 0,"
				+ "description: \"restored survey description\","
				+ "expirationDate: 1242352," + "rootComponent: {"
				+ "id: 2," + "modifications: "
				+ rootComponentModifications
				+ ","
				+ "position: null,"
				+ "components: ["
				+ "{"
				+ "id: 3,"
				+ "modifications: "
				+ component0Modifications
				+ ","
				+ "position: "
				+ component0Position
				+ ","
				+ "components: [null],"
				+ "question: null"
				+ "},"
				+ "{"
				+ "id: 4,"
				+ "modifications: "
				+ component1Modifications
				+ ","
				+ "position: "
				+ component1Position
				+ ","
				+ "components: ["
				+ "{"
				+ "id: 4,"
				+ "modifications: "
				+ component1_0Modifications
				+ ","
				+ "position: "
				+ component1_0Position
				+ ","
				+ "components: null,"
				+ "question: {"
				+ "id: 3,"
				+ "type: \""
				+ question1_0Type.toString().toLowerCase()
				+ "\","
				+ "kind: \""
				+ question1_0Kind.toString().toLowerCase()
				+ "\","
				+ "modifications: "
				+ question1_0Modifications
				+ ","
				+ "content: \""
				+ question1_0Content
				+ "\","
				+ "answers: [{"
				+ "id: 6,"
				+ "position: "
				+ answer1_0_0Position
				+ ","
				+ "modifications: "
				+ answer1_0_0Modifications
				+ ","
				+ "value: \""
				+ answer1_0_0Value
				+ "\""
				+ "},"
				+ "{"
				+ "id: 8,"
				+ "position: "
				+ answer1_0_1Position
				+ ","
				+ "modifications: "
				+ answer1_0_1Modifications
				+ ","
				+ "value: \""
				+ answer1_0_1Value
				+ "\""
				+ "},"
				+ "{"
				+ "id: 16,"
				+ "position: "
				+ answer1_0_2Position
				+ ","
				+ "modifications: "
				+ answer1_0_2Modifications
				+ ","
				+ "value: \""
				+ answer1_0_2Value
				+ "\""
				+ "}]"
				+ "}"
				+ "},"
				+ "{"
				+ "id: 5,"
				+ "modifications: "
				+ component1_1Modifications
				+ ","
				+ "position: "
				+ component1_1Position
				+ ","
				+ "components: null,"
				+ "question: {"
				+ "id: 6,"
				+ "type: \""
				+ question1_1Type.toString().toLowerCase()
				+ "\","
				+ "kind: \""
				+ question1_1Kind.toString().toLowerCase()
				+ "\","
				+ "modifications: "
				+ question1_1Modifications
				+ ","
				+ "content: \""
				+ question1_1Content
				+ "\","
				+ "answers: [{"
				+ "id: 23,"
				+ "position: "
				+ answer1_1_0Position
				+ ","
				+ "modifications: "
				+ answer1_1_0Modifications
				+ ","
				+ "value: "
				+ answer1_1_0Value
				+ ""
				+ "},"
				+ "{"
				+ "id: 12,"
				+ "position: "
				+ answer1_1_1Position
				+ ","
				+ "modifications: "
				+ answer1_1_1Modifications
				+ ","
				+ "value: "
				+ answer1_1_1Value
				+ ""
				+ "},"
				+ "{"
				+ "id: 15,"
				+ "position: "
				+ answer1_1_2Position
				+ ","
				+ "modifications: "
				+ answer1_1_2Modifications
				+ ","
				+ "value: "
				+ answer1_1_2Value
				+ ""
				+ "}]"
				+ "}"
				+ "}],"
				+ "question: null" + "}]," + "question: null" + "}" + "}");

		restorationService.restoreSurvey(survey.getId(), jsonSurvey);

		assertEquals(4, componentDAO.findAll().size() - componentsSize);
		assertEquals(2, questionDAO.findAll().size() - questionsSize);
		assertEquals(6, answerDAO.findAll().size() - answersSize);

		Component rootComponent = survey.getRootComponent();
		assertNotNull(rootComponent);
		assertEquals(rootComponentModifications, rootComponent
				.getModifications());
		assertEquals(null, rootComponent.getPosition());

		List<Component> rootComponents = componentDAO
				.findByParent(rootComponent);
		assertEquals(2, rootComponents.size());
		Component component0 = componentManager.getComponent(rootComponent,
				component0Position);

		assertNotNull(component0);
		assertNull(questionDAO.findByParent(component0));
		assertEquals(0, componentManager.getComponents(component0).size());
		assertEquals(component0Modifications, component0.getModifications());
		assertEquals(component0Position, component0.getPosition());

		Component component1 = componentManager.getComponent(rootComponent,
				component1Position);

		assertNotNull(component1);
		assertNull(questionDAO.findByParent(component1));
		assertEquals(component1Modifications, component1.getModifications());
		assertEquals(component1Position, component1.getPosition());

		List<Component> components1 = componentDAO.findByParent(component1);
		assertEquals(2, components1.size());

		Component component1_0 = componentManager.getComponent(component1,
				component1_0Position);
		assertNotNull(component1_0);
		assertEquals(0, componentManager.getComponents(component1_0).size());
		assertEquals(component1_0Modifications, component1_0.getModifications());
		assertEquals(component1_0Position, component1_0.getPosition());

		Question question1_0 = questionDAO.findByParent(component1_0);
		assertNotNull(question1_0);
		assertEquals(question1_0Modifications, question1_0.getModifications());
		assertEquals(question1_0Type, question1_0.getType());
		assertEquals(question1_0Kind, question1_0.getKind());
		assertEquals(question1_0Content, question1_0.getContent());

		List<Answer> answers1_0 = answerDAO.findByQuestion(question1_0);
		assertEquals(3, answers1_0.size());

		Answer answer1_0_0 = questionManager.getAnswer(question1_0,
				answer1_0_0Position);
		assertNotNull(answer1_0_0);
		assertEquals(answer1_0_0Modifications, answer1_0_0.getModifications());
		assertEquals(answer1_0_0Position, answer1_0_0.getPosition());
		assertEquals(answer1_0_0Value, ((TextAnswer) answer1_0_0).getValue());

		Answer answer1_0_1 = questionManager.getAnswer(question1_0,
				answer1_0_1Position);
		assertNotNull(answer1_0_1);
		assertEquals(answer1_0_1Modifications, answer1_0_1.getModifications());
		assertEquals(answer1_0_1Position, answer1_0_1.getPosition());
		assertEquals(answer1_0_1Value, ((TextAnswer) answer1_0_1).getValue());

		Answer answer1_0_2 = questionManager.getAnswer(question1_0,
				answer1_0_2Position);
		assertNotNull(answer1_0_2);
		assertEquals(answer1_0_2Modifications, answer1_0_2.getModifications());
		assertEquals(answer1_0_2Position, answer1_0_2.getPosition());
		assertEquals(answer1_0_2Value, ((TextAnswer) answer1_0_2).getValue());

		Component component1_1 = componentManager.getComponent(component1,
				component1_1Position);
		assertNotNull(component1_1);
		assertEquals(0, componentManager.getComponents(component1_1).size());
		assertEquals(component1_1Modifications, component1_1.getModifications());
		assertEquals(component1_1Position, component1_1.getPosition());

		Question question1_1 = questionDAO.findByParent(component1_1);
		assertNotNull(question1_1);
		assertEquals(question1_1Modifications, question1_1.getModifications());
		assertEquals(question1_1Type, question1_1.getType());
		assertEquals(question1_1Kind, question1_1.getKind());
		assertEquals(question1_1Content, question1_1.getContent());

		List<Answer> answers1_1 = answerDAO.findByQuestion(question1_1);
		assertEquals(3, answers1_1.size());

		Answer answer1_1_0 = questionManager.getAnswer(question1_1,
				answer1_1_0Position);
		assertNotNull(answer1_1_0);
		assertEquals(answer1_1_0Modifications, answer1_1_0.getModifications());
		assertEquals(answer1_1_0Position, answer1_1_0.getPosition());
		assertEquals(answer1_1_0Value, ((NumericAnswer) answer1_1_0).getValue());

		Answer answer1_1_1 = questionManager.getAnswer(question1_1,
				answer1_1_1Position);
		assertNotNull(answer1_1_1);
		assertEquals(answer1_1_1Modifications, answer1_1_1.getModifications());
		assertEquals(answer1_1_1Position, answer1_1_1.getPosition());
		assertEquals(answer1_1_1Value, ((NumericAnswer) answer1_1_1).getValue());

		Answer answer1_1_2 = questionManager.getAnswer(question1_1,
				answer1_1_2Position);
		assertNotNull(answer1_1_2);
		assertEquals(answer1_1_2Modifications, answer1_1_2.getModifications());
		assertEquals(answer1_1_2Position, answer1_1_2.getPosition());
		assertEquals(answer1_1_2Value, ((NumericAnswer) answer1_1_2).getValue());
		tm.commit(ts);
	}

	@Test
	public void testSurveyToJSON() {
		Survey survey = new Survey();
		survey.setName("survey to json");
		survey.setDescription("survey to json description");
		surveyManager.createSurvey(survey);

		int rootComponentModifications = 9;

		int component0Modifications = 10;
		int component0Position = 0;

		int component1Modifications = 11;
		int component1Position = 1;

		int component1_0Modifications = 12;
		int component1_0Position = 0;

		int question1_0Modifications = 2013;
		QuestionType question1_0Type = QuestionType.TEXT;
		QuestionKind question1_0Kind = QuestionKind.CHECKBOX;
		String question1_0Content = "What is your name?";

		int answer1_0_0Modifications = 14;
		int answer1_0_0Position = 0;
		String answer1_0_0Value = "John";

		int answer1_0_1Modifications = 15;
		int answer1_0_1Position = 1;
		String answer1_0_1Value = "Paul";

		int answer1_0_2Modifications = 16;
		int answer1_0_2Position = 2;
		String answer1_0_2Value = "Mark";

		int component1_1Modifications = 17;
		int component1_1Position = 1;

		int question1_1Modifications = 18;
		QuestionType question1_1Type = QuestionType.NUMERIC;
		QuestionKind question1_1Kind = QuestionKind.CHECKBOX;
		String question1_1Content = "What is your favourite number?";

		int answer1_1_0Modifications = 19;
		int answer1_1_0Position = 0;
		double answer1_1_0Value = 314.0;

		int answer1_1_1Modifications = 20;
		int answer1_1_1Position = 1;
		double answer1_1_1Value = 628.0;

		int answer1_1_2Modifications = 21;
		int answer1_1_2Position = 2;
		double answer1_1_2Value = 75.0;

		Component rootComponent = survey.getRootComponent();
		rootComponent.setModifications(rootComponentModifications);
		componentDAO.update(rootComponent);

		Component component0 = componentManager
				.createComponent(rootComponent);
		component0.setModifications(component0Modifications);
		componentDAO.update(component0);

		Component component1 = componentManager
				.createComponent(rootComponent);
		component1.setModifications(component1Modifications);
		componentDAO.update(component1);

		Question question1_0 = new Question();
		question1_0.setModifications(question1_0Modifications);
		question1_0.setType(question1_0Type);
		question1_0.setKind(question1_0Kind);
		question1_0.setContent(question1_0Content);
		questionManager.createQuestion(question1_0, component1,
				component1_0Position);

		Component component1_0 = question1_0.getParentComponent();
		component1_0.setModifications(component1_0Modifications);
		componentDAO.update(component1_0);

		TextAnswer answer1_0_0 = (TextAnswer) questionManager
				.createAnswer(question1_0);
		answer1_0_0.setModifications(answer1_0_0Modifications);
		questionManager.setAnswer(answer1_0_0, answer1_0_0Value);

		TextAnswer answer1_0_1 = (TextAnswer) questionManager
				.createAnswer(question1_0);
		answer1_0_1.setModifications(answer1_0_1Modifications);
		questionManager.setAnswer(answer1_0_1, answer1_0_1Value);

		TextAnswer answer1_0_2 = (TextAnswer) questionManager
				.createAnswer(question1_0);
		answer1_0_2.setModifications(answer1_0_2Modifications);
		questionManager.setAnswer(answer1_0_2, answer1_0_2Value);

		Question question1_1 = new Question();
		question1_1.setModifications(question1_1Modifications);
		question1_1.setType(question1_1Type);
		question1_1.setKind(question1_1Kind);
		question1_1.setContent(question1_1Content);
		questionManager.createQuestion(question1_1, component1,
				component1_1Position);

		Component component1_1 = question1_1.getParentComponent();
		component1_1.setModifications(component1_1Modifications);
		componentDAO.update(component1_1);

		NumericAnswer answer1_1_0 = (NumericAnswer) questionManager
				.createAnswer(question1_1);
		answer1_1_0.setModifications(answer1_1_0Modifications);
		questionManager.setAnswer(answer1_1_0, answer1_1_0Value);

		NumericAnswer answer1_1_1 = (NumericAnswer) questionManager
				.createAnswer(question1_1);
		answer1_1_1.setModifications(answer1_1_1Modifications);
		questionManager.setAnswer(answer1_1_1, answer1_1_1Value);

		NumericAnswer answer1_1_2 = (NumericAnswer) questionManager
				.createAnswer(question1_1);
		answer1_1_2.setModifications(answer1_1_2Modifications);
		questionManager.setAnswer(answer1_1_2, answer1_1_2Value);

		JSONObject jsonSurvey = restorationService.surveyToJSON(survey.getId());

		assertNotNull(jsonSurvey);

		JSONObject jsonRootComponent = jsonSurvey
				.getJSONObject("rootComponent");

		assertNotNull(jsonRootComponent);

		JSONArray jsonRootComponents = jsonRootComponent
				.getJSONArray("components");

		assertNotNull(jsonRootComponents);
		assertEquals(2, jsonRootComponents.length());

		JSONObject jsonComponent0 = jsonRootComponents.getJSONObject(0);
		assertNotNull(jsonComponent0);
		assertEquals(component0Position, jsonComponent0.getInt("position"));
		assertTrue(jsonComponent0.isNull("question"));

		JSONObject jsonComponent1 = jsonRootComponents.getJSONObject(1);
		assertNotNull(jsonComponent1);
		assertEquals(component1Position, jsonComponent1.getInt("position"));

		JSONArray jsonComponents1 = jsonComponent1.getJSONArray("components");
		assertNotNull(jsonComponents1);
		assertEquals(2, jsonComponents1.length());

		JSONObject jsonComponent1_0 = jsonComponents1.getJSONObject(0);
		assertNotNull(jsonComponent1_0);
		assertEquals(component1_0Position, jsonComponent1_0.getInt("position"));
		assertFalse(jsonComponent1_0.isNull("question"));

		JSONObject jsonQuestion1_0 = jsonComponent1_0.getJSONObject("question");
		assertNotNull(jsonQuestion1_0);
		assertEquals(question1_0Type.toString().toLowerCase(), jsonQuestion1_0
				.getString("type"));
		assertEquals(question1_0Kind.toString().toLowerCase(), jsonQuestion1_0
				.getString("kind"));
		assertEquals(question1_0Content, jsonQuestion1_0.getString("content"));

		JSONArray jsonAnswers1_0 = jsonQuestion1_0.getJSONArray("answers");
		assertNotNull(jsonAnswers1_0);
		assertEquals(3, jsonAnswers1_0.length());

		JSONObject jsonAnswer1_0_0 = jsonAnswers1_0.getJSONObject(0);
		assertNotNull(jsonAnswer1_0_0);
		assertEquals(answer1_0_0Position, jsonAnswer1_0_0.getInt("position"));
		assertEquals(answer1_0_0Value, jsonAnswer1_0_0.getString("value"));

		JSONObject jsonAnswer1_0_1 = jsonAnswers1_0.getJSONObject(1);
		assertNotNull(jsonAnswer1_0_1);
		assertEquals(answer1_0_1Position, jsonAnswer1_0_1.getInt("position"));
		assertEquals(answer1_0_1Value, jsonAnswer1_0_1.getString("value"));

		JSONObject jsonAnswer1_0_2 = jsonAnswers1_0.getJSONObject(2);
		assertNotNull(jsonAnswer1_0_2);
		assertEquals(answer1_0_2Position, jsonAnswer1_0_2.getInt("position"));
		assertEquals(answer1_0_2Value, jsonAnswer1_0_2.getString("value"));

		JSONObject jsonComponent1_1 = jsonComponents1.getJSONObject(1);
		assertNotNull(jsonComponent1_1);
		assertEquals(component1_1Position, jsonComponent1_1.getInt("position"));
		assertFalse(jsonComponent1_0.isNull("question"));

		JSONObject jsonQuestion1_1 = jsonComponent1_1.getJSONObject("question");
		assertNotNull(jsonQuestion1_1);
		assertEquals(question1_1Type.toString().toLowerCase(), jsonQuestion1_1
				.getString("type"));
		assertEquals(question1_1Kind.toString().toLowerCase(), jsonQuestion1_1
				.getString("kind"));
		assertEquals(question1_1Content, jsonQuestion1_1.getString("content"));

		JSONArray jsonAnswers1_1 = jsonQuestion1_1.getJSONArray("answers");
		assertNotNull(jsonAnswers1_1);
		assertEquals(3, jsonAnswers1_1.length());

		JSONObject jsonAnswer1_1_0 = jsonAnswers1_1.getJSONObject(0);
		assertNotNull(jsonAnswer1_1_0);
		assertEquals(answer1_1_0Position, jsonAnswer1_1_0.getInt("position"));
		assertEquals(answer1_1_0Value, jsonAnswer1_1_0.getDouble("value"));

		JSONObject jsonAnswer1_1_1 = jsonAnswers1_1.getJSONObject(1);
		assertNotNull(jsonAnswer1_1_1);
		assertEquals(answer1_1_1Position, jsonAnswer1_1_1.getInt("position"));
		assertEquals(answer1_1_1Value, jsonAnswer1_1_1.getDouble("value"));

		JSONObject jsonAnswer1_1_2 = jsonAnswers1_1.getJSONObject(2);
		assertNotNull(jsonAnswer1_1_2);
		assertEquals(answer1_1_2Position, jsonAnswer1_1_2.getInt("position"));
		assertEquals(answer1_1_2Value, jsonAnswer1_1_2.getDouble("value"));
	}

}
