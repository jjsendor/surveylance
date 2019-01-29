package pl.edu.agh.iosr.surveylance.dao;

import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This interface is implemented by UserDAO implementations.
 * 
 * @author kornel
 */
public interface UserDAO extends GenericDAO<User, Long> {

	/**
	 * This method returns exists user with given google id.
	 * 
	 * @param googleId
	 *            user's google id
	 * @return {@link User} with given google id
	 */
	public User findByGoogleId(String googleId);

}
