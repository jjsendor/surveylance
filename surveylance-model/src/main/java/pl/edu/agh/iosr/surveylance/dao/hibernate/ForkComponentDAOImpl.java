package pl.edu.agh.iosr.surveylance.dao.hibernate;

import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;

/**
 * This class represents ForkComponent object DAO implementation which uses
 * Hibernate technology.
 * 
 * @author kornel
 */
public class ForkComponentDAOImpl extends GenericDAOImpl<ForkComponent, Long>
		implements ForkComponentDAO {

	/**
	 * Public constructor.
	 */
	public ForkComponentDAOImpl() {
		super();
	}

}
