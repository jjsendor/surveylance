package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.DecisionDAO;
import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;

/**
 * This class represents Decision class test.
 * 
 * @author kornel
 */
public class DecisionDAOTest extends GenericDAOTest {

	private DecisionDAO decisionDAO;
	private ComponentDAO componentDAO;
	private AnswerDAO answerDAO;
	private ForkComponentDAO forkComponentDAO;

	private Component parentComponent;
	private NumericAnswer questionAnswer;
	private ForkComponent forkComponent;

	/**
	 * Public constructor.
	 */
	public DecisionDAOTest() {
		decisionDAO = (DecisionDAO) applicationContext.getBean("decisionDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");
		forkComponentDAO = (ForkComponentDAO) applicationContext
				.getBean("forkComponentDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(Decision.class, decisionDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById((Entity) new Decision(), (GenericDAO) decisionDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll((Entity) new Decision(), (GenericDAO) decisionDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[1];
		excludedProperties[0] = "forkComponent";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		Decision decision = new Decision();

		this.parentComponent = new Component();
		this.parentComponent.setModifications(new Integer(0));
		componentDAO.create(this.parentComponent);

		decision.setComponent(this.parentComponent);

		this.questionAnswer = new NumericAnswer();
		this.questionAnswer.setModifications(new Integer(0));
		answerDAO.create(this.questionAnswer);

		decision.setAnswer(this.questionAnswer);

		this.forkComponent = new ForkComponent();
		this.forkComponent.setModifications(new Integer(0));
		forkComponentDAO.create(this.forkComponent);

		decision.setForkComponent(this.forkComponent);
		return decision;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		forkComponentDAO.delete(this.forkComponent);
		answerDAO.delete(this.questionAnswer);
		componentDAO.delete(this.parentComponent);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		return new Decision();
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

		Decision decision = new Decision();

		ForkComponent forkComponent = new ForkComponent();
		forkComponent.setModifications(new Integer(0));
		forkComponentDAO.create(forkComponent);

		decision.setForkComponent(forkComponent);
		super.testFindByExampleEStringArray((Entity) decision,
				(GenericDAO) decisionDAO);

		forkComponentDAO.delete(forkComponent);

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
				(GenericDAO) decisionDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists((Entity) new Decision(), (GenericDAO) decisionDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate((Entity) new Decision(), (GenericDAO) decisionDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		ForkComponent forkComponent = new ForkComponent();
		((Decision) entity).setForkComponent(forkComponent);
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((Decision) entity1).getId(), ((Decision) entity2).getId());
		assertEquals(((Decision) entity1).getForkComponent(),
				((Decision) entity2).getForkComponent());
		assertEquals(((Decision) entity1).getAnswer(), ((Decision) entity2)
				.getAnswer());
		assertEquals(((Decision) entity1).getComponent(), ((Decision) entity2)
				.getComponent());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate((Entity) new Decision(), (GenericDAO) decisionDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete((Entity) new Decision(), (GenericDAO) decisionDAO);
	}

	/**
	 * This method tests findByForkComponent() and
	 * findByForkComponentAndComponent() methods.
	 */
	@Test
	public void testFindByForkComponentAndComponent() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		ForkComponent forkComponent = new ForkComponent();
		forkComponent.setModifications(new Integer(0));
		forkComponentDAO.create(forkComponent);

		Component component = new Component();
		component.setModifications(new Integer(3));
		componentDAO.create(component);

		int sizeBefore = decisionDAO.findByForkComponent(forkComponent).size();

		Decision decision1 = new Decision();
		Decision decision2 = new Decision();
		decision1.setForkComponent(forkComponent);
		decision1.setComponent(component);
		decision2.setForkComponent(forkComponent);
		decisionDAO.create(decision1);
		decisionDAO.create(decision2);

		int sizeAfter = decisionDAO.findByForkComponent(forkComponent).size();

		assertEquals(sizeBefore + 2, sizeAfter);

		sizeAfter = decisionDAO.findByForkComponentAndComponent(forkComponent,
				component).size();

		assertEquals(sizeBefore + 1, sizeAfter);

		forkComponentDAO.delete(forkComponent);
		decisionDAO.delete(decision1);
		decisionDAO.delete(decision2);

		tm.commit(ts);
	}

}
