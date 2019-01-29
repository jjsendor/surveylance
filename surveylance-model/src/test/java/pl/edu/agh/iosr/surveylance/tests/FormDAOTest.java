package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This class represents Form class test.
 * 
 * @author fibi
 * @author kornel
 */
public class FormDAOTest extends GenericDAOTest {

	private FormDAO formDAO;
	private SurveyDAO surveyDAO;
	private UserDAO userDAO;

	private Survey survey;
	private User user;

	/**
	 * Public constructor.
	 */
	public FormDAOTest() {
		formDAO = (FormDAO) applicationContext.getBean("formDAO");
		surveyDAO = (SurveyDAO) applicationContext.getBean("surveyDAO");
		userDAO = (UserDAO) applicationContext.getBean("userDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(Form.class, formDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById(createNewEntityWithoutData(), (GenericDAO) formDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll(createNewEntityWithoutData(), (GenericDAO) formDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[4];
		excludedProperties[0] = "submitted";
		excludedProperties[1] = "hash";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		Form form = new Form();
		form.setHash("example_hash");
		form.setSubmitted(false);
		form.setX(new Double(23.56));
		form.setY(new Double(-4645.342));

		this.survey = new Survey();
		this.survey.setDescription("murzyn murzyn");
		this.survey.setExpirationDate(new Date());
		this.survey.setModifications(5);
		this.survey.setName("murzyn");
		this.surveyDAO.create(survey);

		this.user = new User();
		this.user.setGoogleId("qwerty");
		userDAO.create(user);

		form.setSurvey(survey);
		form.setUser(user);

		return form;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		surveyDAO.delete(this.survey);
		userDAO.delete(this.user);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		Form form = new Form();
		return form;
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

		Form form = new Form();
		form.setHash("haaaash");
		form.setSubmitted(true);
		super
				.testFindByExampleEStringArray((Entity) form,
						(GenericDAO) formDAO);

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

		super.testFindByExampleE(this.createNewEntityWithData(),
				(GenericDAO) formDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists(createNewEntityWithoutData(), (GenericDAO) formDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate(createNewEntityWithoutData(), (GenericDAO) formDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((Form) entity).setHash("aaaaaaaaaaa");
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		Form form1 = (Form) entity1;
		Form form2 = (Form) entity2;

		assertEquals(form1.getHash(), form2.getHash());
		assertEquals(form1.getId(), form2.getId());
		assertEquals(form1.getSubmitted(), form2.getSubmitted());
		assertEquals(form1.getSurvey(), form2.getSurvey());
		assertEquals(form1.getuser(), form2.getuser());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate(createNewEntityWithoutData(), (GenericDAO) formDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete(createNewEntityWithoutData(), (GenericDAO) formDAO);
	}

	/**
	 * This method tests findByHash() method.
	 */
	@Test
	public void testFindByHash() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Form form = new Form();
		form.setHash("qwedsa");
		form.setSubmitted(false);
		formDAO.create(form);

		assertEquals(form, formDAO.findByHash("qwedsa"));

		formDAO.delete(form);

		tm.commit(ts);
	}

}
