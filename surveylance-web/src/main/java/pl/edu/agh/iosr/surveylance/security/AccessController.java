package pl.edu.agh.iosr.surveylance.security;

import java.io.IOException;
import java.net.URLEncoder;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * Controls user access for requested page.
 * 
 * @author kuba
 */
public class AccessController implements Dispatcher {

	private ApplicationStateManager asm;
	private static final String[] PAGES = { "/", "/user/auth", "/user/login",
			"/survey/fill/.*", "/survey/submit/.*", "/assets/.*" };

	/**
	 * Constructs AccessController object and sets ApplicationStateManager used
	 * for accessing information about user's session. Tapestry 5 automatically
	 * provide state manager while binding service.
	 * 
	 * @param asm
	 *            application state manager for current application
	 */
	public AccessController(ApplicationStateManager asm) {
		this.asm = asm;
	}

	/**
	 * Checks if given path is restricted.
	 * 
	 * @param path
	 *            path to check
	 * @return <code>true</code> if path is restricted, <code>false</code>
	 *         otherwise
	 */
	private boolean isRestricted(String path) {
		for (int i = 0; i < PAGES.length; i++) {
			if (path.matches(PAGES[i]))
				return false;
		}

		return true;
	}

	/**
	 * Controls user rights for accessing page.
	 * 
	 * @param request
	 *            server request
	 * @param response
	 *            server response
	 * 
	 * @return <code>false</code> if user has access to the page, otherwise
	 *         exception is thrown
	 * 
	 * @throws IOException
	 *             if redirection to authorization page cannot be send
	 */
	public boolean dispatch(Request request, Response response)
			throws IOException {
		boolean canAccess = false;

		if (asm.exists(UserSessionInfo.class)) {
			UserSessionInfo userInfo = asm.get(UserSessionInfo.class);

			if (userInfo.getUser() != null)
				canAccess = true;
		}

		if (!canAccess && isRestricted(request.getPath())) {
			String host = request.getHeader("Host");
			String context = request.getContextPath();
			String referer = request.getPath();
			response.sendRedirect(request.getContextPath()
					+ "/user/auth?redirect="
					+ URLEncoder.encode("http://" + host + context + referer,
							"UTF-8"));
		}

		return false;
	}

}
