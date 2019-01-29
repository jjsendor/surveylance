package pl.edu.agh.iosr.surveylance.pages.user;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Session;

import pl.edu.agh.iosr.surveylance.pages.Index;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class LogoutUser {

	@Inject
	private RequestGlobals requestGlobals;

	@InjectPage
	private Index index;

	@SuppressWarnings("unused")
	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	Object onActivate() {
		Session session = requestGlobals.getRequest().getSession(false);

		if (session != null) {
			userInfo = null;
			session.invalidate();
		}

		return index;
	}

}
