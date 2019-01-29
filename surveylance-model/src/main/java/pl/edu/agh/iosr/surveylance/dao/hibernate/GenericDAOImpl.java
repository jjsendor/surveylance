package pl.edu.agh.iosr.surveylance.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.Entity;

/**
 * This class represents hibernate DAO implementation. This class constitutes
 * base class for hibernate DAO implementations.
 * 
 * @author kornel
 * 
 * @param <E>
 *            entity, which is represented by this DAO
 * @param <ID>
 *            entity ID
 */
public abstract class GenericDAOImpl<E extends Entity, ID extends Serializable>
		extends HibernateDaoSupport implements GenericDAO<E, ID> {

	private Class<E> persistentClass;

	/**
	 * Public constructor.
	 */
	@SuppressWarnings("unchecked")
	public GenericDAOImpl() {
		this.persistentClass = (Class<E>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/** {@inheritDoc} */
	@Override
	public Class<E> getEntityClass() {
		return persistentClass;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public E findById(ID id, boolean lock) {
		E entity;
		try {
			if (lock)
				entity = (E) getHibernateTemplate().get(this.getEntityClass(),
						id, LockMode.UPGRADE);
			else
				entity = (E) getHibernateTemplate().get(this.getEntityClass(),
						id);
			return entity;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<E> findAll() {
		return this.findByCriteria();
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public List<E> findByExample(E exampleInstance, String[] excludeProperty) {
		try {
			// create example object and exclude given properties from it
			final Example example = Example.create(exampleInstance);
			for (String exclude : excludeProperty)
				example.excludeProperty(exclude);

			List<E> result = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session)
								throws SQLException {
							Criteria criteria = session
									.createCriteria(getEntityClass());
							criteria.add(example);
							return criteria.list();
						}
					});
			return result;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<E> findByExample(E exampleInstance) {
		return findByExample(exampleInstance, new String[0]);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public boolean exists(ID id) {
		try {
			E entity = (E) getHibernateTemplate()
					.get(this.getEntityClass(), id);
			if (entity == null)
				return false;
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Serializable create(E entity) {
		try {
			return getHibernateTemplate().save(entity);
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void update(E entity) {
		try {
			getHibernateTemplate().saveOrUpdate(entity);
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void delete(E entity) {
		try {
			getHibernateTemplate().delete(entity);
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

	/**
	 * This method returns objects with grant given criteria.
	 * 
	 * @param criterion
	 *            criteria
	 * @return list of objects
	 */
	@SuppressWarnings("unchecked")
	protected List<E> findByCriteria(final Criterion... criterion) {
		try {
			List<E> result = getHibernateTemplate().executeFind(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session)
								throws SQLException {
							Criteria criteria = session
									.createCriteria(getEntityClass());
							for (Criterion c : criterion)
								criteria.add(c);
							return criteria.list();
						}
					});
			return result;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}

}
