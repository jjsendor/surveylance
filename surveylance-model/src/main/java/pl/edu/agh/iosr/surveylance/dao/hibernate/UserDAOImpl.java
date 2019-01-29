package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This class represents User object DAO implementation which uses Hibernate
 * technology.
 * 
 * @author kornel
 */
public class UserDAOImpl extends GenericDAOImpl<User, Long> implements UserDAO {

	/**
	 * Public constructor.
	 */
	public UserDAOImpl() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public User findByGoogleId(String googleId) {
		List<User> users = super.findByCriteria(Restrictions.eq("googleId",
				googleId));
		if (users.size() == 1)
			return users.get(0);
		else
			return null;
	}

}
