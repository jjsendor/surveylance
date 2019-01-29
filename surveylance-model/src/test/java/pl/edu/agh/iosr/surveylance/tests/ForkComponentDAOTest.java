package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;

/**
 * This class represents ForkComponent class test.
 * 
 * @author kornel
 */
public class ForkComponentDAOTest extends GenericDAOTest {

	private ForkComponentDAO forkComponentDAO;
	private ComponentDAO componentDAO;

	private Component parentComponent;

	/**
	 * Public constructor.
	 */
	public ForkComponentDAOTest() {
		forkComponentDAO = (ForkComponentDAO) applicationContext
				.getBean("forkComponentDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(ForkComponent.class, forkComponentDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById((Entity) createNewEntityWithoutData(),
				(GenericDAO) forkComponentDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll((Entity) createNewEntityWithoutData(),
				(GenericDAO) forkComponentDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[1];
		excludedProperties[0] = "modifications";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		ForkComponent forkComponent = new ForkComponent();
		forkComponent.setModifications(new Integer(0));

		this.parentComponent = new Component();
		this.parentComponent.setModifications(new Integer(1));
		componentDAO.create(this.parentComponent);

		forkComponent.setParentComponent(this.parentComponent);
		return forkComponent;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		componentDAO.delete(this.parentComponent);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		ForkComponent forkComponent = new ForkComponent();
		forkComponent.setModifications(new Integer(0));
		return forkComponent;
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

		ForkComponent forkComponent = new ForkComponent();
		super.testFindByExampleEStringArray((Entity) forkComponent,
				(GenericDAO) forkComponentDAO);

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
				(GenericDAO) forkComponentDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists((Entity) createNewEntityWithoutData(),
				(GenericDAO) forkComponentDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate((Entity) createNewEntityWithoutData(),
				(GenericDAO) forkComponentDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((ForkComponent) entity).setModifications(new Integer(10));
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((ForkComponent) entity1).getId(),
				((ForkComponent) entity2).getId());
		assertEquals(((ForkComponent) entity1).getModifications(),
				((ForkComponent) entity2).getModifications());
		assertEquals(((ForkComponent) entity1).getParentComponent(),
				((ForkComponent) entity2).getParentComponent());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate((Entity) createNewEntityWithoutData(),
				(GenericDAO) forkComponentDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete((Entity) createNewEntityWithoutData(),
				(GenericDAO) forkComponentDAO);
	}

}
