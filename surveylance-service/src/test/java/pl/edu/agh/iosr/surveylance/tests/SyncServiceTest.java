package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.SyncService;

public class SyncServiceTest {

	private static ApplicationContext applicationContext;

	private SurveyDAO surveyDAO;
	private ComponentDAO componentDAO;
	private AnswerDAO answerDAO;
	private UserDAO userDAO;

	private SyncService syncService;
	private SurveyManager surveyManager;
	private ComponentManager componentManager;
	private QuestionManager questionManager;

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
		surveyDAO = (SurveyDAO) applicationContext.getBean("surveyDAO");
		componentDAO =
			(ComponentDAO) applicationContext.getBean("componentDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");
		userDAO = (UserDAO) applicationContext.getBean("userDAO");

		syncService = (SyncService) applicationContext.getBean("syncService");
		surveyManager =
			(SurveyManager) applicationContext.getBean("surveyManager");
		componentManager =
			(ComponentManager) applicationContext.getBean("componentManager");
		questionManager =
			(QuestionManager) applicationContext.getBean("questionManager");
	}

	/**
	 * Tears down testing environment after single test.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		surveyDAO = null;
		componentDAO = null;
		answerDAO = null;
		userDAO = null;
		
		syncService = null;
		surveyManager = null;
		componentManager = null;
		questionManager = null;
	}

	@Test
	public void testSyncSurveysModifications() {
		String[] names = {
				"survey1name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey2name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey3name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey4name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey5name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey6name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey7name_" + String.valueOf(Math.random()).substring(0, 6),
				"survey8name_" + String.valueOf(Math.random()).substring(0, 6)
		};
		String[] descriptions = {
				"survey1description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey2description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey3description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey4description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey5description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey6description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey7description_"
					+ String.valueOf(Math.random()).substring(0, 6),
				"survey8description_"
					+ String.valueOf(Math.random()).substring(0, 6)
		};
		Survey[] surveys = new Survey[8];

		String userGoogleId = "sync@gmail.com"
			+ String.valueOf(Math.random()).substring(0, 6);
		User user = userDAO.findByGoogleId(userGoogleId);

		if (user == null) {
			user = new User();
			user.setGoogleId(userGoogleId);
			userDAO.create(user);
		}
		else {
			for (Survey survey : surveyDAO.findByOwner(user))
				surveyManager.deleteSurvey(survey);
		}

		Map<Long, Integer> modificationsMap = new HashMap<Long, Integer>(6);

		for (int i = 0; i < 8; i++) {
			Survey survey = new Survey();
			survey.setName(names[i]);
			survey.setDescription(descriptions[i]);
			survey.setOwner(user);
			surveys[i] = survey;
			
			if (i < 6) {
				surveyManager.createSurvey(survey);
				modificationsMap.put(survey.getId(), 0);
			}
		}

		JSONObject jsonModifications1 =
			syncService.getSurveysModifications(user, modificationsMap);

		assertNotNull(jsonModifications1);

		JSONArray created1 = jsonModifications1.getJSONArray("created");

		assertNotNull(created1);
		assertEquals(0, created1.length());

		JSONArray removed1 = jsonModifications1.getJSONArray("removed");

		assertNotNull(removed1);
		assertEquals(0, removed1.length());

		JSONArray updated1 = jsonModifications1.getJSONArray("updated");

		assertNotNull(updated1);
		assertEquals(0, updated1.length());

		// create 2 new surveys
		surveyManager.createSurvey(surveys[6]);
		surveyManager.createSurvey(surveys[7]);

		// delete 2 surveys
		long survey1Id = surveys[1].getId();
		surveyManager.deleteSurvey(surveys[1]);
		long survey3Id = surveys[3].getId();
		surveyManager.deleteSurvey(surveys[3]);

		// modify 2 surveys
		surveys[0].setModifications(1);
		surveyDAO.update(surveys[0]);
		surveys[4].setModifications(1);
		surveyDAO.update(surveys[4]);

		JSONObject jsonModifications2 =
			syncService.getSurveysModifications(user, modificationsMap);

		assertNotNull(jsonModifications2);

		JSONArray created2 = jsonModifications2.getJSONArray("created");

		assertNotNull(created2);
		assertEquals(2, created2.length());

		JSONObject jsonSurvey6, jsonSurvey7;

		if (surveys[6].getId().equals(created2.getJSONObject(0).get("id"))) {
			jsonSurvey6 = created2.getJSONObject(0);
			jsonSurvey7 = created2.getJSONObject(1);
		}
		else {
			jsonSurvey7 = created2.getJSONObject(0);
			jsonSurvey6 = created2.getJSONObject(1);
		}
		

		assertEquals(surveys[6].getId(), jsonSurvey6.get("id"));
		assertEquals(surveys[6].getName(), jsonSurvey6.get("name"));
		assertEquals(surveys[6].getDescription(),
				jsonSurvey6.get("description"));
		assertEquals(surveys[6].getModifications(),
				jsonSurvey6.get("modifications"));

		assertEquals(surveys[7].getId(), jsonSurvey7.get("id"));
		assertEquals(surveys[7].getName(), jsonSurvey7.get("name"));
		assertEquals(surveys[7].getDescription(),
				jsonSurvey7.get("description"));
		assertEquals(surveys[7].getModifications(),
				jsonSurvey7.get("modifications"));

		JSONArray removed2 = jsonModifications2.getJSONArray("removed");

		assertNotNull(removed2);
		assertEquals(2, removed2.length());

		if (survey1Id == removed2.getLong(0))
			assertEquals(survey3Id, removed2.getLong(1));
		else {
			assertEquals(survey3Id, removed2.getLong(0));
			assertEquals(survey1Id, removed2.getLong(1));
		}

		JSONArray updated2 = jsonModifications2.getJSONArray("updated");

		assertNotNull(updated2);
		assertEquals(2, updated2.length());

		JSONObject jsonSurvey0, jsonSurvey4;

		if (surveys[0].getId().equals(updated2.getJSONObject(0).get("id"))) {
			jsonSurvey0 = updated2.getJSONObject(0);
			jsonSurvey4 = updated2.getJSONObject(1);
		}
		else {
			jsonSurvey4 = updated2.getJSONObject(0);
			jsonSurvey0 = updated2.getJSONObject(1);
		}

		assertEquals(surveys[0].getId(), jsonSurvey0.get("id"));
		assertEquals(surveys[0].getName(), jsonSurvey0.get("name"));
		assertEquals(surveys[0].getDescription(),
				jsonSurvey0.get("description"));
		assertEquals(surveys[0].getModifications(),
				jsonSurvey0.get("modifications"));

		assertEquals(surveys[4].getId(), jsonSurvey4.get("id"));
		assertEquals(surveys[4].getName(), jsonSurvey4.get("name"));
		assertEquals(surveys[4].getDescription(),
				jsonSurvey4.get("description"));
		assertEquals(surveys[4].getModifications(),
				jsonSurvey4.get("modifications"));
	}

	@Test
	public void testSyncComponentsModifications() {
		Component[] components = new Component[8];

		Component parent = new Component();
		componentDAO.create(parent);

		Map<Long, Integer> modificationsMap = new HashMap<Long, Integer>(6);

		for (int i = 0; i < 6; i++) {
			Component component = componentManager.createComponent(parent);
			components[i] = component;
			modificationsMap.put(component.getId(), 0);
		}

		JSONObject jsonModifications1 =
			syncService.getComponentsModifications(parent, modificationsMap);

		assertNotNull(jsonModifications1);

		JSONArray created1 = jsonModifications1.getJSONArray("created");

		assertNotNull(created1);
		assertEquals(0, created1.length());

		JSONArray removed1 = jsonModifications1.getJSONArray("removed");

		assertNotNull(removed1);
		assertEquals(0, removed1.length());

		JSONArray updated1 = jsonModifications1.getJSONArray("updated");

		assertNotNull(updated1);
		assertEquals(0, updated1.length());

		// create 2 new components
		components[6] = componentManager.createComponent(parent);
		components[7] = componentManager.createComponent(parent);

		// delete 2 components
		long component1Id = components[1].getId();
		components[1].setParentComponent(null);
		componentDAO.delete(components[1]);
		long component3Id = components[3].getId();
		components[3].setParentComponent(null);
		componentDAO.delete(components[3]);

		// modify 2 components
		components[0].setModifications(1);
		componentDAO.update(components[0]);
		components[4].setModifications(1);
		componentDAO.update(components[4]);

		JSONObject jsonModifications2 =
			syncService.getComponentsModifications(parent, modificationsMap);

		assertNotNull(jsonModifications2);

		JSONArray created2 = jsonModifications2.getJSONArray("created");

		assertNotNull(created2);
		assertEquals(2, created2.length());

		JSONObject jsonComponent6, jsonComponent7;

		if (components[6].getId().equals(created2.getJSONObject(0).get("id"))) {
			jsonComponent6 = created2.getJSONObject(0);
			jsonComponent7 = created2.getJSONObject(1);
		}
		else {
			jsonComponent7 = created2.getJSONObject(0);
			jsonComponent6 = created2.getJSONObject(1);
		}
		

		assertEquals(components[6].getId(), jsonComponent6.get("id"));
		assertEquals(components[6].getPosition(), jsonComponent6.get("position"));
		assertEquals(components[6].getModifications(),
				jsonComponent6.get("modifications"));

		assertEquals(components[7].getId(), jsonComponent7.get("id"));
		assertEquals(components[7].getPosition(), jsonComponent7.get("position"));
		assertEquals(components[7].getModifications(),
				jsonComponent7.get("modifications"));

		JSONArray removed2 = jsonModifications2.getJSONArray("removed");

		assertNotNull(removed2);
		assertEquals(2, removed2.length());

		if (component1Id == removed2.getLong(0))
			assertEquals(component3Id, removed2.getLong(1));
		else {
			assertEquals(component3Id, removed2.getLong(0));
			assertEquals(component1Id, removed2.getLong(1));
		}

		JSONArray updated2 = jsonModifications2.getJSONArray("updated");

		assertNotNull(updated2);
		assertEquals(2, updated2.length());

		JSONObject jsonComponent0, jsonComponent4;

		if (components[0].getId().equals(updated2.getJSONObject(0).get("id"))) {
			jsonComponent0 = updated2.getJSONObject(0);
			jsonComponent4 = updated2.getJSONObject(1);
		}
		else {
			jsonComponent4 = updated2.getJSONObject(0);
			jsonComponent0 = updated2.getJSONObject(1);
		}

		assertEquals(components[0].getId(), jsonComponent0.get("id"));
		assertEquals(components[0].getPosition(), jsonComponent0.get("position"));
		assertEquals(components[0].getModifications(),
				jsonComponent0.get("modifications"));

		assertEquals(components[4].getId(), jsonComponent4.get("id"));
		assertEquals(components[4].getPosition(), jsonComponent4.get("position"));
		assertEquals(components[4].getModifications(),
				jsonComponent4.get("modifications"));
	}

	@Test
	public void testSyncAnswersModifications() {
		double[] answersValues = {
				Math.random(),
				Math.random(),
				Math.random(),
				Math.random(),
				Math.random(),
				Math.random(),
				Math.random(),
				Math.random()
		};
		Answer[] answers = new Answer[8];

		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);
		question.setKind(QuestionKind.RADIO);
		questionManager.createQuestion(question, parent, 1);

		Map<Long, Integer> modificationsMap = new HashMap<Long, Integer>(6);

		for (int i = 0; i < 6; i++) {
			Answer answer = questionManager.createAnswer(question);
			answers[i] = answer;
//			questionManager.setAnswer(answer, answersValues[i]);
			modificationsMap.put(answer.getId(), 0);
		}

		JSONObject jsonModifications1 =
			syncService.getAnswersModifications(question, modificationsMap);

		assertNotNull(jsonModifications1);

		JSONArray created1 = jsonModifications1.getJSONArray("created");

		assertNotNull(created1);
		assertEquals(0, created1.length());

		JSONArray removed1 = jsonModifications1.getJSONArray("removed");

		assertNotNull(removed1);
		assertEquals(0, removed1.length());

		JSONArray updated1 = jsonModifications1.getJSONArray("updated");

		assertNotNull(updated1);
		assertEquals(0, updated1.length());

		// create 2 new answers
		answers[6] = questionManager.createAnswer(question);
		questionManager.setAnswer(answers[6], answersValues[6]);
		answers[7] = questionManager.createAnswer(question);
		questionManager.setAnswer(answers[7], answersValues[7]);

		// delete 2 answers
		long answer1Id = answers[1].getId();
		answers[1].setQuestion(null);
		answerDAO.delete(answers[1]);
		long answer3Id = answers[3].getId();
		answers[3].setQuestion(null);
		answerDAO.delete(answers[3]);

		// modify 2 answers
		answers[0].setModifications(1);
		answerDAO.update(answers[0]);
		answers[4].setModifications(1);
		answerDAO.update(answers[4]);

		JSONObject jsonModifications2 =
			syncService.getAnswersModifications(question, modificationsMap);

		assertNotNull(jsonModifications2);

		JSONArray created2 = jsonModifications2.getJSONArray("created");

		assertNotNull(created2);
		assertEquals(2, created2.length());

		JSONObject jsonAnswer6, jsonAnswer7;

		if (answers[6].getId().equals(created2.getJSONObject(0).get("id"))) {
			jsonAnswer6 = created2.getJSONObject(0);
			jsonAnswer7 = created2.getJSONObject(1);
		}
		else {
			jsonAnswer7 = created2.getJSONObject(0);
			jsonAnswer6 = created2.getJSONObject(1);
		}
		

		assertEquals(answers[6].getId(), jsonAnswer6.get("id"));
		assertEquals(answers[6].getPosition(), jsonAnswer6.get("position"));
		assertEquals(answers[6].getModifications(),
				jsonAnswer6.get("modifications"));

		assertEquals(answers[7].getId(), jsonAnswer7.get("id"));
		assertEquals(answers[7].getPosition(), jsonAnswer7.get("position"));
		assertEquals(answers[7].getModifications(),
				jsonAnswer7.get("modifications"));

		JSONArray removed2 = jsonModifications2.getJSONArray("removed");

		assertNotNull(removed2);
		assertEquals(2, removed2.length());

		if (answer1Id == removed2.getLong(0))
			assertEquals(answer3Id, removed2.getLong(1));
		else {
			assertEquals(answer3Id, removed2.getLong(0));
			assertEquals(answer1Id, removed2.getLong(1));
		}

		JSONArray updated2 = jsonModifications2.getJSONArray("updated");

		assertNotNull(updated2);
		assertEquals(2, updated2.length());

		JSONObject jsonAnswer0, jsonAnswer4;

		if (answers[0].getId().equals(updated2.getJSONObject(0).get("id"))) {
			jsonAnswer0 = updated2.getJSONObject(0);
			jsonAnswer4 = updated2.getJSONObject(1);
		}
		else {
			jsonAnswer4 = updated2.getJSONObject(0);
			jsonAnswer0 = updated2.getJSONObject(1);
		}

		assertEquals(answers[0].getId(), jsonAnswer0.get("id"));
		assertEquals(answers[0].getPosition(), jsonAnswer0.get("position"));
		assertEquals(answers[0].getModifications(),
				jsonAnswer0.get("modifications"));

		assertEquals(answers[4].getId(), jsonAnswer4.get("id"));
		assertEquals(answers[4].getPosition(), jsonAnswer4.get("position"));
		assertEquals(answers[4].getModifications(),
				jsonAnswer4.get("modifications"));
	}

}
