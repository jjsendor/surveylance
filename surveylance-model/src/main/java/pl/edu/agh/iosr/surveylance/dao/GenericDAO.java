package pl.edu.agh.iosr.surveylance.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.edu.agh.iosr.surveylance.entities.Entity;

/**
 * This interface is implemented by DAO implementations.
 * 
 * @author kornel
 * 
 * @param <E>
 *            entity type (has to extend
 *            pl.edu.agh.iosr.surveylance.entities.Entity)
 * @param <ID>
 *            entity id type (has to extend java.io.Serializable)
 */
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public interface GenericDAO<E extends Entity, ID extends Serializable> {

	/**
	 * This method return internal persistent class.
	 * 
	 * @return internal persistent class
	 */
	public Class<E> getEntityClass();

	/**
	 * This method returns object with given id from database.
	 * 
	 * @param id
	 *            object database id
	 * @param lock
	 *            lock flag
	 * @return object with given id
	 */
	public E findById(ID id, boolean lock);

	/**
	 * This method returns all survey objects from database.
	 * 
	 * @return {@link List} of surveys from database
	 */
	public List<E> findAll();

	/**
	 * This method returns all survey object from database which are matching to
	 * given example object. Returned object has all its properties except given
	 * in second parameter list.
	 * 
	 * @param exampleInstance
	 *            example object
	 * @param excludeProperty
	 *            list of excluded object properties
	 * @return {@link List} of surveys from database which are matching to given
	 *         object instance
	 */
	public List<E> findByExample(E exampleInstance, String[] excludeProperty);

	/**
	 * This method returns all survey object from database which are matching to
	 * given example object.
	 * 
	 * @param exampleInstance
	 *            example object
	 * @return {@link List} of surveys from database which are matching to given
	 *         object instance
	 */
	public List<E> findByExample(E exampleInstance);

	/**
	 * This method checks whether object with given id exists in database.
	 * 
	 * @param id
	 *            object id
	 * @return true if object with given id is in database, false otherwise
	 */
	public boolean exists(ID id);

	/**
	 * This method persist given object in database.
	 * 
	 * @param entity
	 *            object to persist
	 * @return inserted object's key
	 */
	@Transactional(readOnly = false)
	public Serializable create(E entity);

	/**
	 * This method update given object in database.
	 * 
	 * @param entity
	 *            object to update
	 */
	@Transactional(readOnly = false)
	public void update(E entity);

	/**
	 * This method removes given object from database.
	 * 
	 * @param entity
	 *            object to remove
	 */
	@Transactional(readOnly = false)
	public void delete(E entity);

}
