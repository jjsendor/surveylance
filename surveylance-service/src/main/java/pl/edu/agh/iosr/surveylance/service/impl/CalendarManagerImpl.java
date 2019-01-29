package pl.edu.agh.iosr.surveylance.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;

import pl.edu.agh.iosr.surveylance.entities.Event;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.CalendarManager;

/**
 * Implements {@link CalendarManager} interface.
 * 
 * @author stefan
 */
public class CalendarManagerImpl implements CalendarManager {

	private static final Logger logger = Logger
			.getLogger(ContactManagerImpl.class);

	private static final String separator = " / ";

	private CalendarService calendarService = new CalendarService(
			"surveylance-1.0");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents() {
		List<Event> result = new ArrayList<Event>();

		URL feedUrl = null;
		try {
			feedUrl = new URL(
					"http://www.google.com/calendar/feeds/default/private/full");
		} catch (MalformedURLException e) {
			logger.error("Exception while getting events " + e.getMessage());
		}

		CalendarEventFeed myFeed = null;
		try {
			myFeed = calendarService.getFeed(feedUrl, CalendarEventFeed.class);
		} catch (IOException e) {
			logger.error("Exception while getting events " + e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while getting events " + e.getMessage());
		}

		for (CalendarEventEntry cee : myFeed.getEntries()) {
			String[] parts = cee.getTitle().getPlainText().split(separator);
			try {
				Long Id = Long.valueOf(parts[0]);

				result.add(new Event(Id, new Date(cee.getTimes().get(0)
						.getStartTime().getValue()), new Date(cee.getTimes()
						.get(0).getEndTime().getValue()), parts[1], cee
						.getPlainTextContent()));
			} catch (NumberFormatException e) {
				// do nothing - the event doesn't belong to the survey
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSessionToken(String sessionToken) {
		calendarService.setAuthSubToken(sessionToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addEvent(Survey survey) {
		URL postURL = null;
		try {
			postURL = new URL(
					"http://www.google.com/calendar/feeds/default/private/full");
		} catch (MalformedURLException e) {
			logger
					.error("Exception while adding an event (MalformedURLException) "
							+ e.getMessage());
		}

		CalendarEventEntry myEvent = new CalendarEventEntry();

		myEvent.setTitle(new PlainTextConstruct(survey.getId() + separator
				+ survey.getName()));
		myEvent.setContent(new PlainTextConstruct(survey.getDescription()));

		DateTime startTime = DateTime.now();
		DateTime endTime = new DateTime(survey.getExpirationDate());
		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEvent.addTime(eventTimes);

		try {
			calendarService.insert(postURL, myEvent);
		} catch (IOException e) {
			logger.error("Exception while adding an event (IOException) "
					+ e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while adding an event (ServiceException) "
					+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeEvent(Long surveyId) {
		URL feedUrl = null;
		try {
			feedUrl = new URL(
					"http://www.google.com/calendar/feeds/default/private/full");
		} catch (MalformedURLException e) {
			logger.error("Exception while getting events " + e.getMessage());
		}

		CalendarEventFeed myFeed = null;
		try {
			myFeed = calendarService.getFeed(feedUrl, CalendarEventFeed.class);
		} catch (IOException e) {
			logger.error("Exception while getting events " + e.getMessage());
		} catch (ServiceException e) {
			logger.error("Exception while getting events " + e.getMessage());
		}

		for (CalendarEventEntry cee : myFeed.getEntries()) {
			String[] parts = cee.getTitle().getPlainText().split(separator);
			Long calendarEventId = Long.valueOf(parts[0]);

			if (calendarEventId.compareTo(surveyId) == 0) {
				URL deleteUrl = null;
				try {
					deleteUrl = new URL(cee.getEditLink().getHref());
				} catch (MalformedURLException e) {
					logger.error("Exception while removing event "
							+ e.getMessage());
				}
				try {
					calendarService.delete(deleteUrl);
				} catch (IOException e) {
					logger.error("Exception while removing event "
							+ e.getMessage());
				} catch (ServiceException e) {
					logger.error("Exception while removing event "
							+ e.getMessage());
				}

				break;
			}
		}
	}

}
