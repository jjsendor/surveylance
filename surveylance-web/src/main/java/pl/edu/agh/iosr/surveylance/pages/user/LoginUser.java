package pl.edu.agh.iosr.surveylance.pages.user;

import java.net.URL;
import java.net.URLDecoder;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import pl.edu.agh.iosr.surveylance.pages.Index;
import pl.edu.agh.iosr.surveylance.service.UserManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

public class LoginUser {

	@Inject
	private Request request;

	@Inject
	private UserManager userManager;

	@SuppressWarnings("unused")
	@ApplicationState
	private UserSessionInfo userInfo;

	@InjectPage
	private Index index;

	Object onActivate() throws Exception {
		String token = request.getParameter("token");
		this.userInfo = userManager.login(token);

		String redirect = request.getParameter("redirect");

		if (redirect != null)
			return new URL(URLDecoder.decode(redirect, "UTF-8"));

		return index;
	}

}
