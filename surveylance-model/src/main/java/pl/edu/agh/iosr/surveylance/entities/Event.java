package pl.edu.agh.iosr.surveylance.entities;

import java.util.Date;

/**
 * Represents Google Calendar event.
 * 
 * @author stefan
 * @author kornel
 */
public class Event {

	private Date startTime;
	private Date endTime;
	private String name;
	private String desription;
	private Long id;

	/**
	 * Public constructor.
	 * 
	 * @param id
	 *            event's id number
	 * @param startTime
	 *            event's start time in {@link Date} format
	 * @param endTime
	 *            event's end time in {@link Date} format
	 * @param name
	 *            event's name
	 * @param desription
	 *            event's description
	 */
	public Event(Long id, Date startTime, Date endTime, String name,
			String desription) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.desription = desription;
	}

	/**
	 * Sets event's id.
	 * 
	 * @param id
	 *            {@link Long} number
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns event's id.
	 * 
	 * @return event's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets event's name.
	 * 
	 * @param name
	 *            event's name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns event's name.
	 * 
	 * @return event's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets event's description.
	 * 
	 * @param desription
	 *            event's description
	 */
	public void setDesription(String desription) {
		this.desription = desription;
	}

	/**
	 * Returns event's description.
	 * 
	 * @return event's description
	 */
	public String getDesription() {
		return this.desription;
	}

	/**
	 * Sets event's start time.
	 * 
	 * @param startTime
	 *            event's start time ({@link Date} object)
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * Returns event's start time.
	 * 
	 * @return event's start time ({@link Date} object)
	 */
	public Date getStartTime() {
		return this.startTime;
	}

	/**
	 * Sets event's end time.
	 * 
	 * @param endTime
	 *            event's end time ({@link Date} object)
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Returns event's end time.
	 * 
	 * @return event's end time ({@link Date} object)
	 */
	public Date getEndTime() {
		return this.endTime;
	}

}
