package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.entities.Entity;

/**
 * This class represents test of GenericDAO implementation.
 * 
 * @author kornel
 */
public abstract class GenericDAOTest {

	protected static ApplicationContext applicationContext;

	static {
		String[] paths = { "applicationContextDao.xml" };
		applicationContext = new ClassPathXmlApplicationContext(paths);
	}

	/**
	 * This method is generic test of findById() method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testFindById(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Serializable id = entityDAO.create(entity);

		assertEquals(entity, entityDAO.findById(id, false));
		assertEquals(entity, entityDAO.findById(id, true));

		entityDAO.delete(entity);

		tm.commit(ts);
	}

	/**
	 * This method is generic test of findAll() method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testFindAll(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		int numberOfEntitiesBeforeCreate = entityDAO.findAll().size();

		entityDAO.create(entity);
		int numberOfEntitiesAfterCreate = entityDAO.findAll().size();
		assertEquals(1, numberOfEntitiesAfterCreate
				- numberOfEntitiesBeforeCreate);

		entityDAO.delete(entity);

		tm.commit(ts);
	}

	/**
	 * This is helper method for testFindByExampleEStringArray method.
	 * 
	 * @return table of excluded properties for current tested entity
	 */
	protected abstract String[] getExcludedProperties();

	/**
	 * This is helper method which returns new entity instance with proper data
	 * inside.
	 * 
	 * @return created entity with data
	 */
	protected abstract Entity createNewEntityWithData();

	/**
	 * This method removes entities related to entity created by
	 * createNewEntityWithData() method.
	 */
	protected abstract void deleteRelatedEntites();

	/**
	 * This is helper method which returns new entity instance without proper
	 * data inside.
	 * 
	 * @return created entity without data
	 */
	protected abstract Entity createNewEntityWithoutData();

	/**
	 * This method is generic test of findByExample() (with excluded properties
	 * parameter) method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testFindByExampleEStringArray(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		int numberOfEntitiesBeforeCreate = entityDAO.findByExample(entity,
				getExcludedProperties()).size();

		Entity filledEntity = createNewEntityWithData();
		Entity emptyEntity = createNewEntityWithoutData();
		entityDAO.create(filledEntity);
		entityDAO.create(emptyEntity);

		int numberOfEntitiesAfterCreate = entityDAO.findByExample(entity,
				getExcludedProperties()).size();
		assertEquals(2, numberOfEntitiesAfterCreate
				- numberOfEntitiesBeforeCreate);

		entityDAO.delete(emptyEntity);
		entityDAO.delete(filledEntity);
		deleteRelatedEntites();
	}

	/**
	 * This method is generic test of findByExample() (without excluded
	 * properties parameter) method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testFindByExampleE(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		int numberOfEntitiesBeforeCreate = entityDAO.findByExample(entity)
				.size();

		entityDAO.create(entity);

		int numberOfEntitiesAfterCreate = entityDAO.findByExample(entity)
				.size();
		assertEquals(1, numberOfEntitiesAfterCreate
				- numberOfEntitiesBeforeCreate);

		entityDAO.delete(entity);
		deleteRelatedEntites();
	}

	/**
	 * This method is generic test of exists() method.
	 */
	protected void testExists(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Serializable id = entityDAO.create(entity);
		assertTrue(entityDAO.exists(id));

		entityDAO.delete(entity);
		assertFalse(entityDAO.exists(id));

		tm.commit(ts);
	}

	/**
	 * This method is generic test of create() method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testCreate(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		int numberOfEntitiesBeforeCreate = entityDAO.findAll().size();

		entityDAO.create(entity);
		int numberOfEntitiesAfterCreate = entityDAO.findAll().size();
		assertEquals(1, numberOfEntitiesAfterCreate
				- numberOfEntitiesBeforeCreate);

		entityDAO.delete(entity);

		tm.commit(ts);
	}

	/**
	 * This is helper method for testUpdate method.
	 * 
	 * @param entity
	 *            entity to update
	 * @return updated entity
	 */
	protected abstract Entity getUpdatedEntity(Entity entity);

	/**
	 * This is helper method for testUpdate method that compares two entities in
	 * proper way.
	 * 
	 * @param entity1
	 *            first entity
	 * @param entity2
	 *            second entity
	 */
	protected abstract void compareEntities(Entity entity1, Entity entity2);

	/**
	 * This method is generic test of update() method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testUpdate(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Serializable id = entityDAO.create(entity);

		entityDAO.update(getUpdatedEntity(entity));
		entityDAO.update(entity);

		Entity updatedEntity = entityDAO.findById(id, false);
		compareEntities(entity, updatedEntity);

		entityDAO.delete(updatedEntity);

		tm.commit(ts);
	}

	/**
	 * This method is generic test of delete() method.
	 * 
	 * @param entity
	 *            tested entity object
	 * @param entityDAO
	 *            tested entity dao
	 */
	protected void testDelete(Entity entity,
			GenericDAO<Entity, Serializable> entityDAO) {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		entityDAO.create(entity);
		int numberOfEntitiesBeforeDelete = entityDAO.findAll().size();

		entityDAO.delete(entity);
		int numberOfEntitiesAfterDelete = entityDAO.findAll().size();
		assertEquals(1, numberOfEntitiesBeforeDelete
				- numberOfEntitiesAfterDelete);

		tm.commit(ts);
	}

}
