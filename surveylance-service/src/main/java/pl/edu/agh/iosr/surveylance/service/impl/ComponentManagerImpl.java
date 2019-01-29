package pl.edu.agh.iosr.surveylance.service.impl;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.service.ModificationService;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.exceptions.ComponentDoesNotExistException;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Manages components inside survey.
 *
 * @author michal
 * @author kuba
 */
public class ComponentManagerImpl implements ComponentManager {

	private ModificationService modificationService;

	private ComponentDAO componentDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentDAO(ComponentDAO componentDAO) {
		this.componentDAO = componentDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQuestionDAO(QuestionDAO questionDAO) {
		this.questionDAO = questionDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnswerDAO(AnswerDAO answerDAO) {
		this.answerDAO = answerDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setModificationService(
			ModificationService modificationService) {
		this.modificationService = modificationService;
	}

	/**
	 * This method set position of component (component will be at the end).
	 *
	 * @param	parent		parent order component's
	 *
	 * @return	maximum postion number
	 */
	private int getMaxPosition(Component parent) {
		return getComponents(parent).size();
	}

	/**
	 * Adjusts position of component inside parent component. All components
	 * that have position equal or greater than <code>position</code> are
	 * moved one place further.
	 *
	 * @param	parent		parent component's id
	 * @param	position	expected position of component inside parent
	 * 						component
	 *
	 * @return	adjusted position of the component expected position
	 */
	private int adjustPosition(Component parent, int position) {
		int maxPosition = 0;

		for (Component child : getComponents(parent)) {
			if (child.getPosition() != null
					&& child.getPosition() >= position) {
				child.setPosition(child.getPosition() + 1);
				child.setModifications(child.getModifications() + 1);
				componentDAO.update(child);
			}

			if (child.getPosition() != null
					&& child.getPosition() > maxPosition)
				maxPosition = child.getPosition();
		}

		if (maxPosition + 1 < position)
			position = maxPosition + 1;

		return position;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component createComponent(Component parent, int position) {
		position = adjustPosition(parent, position);

		Component component = new Component();
		component.setPosition(position);
		component.setParentComponent(parent);

		componentDAO.create(component);

		modificationService.markModified(parent);

		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component createComponent(Component parent) {
		return createComponent(parent, getMaxPosition(parent));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component createComponent(long parentId)
			throws ComponentDoesNotExistException {
		Component parent = componentDAO.findById(parentId, false);

		if (parent == null) {
			throw new ComponentDoesNotExistException("Component with id "
					+ parentId + " does not exist in database");
		}

		return createComponent(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component createComponent(long parentId, int position)
			throws ComponentDoesNotExistException {
		Component parent = componentDAO.findById(parentId, false);

		if (parent == null) {
			throw new ComponentDoesNotExistException("Component with id "
					+ parentId + " does not exist in database");
		}

		return createComponent(parent, position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addComponent(Component parent, Component component,
			int position) {
		// mark modified old and new parent
		if (component.getParentComponent() != null)
			modificationService.markModified(component.getParentComponent());
		modificationService.markModified(parent);

		position = adjustPosition(parent, position);
		component.setPosition(position);
		component.setParentComponent(parent);
		
		componentDAO.update(component);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addComponent(Component parent, Component component) {
		addComponent(parent, component, getMaxPosition(parent));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addComponent(long parentId, long componentId)
			throws ComponentDoesNotExistException {
		Component parent = componentDAO.findById(parentId, false);
		Component component = componentDAO.findById(componentId, false);

		if (parent != null && component != null)
			addComponent(parent, component);
		else {
			long id;

			if (parent == null)
				id = parentId;
			else
				id = componentId;

			throw new ComponentDoesNotExistException("Component with id "
					+ id + " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addComponent(long parentId, long componentId, int position)
			throws ComponentDoesNotExistException {
		Component parent = componentDAO.findById(parentId, false);
		Component component = componentDAO.findById(componentId, false);

		if (parent != null && component != null)
			addComponent(parent, component, position);
		else {
			long id;
			
			if (parent == null)
				id = parentId;
			else
				id = componentId;

			throw new ComponentDoesNotExistException("Component with id "
					+ id + " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Component> getComponents(Component parent) {
		if (parent == null)
			return new ArrayList<Component>();

		return componentDAO.findByParent(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Component> getComponents(long parentId)
			throws ComponentDoesNotExistException {
		Component parent = componentDAO.findById(parentId, false);

		if (parent == null) {
			throw new ComponentDoesNotExistException("Component with id "
					+ parentId + " does not exist in database");
		}

		return getComponents(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponent(Component parent, int position) {
		Iterator<Component> iterator = getComponents(parent).iterator();

		while (iterator.hasNext()) {
			Component comp = iterator.next();
			if (comp.getPosition() != null
					&& comp.getPosition().intValue() == position)
				return comp;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponent(long parentId, int position)
			throws ComponentDoesNotExistException {
		Component parent = componentDAO.findById(parentId, false);

		if (parent == null) {
			throw new ComponentDoesNotExistException("Component with id "
					+ parentId + " does not exist in database");
		}

		return getComponent(parent, position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteComponent(Component component) {
		// delete child components (cascade delete)
		for (Component child : getComponents(component))
			deleteComponent(child);

		Question question = questionDAO.findByParent(component);

		// if component is asociated with question - also delete it
		if (question != null) {
			question.setParentComponent(null);	// to prevent recursive
												// component delete

			QuestionManager questionManager = new QuestionManagerImpl();
			questionManager.setQuestionDAO(questionDAO);
			questionManager.setAnswerDAO(answerDAO);

			questionManager.deleteQuestion(question);
		}

		for (Component comp : getComponents(component.getParentComponent()))
			if (comp.getPosition() != null
					&& comp.getPosition() > component.getPosition()) {
				comp.setPosition(comp.getPosition() - 1);
				componentDAO.update(comp);
			}

		if (component.getParentComponent() != null)
			modificationService.markModified(component.getParentComponent());

		component.setParentComponent(null);
		componentDAO.delete(component);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteComponent(long componentId)
			throws ComponentDoesNotExistException {
		Component component = componentDAO.findById(componentId, false);

		if (component != null)
			deleteComponent(component);
		else {
			throw new ComponentDoesNotExistException("Component with id "
					+ componentId + " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteComponent(long parentId, int position)
			throws ComponentDoesNotExistException {
		Component component = getComponent(parentId, position);

		if (component != null)
			deleteComponent(component);
		else {
			throw new ComponentDoesNotExistException("Component with id "
					+ parentId + " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int moveComponent(Component component, int newPosition) {
		int position = component.getPosition();

		if (position == newPosition)
			return position;

		if (component.getParentComponent() != null) {
			long parentId = component.getParentComponent().getId();
			int maxPosition = 0;

			for (Component child : getComponents(parentId)) {
				if (child.getPosition() != null)
					if (child.getPosition() > position
							&& child.getPosition() <= newPosition) {
						child.setPosition(child.getPosition() - 1);
						child.setModifications(child.getModifications() + 1);
						componentDAO.update(child);
					}
					else if (child.getPosition() < position
							&& child.getPosition() >= newPosition) {
						child.setPosition(child.getPosition() + 1);
						child.setModifications(child.getModifications() + 1);
						componentDAO.update(child);
					}

				if (child.getPosition() != null
						&& child.getPosition() > maxPosition)
					maxPosition = child.getPosition();
			}

			if (maxPosition < newPosition)
				newPosition = maxPosition;

			component.setPosition(newPosition);
			componentDAO.update(component);

			modificationService.markModified(component);

			return newPosition;
		}

		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int moveComponent(long componentId, int newPosition)
			throws ComponentDoesNotExistException {
		Component component = componentDAO.findById(componentId, false);

		if (component == null) {
			throw new ComponentDoesNotExistException("Component with id "
					+ componentId + " does not exist in database");
		}

		return moveComponent(component, newPosition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getRoot(Component component)
		throws ComponentDoesNotExistException{
		return componentDAO.findRootComponent(component);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getRoot(long componentId)
		throws ComponentDoesNotExistException{
		return getRoot(componentDAO.findById(componentId, false));
	}	
}
