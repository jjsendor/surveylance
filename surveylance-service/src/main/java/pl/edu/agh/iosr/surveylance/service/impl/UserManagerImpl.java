package pl.edu.agh.iosr.surveylance.service.impl;

import java.net.URL;
import java.util.List;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.Person;
import com.google.gdata.data.contacts.ContactFeed;

import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.entities.User;
import pl.edu.agh.iosr.surveylance.service.UserManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;
import pl.edu.agh.iosr.surveylance.service.exceptions.LoginException;

/**
 * Implementation of UserManager service.
 * 
 * @author kuba
 */
public class UserManagerImpl implements UserManager {

	private UserDAO userDAO;

	/**
	 * Services for which user is granting access which are used by application.
	 */
	private String[] services = { "http://www.google.com/calendar/feeds/",
			"http://www.google.com/m8/feeds/" };

	/**
	 * Creates user in database.
	 * 
	 * @param login
	 *            user's login (Google ID)
	 * 
	 * @return user entity
	 */
	private User createUser(String login) {
		User user = new User();
		user.setGoogleId(login);
		userDAO.create(user);

		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAuthRequestUrl(String nextUrl) {
		StringBuffer scopeBuffer = new StringBuffer();

		for (int i = 0; i < services.length; i++) {
			scopeBuffer.append(services[i]);
			if (i < services.length - 1)
				scopeBuffer.append(" ");
		}

		String scope = scopeBuffer.toString();

		return AuthSubUtil.getRequestUrl(nextUrl, scope, false, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public UserSessionInfo login(String token) {
		try {
			String sessionToken = AuthSubUtil.exchangeForSessionToken(token,
					null);
			ContactsService contactsService = new ContactsService(
					"surveylance-1.0");
			contactsService.setAuthSubToken(sessionToken);

			URL feedUrl = new URL(
					"http://www.google.com/m8/feeds/contacts/default/full");
			ContactFeed resultFeed = contactsService.getFeed(feedUrl,
					ContactFeed.class);
			List<Person> authors = resultFeed.getAuthors();

			if (authors.isEmpty())
				throw new LoginException(
						"Cannot read user email from contacts feed.");

			String login = authors.get(0).getEmail();

			User user = userDAO.findByGoogleId(login);

			if (user == null)
				user = createUser(login);

			return new UserSessionInfo(user, sessionToken);
		} catch (Exception e) {
			throw new LoginException("Cannot login user", e);
		}
	}

}
