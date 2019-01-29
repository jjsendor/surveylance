package pl.edu.agh.iosr.surveylance.pages.calendar;

import java.util.List;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.entities.Event;
import pl.edu.agh.iosr.surveylance.service.CalendarManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class CalendarList {

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	@Inject
	@Service("calendarManager")
	private CalendarManager calendarManager;

	@SuppressWarnings("unused")
	@Property
	private Event event;

	void onActivate() {
		calendarManager.setSessionToken(userInfo.getSessionToken());
	}

	public List<Event> getEvents() {
		return calendarManager.getEvents();
	}

}
