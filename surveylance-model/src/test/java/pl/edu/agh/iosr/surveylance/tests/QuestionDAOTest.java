package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.Question;

/**
 * This class represents Question class test.
 * 
 * @author kornel
 */
public class QuestionDAOTest extends GenericDAOTest {

	private QuestionDAO questionDAO;
	private ComponentDAO componentDAO;

	private Component parentComponent;

	/**
	 * Public constructor.
	 */
	public QuestionDAOTest() {
		questionDAO = (QuestionDAO) applicationContext.getBean("questionDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(Question.class, questionDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById((Entity) new Question(), (GenericDAO) questionDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll((Entity) new Question(), (GenericDAO) questionDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[1];
		excludedProperties[0] = "content";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		Question question = new Question();
		question.setContent("Example question?");
		question.setType(QuestionType.TEXT);
		question.setKind(QuestionKind.INPUT_TEXT);

		this.parentComponent = new Component();
		componentDAO.create(this.parentComponent);

		question.setParentComponent(this.parentComponent);

		return question;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		componentDAO.delete(this.parentComponent);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		return new Question();
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

		Question question = new Question();
		super.testFindByExampleEStringArray((Entity) question,
				(GenericDAO) questionDAO);

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
				(GenericDAO) questionDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists((Entity) new Question(), (GenericDAO) questionDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate((Entity) new Question(), (GenericDAO) questionDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((Question) entity).setModifications(new Integer(10));
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((Question) entity1).getId(), ((Question) entity2).getId());
		assertEquals(((Question) entity1).getModifications(),
				((Question) entity2).getModifications());
		assertEquals(((Question) entity1).getContent(), ((Question) entity2)
				.getContent());
		assertEquals(((Question) entity1).getParentComponent(),
				((Question) entity2).getParentComponent());
		assertEquals(((Question) entity1).getType(), ((Question) entity2)
				.getType());
		assertEquals(((Question) entity1).getKind(), ((Question) entity2)
				.getKind());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate((Entity) new Question(), (GenericDAO) questionDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete((Entity) new Question(), (GenericDAO) questionDAO);
	}

	/**
	 * This method tests findByParent() method.
	 */
	@Test
	public void testFindByParent() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Component component = new Component();
		component.setModifications(new Integer(1));
		Question question = new Question();
		question.setParentComponent(component);

		componentDAO.create(component);
		questionDAO.create(question);

		assertEquals(question.getId(), questionDAO.findByParent(component)
				.getId());

		questionDAO.delete(question);
		componentDAO.delete(component);

		tm.commit(ts);
	}

}
