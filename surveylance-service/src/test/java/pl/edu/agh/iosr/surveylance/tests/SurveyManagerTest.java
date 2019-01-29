package pl.edu.agh.iosr.surveylance.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
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

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;

public class SurveyManagerTest {

	private static ApplicationContext applicationContext;

	private SurveyManager surveyManager;

	private SurveyDAO surveyDAO;
	private ComponentDAO componentDAO;
	private UserDAO userDAO;

	/**
	 * Sets up testing environment. That means opening hibernate session factory
	 * and opening new hibernate session which is used in tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] paths = {
				"applicationContext.xml", "applicationContextDao.xml" };
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
	 * Sets up testing environment for single test: creates DAO for survey and
	 * manager service for surveys.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		surveyDAO = (SurveyDAO) applicationContext.getBean("surveyDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		userDAO = (UserDAO) applicationContext.getBean("userDAO");

		surveyManager = (SurveyManager) applicationContext
				.getBean("surveyManager");
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
		userDAO = null;
		surveyManager = null;
	}

	@Test
	public void testCreateSurvey() {
		Survey survey = new Survey();
		survey.setName("test survey1");
		survey.setDescription("test description1");
		survey.setExpirationDate(Calendar.getInstance().getTime());

		try {
			surveyManager.createSurvey(survey);
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}
	}

	@Test
	public void testGetSurvey() {
		Survey survey = new Survey();
		survey.setName("test survey2");
		survey.setDescription("test description2");
		Date date = Calendar.getInstance().getTime();
		survey.setExpirationDate(date);
		try {
			surveyManager.createSurvey(survey);
			Survey survey2 = surveyManager.getSurvey(survey.getId());

			assertEquals("test survey2", survey2.getName());
			assertEquals("test description2", survey2.getDescription());
			Calendar cal1 = new GregorianCalendar();
			cal1.setTime(date);
			Calendar cal2 = new GregorianCalendar();
			cal2.setTime(survey2.getExpirationDate());
			assertEquals(cal1.getTime().toString(), cal2.getTime().toString());
			assertNotNull(survey2.getRootComponent());
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteSurvey() {
		Survey survey = new Survey();
		survey.setName("test survey3");
		survey.setDescription("test description3");
		survey.setExpirationDate(Calendar.getInstance().getTime());

		try {
			HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
					.getBean("transactionManager");
			TransactionStatus ts = tm
					.getTransaction(new DefaultTransactionDefinition());

			surveyManager.createSurvey(survey);
			int beforeSize = surveyDAO.findAll().size();
			surveyManager.deleteSurvey(survey);
			tm.commit(ts);

			int afterSize = surveyDAO.findAll().size();
			assertEquals(beforeSize - 1, afterSize);
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteSurveyById() {
		Survey survey = new Survey();
		survey.setName("test survey4");
		survey.setDescription("test description4");
		survey.setExpirationDate(Calendar.getInstance().getTime());

		try {
			HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
					.getBean("transactionManager");
			TransactionStatus ts = tm
					.getTransaction(new DefaultTransactionDefinition());

			surveyManager.createSurvey(survey);
			int beforeSize = surveyDAO.findAll().size();
			surveyManager.deleteSurvey(survey.getId());

			tm.commit(ts);

			int afterSize = surveyDAO.findAll().size();
			assertEquals(beforeSize - 1, afterSize);
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}
	}

	@Test
	public void testGetSurveys() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			int beforeSize = surveyManager.getSurveys().size();

			Survey survey = new Survey();
			survey.setName("test name");
			survey.setDescription("test description");
			surveyManager.createSurvey(survey);

			int afterSize = surveyManager.getSurveys().size();

			assertEquals(beforeSize + 1, afterSize);
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}

		tm.commit(ts);
	}

	@Test
	public void testGetSurveysForUser() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			String userGoogleId1 = "user1@gmail.com";
			String userGoogleId2 = "user2@gmail.com";

			User user1 = userDAO.findByGoogleId(userGoogleId1);

			if (user1 == null) {
				user1 = new User();
				user1.setGoogleId(userGoogleId1);
				userDAO.create(user1);
			}

			User user2 = userDAO.findByGoogleId(userGoogleId2);

			if (user2 == null) {
				user2 = new User();
				user2.setGoogleId(userGoogleId2);
				userDAO.create(user2);
			}

			int beforeSize = surveyManager.getSurveys(user1).size();

			Survey survey1 = new Survey();
			survey1.setOwner(user1);
			surveyManager.createSurvey(survey1);
			Survey survey2 = new Survey();
			survey2.setOwner(user2);
			surveyManager.createSurvey(survey2);

			int afterSize = surveyManager.getSurveys(user1).size();

			assertEquals(beforeSize + 1, afterSize);

			survey1.setOwner(null);
			survey2.setOwner(null);
			surveyDAO.delete(survey1);
			surveyDAO.delete(survey2);
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}

		tm.commit(ts);
	}

	@Test
	public void testOwnership() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Survey survey = new Survey();
			surveyDAO.create(survey);

			String userGoogleId1 = "user1@gmail.com";
			String userGoogleId2 = "user2@gmail.com";

			User user1 = userDAO.findByGoogleId(userGoogleId1);

			if (user1 == null) {
				user1 = new User();
				user1.setGoogleId(userGoogleId1);
				userDAO.create(user1);
			}

			User user2 = userDAO.findByGoogleId(userGoogleId2);

			if (user2 == null) {
				user2 = new User();
				user2.setGoogleId(userGoogleId2);
				userDAO.create(user2);
			}

			survey.setOwner(user1);
			surveyDAO.update(survey);

			assertEquals(true, surveyManager.isSurveyOwner(survey.getId(),
					user1));
			assertEquals(false, surveyManager.isSurveyOwner(survey.getId(),
					user2));

			Component comp1 = new Component();
			componentDAO.create(comp1);
			survey.setRootComponent(comp1);
			surveyDAO.update(survey);

			Component comp2 = new Component();
			comp2.setParentComponent(comp1);
			componentDAO.create(comp2);

			assertEquals(true, surveyManager.isComponentOwner(comp2.getId(),
					user1));
			assertEquals(false, surveyManager.isComponentOwner(comp2.getId(),
					user2));

			survey.setOwner(null);
			surveyDAO.delete(survey);
			componentDAO.delete(comp1);
			componentDAO.delete(comp2);
		} catch (DAOException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}

		tm.commit(ts);
	}

}
