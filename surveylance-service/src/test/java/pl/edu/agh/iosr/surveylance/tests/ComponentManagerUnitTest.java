package pl.edu.agh.iosr.surveylance.tests;

import static org.mockito.Mockito.*;

import junit.framework.TestCase;
import java.util.List;
import java.util.ArrayList;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.ModificationService;
import pl.edu.agh.iosr.surveylance.service.exceptions.ComponentDoesNotExistException;
import pl.edu.agh.iosr.surveylance.service.impl.ComponentManagerImpl;

/**
 * This class tests component manager
 * 
 * @author michal
 */
public class ComponentManagerUnitTest extends TestCase {

	private ComponentManager manager;
	private ModificationService modificationService;

	private ComponentDAO componentDAO;
	private QuestionDAO questionDAO;

	/**
	 * Sets up the test fixture (Called before every test case method).
	 */
	public void setUp() throws Exception {
		componentDAO = mock(ComponentDAO.class);
		questionDAO = mock(QuestionDAO.class);
		modificationService = mock(ModificationService.class);
		
		manager = new ComponentManagerImpl();
		manager.setComponentDAO(componentDAO);
		manager.setQuestionDAO(questionDAO);
		manager.setModificationService(modificationService);
	}

	/**
	 * Tears down the test fixture (Called after every test case method).
	 */
	public void tearDown() throws Exception {
		componentDAO = null;
		questionDAO = null;
		manager = null;
	}

	/**
	 * This is get methods test.
	 */
	public void testGet() {
		try{
			when(componentDAO.findById(0L, false)).thenReturn(new Component());
			
			manager.getComponents(0L);
			manager.getComponent(0L,3);
			
			verify(componentDAO, times(2)).findById(0L, false);
			verify(componentDAO, times(2)).findByParent(isA(Component.class));
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}

	/**
	 * This is add methods test.
	 */
	public void testAdd() {
		try{
			when(componentDAO.findById(2L, false)).thenReturn(new Component());

			try {
				manager.createComponent(0L);
			} catch (ComponentDoesNotExistException e) {
			}

			try {
				manager.addComponent(0L, 2L);
			} catch (ComponentDoesNotExistException e) {
			}

			try {
				manager.createComponent(0L, 2);
			} catch (ComponentDoesNotExistException e) {
			}

			try {
				manager.addComponent(0L, 2L, 2);
			} catch (ComponentDoesNotExistException e) {
			}

			verify(componentDAO, times(2)).findById(2L, false);
			verify(componentDAO, times(4)).findById(0L, false);
			verify(componentDAO, times(0)).create(isA(Component.class));
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}

	/**
	 * This is delete methods test.
	 */
	public void testDelete() {
		try{
			Component parent = mock(Component.class);
			when(parent.getId()).thenReturn(0L);
			
			Component comp = new Component();
			comp.setParentComponent(parent);
			when(componentDAO.findById(1L, false)).thenReturn(comp);
			
			comp.setParentComponent(parent);
			
			try {
				manager.deleteComponent(1L);
			} catch (ComponentDoesNotExistException e) {
			}
			comp.setParentComponent(parent);
			List<Component> list = new ArrayList<Component>();
			list.add(comp);
			
			when(componentDAO.findById(0L, false)).thenReturn(comp);
			when(componentDAO.findByParent(isA(Component.class))).thenReturn(list);	
			
			verify(componentDAO, times(1)).findById(1L, false);
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}

}
