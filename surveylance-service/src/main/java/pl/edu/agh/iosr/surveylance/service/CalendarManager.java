package pl.edu.agh.iosr.surveylance.service;

import java.util.List;
import pl.edu.agh.iosr.surveylance.entities.Event;
import pl.edu.agh.iosr.surveylance.entities.Survey;

/**
 * Basic calendar event listing.
 * 
 * @author stefan
 */
public interface CalendarManager {

	/**
	 * Sets AuthSub session token.
	 * 
	 * @param sessionToken
	 *            session token to be set
	 */
	public void setSessionToken(String sessionToken);

	/**
	 * Adds specific survey to calendar
	 * 
	 * @param survey
	 *            event being added
	 */
	public void addEvent(Survey survey);

	/**
	 * Removes specific survey to calendar
	 * 
	 * @param surveyId
	 *            id of event being removed
	 */
	public void removeEvent(Long surveyId);

	/**
	 * Looks for all surveys in currently logged user's calendar.
	 * 
	 * @return all user's surveys
	 */
	public List<Event> getEvents();

}
