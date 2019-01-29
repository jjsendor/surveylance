package pl.edu.agh.iosr.surveylance.pages.user;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

import pl.edu.agh.iosr.surveylance.service.UserManager;

public class AuthUser {

	private static final String next = "login";

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private UserManager userManager;

	Object onActivate() throws MalformedURLException,
			UnsupportedEncodingException {
		String redirect = requestGlobals.getRequest().getParameter("redirect");

		String nextURL = requestGlobals.getHTTPServletRequest().getRequestURL()
				.toString().replaceAll("auth", next)
				+ (redirect != null ? "?redirect="
						+ URLEncoder.encode(redirect, "UTF-8") : "");

		return new URL(userManager.getAuthRequestUrl(nextURL));
	}

}
