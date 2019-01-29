package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Entity;

/**
 * This class represents Component class test.
 * 
 * @author kornel
 */
public class ComponentDAOTest extends GenericDAOTest {

	private ComponentDAO componentDAO;

	private Component parentComponent;

	/**
	 * Public constructor.
	 */
	public ComponentDAOTest() {
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(Component.class, componentDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById(createNewEntityWithoutData(),
				(GenericDAO) componentDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll(createNewEntityWithoutData(),
				(GenericDAO) componentDAO);
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
		Component component = new Component();
		component.setPosition(new Integer(0));
		component.setModifications(new Integer(0));

		this.parentComponent = new Component();
		this.parentComponent.setPosition(new Integer(0));
		this.parentComponent.setModifications(new Integer(0));
		componentDAO.create(this.parentComponent);

		component.setParentComponent(this.parentComponent);
		return component;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		componentDAO.delete(this.parentComponent);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		Component component = new Component();
		return component;
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

		Component component = new Component();
		component.setPosition(new Integer(0));
		component.setModifications(new Integer(0));
		super.testFindByExampleEStringArray((Entity) component,
				(GenericDAO) componentDAO);

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
				(GenericDAO) componentDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists(createNewEntityWithoutData(),
				(GenericDAO) componentDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate(createNewEntityWithoutData(),
				(GenericDAO) componentDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((Component) entity).setModifications(new Integer(10));
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((Component) entity1).getId(), ((Component) entity2)
				.getId());
		assertEquals(((Component) entity1).getModifications(),
				((Component) entity2).getModifications());
		assertEquals(((Component) entity1).getPosition(), ((Component) entity2)
				.getPosition());
		assertEquals(((Component) entity1).getParentComponent(),
				((Component) entity2).getParentComponent());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate(createNewEntityWithoutData(),
				(GenericDAO) componentDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete(createNewEntityWithoutData(),
				(GenericDAO) componentDAO);
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

		Component parent = (Component) createNewEntityWithoutData();
		componentDAO.create(parent);

		int sizeBefore = componentDAO.findByParent(parent).size();

		Component child1 = (Component) createNewEntityWithoutData();
		Component child2 = (Component) createNewEntityWithoutData();
		child1.setParentComponent(parent);
		child2.setParentComponent(parent);
		componentDAO.create(child1);
		componentDAO.create(child2);

		int sizeAfter = componentDAO.findByParent(parent).size();

		assertEquals(sizeBefore + 2, sizeAfter);

		componentDAO.delete(child1);
		componentDAO.delete(child2);
		componentDAO.delete(parent);

		tm.commit(ts);
	}

	/**
	 * This method tests findByParent() method.
	 */
	@Test
	public void testParentUpdate() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Component parent = (Component) createNewEntityWithoutData();
		componentDAO.create(parent);

		int sizeBefore = componentDAO.findByParent(parent).size();

		Component child1 = (Component) createNewEntityWithoutData();
		Component child2 = (Component) createNewEntityWithoutData();
		child1.setParentComponent(parent);
		componentDAO.create(child1);

		int sizeAfter = componentDAO.findByParent(parent).size();
		assertEquals(1, sizeAfter - sizeBefore);

		child2.setParentComponent(parent);
		componentDAO.update(child2);
		sizeAfter = componentDAO.findByParent(parent).size();
		assertEquals(2, sizeAfter - sizeBefore);

		componentDAO.delete(child1);
		componentDAO.delete(child2);
		componentDAO.delete(parent);

		tm.commit(ts);
	}

	/**
	 * This method tests findRootComponent() and compareTo() methods.
	 */
	@Test
	public void testFindRootComponent() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());
		Component rootComponent1 = (Component) createNewEntityWithoutData();
		rootComponent1.setPosition(new Integer(1));
		Component rootComponent2 = (Component) createNewEntityWithoutData();
		rootComponent2.setPosition(new Integer(4));
		Component firstComponent = (Component) createNewEntityWithoutData();
		firstComponent.setPosition(new Integer(2));
		Component secondComponent = (Component) createNewEntityWithoutData();
		secondComponent.setPosition(new Integer(2));
		Component thirdComponent = (Component) createNewEntityWithoutData();
		thirdComponent.setPosition(new Integer(100));
		Component fourthComponent = (Component) createNewEntityWithoutData();
		fourthComponent.setPosition(new Integer(12));
		Component fivethComponent = (Component) createNewEntityWithoutData();
		fivethComponent.setPosition(new Integer(55));

		firstComponent.setParentComponent(rootComponent1);
		secondComponent.setParentComponent(firstComponent);
		thirdComponent.setParentComponent(rootComponent2);
		fourthComponent.setParentComponent(thirdComponent);
		fivethComponent.setParentComponent(fourthComponent);

		componentDAO.create(rootComponent1);
		componentDAO.create(rootComponent2);
		componentDAO.create(firstComponent);
		componentDAO.create(secondComponent);
		componentDAO.create(thirdComponent);
		componentDAO.create(fourthComponent);
		componentDAO.create(fivethComponent);

		Component root1 = componentDAO.findRootComponent(fivethComponent);
		assertEquals(rootComponent2.getId(), root1.getId());

		Component root2 = componentDAO.findRootComponent(secondComponent);
		assertEquals(rootComponent1.getId(), root2.getId());

		Component root3 = componentDAO.findRootComponent(thirdComponent);
		assertEquals(rootComponent2.getId(), root3.getId());

		List<Component> components = new LinkedList<Component>();
		components.add(rootComponent1);
		components.add(rootComponent2);
		components.add(firstComponent);
		components.add(secondComponent);
		components.add(thirdComponent);
		components.add(fourthComponent);
		components.add(fivethComponent);
		Collections.sort(components);
		assertEquals(rootComponent1.getId(), components.get(0).getId());
		assertEquals(rootComponent2.getId(), components.get(3).getId());
		assertEquals(fourthComponent.getId(), components.get(4).getId());
		assertEquals(fivethComponent.getId(), components.get(5).getId());
		assertEquals(thirdComponent.getId(), components.get(6).getId());

		componentDAO.delete(fivethComponent);
		componentDAO.delete(fourthComponent);
		componentDAO.delete(thirdComponent);
		componentDAO.delete(secondComponent);
		componentDAO.delete(firstComponent);
		componentDAO.delete(rootComponent2);
		componentDAO.delete(rootComponent1);

		tm.commit(ts);
	}

}
