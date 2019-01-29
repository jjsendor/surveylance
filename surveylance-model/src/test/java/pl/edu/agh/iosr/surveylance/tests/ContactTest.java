package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.iosr.surveylance.entities.Contact;

/**
 * This class represents Contact class test.
 * 
 * @author kornel
 */
public class ContactTest {

	private Contact contact;

	/**
	 * Sets up testing environment before single test.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Before
	public void setUp() throws Exception {
		List<String> groups = new LinkedList<String>();
		groups.add("friends");
		this.contact = new Contact("example@ex.com", "Example", groups);
	}

	/**
	 * Tears down testing environment after single test.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@After
	public void tearDown() throws Exception {
		this.contact = null;
	}

	/**
	 * Tests xDisplayName() methods.
	 */
	@Test
	public void testDisplayName() {
		this.contact.setDisplayName("Ex");
		assertEquals("Ex", this.contact.getDisplayName());
	}

	/**
	 * Tests xMail() methods.
	 */
	@Test
	public void testEmail() {
		this.contact.setMail("ex@yahoo.com");
		assertEquals("ex@yahoo.com", this.contact.getMail());
	}

	/**
	 * Tests xGroupsX() methods.
	 */
	@Test
	public void testGroups() {
		assertEquals(true, this.contact.isGroupsExist());
		this.contact.setGroups(null);
		assertEquals(false, this.contact.isGroupsExist());
	}

	/**
	 * Tests toString() method.
	 */
	@Test
	public void testToString() {
		assertEquals("Example example@ex.com", this.contact.toString());
	}

}
