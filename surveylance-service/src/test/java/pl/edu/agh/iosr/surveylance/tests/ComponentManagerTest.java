package pl.edu.agh.iosr.surveylance.tests;

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

import static org.junit.Assert.*;

import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;

public class ComponentManagerTest {

	private static ApplicationContext applicationContext;

	private ComponentManager componentManager;

	private ComponentDAO componentDAO;

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
	 * Sets up the test fixture (Called before every test case method).
	 */
	@Before
	public void setUp() throws Exception {
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");

		componentManager =
			(ComponentManager) applicationContext.getBean("componentManager");
	}

	/**
	 * Tears down the test fixture (Called after every test case method).
	 */
	@After
	public void tearDown() throws Exception {
		componentDAO = null;
		componentManager = null;
	}

	/**
	 * This is get methods test.
	 */
	@Test
	public void testGet() {
		try {
			Component parent = new Component();
			componentDAO.create(parent);
			Component[] components = new Component[5];

			for (int i = 0; i < components.length; i++) {
				components[i] = new Component();
				components[i].setParentComponent(parent);
				components[i].setPosition(i + 1);
				componentDAO.create(components[i]);
			}

			assertEquals(componentManager.getComponents(parent.getId()).size(),
					components.length);
			assertEquals(componentManager.getComponent(parent.getId(), 3).getId(),
					components[2].getId());
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}

	/**
	 * This is add methods test.
	 */
	@Test
	public void testAdd() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			int size = componentDAO.findAll().size();

			Component parent = new Component();
			componentDAO.create(parent);

			Component[] comp = new Component[4];
			assertEquals(componentDAO.findAll().size(), size + 1);

			comp[0] = componentManager.createComponent(parent.getId());
			assertEquals(0, comp[0].getPosition().intValue());
			assertEquals(comp[0].getParentComponent(), parent);

			comp[3] = new Component();
			componentDAO.create(comp[3]);
			componentManager.addComponent(parent.getId(), comp[3].getId());
			assertEquals(1, comp[3].getPosition());
			assertEquals(comp[3].getParentComponent(), parent);

			comp[2] = componentManager.createComponent(parent.getId(), 1);
			assertEquals(1, comp[2].getPosition());
			assertEquals(comp[2].getParentComponent(), parent);

			comp[1] = new Component();
			componentDAO.create(comp[1]);
			componentManager.addComponent(parent.getId(), comp[1].getId(), 1);
			assertEquals(1, comp[1].getPosition());
			assertEquals(comp[1].getParentComponent(), parent);

			assertEquals(componentDAO.findByParent(parent).size(), comp.length);
			for (int i = 0; i < comp.length; i++)
				assertEquals(i, comp[i].getPosition());

			assertEquals(componentDAO.findAll().size(), size + 1 + comp.length);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is delete methods test.
	 */
	@Test
	public void testDelete() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			int size = componentDAO.findAll().size();

			Component parent = new Component();
			componentDAO.create(parent);
			Component[] components = new Component[5];

			for (int i = 0; i < components.length; i++) {
				components[i] = new Component();
				components[i].setParentComponent(parent);
				components[i].setPosition(i);
				componentDAO.create(components[i]);
			}

			assertEquals(components.length, componentManager.getComponents(
					parent.getId()).size());
			assertEquals(size + components.length + 1, componentDAO.findAll()
					.size());
			for (int i = 0; i < components.length; i++)
				assertEquals(i, components[i].getPosition().intValue());

			componentManager.deleteComponent(components[0].getId());

			assertEquals(components.length - 1, componentManager.getComponents(
					parent.getId()).size());
			assertEquals(size + components.length, componentDAO.findAll()
					.size());
			for (int i = 1; i < components.length; i++)
				assertEquals(i - 1, components[i].getPosition().intValue());

			componentManager.deleteComponent(components[1].getId());

			assertEquals(components.length - 2, componentManager.getComponents(
					parent.getId()).size());
			assertEquals(size + components.length - 1, componentDAO.findAll()
					.size());
			for (int i = 2; i < components.length; i++)
				assertEquals(i - 2, components[i].getPosition().intValue());

			componentManager.deleteComponent(parent.getId(), 0);

			assertEquals(components.length - 3, componentManager.getComponents(
					parent.getId()).size());
			assertEquals(size + components.length - 2, componentDAO.findAll()
					.size());
			for (int i = 3; i < components.length; i++)
				assertEquals(i - 3, components[i].getPosition().intValue());

			componentManager.deleteComponent(parent.getId(), 0);

			assertEquals(components.length - 4, componentManager.getComponents(
					parent.getId()).size());
			assertEquals(size + components.length - 3, componentDAO.findAll()
					.size());
			for (int i = 4; i < components.length; i++)
				assertEquals(i - 4, components[i].getPosition().intValue());
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is move method test.
	 */
	@Test
	public void testMove() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Component parent = new Component();
			componentDAO.create(parent);
			Component[] components = new Component[5];

			for (int i = 0; i < components.length; i++) {
				components[i] = new Component();
				components[i].setParentComponent(parent);
				components[i].setPosition(i + 1);
				componentDAO.create(components[i]);
			}

			componentManager.moveComponent(components[0].getId(), 4);
			for (int i = 1; i < 4; i++)
				assertEquals(components[i].getPosition().intValue(), i);
			assertEquals(components[4].getPosition().intValue(), 5);
			assertEquals(components[0].getPosition().intValue(), 4);

			componentManager.moveComponent(components[4].getId(), 2);
			assertEquals(components[0].getPosition().intValue(), 5);
			assertEquals(components[1].getPosition().intValue(), 1);
			assertEquals(components[2].getPosition().intValue(), 3);
			assertEquals(components[3].getPosition().intValue(), 4);
			assertEquals(components[4].getPosition().intValue(), 2);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

}
