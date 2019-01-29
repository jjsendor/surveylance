package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.iosr.surveylance.entities.Event;

/**
 * This class represents Event class test.
 * 
 * @author kornel
 */
public class EventTest {

	private Event event;

	/**
	 * Sets up testing environment before single test.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Before
	public void setUp() throws Exception {
		this.event = new Event(new Long(0), Calendar.getInstance().getTime(),
				Calendar.getInstance().getTime(), "Example", "Description");
	}

	/**
	 * Tears down testing environment after single test.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@After
	public void tearDown() throws Exception {
		this.event = null;
	}

	/**
	 * Tests xId() methods.
	 */
	@Test
	public void testId() {
		this.event.setId(new Long(1));
		assertEquals(new Long(1).longValue(), this.event.getId().longValue());
	}

	/**
	 * Tests xName() methods.
	 */
	@Test
	public void testName() {
		this.event.setName("Ex");
		assertEquals("Ex", this.event.getName());
	}

	/**
	 * Tests xDescription() methods.
	 */
	@Test
	public void testDescription() {
		this.event.setDesription("X");
		assertEquals("X", this.event.getDesription());
	}

	/**
	 * Tests xStartTime() methods.
	 */
	@Test
	public void testStartTime() {
		Date time = Calendar.getInstance().getTime();
		this.event.setStartTime(time);
		assertEquals(time, this.event.getStartTime());
	}

	/**
	 * Tests xEndTime() methods.
	 */
	@Test
	public void testEndTime() {
		Date time = Calendar.getInstance().getTime();
		this.event.setEndTime(time);
		assertEquals(time, this.event.getEndTime());
	}

}
