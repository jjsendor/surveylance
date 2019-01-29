package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This class represents User class test.
 * 
 * @author kornel
 */
public class UserDAOTest extends GenericDAOTest {

	private UserDAO userDAO;

	/**
	 * Public constructor.
	 */
	public UserDAOTest() {
		userDAO = (UserDAO) applicationContext.getBean("userDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(User.class, userDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById((Entity) createNewEntityWithoutData(),
				(GenericDAO) userDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll((Entity) createNewEntityWithoutData(),
				(GenericDAO) userDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[1];
		excludedProperties[0] = "googleId";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		User user = new User();
		user.setGoogleId("test@google.com");
		return user;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		User user = new User();
		user.setGoogleId("test_@google.com");
		return user;
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

		super.testFindByExampleEStringArray(
				(Entity) createNewEntityWithoutData(), (GenericDAO) userDAO);

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
				(GenericDAO) userDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists((Entity) createNewEntityWithoutData(),
				(GenericDAO) userDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate((Entity) createNewEntityWithoutData(),
				(GenericDAO) userDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((User) entity).setGoogleId("new@google.com");
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((User) entity1).getId(), ((User) entity2).getId());
		assertEquals(((User) entity1).getGoogleId(), ((User) entity2)
				.getGoogleId());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate((Entity) createNewEntityWithoutData(),
				(GenericDAO) userDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete((Entity) createNewEntityWithoutData(),
				(GenericDAO) userDAO);
	}

	/**
	 * This method tests findByGoogleId() method.
	 */
	@Test
	public void testFindByGoogleId() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		User user = new User();
		user.setGoogleId("test@google.com");
		Long id = (Long) userDAO.create(user);
		User userFromDB = userDAO.findByGoogleId("test@google.com");
		assertEquals(user.getGoogleId(), userFromDB.getGoogleId());
		assertEquals(id, userFromDB.getId());
		userDAO.delete(user);

		assertNull(userDAO.findByGoogleId("test@google.com"));

		tm.commit(ts);
	}

}
