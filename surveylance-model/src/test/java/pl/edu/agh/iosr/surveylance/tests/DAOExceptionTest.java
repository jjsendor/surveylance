package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;

/**
 * This class represents DAOException class test.
 * 
 * @author kornel
 */
public class DAOExceptionTest {

	private DAOException daoException;

	@After
	public void tearDown() throws Exception {
		this.daoException = null;
	}

	/**
	 * This method tests first constructor.
	 */
	@Test
	public void testDAOException() {
		this.daoException = new DAOException();
		assertNotNull(this.daoException);
	}

	/**
	 * This method tests second constructor.
	 */
	@Test
	public void testDAOExceptionString() {
		this.daoException = new DAOException("Test message");
		assertNotNull(this.daoException);
	}

	/**
	 * This method tests third constructor.
	 */
	@Test
	public void testDAOExceptionStringThrowable() {
		this.daoException = new DAOException("Test message", new Exception());
		assertNotNull(this.daoException);
	}

	/**
	 * This method tests fourth constructor.
	 */
	@Test
	public void testDAOExceptionThrowable() {
		this.daoException = new DAOException(new Exception());
		assertNotNull(this.daoException);
	}

}
