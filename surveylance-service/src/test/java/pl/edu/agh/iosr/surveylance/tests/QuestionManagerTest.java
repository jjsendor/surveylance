package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.*;

import java.util.List;

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

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.exceptions.QuestionDoesNotExistException;

public class QuestionManagerTest {

	private static ApplicationContext applicationContext;

	private QuestionManager questionManager;

	private QuestionDAO questionDAO;
	private ComponentDAO componentDAO;
	private AnswerDAO answerDAO;

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
	 * Sets up testing environment for single test: creates DAO for question and
	 * manager service for question.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		questionDAO = (QuestionDAO) applicationContext.getBean("questionDAO");
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");

		questionManager =
			(QuestionManager) applicationContext.getBean("questionManager");
	}

	/**
	 * Tears down testing environment after single test.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		questionManager = null;
		questionDAO = null;
		componentDAO = null;
		answerDAO = null;
	}

	@Test
	public void testCreateQuestion() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();

		int questionsSizeBefore = questionDAO.findAll().size();
		int componentsSizeBefore = componentDAO.findAll().size();

		questionManager.createQuestion(question, parent, 1);

		int questionsSizeAfter = questionDAO.findAll().size();
		int componentsSizeAfter = componentDAO.findAll().size();

		assertNotNull(question.getId());
		assertNotNull(question.getParentComponent());
		assertNotNull(question.getParentComponent().getId());
		assertEquals(parent, question.getParentComponent().getParentComponent());
		assertEquals(1, questionsSizeAfter - questionsSizeBefore);
		assertEquals(1, componentsSizeAfter - componentsSizeBefore);
	}

	@Test
	public void testCreateQuestionById() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();

		int questionsSizeBefore = questionDAO.findAll().size();
		int componentsSizeBefore = componentDAO.findAll().size();

		questionManager.createQuestion(question, parent.getId(), 1);

		int questionsSizeAfter = questionDAO.findAll().size();
		int componentsSizeAfter = componentDAO.findAll().size();

		assertNotNull(question.getId());
		assertNotNull(question.getParentComponent());
		assertNotNull(question.getParentComponent().getId());
		assertEquals(parent, question.getParentComponent().getParentComponent());
		assertEquals(1, questionsSizeAfter - questionsSizeBefore);
		assertEquals(1, componentsSizeAfter - componentsSizeBefore);

		tm.commit(ts);
	}

	@Test
	public void testGetQuestion() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setContent("get question test content");

		questionManager.createQuestion(question, parent, 1);

		Question question2 = questionManager.getQuestion(question.getId());

		assertNotNull(question2);
		assertEquals(question.getId(), question2.getId());
		assertEquals("get question test content", question2.getContent());
	}

	@Test
	public void testGetByParentQuestion() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setContent("get by parent question test content");

		questionManager.createQuestion(question, parent, 1);

		Question question2 = questionManager.getQuestion(question
				.getParentComponent());

		assertNotNull(question2);
		assertEquals(question.getId(), question2.getId());
		assertEquals("get by parent question test content", question2
				.getContent());
	}

	@Test
	public void testSetQuestionContent() throws QuestionDoesNotExistException {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();

		questionManager.createQuestion(question, parent, 1);

		questionManager.setQuestionContent(question.getId(), "test content");

		assertEquals("test content", question.getContent());

		tm.commit(ts);
	}

	@Test
	public void deleteQuestion() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();

		questionManager.createQuestion(question, parent, 1);

		int componentsSizeBefore = componentDAO.findAll().size();
		int questionsSizeBefore = questionDAO.findAll().size();

		long questionId = question.getId();
		long componentId = question.getParentComponent().getId();

		questionManager.deleteQuestion(question);

		int componentsSizeAfter = componentDAO.findAll().size();
		int questionsSizeAfter = questionDAO.findAll().size();

		Component component = componentDAO.findById(componentId, false);
		assertNull(component);

		question = questionDAO.findById(questionId, false);
		assertNull(question);

		assertEquals(1, componentsSizeBefore - componentsSizeAfter);
		assertEquals(1, questionsSizeBefore - questionsSizeAfter);

		tm.commit(ts);
	}

	@Test
	public void testDeleteQuestionById() throws QuestionDoesNotExistException {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();

		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		questionManager.createQuestion(question, parent, 1);
		long questionId = question.getId();
		long componentId = question.getParentComponent().getId();

		int componentsSizeBefore = componentDAO.findAll().size();
		int questionsSizeBefore = questionDAO.findAll().size();

		questionManager.deleteQuestion(questionId);
		tm.commit(ts);

		int componentsSizeAfter = componentDAO.findAll().size();
		int questionsSizeAfter = questionDAO.findAll().size();

		Component component = componentDAO.findById(componentId, false);
		assertNull(component);

		question = questionDAO.findById(questionId, false);
		assertNull(question);

		assertEquals(1, questionsSizeBefore - questionsSizeAfter);
		assertEquals(1, componentsSizeBefore - componentsSizeAfter);
	}

	@Test
	public void testCreateNumericAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);
		NumericAnswer answer = (NumericAnswer) questionManager.createAnswer(
				question, 0);
		answer.setValue(263.0);
		answerDAO.update(answer);

		assertNotNull(answer);
		assertNotNull(answer.getId());

		NumericAnswer answer2 = (NumericAnswer) questionManager.createAnswer(
				question, 2);
		answer2.setValue(271.0);
		answerDAO.update(answer2);

		assertNotNull(answer2);
		assertNotNull(answer2.getId());
		assertEquals(1, answer2.getPosition());

		NumericAnswer answer3 = (NumericAnswer) questionManager
				.createAnswer(question);
		answer3.setValue(280.0);
		answerDAO.update(answer3);

		assertNotNull(answer3);
		assertNotNull(answer3.getId());
		assertEquals(2, answer3.getPosition());
	}

	@Test
	public void testCreateTextAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);
		TextAnswer answer = (TextAnswer) questionManager.createAnswer(question,
				0);
		answer.setValue("answer 298");
		answerDAO.update(answer);

		assertNotNull(answer);
		assertNotNull(answer.getId());

		TextAnswer answer2 = (TextAnswer) questionManager.createAnswer(
				question, 2);
		answer2.setValue("answer 306");
		answerDAO.update(answer2);

		assertNotNull(answer2);
		assertNotNull(answer2.getId());
		assertEquals(1, answer2.getPosition());

		TextAnswer answer3 = (TextAnswer) questionManager
				.createAnswer(question);
		answer3.setValue("answer 315");
		answerDAO.update(answer3);

		assertNotNull(answer3);
		assertNotNull(answer3.getId());
		assertEquals(2, answer3.getPosition());
	}

	@Test
	public void testGetNumericAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);
		NumericAnswer answer = (NumericAnswer) questionManager.createAnswer(
				question, 0);

		NumericAnswer answer2 = (NumericAnswer) questionManager
				.getAnswer(answer.getId());

		assertNotNull(answer2);
	}

	@Test
	public void testGetTextAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);
		TextAnswer answer = (TextAnswer) questionManager.createAnswer(question,
				0);

		TextAnswer answer2 = (TextAnswer) questionManager.getAnswer(answer
				.getId());

		assertNotNull(answer2);
	}

	@Test
	public void testGetNumericAnswers() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);

		int n = 3;

		for (int i = 0; i < n; i++)
			questionManager.createAnswer(question, 0);

		List<Answer> answers = questionManager.getAnswers(question);

		assertEquals(n, answers.size());
	}

	@Test
	public void testGetTextAnswers() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);

		int n = 3;

		for (int i = 0; i < n; i++)
			questionManager.createAnswer(question, 0);

		List<Answer> answers = questionManager.getAnswers(question);

		assertEquals(n, answers.size());
	}

	@Test
	public void testSetNumericAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);
		NumericAnswer answer = (NumericAnswer) questionManager.createAnswer(
				question, 0);

		double value = 5.0;

		questionManager.setAnswer(answer, value);

		NumericAnswer answer2 = (NumericAnswer) questionManager
				.getAnswer(answer.getId());

		assertEquals(value, answer2.getValue());
	}

	@Test
	public void testSetNumericAnswerLiteral() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);
		NumericAnswer answer = (NumericAnswer) questionManager.createAnswer(
				question, 0);

		double value = 415.0;

		questionManager.setAnswer(answer, new Double(value).toString());

		NumericAnswer answer2 = (NumericAnswer) questionManager
				.getAnswer(answer.getId());

		assertEquals(value, answer2.getValue());
	}

	@Test
	public void testSetNumericAnswerById() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);
		NumericAnswer answer = (NumericAnswer) questionManager.createAnswer(
				question, 0);

		double value = 5.0;

		questionManager.setAnswer(answer.getId(), value);

		NumericAnswer answer2 = (NumericAnswer) questionManager
				.getAnswer(answer.getId());

		assertEquals(value, answer2.getValue());
	}

	@Test
	public void testSetTextAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);
		TextAnswer answer = (TextAnswer) questionManager.createAnswer(question,
				0);

		String text = "answer test - object";

		questionManager.setAnswer(answer, (Object) text);

		TextAnswer answer2 = (TextAnswer) questionManager.getAnswer(answer
				.getId());

		assertEquals(text, answer2.getValue());
	}

	@Test
	public void testSetTextAnswerLiteral() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);
		TextAnswer answer = (TextAnswer) questionManager.createAnswer(question,
				0);

		String text = "answer test - literal";

		questionManager.setAnswer(answer, text);

		TextAnswer answer2 = (TextAnswer) questionManager.getAnswer(answer
				.getId());

		assertEquals(text, answer2.getValue());
	}

	@Test
	public void testSetTextAnswerById() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);
		TextAnswer answer = (TextAnswer) questionManager.createAnswer(question,
				0);

		String text = "another answer test";

		questionManager.setAnswer(answer.getId(), text);

		TextAnswer answer2 = (TextAnswer) questionManager.getAnswer(answer
				.getId());

		assertEquals(text, answer2.getValue());
	}

	@Test
	public void testDeleteNumericAnswer() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);

		int n = 4;

		for (int i = 0; i < n; i++)
			questionManager.createAnswer(question, 0);

		List<Answer> answers = questionManager.getAnswers(question);

		NumericAnswer answer = (NumericAnswer) answers.get(0);
		Long answerId = answers.get(0).getId();

		questionManager.deleteAnswer(answer);

		List<Answer> answers2 = questionManager.getAnswers(question);

		assertEquals(n - 1, answers2.size());
		assertNull(answerDAO.findById(answerId, false));

		tm.commit(ts);
	}

	@Test
	public void testDeleteNumericAnswerById() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.NUMERIC);

		questionManager.createQuestion(question, parent, 1);

		int n = 4;

		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		for (int i = 0; i < n; i++)
			questionManager.createAnswer(question, 0);

		List<Answer> answers = questionManager.getAnswers(question);

		Long answerId = answers.get(0).getId();

		questionManager.deleteAnswer(answerId);
		tm.commit(ts);

		List<Answer> answers2 = questionManager.getAnswers(question);

		assertEquals(n - 1, answers2.size());
		assertNull(answerDAO.findById(answerId, false));
	}

	@Test
	public void testDeleteTextAnswer() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);

		int n = 4;

		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		for (int i = 0; i < n; i++)
			questionManager.createAnswer(question, 0);

		List<Answer> answers = questionManager.getAnswers(question);

		TextAnswer answer = (TextAnswer) answers.get(0);
		Long answerId = answers.get(0).getId();

		questionManager.deleteAnswer(answer);
		tm.commit(ts);

		List<Answer> answers2 = questionManager.getAnswers(question);

		assertEquals(n - 1, answers2.size());
		assertNull(answerDAO.findById(answerId, false));
	}

	@Test
	public void testDeleteTextAnswerById() {
		Component parent = new Component();
		componentDAO.create(parent);
		Question question = new Question();
		question.setType(QuestionType.TEXT);

		questionManager.createQuestion(question, parent, 1);

		int n = 4;

		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		for (int i = 0; i < n; i++)
			questionManager.createAnswer(question, 0);

		List<Answer> answers = questionManager.getAnswers(question);

		Long answerId = answers.get(0).getId();

		questionManager.deleteAnswer(answerId);
		tm.commit(ts);

		List<Answer> answers2 = questionManager.getAnswers(question);

		assertEquals(n - 1, answers2.size());
		assertNull(answerDAO.findById(answerId, false));
	}

}
