package pl.edu.agh.iosr.surveylance.service;

import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * Manages users. This class provides method enabling authenticating user via <a
 * href="http://code.google.com/apis/accounts/docs/AuthSub.html#AuthSub"
 * >AuthSub</a> method.
 * 
 * @author kuba
 */
public interface UserManager {

	/**
	 * Sets {@link UserDAO} objects used by this service.
	 * 
	 * @param userDAO
	 *            data acces object for users' information
	 */
	public void setUserDAO(UserDAO userDAO);

	/**
	 * Creates request url for <a
	 * href="http://code.google.com/apis/accounts/docs/AuthSub.html#AuthSub"
	 * >Google AuthSub</a> authentication method.
	 * 
	 * @param nextUrl
	 *            URL address of the page to which user will be redirected after
	 *            authentication
	 * 
	 * @return AuthSub authenatication request URL
	 */
	public String getAuthRequestUrl(String nextUrl);

	/**
	 * Obtains session token for user (using given token) and creates
	 * {@link UserSessionInfo} object which stores information about user and
	 * his current token session. If user is not yet in application database,
	 * new user entity is created and stored in database.
	 * 
	 * @param token
	 *            single-use token required for obtainng session token
	 * 
	 * @return {@link UserSessionInfo} object which stores information about
	 *         logged in user and his session data
	 */
	public UserSessionInfo login(String token);

}
