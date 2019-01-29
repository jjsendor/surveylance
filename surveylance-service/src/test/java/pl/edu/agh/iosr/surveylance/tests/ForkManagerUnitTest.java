package pl.edu.agh.iosr.surveylance.tests;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import org.junit.Test;

import junit.framework.TestCase;

import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.impl.ForkManagerImpl;
import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.DecisionDAO;
import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.dao.hibernate.AnswerDAOImpl;
import pl.edu.agh.iosr.surveylance.dao.hibernate.ComponentDAOImpl;
import pl.edu.agh.iosr.surveylance.dao.hibernate.DecisionDAOImpl;
import pl.edu.agh.iosr.surveylance.dao.hibernate.ForkComponentDAOImpl;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;

public class ForkManagerUnitTest extends TestCase {

	private ForkComponentDAO forkComponentDAO;
	private ComponentDAO componentDAO;
	private DecisionDAO decisionDAO;
	private AnswerDAO answerDAO;
	
	private ForkManager manager;

	/**
	 * Sets up the test fixture (Called before every test case method).
	 */
	public void setUp() throws Exception {
		forkComponentDAO = mock(ForkComponentDAOImpl.class);
		componentDAO = mock(ComponentDAOImpl.class);
		decisionDAO = mock(DecisionDAOImpl.class);
		answerDAO = mock(AnswerDAOImpl.class);
		
		manager = new ForkManagerImpl();
		manager.setForkComponentDAO(forkComponentDAO);
		manager.setComponentDAO(componentDAO);
		manager.setDecisionDAO(decisionDAO);
		manager.setAnswerDAO(answerDAO);
	}
	
	/**
	 * Tears down the test fixture (Called after every test case method).
	 */
	public void tearDown() throws Exception {
		manager = null;
		forkComponentDAO = null;
		componentDAO = null;
		decisionDAO = null;
		answerDAO = null;
	}
	
	/**
	 * This is fork methods test.
	 */
	@Test
	public void testFork() {		
		try{						
			for(int i=0 ; i<5 ; i++)
				manager.createFork();
			
			verify(forkComponentDAO, times(5)).create(isA(ForkComponent.class));
			
			manager.getFork(5L);
			verify(forkComponentDAO).findById(5L, false);
			
			ForkComponent fork=new ForkComponent();
			when(forkComponentDAO.findById(6L, false)).thenReturn(fork);			
			manager.deleteFork(6L);
			verify(forkComponentDAO).delete(fork);
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}	
	
	/**
	 * This is test that check's management of questions's answers in decisions .
	 */
	public void testOuestionAnswerAndDecision() {
		try{											
			when(decisionDAO.findById(0L, false)).thenReturn(new Decision());
			
			manager.addNumericAnswerToDecision(0L);
			manager.addTextAnswerToDecision(0L);
			manager.addAnswerToDecision(0L, 1L);			
			manager.addAnswerToDecision(0L, 2L);

			verify(decisionDAO, times(4)).update(isA(Decision.class));
			
			manager.deleteAnswerFromDecision(0L, false);			
			manager.deleteAnswerFromDecision(0L, true);			
			
			verify(decisionDAO, times(6)).findById(0L, false);
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}		
	}		
	
	/**
	 * This is test that check's management of choices in decisions .
	 */
	public void testChoiceAndDecision() {
		try{						
			when(decisionDAO.findById(0L, false)).thenReturn(new Decision());
			
			manager.addChoiceToDecision(0L);
			manager.addChoiceToDecision(0L, 1L);			

			verify(decisionDAO, times(2)).update(isA(Decision.class));
			
			manager.deleteChoiceFromDecision(0L, false);			
			manager.deleteChoiceFromDecision(0L, true);			
			
			verify(decisionDAO, times(4)).findById(0L, false);		
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}	
	
	/**
	 * This is test that check's decision management in fork .
	 */
	public void testDecisionAndFork() {		
		try{
			when(decisionDAO.findById(1L, false)).thenReturn(new Decision());
			manager.addDecisionToFork(0L);						
			manager.addDecisionToFork(0L,1L);
			verify(decisionDAO).create(isA(Decision.class));				
			verify(decisionDAO).update(isA(Decision.class));							
			
			manager.deleteDecisionFromFork(0L, 1L, false);			
			manager.deleteDecisionFromFork(0L, 1L, true);			
			manager.deleteDecisionsFromFork(0L, 2L, false);									
			manager.deleteDecisionsFromFork(0L, 2L, true);			
				
			verify(decisionDAO, times(2)).findByForkComponentAndComponent(null,null);
		}
		catch(DAOException ex){
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}
	}		
}
