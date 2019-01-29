package pl.edu.agh.iosr.surveylance.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;

import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.DecisionDAO;
import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import static org.junit.Assert.*;

public class ForkManagerTest {

	private static ApplicationContext applicationContext;

	private ForkComponentDAO forkComponentDAO;
	private ComponentDAO componentDAO;
	private DecisionDAO decisionDAO;
	private AnswerDAO answerDAO;

	private ForkManager forkManager;

	/**
	 * Sets up testing environment. That means opening hibernate session factory
	 * and opening new hibernate session which is used in tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] paths = {
				"applicationContext.xml", "applicationContextDao.xml" };
		applicationContext = new ClassPathXmlApplicationContext(paths);
	}

	/**
	 * Tears down testing environment. Closes hibernate session.
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets up the test fixture (Called before every test case method).
	 */
	@Before
	public void setUp() throws Exception {
		forkComponentDAO = (ForkComponentDAO) applicationContext
				.getBean("forkComponentDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		decisionDAO = (DecisionDAO) applicationContext.getBean("decisionDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");

		forkManager =
			(ForkManager) applicationContext.getBean("forkManager");
	}

	/**
	 * Tears down the test fixture (Called after every test case method).
	 */
	@After
	public void tearDown() throws Exception {
		forkManager = null;
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
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			int size = forkComponentDAO.findAll().size();
			ForkComponent[] fork = new ForkComponent[5];
			for (int i = 0; i < fork.length; i++)
				fork[i] = forkManager.createFork();

			assertEquals(forkComponentDAO.findAll().size(), size + fork.length);

			for (ForkComponent comp : fork)
				assertEquals(forkManager.getFork(comp.getId()), comp);

			Component comp = new Component();
			componentDAO.create(comp);
			forkManager.addParentToFork(fork[0].getId(), comp.getId());
			assertEquals(fork[0].getParentComponent(), comp);

			forkManager.deleteFork(fork[0].getId());
			assertEquals(forkComponentDAO.findAll().size(), size + fork.length
					- 1);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is test that check's management of questions's answers in decisions
	 * .
	 */
	@Test
	public void testQuestionAnswerAndDecision() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Decision[] decision = new Decision[4];

			for (int i = 0; i < decision.length; i++) {
				decision[i] = new Decision();
				decisionDAO.create(decision[i]);
			}

			Answer[] qa = new Answer[4];

			int answersSize = answerDAO.findAll().size();
			qa[0] = forkManager.addNumericAnswerToDecision(decision[0].getId());
			assertEquals(answersSize + 1, answerDAO.findAll().size());

			qa[1] = forkManager.addTextAnswerToDecision(decision[1].getId());
			assertEquals(answersSize + 2, answerDAO.findAll().size());

			qa[2] = new NumericAnswer();
			answerDAO.create((NumericAnswer) qa[2]);
			forkManager.addAnswerToDecision(decision[2].getId(), qa[2].getId());
			assertEquals(answersSize + 3, answerDAO.findAll().size());

			qa[3] = new TextAnswer();
			answerDAO.create((Answer) qa[3]);
			forkManager.addAnswerToDecision(decision[3].getId(), qa[3].getId());
			assertEquals(answersSize + 4, answerDAO.findAll().size());

			for (int i = 0; i < decision.length; i++)
				assertEquals(
						forkManager.getAnswerFromDecision(decision[i].getId()),
						qa[i]);

			forkManager.deleteAnswerFromDecision(decision[0].getId(), false);
			assertEquals(decision[0].getAnswer(), null);
			assertEquals(answersSize + 4, answerDAO.findAll().size());

			forkManager.deleteAnswerFromDecision(decision[2].getId(), true);
			assertEquals(decision[2].getAnswer(), null);
			assertEquals(answersSize + 3, answerDAO.findAll().size());

			forkManager.deleteAnswerFromDecision(decision[1].getId(), false);
			assertEquals(decision[1].getAnswer(), null);
			assertEquals(answersSize + 3, answerDAO.findAll().size());

			forkManager.deleteAnswerFromDecision(decision[3].getId(), true);
			assertEquals(decision[3].getAnswer(), null);
			assertEquals(answersSize + 2, answerDAO.findAll().size());
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is test that check's management of choices in decisions .
	 */
	@Test
	public void testChoiceAndDecision() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Decision[] decision = new Decision[2];

			int size = componentDAO.findAll().size();

			for (int i = 0; i < decision.length; i++) {
				decision[i] = new Decision();
				decisionDAO.create(decision[i]);
			}

			Component[] comp = new Component[2];
			comp[0] = forkManager.addChoiceToDecision(decision[0].getId());
			assertEquals(componentDAO.findAll().size(), size + 1);

			comp[1] = new Component();
			componentDAO.create(comp[1]);
			forkManager.addChoiceToDecision(decision[1].getId(), comp[1].getId());
			assertEquals(componentDAO.findAll().size(), size + 2);

			for (int i = 0; i < decision.length; i++)
				assertEquals(
						forkManager.getChoiceFromDecision(decision[i].getId()),
						comp[i]);

			forkManager.deleteChoiceFromDecision(decision[0].getId(), false);
			assertEquals(decision[0].getComponent(), null);
			assertEquals(componentDAO.findAll().size(), size + 2);

			forkManager.deleteChoiceFromDecision(decision[1].getId(), true);
			assertEquals(decision[1].getComponent(), null);
			assertEquals(componentDAO.findAll().size(), size + 1);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is test that check's decision management in fork .
	 */
	@Test
	public void testDecisionAndFork() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Decision dec = new Decision();
			decisionDAO.create(dec);
			assertEquals(forkManager.getDecision(dec.getId()), dec);

			Decision[] decision = new Decision[4];
			ForkComponent fork = new ForkComponent();
			forkComponentDAO.create(fork);

			int size = decisionDAO.findAll().size();

			for (int i = 0; i < 2; i++) {
				decision[i] = forkManager.addDecisionToFork(fork.getId());
				assertEquals(decisionDAO.findAll().size(), size + i + 1);
				assertEquals(decision[i].getForkComponent(), fork);
			}

			Component choice = new Component();
			for (int i = 2; i < decision.length; i++) {
				decision[i] = new Decision();
				decisionDAO.create(decision[i]);
				forkManager.addDecisionToFork(fork.getId(), decision[i].getId());
				assertEquals(decisionDAO.findAll().size(), size + i + 1);
				assertEquals(decision[i].getForkComponent(), fork);

				decision[i].setComponent(choice);
			}

			assertEquals(forkManager.getDecisionsFromFork(fork.getId()).size(),
					decision.length);
			assertEquals(forkManager.getDecisionsFromFork(fork.getId(),
					choice.getId()).size(), 2);

			forkManager.deleteDecisionFromFork(fork.getId(), decision[0].getId(),
					false);
			assertEquals(decision[0].getForkComponent(), null);
			assertEquals(decisionDAO.findAll().size(), size + decision.length);

			forkManager.deleteDecisionFromFork(fork.getId(), decision[1].getId(),
					true);
			assertEquals(decision[1].getForkComponent(), null);
			assertEquals(decisionDAO.findAll().size(), size + decision.length
					- 1);

			forkManager
					.deleteDecisionsFromFork(fork.getId(), choice.getId(),
							false);
			assertEquals(decision[2].getForkComponent(), null);
			assertEquals(decision[3].getForkComponent(), null);
			assertEquals(decisionDAO.findAll().size(), size + decision.length
					- 1);

			decision[2].setComponent(choice);
			forkManager.addDecisionToFork(fork.getId(), decision[2].getId());
			decision[3].setComponent(choice);
			forkManager.addDecisionToFork(fork.getId(), decision[3].getId());

			forkManager.deleteDecisionsFromFork(fork.getId(), choice.getId(), true);
			assertEquals(decision[2].getForkComponent(), null);
			assertEquals(decision[3].getForkComponent(), null);
			assertEquals(decisionDAO.findAll().size(), size + decision.length
					- 3);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

}
