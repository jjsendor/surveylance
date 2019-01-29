package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This is test of GenericDAO class.
 * 
 * @author kornel
 */
public class GenericDAOSelfTest {

	private UserDAO userDAO;

	/**
	 * Public constructor.
	 */
	public GenericDAOSelfTest() {
		String[] paths = { "applicationContextDao.xml" };
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				paths);
		userDAO = (UserDAO) applicationContext.getBean("userDAO");
	}

	/**
	 * This test case test GenericDAO class functionality that is not tested in
	 * other tests.
	 */
	@Test
	public void test() {
		boolean exceptionThrown = false;

		User user = new User();
		try {
			userDAO.create(user);
		} catch (DAOException ex) {
			exceptionThrown = true;
		}
		if (!exceptionThrown)
			fail("Expected exception but nothing was raised.");
		exceptionThrown = false;
		try {
			userDAO.update(user);
		} catch (DAOException ex) {
			exceptionThrown = true;
		}
		if (!exceptionThrown)
			fail("Expected exception but nothing was raised.");
		exceptionThrown = false;
	}

}
