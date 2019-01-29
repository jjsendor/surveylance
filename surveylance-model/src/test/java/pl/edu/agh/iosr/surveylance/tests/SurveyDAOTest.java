package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This class represents Survey class test.
 * 
 * @author kornel
 */
public class SurveyDAOTest extends GenericDAOTest {

	private SurveyDAO surveyDAO;
	private UserDAO userDAO;
	private ComponentDAO componentDAO;

	private User owner;
	private Component component;

	/**
	 * Public constructor.
	 */
	public SurveyDAOTest() {
		surveyDAO = (SurveyDAO) applicationContext.getBean("surveyDAO");
		userDAO = (UserDAO) applicationContext.getBean("userDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
	}

	/**
	 * This method tests findByExpirationDate() method.
	 */
	@Test
	public void testFindByExpirationDate() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		GregorianCalendar calendar = new GregorianCalendar(2009, 12, 12);
		int numberOfSurveysBeforeCreate = surveyDAO.findByExpirationDate(
				new Date(calendar.getTimeInMillis())).size();

		Survey survey = new Survey();
		survey.setExpirationDate(new Date(calendar.getTimeInMillis()));
		surveyDAO.create(survey);
		int numberOfSurveysAfterCreate = surveyDAO.findByExpirationDate(
				new Date(calendar.getTimeInMillis())).size();

		assertEquals(1, numberOfSurveysAfterCreate
				- numberOfSurveysBeforeCreate);

		surveyDAO.delete(survey);

		tm.commit(ts);
	}

	/**
	 * This method tests findByName() method.
	 */
	@Test
	public void testFindByName() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		int numberOfSurveysBeforeCreate = surveyDAO.findByName("Test survey")
				.size();

		Survey survey = new Survey();
		survey.setName("Test survey");
		surveyDAO.create(survey);
		int numberOfSurveysAfterCreate = surveyDAO.findByName("Test survey")
				.size();

		assertEquals(1, numberOfSurveysAfterCreate
				- numberOfSurveysBeforeCreate);

		surveyDAO.delete(survey);

		tm.commit(ts);
	}

	/**
	 * This method tests findByOwner() method.
	 */
	@Test
	public void testFindByOwner() {
		HibernateTransactionManager tm0 = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts0 = tm0
				.getTransaction(new DefaultTransactionDefinition());

		User user = new User();
		user.setGoogleId("test@google.com");
		userDAO.create(user);
		int numberOfSurveysBeforeCreate = surveyDAO.findByOwner(user).size();

		Survey survey = new Survey();
		surveyDAO.create(survey);
		int numberOfSurveysAfterCreate = surveyDAO.findByOwner(user).size();
		assertEquals(0, numberOfSurveysAfterCreate
				- numberOfSurveysBeforeCreate);

		survey.setOwner(user);
		surveyDAO.update(survey);
		numberOfSurveysAfterCreate = surveyDAO.findByOwner(user).size();
		assertEquals(1, numberOfSurveysAfterCreate
				- numberOfSurveysBeforeCreate);

		tm0.commit(ts0);
		HibernateTransactionManager tm1 = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts1 = tm1
				.getTransaction(new DefaultTransactionDefinition());

		surveyDAO.delete(survey);
		userDAO.delete(user);

		tm1.commit(ts1);
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(Survey.class, surveyDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById((Entity) new Survey(), (GenericDAO) surveyDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll((Entity) new Survey(), (GenericDAO) surveyDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[1];
		excludedProperties[0] = "name";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		Survey survey = new Survey();
		survey.setName("Test name");
		survey.setDescription("This is test survey.");
		survey.setExpirationDate(new Date(System.currentTimeMillis()));
		survey.setModifications(new Integer(0));
		survey.setReadonlyFlag();

		this.owner = new User();
		this.owner.setGoogleId("test@google.com");
		userDAO.create(this.owner);

		survey.setOwner(this.owner);

		this.component = new Component();
		this.component.setModifications(new Integer(0));
		componentDAO.create(this.component);

		survey.setRootComponent(this.component);
		return survey;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		componentDAO.delete(this.component);
		userDAO.delete(this.owner);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		Survey survey = new Survey();
		survey.setReadonlyFlag();
		return survey;
	}

	/**
	 * This method tests findByExample() (with excluded properties parameter)
	 * method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindByExampleEStringArray() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Survey survey = new Survey();
		survey.setReadonlyFlag();
		super.testFindByExampleEStringArray((Entity) survey,
				(GenericDAO) surveyDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests findByExample() (without excluded properties parameter)
	 * method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindByExampleE() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		super.testFindByExampleE((Entity) createNewEntityWithData(),
				(GenericDAO) surveyDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists((Entity) new Survey(), (GenericDAO) surveyDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate((Entity) new Survey(), (GenericDAO) surveyDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((Survey) entity).setName("Test name");
		((Survey) entity).setDescription("This is test survey.");
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((Survey) entity1).getId(), ((Survey) entity2).getId());
		assertEquals(((Survey) entity1).getName(), ((Survey) entity2).getName());
		assertEquals(((Survey) entity1).getDescription(), ((Survey) entity2)
				.getDescription());
		assertEquals(((Survey) entity1).getExpirationDate(), ((Survey) entity2)
				.getExpirationDate());
		assertEquals(((Survey) entity1).getModifications(), ((Survey) entity2)
				.getModifications());
		assertEquals(((Survey) entity1).getOwner(), ((Survey) entity2)
				.getOwner());
		assertEquals(((Survey) entity1).getRootComponent(), ((Survey) entity2)
				.getRootComponent());
		assertEquals(((Survey) entity1).isReadonly(), ((Survey) entity2)
				.isReadonly());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate((Survey) new Survey(), (GenericDAO) surveyDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete((Survey) new Survey(), (GenericDAO) surveyDAO);
	}

	/**
	 * This method tests findByRootComponent() method.
	 */
	public void testFindByRootComponent() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Survey survey = (Survey) createNewEntityWithoutData();
		surveyDAO.create(survey);
		assertNull(surveyDAO.findByRootComponent(component));

		Component component = new Component();
		componentDAO.create(component);
		survey.setRootComponent(component);
		surveyDAO.update(survey);
		assertNotNull(surveyDAO.findByRootComponent(component));

		surveyDAO.delete(survey);
		componentDAO.delete(component);

		tm.commit(ts);
	}

}
