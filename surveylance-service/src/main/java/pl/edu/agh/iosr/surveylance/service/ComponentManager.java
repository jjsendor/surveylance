package pl.edu.agh.iosr.surveylance.service;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.service.exceptions.ComponentDoesNotExistException;

import java.util.List;

/**
 * Manages components across survey.
 * 
 * @author michal
 */
public interface ComponentManager {

	/**
	 * This method sets component Data Access Object.
	 * 
	 * @param componentDAO
	 *            component Data Access Object
	 */
	public void setComponentDAO(ComponentDAO componentDAO);

	/**
	 * This method sets question Data Access Object.
	 * 
	 * @param questionDAO
	 *            question Data Access Object
	 */
	public void setQuestionDAO(QuestionDAO questionDAO);

	/**
	 * This method sets answer Data Access Object.
	 * 
	 * @param answerDAO
	 *            answer Data Access Object
	 */
	public void setAnswerDAO(AnswerDAO answerDAO);

	/**
	 * Sets {@link ModificationService} object which is used by this service.
	 * 
	 * @param modificationService
	 */
	public void setModificationService(ModificationService modificationService);

	/**
	 * This method creates new component and adds it to parent component.
	 * 
	 * @param parent
	 *            parent component
	 * 
	 * @return created component
	 */
	public Component createComponent(Component parent);

	/**
	 * This method creates new component and adds it to parent component.
	 * 
	 * @param parentId
	 *            parent component's id
	 * 
	 * @return created component
	 */
	public Component createComponent(long parentId)
			throws ComponentDoesNotExistException;

	/**
	 * This method creates new component and adds it to parent component on
	 * specified position.
	 * 
	 * @param parent
	 *            parent component
	 * @param position
	 *            on which position component should be added
	 * 
	 * @return created component
	 */
	public Component createComponent(Component parent, int position);

	/**
	 * This method creates new component and adds it to parent component on
	 * specified position.
	 * 
	 * @param parentId
	 *            parent component's id
	 * @param position
	 *            on which position component should be added
	 * 
	 * @return created component
	 */
	public Component createComponent(long parentId, int position)
			throws ComponentDoesNotExistException;

	/**
	 * This method adds component to parent component.
	 * 
	 * @param parent
	 *            parent component
	 * @param component
	 *            component to be added
	 */
	public void addComponent(Component parent, Component component);

	/**
	 * This method adds component to parent component.
	 * 
	 * @param parentId
	 *            parent component's id
	 * @param componentId
	 *            component's id
	 */
	public void addComponent(long parentId, long componentId)
			throws ComponentDoesNotExistException;

	/**
	 * This method adds component to parent component on specified position.
	 * 
	 * @param parent
	 *            parent component
	 * @param component
	 *            component to be added
	 * @param position
	 *            on which position component should be added
	 */
	public void addComponent(Component parent, Component component,
			int position);

	/**
	 * This method adds component to parent component on specified position.
	 * 
	 * @param parentId
	 *            parent component's id
	 * @param componentId
	 *            component's id
	 * @param position
	 *            on which position component should be added
	 */
	public void addComponent(long parentId, long componentId, int position)
			throws ComponentDoesNotExistException;

	/**
	 * Returns lists of all child component for specified component.
	 * 
	 * @param parent
	 *            component entity which child components are returned
	 * 
	 * @return list of child components
	 */
	public List<Component> getComponents(Component parent);

	/**
	 * Returns list of all child components for component with given id.
	 * 
	 * @param parentId
	 *            parent component's id
	 * 
	 * @return list of components
	 */
	public List<Component> getComponents(long parentId)
			throws ComponentDoesNotExistException;

	/**
	 * This method gets component from specified position.
	 * 
	 * @param parent
	 *            parent component
	 * @param position
	 *            position of component
	 * 
	 * @return component
	 */
	public Component getComponent(Component parent, int position);

	/**
	 * This method gets component from specified position.
	 * 
	 * @param parentId
	 *            parent component's id
	 * @param position
	 *            position of component
	 * 
	 * @return component
	 */
	public Component getComponent(long parentId, int position)
			throws ComponentDoesNotExistException;

	/**
	 * This method deletes component.
	 * 
	 * @param component
	 *            component that will be deleted
	 */
	public void deleteComponent(Component component);

	/**
	 * This method deletes component.
	 * 
	 * @param componentId
	 *            component's id
	 */
	public void deleteComponent(long componentId)
			throws ComponentDoesNotExistException;

	/**
	 * This method deletes component under specified position.
	 * 
	 * @param parentId
	 *            parent component's id
	 * @param position
	 *            position of component
	 */
	public void deleteComponent(long parentId, int position)
			throws ComponentDoesNotExistException;

	/**
	 * Changes component position to <code>newPosition</code>.
	 *
	 * @param component
	 *            component entity
	 * @param newPosition
	 *            new position of component
	 *
	 * @return final component position
	 */
	public int moveComponent(Component component, int newPosition);

	/**
	 * Changes component position to <code>newPosition</code>.
	 *
	 * @param componentId
	 *            component's id
	 * @param newPosition
	 *            new position of component
	 *
	 * @return final component position
	 */
	public int moveComponent(long componentId, int newPosition)
			throws ComponentDoesNotExistException;

	/**
	 * Gets root component.
	 *
	 * @param component
	 *            component
	 *
	 * @return root component
	 */
	public Component getRoot(Component component)
		throws ComponentDoesNotExistException;
	
	/**
	 * Gets root component.
	 *
	 * @param componentId
	 *            component's id
	 *
	 * @return root component
	 */
	public Component getRoot(long componentId)
		throws ComponentDoesNotExistException;
}
