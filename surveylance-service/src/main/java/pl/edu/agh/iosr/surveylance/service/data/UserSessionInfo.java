package pl.edu.agh.iosr.surveylance.service.data;

import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * Stores information about logged in user and session token for user's
 * services.
 * 
 * @author kuba
 */
public class UserSessionInfo {

	private User user;
	private String sessionToken;
	private boolean initGears;

	/**
	 * Constructs new UserSessionInfo object for specified user and his session
	 * token.
	 * 
	 * @param user
	 *            entity for user
	 * @param sessionToken
	 *            user's session token
	 */
	public UserSessionInfo(User user, String sessionToken) {
		this.user = user;
		this.sessionToken = sessionToken;
		this.initGears = false;
	}

	/**
	 * Returns user entity asociated with this session.
	 * 
	 * @return user asociated with this UserSessionInfo object
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets user entity.
	 * 
	 * @param user
	 *            User object to be asociated with this UserSessionInfo
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns session token.
	 * 
	 * @return session token for user in current session
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * Sets session token.
	 * 
	 * @param sessionToken
	 *            session token to be asociated with UserSessionInfo
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * Checks if Gears are initialized.
	 * 
	 * @return <code>true</code> if Gears are initialized, <code>false</code>
	 *         otherwise
	 */
	public boolean isInitGears() {
		return initGears;
	}

	/**
	 * This method should be called after initialization Gears client side. It
	 * ensures that Gears initialization code is done only once, right after
	 * logging in.
	 */
	public void initGears() {
		this.initGears = true;
	}

}
