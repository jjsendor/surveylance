package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyResultDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.SurveyResult;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.entities.User;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class SurveyResultManagerTest {

	private static ApplicationContext applicationContext;

	private SurveyResultManager resultManager = null;

	private ComponentDAO componentDAO = null;
	private AnswerDAO answerDAO = null;
	private QuestionDAO questionDAO = null;
	private SurveyDAO surveyDAO = null;
	private SurveyResultDAO surveyResultDAO = null;
	private UserDAO userDAO = null;
	private FormDAO formDAO;

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
		componentDAO = (ComponentDAO) applicationContext
				.getBean("componentDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");
		questionDAO = (QuestionDAO) applicationContext.getBean("questionDAO");
		surveyDAO = (SurveyDAO) applicationContext.getBean("surveyDAO");
		surveyResultDAO = (SurveyResultDAO) applicationContext
				.getBean("surveyResultDAO");
		userDAO = (UserDAO) applicationContext.getBean("userDAO");
		formDAO = (FormDAO) applicationContext.getBean("formDAO");

		resultManager = (SurveyResultManager)
			applicationContext.getBean("surveyResultManager");
	}

	/**
	 * Tears down the test fixture (Called after every test case method).
	 */
	@After
	public void tearDown() throws Exception {
		componentDAO = null;
		questionDAO = null;
		resultManager = null;
	}

	/**
	 * This is getSurveys methods test.
	 */
	@Test
	public void testGetSurveys() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			String userGoogleId1 = "user1@gmail.com";

			User user1 = userDAO.findByGoogleId(userGoogleId1);

			if (user1 == null) {
				user1 = new User();
				user1.setGoogleId(userGoogleId1);
				userDAO.create(user1);
			}

			int size = resultManager.getSurveys(user1).size();

			Survey[] surveys = new Survey[5];
			for (int i = 0; i < surveys.length; i++) {
				surveys[i] = new Survey();
				surveys[i].setOwner(user1);
				surveyDAO.create(surveys[i]);
			}

			List<Survey> s = resultManager.getSurveys(user1);
			assertEquals(s.size(), surveys.length + size);

			for (Survey survey : surveys)
				assertEquals(s.contains(survey), true);

			s = resultManager.getSurveys(user1.getId());
			assertEquals(s.size(), surveys.length + size);

			for (Survey survey : surveys)
				assertEquals(s.contains(survey), true);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is getQuestions methods test.
	 */
	@Test
	public void testGetQuestions() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Component component = new Component();
			componentDAO.create(component);

			Survey survey = new Survey();
			survey.setRootComponent(component);
			surveyDAO.create(survey);

			Question[] questions = new Question[5];
			for (int i = 0; i < questions.length; i++) {
				questions[i] = new Question();
				questions[i].setParentComponent(component);
				questionDAO.create(questions[i]);
			}

			List<Question> q = resultManager.getQuestions(survey);
			assertEquals(q.size(), questions.length);

			for (Question question : questions)
				assertEquals(q.contains(question), true);

			q = resultManager.getQuestions(survey.getId());
			assertEquals(q.size(), questions.length);

			for (Question question : questions)
				assertEquals(q.contains(question), true);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is getResults methods test.
	 */
	@Test
	public void testGetResults() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			NumericAnswer answer = new NumericAnswer();
			answerDAO.create(answer);

			Question question = new Question();
			questionDAO.create(question);

			SurveyResult[] results = new SurveyResult[5];
			for (int i = 0; i < results.length; i++) {
				results[i] = new SurveyResult();
				results[i].setQuestion(question);
				results[i].setAnswer(answer);
				surveyResultDAO.create(results[i]);
			}

			List<SurveyResult> q = resultManager.getResults(question);
			assertEquals(q.size(), results.length);

			for (SurveyResult result : results)
				assertEquals(q.contains(result), true);

			q = resultManager.getResults(question.getId());
			assertEquals(q.size(), results.length);

			for (SurveyResult result : results)
				assertEquals(q.contains(result), true);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is getAnswers and getPossibleAnswers methods test.
	 */
	@Test
	public void testGetAnswers() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Question question = new Question();
			questionDAO.create(question);

			NumericAnswer[] numericAnswer = new NumericAnswer[3];

			for (int i = 0; i < numericAnswer.length; i++) {
				numericAnswer[i] = new NumericAnswer();
				numericAnswer[i].setQuestion(question);
				answerDAO.create(numericAnswer[i]);

				if (i > 0) {
					SurveyResult result = new SurveyResult();
					result.setQuestion(question);
					result.setAnswer(numericAnswer[i]);
					surveyResultDAO.create(result);
				}
			}

			TextAnswer[] textAnswer = new TextAnswer[3];

			for (int i = 0; i < textAnswer.length; i++) {
				textAnswer[i] = new TextAnswer();
				textAnswer[i].setQuestion(question);
				answerDAO.create(textAnswer[i]);

				if (i > 0) {
					SurveyResult result = new SurveyResult();
					result.setQuestion(question);
					result.setAnswer(textAnswer[i]);
					surveyResultDAO.create(result);
				}
			}

			List<Answer> answers = resultManager.getPossibleAnswers(question);
			assertEquals(answers.size(), numericAnswer.length
					+ textAnswer.length);

			for (NumericAnswer answer : numericAnswer)
				assertEquals(answers.contains(answer), true);

			for (TextAnswer answer : textAnswer)
				assertEquals(answers.contains(answer), true);

			answers = resultManager.getAnswers(question);
			assertEquals(answers.size(), numericAnswer.length
					+ textAnswer.length - 2);

			for (int i = 1; i < numericAnswer.length; i++)
				assertEquals(answers.contains(numericAnswer[i]), true);

			for (int i = 1; i < textAnswer.length; i++)
				assertEquals(answers.contains(textAnswer[i]), true);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is statistics methods test.
	 */
	@Test
	public void testStatistics() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Question question = new Question();
			questionDAO.create(question);

			NumericAnswer[] numericAnswer = new NumericAnswer[10];

			int sum1 = 0, sum2 = 0;
			for (int i = 0; i < numericAnswer.length; i++) {
				numericAnswer[i] = new NumericAnswer();
				numericAnswer[i].setValue(new Double(i + 1));
				numericAnswer[i].setQuestion(question);
				answerDAO.create(numericAnswer[i]);

				for (int j = 0; j <= i; j++) {
					SurveyResult result = new SurveyResult();
					result.setQuestion(question);
					result.setAnswer(numericAnswer[i]);
					surveyResultDAO.create(result);
				}

				sum1 += i + 1;
				sum2 += (i + 1) * (i + 1);
			}
			double average = (double) sum1 / (double) numericAnswer.length;
			double weightedAverage = (double) sum2 / (double) sum1;

			assertEquals(resultManager.getMin(question).intValue(), 1);
			assertEquals(resultManager.getMin(question.getId()).intValue(), 1);
			assertEquals(resultManager.getMax(question).intValue(), 10);
			assertEquals(resultManager.getMax(question.getId()).intValue(), 10);
			assertEquals(resultManager.getAverage(question).doubleValue(), average);
			assertEquals(resultManager.getAverage(question.getId()).doubleValue(),
					average);
			assertEquals(resultManager.getWeightedAverage(question).doubleValue(),
					weightedAverage);
			assertEquals(resultManager.getWeightedAverage(question.getId())
					.doubleValue(), weightedAverage);
			assertEquals(resultManager.getMedian(question), numericAnswer[4]
					.getValue());
			assertEquals(resultManager.getMedian(question.getId()), numericAnswer[4]
					.getValue());
			assertEquals(resultManager.getMedianWithRepeatings(question),
					numericAnswer[6].getValue());
			assertEquals(resultManager.getMedianWithRepeatings(question.getId()),
					numericAnswer[6].getValue());

			NumericAnswer numericAnswer11 = new NumericAnswer();
			numericAnswer11.setValue(11.0);
			numericAnswer11.setQuestion(question);
			answerDAO.create(numericAnswer11);

			SurveyResult result = new SurveyResult();
			result.setQuestion(question);
			result.setAnswer(numericAnswer11);
			surveyResultDAO.create(result);

			assertEquals(resultManager.getMedian(question), numericAnswer[5]
					.getValue());
			assertEquals(resultManager.getMedian(question.getId()), numericAnswer[5]
					.getValue());
			assertEquals(resultManager.getMedianWithRepeatings(question),
					numericAnswer[6].getValue());
			assertEquals(resultManager.getMedianWithRepeatings(question.getId()),
					numericAnswer[6].getValue());

			NumericAnswer numericAnswer12 = new NumericAnswer();
			numericAnswer12.setValue(12.0);
			numericAnswer12.setQuestion(question);
			answerDAO.create(numericAnswer12);

			result = new SurveyResult();
			result.setQuestion(question);
			result.setAnswer(numericAnswer12);
			surveyResultDAO.create(result);

			assertEquals(resultManager.getMedianWithRepeatings(question),
					numericAnswer[7].getValue());
			assertEquals(resultManager.getMedianWithRepeatings(question.getId()),
					numericAnswer[7].getValue());

			List<Answer> mostPopular = resultManager.getMostPopularAnswers(question);
			assertEquals(mostPopular.size(), 1);
			assertEquals(mostPopular.contains(numericAnswer[9]), true);
			mostPopular = resultManager.getMostPopularAnswers(question.getId());
			assertEquals(mostPopular.size(), 1);
			assertEquals(mostPopular.contains(numericAnswer[9]), true);

			List<Answer> leastPopular = resultManager
					.getLeastPopularAnswers(question);
			assertEquals(leastPopular.size(), 3);
			assertEquals(leastPopular.contains(numericAnswer[0]), true);
			assertEquals(leastPopular.contains(numericAnswer11), true);
			assertEquals(leastPopular.contains(numericAnswer12), true);
			leastPopular = resultManager.getLeastPopularAnswers(question.getId());
			assertEquals(leastPopular.size(), 3);
			assertEquals(leastPopular.contains(numericAnswer[0]), true);
			assertEquals(leastPopular.contains(numericAnswer11), true);
			assertEquals(leastPopular.contains(numericAnswer12), true);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	/**
	 * This is getQuantity and getFrequency methods test.
	 */
	@Test
	public void testGetQuantityAndGetFrequency() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Question question = new Question();
			questionDAO.create(question);

			NumericAnswer[] numericAnswer = new NumericAnswer[3];

			int allAnswersQuantity = 0;
			for (int i = 0; i < numericAnswer.length; i++) {
				numericAnswer[i] = new NumericAnswer();
				numericAnswer[i].setQuestion(question);
				answerDAO.create(numericAnswer[i]);

				for (int j = 0; j <= i; j++) {
					SurveyResult result = new SurveyResult();
					result.setQuestion(question);
					result.setAnswer(numericAnswer[i]);
					surveyResultDAO.create(result);
				}

				allAnswersQuantity += i + 1;
			}

			TextAnswer[] textAnswer = new TextAnswer[3];

			for (int i = 0; i < textAnswer.length; i++) {
				textAnswer[i] = new TextAnswer();
				textAnswer[i].setQuestion(question);
				answerDAO.create(textAnswer[i]);

				for (int j = 0; j <= i; j++) {
					SurveyResult result = new SurveyResult();
					result.setQuestion(question);
					result.setAnswer(textAnswer[i]);
					surveyResultDAO.create(result);
				}

				allAnswersQuantity += i + 1;
			}

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(resultManager.getQuantity(question, numericAnswer[i])
						.intValue(), i + 1);

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(resultManager.getQuantity(question.getId(),
						numericAnswer[i].getId()).intValue(), i + 1);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(resultManager.getQuantity(question, textAnswer[i])
						.intValue(), i + 1);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(resultManager.getQuantity(question.getId(),
						textAnswer[i].getId()).intValue(), i + 1);

			Map<Answer, Integer> quantities = resultManager.getQuantity(question);
			assertEquals(quantities.keySet().size(), numericAnswer.length
					+ textAnswer.length);

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(quantities.get(numericAnswer[i]), i + 1);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(quantities.get(textAnswer[i]), i + 1);

			quantities = resultManager.getQuantity(question.getId());
			assertEquals(quantities.keySet().size(), numericAnswer.length
					+ textAnswer.length);

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(quantities.get(numericAnswer[i]), i + 1);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(quantities.get(textAnswer[i]), i + 1);

			assertEquals(resultManager.getAllAnswersQuantity(question),
					allAnswersQuantity);
			assertEquals(resultManager.getAllAnswersQuantity(question.getId()),
					allAnswersQuantity);

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(resultManager.getFrequency(question, numericAnswer[i])
						.doubleValue(), (double) (i + 1)
						/ (double) allAnswersQuantity);

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(resultManager.getFrequency(question.getId(),
						numericAnswer[i].getId()).doubleValue(),
						(double) (i + 1) / (double) allAnswersQuantity);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(resultManager.getFrequency(question, textAnswer[i])
						.doubleValue(), (double) (i + 1)
						/ (double) allAnswersQuantity);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(resultManager.getFrequency(question.getId(),
						textAnswer[i].getId()).doubleValue(), (double) (i + 1)
						/ (double) allAnswersQuantity);

			Map<Answer, Double> frequencies = resultManager.getFrequency(question);
			assertEquals(frequencies.keySet().size(), numericAnswer.length
					+ textAnswer.length);

			for (int i = 0; i < numericAnswer.length; i++)
				assertEquals(frequencies.get(numericAnswer[i]),
						(double) (i + 1) / (double) allAnswersQuantity);

			for (int i = 0; i < textAnswer.length; i++)
				assertEquals(frequencies.get(textAnswer[i]), (double) (i + 1)
						/ (double) allAnswersQuantity);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}

	@Test
	public void testCreateResultForAnswer() {
		Form form = new Form();
		formDAO.create(form);

		Question question = new Question();
		questionDAO.create(question);

		Answer answer = new TextAnswer();
		answerDAO.create(answer);

		SurveyResult result = resultManager.createResult(form, question, answer);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(form, result.getForm());
		assertEquals(question, result.getQuestion());
		assertEquals(answer, result.getAnswer());
	}

	@Test
	public void testCreateResultForAnswerValue() {
		Form form = new Form();
		formDAO.create(form);

		Question question = new Question();
		questionDAO.create(question);

		String answerValue = "test answer value";

		SurveyResult result = resultManager.createResult(form, question, answerValue);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(form, result.getForm());
		assertEquals(question, result.getQuestion());
		assertEquals(answerValue, result.getTextAnswer());
	}
	
	@Test
	public void testQuestionsEquality() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());

		Question [] questions = new Question[2];
		
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions[j]);
			
			for (int i = 0; i < 5; i++) {
				NumericAnswer answer = new NumericAnswer();
				answer.setValue(new Double(i + 5));
				answer.setQuestion(questions[j]);
				answerDAO.create(answer);	
			}
		}
		
		assertEquals(resultManager.areQuestionsEqual(questions[0], questions[1]), true);
		assertEquals(resultManager.areQuestionsEqual(questions[0].getId(), questions[1].getId()), true);
		
		NumericAnswer numeric = new NumericAnswer();
		numeric.setValue(20.0);
		numeric.setQuestion(questions[0]);
		answerDAO.create(numeric);	
		
		assertEquals(resultManager.areQuestionsEqual(questions[0], questions[1]), false);
		assertEquals(resultManager.areQuestionsEqual(questions[0].getId(), questions[1].getId()), false);
		
		Question [] questions2 = new Question[2];
		for(int j = 0 ; j < questions2.length ; j++){
			questions2[j] = new Question();
			questions2[j].setContent("Przykladowa tresc pytania 2");
			questions2[j].setType(QuestionType.TEXT);
			questions2[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions2[j]);
			
			for (int i = 0; i < 5; i++) {
				TextAnswer answer = new TextAnswer();
				answer.setValue("Odp nr " + i);
				answer.setQuestion(questions2[j]);
				answerDAO.create(answer);
			}
		}
		
		assertEquals(resultManager.areQuestionsEqual(questions2[0], questions2[1]), true);
		assertEquals(resultManager.areQuestionsEqual(questions2[0].getId(), questions2[1].getId()), true);
		assertEquals(resultManager.areQuestionsEqual(questions[0], questions2[0]), false);		
		assertEquals(resultManager.areQuestionsEqual(questions[0].getId(), questions2[0].getId()), false);
		
		Question [] questions3 = new Question[2];
		
		for(int j = 0 ; j < questions.length ; j++){
			questions3[j] = new Question();
			questions3[j].setContent("Przykladowa tresc pytania");
			questions3[j].setType(QuestionType.NUMERIC);
			questions3[j].setKind(QuestionKind.INPUT_TEXT);
			questionDAO.create(questions3[j]);			
		}		
		
		assertEquals(resultManager.areQuestionsEqual(questions3[0], questions3[1]), true);
		assertEquals(resultManager.areQuestionsEqual(questions3[0].getId(), questions3[1].getId()), true);
		assertEquals(resultManager.areQuestionsEqual(questions3[0], questions2[0]), false);		
		assertEquals(resultManager.areQuestionsEqual(questions3[0].getId(), questions2[0].getId()), false);
				
		tm.commit(ts);
	}
	
	@Test
	public void testGetEqualQuestions() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());	
		
		Component [] rootComponents = new Component[3];
		for(int j = 0 ; j < rootComponents.length ; j++){
			rootComponents[j] = new Component();
			rootComponents[j].setModifications(1);
			rootComponents[j].setPosition(1);
			rootComponents[j].setParentComponent(null);
			componentDAO.create(rootComponents[j]);	
		}
		
		Survey [] surveys = new Survey[3];
		
		for(int j = 0 ; j < surveys.length ; j++){
			surveys[j] = new Survey();
			surveys[j].setDescription("jakis opis ankiety");
			surveys[j].setExpirationDate(new Date());
			surveys[j].setModifications(5);
			surveys[j].setName("ankietka");
			surveys[j].setRootComponent(rootComponents[j]);
			surveyDAO.create(surveys[j]);		
		}
		
		Component [][] components = new Component[3][3];
		for(int i = 0 ; i<components.length ; i++)
			for(int j = 0 ; j < components[i].length ; j++){
				components[i][j] = new Component();
				components[i][j].setModifications(1);
				components[i][j].setPosition(1);
				components[i][j].setParentComponent(rootComponents[i]);
				componentDAO.create(components[i][j]);
			}
		
		Question [] questions = new Question[3];
		
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questions[j].setParentComponent(components[j][0]);
			questionDAO.create(questions[j]);
			
			for (int i = 0; i < 5; i++) {
				NumericAnswer answer = new NumericAnswer();
				answer.setValue(new Double(i + 5));
				answer.setQuestion(questions[j]);
				answerDAO.create(answer);	
			}
		}
		
		Question [] questions2 = new Question[3];
		for(int j = 0 ; j < questions2.length ; j++){
			questions2[j] = new Question();
			questions2[j].setContent("Przykladowa tresc pytania 2");
			questions2[j].setType(QuestionType.TEXT);
			questions2[j].setKind(QuestionKind.RADIO);
			questions2[j].setParentComponent(components[j][1]);
			questionDAO.create(questions2[j]);
			
			for (int i = 0; i < 5; i++) {
				TextAnswer answer = new TextAnswer();
				answer.setValue("Odp nr " + i);
				answer.setQuestion(questions2[j]);
				answerDAO.create(answer);
			}
		}
		
		Question [] questions3 = new Question[3];
		
		for(int j = 0 ; j < questions.length ; j++){
			questions3[j] = new Question();
			questions3[j].setContent("Przykladowa tresc pytania");
			questions3[j].setType(QuestionType.NUMERIC);
			questions3[j].setKind(QuestionKind.INPUT_TEXT);
			questions3[j].setParentComponent(components[j][2]);
			questionDAO.create(questions3[j]);			
		}		
			
		List<Survey> surveysList = new ArrayList<Survey>();
		for(Survey survey: surveys)
			surveysList.add(survey);
		List<List<Question>> equalQuestions = resultManager.getEqualQuestions(surveysList);
		
		assertEquals(equalQuestions.size(), 3);
		for(List<Question> list: equalQuestions){
			assertEquals(list.size(), 3);
			
			if(list.contains(questions[0])){
				assertEquals(list.get(0), questions[0]);
				assertEquals(list.get(1), questions[1]);
				assertEquals(list.get(2), questions[2]);
			}
			else if(list.contains(questions2[0])){
				assertEquals(list.get(0), questions2[0]);
				assertEquals(list.get(1), questions2[1]);
				assertEquals(list.get(2), questions2[2]);
			}
			else if(list.contains(questions3[0])){
				assertEquals(list.get(0), questions3[0]);
				assertEquals(list.get(1), questions3[1]);
				assertEquals(list.get(2), questions3[2]);
			}
			else
				fail();
		}			
		
		tm.commit(ts);
	}
	
	@Test
	public void testGettingAnswersAndResults() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());

		Question [] questions = new Question[3];
		NumericAnswer [][] answers = new NumericAnswer[3][5];
		
		int sum = 0;
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions[j]);
			
			for (int i = 0; i < 5; i++) {
				answers[j][i] = new NumericAnswer();
				answers[j][i].setValue(new Double(i + 5));
				answers[j][i].setQuestion(questions[j]);
				answerDAO.create(answers[j][i]);	
				
				sum += (j+1)*i + 3;
				for (int p = 0; p < (j+1)*i + 3; p++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answers[j][i]);
					result.setQuestion(questions[j]);
					surveyResultDAO.create(result);
				}				
			}
		}
		
		List<Question> questionsList = new ArrayList<Question>();
		for(int j = 0 ; j < questions.length ; j++)
			questionsList.add(questions[j]);
		List<List<Answer>> equalAnswers = resultManager.getPossibleAnswers(questionsList);
		assertEquals(equalAnswers.size(), 5);
		for(List<Answer> list: equalAnswers){
			assertEquals(list.size(), 3);
			
			int row = -1;
			for (int i = 0; i < 5; i++)
				if(list.contains(answers[0][i])){
					row = i;
					break;
				}
				
			if(row == -1)
				fail();
			else
				for (int i = 0; i < list.size(); i++)
					assertEquals(list.get(i), answers[i][row]);
				
		}			
		
		List<Answer> answersTogether = resultManager.getAnswers(questionsList);
		assertEquals(answersTogether.size(), 5*questions.length);
		for(int j = 0 ; j < questions.length ; j++)
			for (int i = 0; i < 5; i++)
				assertEquals(answersTogether.contains(answers[j][i]), true);
		
		List<SurveyResult> results = resultManager.getResults(questionsList);
		assertEquals(results.size(), sum);
		
		Question [] questions2 = new Question[3];
		TextAnswer [][] answers2 = new TextAnswer[3][5];
		sum = 0;
		for(int j = 0 ; j < questions2.length ; j++){
			questions2[j] = new Question();
			questions2[j].setContent("Przykladowa tresc pytania 2");
			questions2[j].setType(QuestionType.TEXT);
			questions2[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions2[j]);
			
			for (int i = 0; i < 5; i++) {
				answers2[j][i] = new TextAnswer();
				answers2[j][i].setValue("Odp nr " + i);
				answers2[j][i].setQuestion(questions2[j]);
				answerDAO.create(answers2[j][i]);
				
				sum += (j+1)*i + 3;
				for (int p = 0; p < (j+1)*i + 3; p++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answers2[j][i]);
					result.setQuestion(questions2[j]);
					surveyResultDAO.create(result);
				}				
			}
		}
		
		List<Question> questionsList2 = new ArrayList<Question>();
		for(int j = 0 ; j < questions2.length ; j++)
			questionsList2.add(questions2[j]);
		List<List<Answer>> equalAnswers2 = resultManager.getPossibleAnswers(questionsList2);
		assertEquals(equalAnswers2.size(), 5);
		for(List<Answer> list: equalAnswers2){
			assertEquals(list.size(), 3);
			
			int row = -1;
			for (int i = 0; i < 5; i++)
				if(list.contains(answers2[0][i])){
					row = i;
					break;
				}
				
			if(row == -1)
				fail();
			else
				for (int i = 0; i < list.size(); i++)
					assertEquals(list.get(i), answers2[i][row]);
				
		}			
		
		List<Answer> answersTogether2 = resultManager.getAnswers(questionsList2);
		assertEquals(answersTogether2.size(), 5*questions2.length);
		for(int j = 0 ; j < questions2.length ; j++)
			for (int i = 0; i < 5; i++)
				assertEquals(answersTogether2.contains(answers2[j][i]), true);
		
		List<SurveyResult> results2 = resultManager.getResults(questionsList);
		assertEquals(results2.size(), sum);
		
		tm.commit(ts);
	}
	
	@Test
	public void testQuantityTrend() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());

		Question [] questions = new Question[4];
		NumericAnswer [][] answers = new NumericAnswer[4][5];
		
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions[j]);
			
			for (int i = 0; i < 5; i++) {
				answers[j][i] = new NumericAnswer();
				answers[j][i].setValue(new Double(i + 5));
				answers[j][i].setQuestion(questions[j]);
				answerDAO.create(answers[j][i]);	
				
				for (int p = 0; p < (j+1)*i + 3; p++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answers[j][i]);
					result.setQuestion(questions[j]);
					surveyResultDAO.create(result);
				}
			}
		}	
		
		List<Question> questionsList = new ArrayList<Question>();
		for(int j = 0 ; j < questions.length ; j++)
			questionsList.add(questions[j]);
		
		Map<Answer,List<Integer>> trends = resultManager.getQuantityTrend(questionsList);
		Set<Answer> keys = trends.keySet();
		assertEquals(keys.size(), 5);
		for(Answer key: keys){
			int row = -1;
			for (int i = 0; i < 5; i++)
				if(key.equals(answers[0][i])){
					row = i;
					break;
				}
				
			if(row == -1)
				fail();
			else{
				List<Integer> trend = trends.get(key);
				assertEquals(trend.size(), questions.length);	
				for(int i=0 ; i<trend.size() ; i++)
					assertEquals(trend.get(i), (i+1)*row+3);
			}
		}
		
		tm.commit(ts);
	}
	
	@Test
	public void testFrequencyTrend() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());

		Question [] questions = new Question[4];
		NumericAnswer [][] answers = new NumericAnswer[4][5];
		int [] sums = new int[questions.length];
		
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions[j]);
			
			sums[j] = 0;
			for (int i = 0; i < 5; i++) {
				answers[j][i] = new NumericAnswer();
				answers[j][i].setValue(new Double(i + 5));
				answers[j][i].setQuestion(questions[j]);
				answerDAO.create(answers[j][i]);	
				
				sums[j] += (j+1)*i + 3;
				for (int p = 0; p < (j+1)*i + 3; p++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answers[j][i]);
					result.setQuestion(questions[j]);
					surveyResultDAO.create(result);
				}
			}
		}	
		
		List<Question> questionsList = new ArrayList<Question>();
		for(int j = 0 ; j < questions.length ; j++)
			questionsList.add(questions[j]);
		
		Map<Answer,List<Double>> trends = resultManager.getFrequencyTrend(questionsList);
		Set<Answer> keys = trends.keySet();
		assertEquals(keys.size(), 5);
		for(Answer key: keys){
			int row = -1;
			for (int i = 0; i < 5; i++)
				if(key.equals(answers[0][i])){
					row = i;
					break;
				}
				
			if(row == -1)
				fail();
			else{
				List<Double> trend = trends.get(key);
				assertEquals(trend.size(), questions.length);	
				for(int i=0 ; i<trend.size() ; i++)
					assertEquals(trend.get(i), (double)((i+1)*row + 3)/sums[i]);
			}
		}
		
		tm.commit(ts);
	}	
	
	@Test
	public void testQuantitiesSum() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());

		Question [] questions = new Question[4];
		NumericAnswer [][] answers = new NumericAnswer[4][5];
		int [] sums = new int[5];
		for (int i = 0; i < 5; i++)
			sums[i] = 0;
		
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions[j]);
			
			for (int i = 0; i < 5; i++) {
				answers[j][i] = new NumericAnswer();
				answers[j][i].setValue(new Double(i + 5));
				answers[j][i].setQuestion(questions[j]);
				answerDAO.create(answers[j][i]);	
				
				sums[i] += (j+1)*i + 3;
				for (int p = 0; p < (j+1)*i + 3; p++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answers[j][i]);
					result.setQuestion(questions[j]);
					surveyResultDAO.create(result);
				}
			}
		}	
		
		List<Question> questionsList = new ArrayList<Question>();
		for(int j = 0 ; j < questions.length ; j++)
			questionsList.add(questions[j]);
		
		Map<Answer,Integer> sumsMap = resultManager.getQuantitiesSum(questionsList);
		Set<Answer> keys = sumsMap.keySet();
		assertEquals(keys.size(), 5);
		for(Answer key: keys){
			int row = -1;
			for (int i = 0; i < 5; i++)
				if(key.equals(answers[0][i])){
					row = i;
					break;
				}
				
			if(row == -1)
				fail();
			else
				assertEquals(sumsMap.get(key), sums[row]);
		}
		
		tm.commit(ts);
	}	
	
	@Test
	public void testFrequenciesAverage() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
		.getBean("transactionManager");
		TransactionStatus ts = tm
		.getTransaction(new DefaultTransactionDefinition());

		Question [] questions = new Question[4];
		NumericAnswer [][] answers = new NumericAnswer[4][5];
		int [] sums = new int[5];
		for (int i = 0; i < 5; i++)
			sums[i] = 0;
		
		for(int j = 0 ; j < questions.length ; j++){
			questions[j] = new Question();
			questions[j].setContent("Przykladowa tresc pytania");
			questions[j].setType(QuestionType.NUMERIC);
			questions[j].setKind(QuestionKind.RADIO);
			questionDAO.create(questions[j]);
			
			for (int i = 0; i < 5; i++) {
				answers[j][i] = new NumericAnswer();
				answers[j][i].setValue(new Double(i + 5));
				answers[j][i].setQuestion(questions[j]);
				answerDAO.create(answers[j][i]);	
				
				sums[i] += (j+1)*i + 3;
				for (int p = 0; p < (j+1)*i + 3; p++) {
					SurveyResult result = new SurveyResult();
					result.setAnswer(answers[j][i]);
					result.setQuestion(questions[j]);
					surveyResultDAO.create(result);
				}
			}
		}	
		
		int sumAll = 0;
		for (int i = 0; i < 5; i++)
			sumAll += sums[i];
		
		List<Question> questionsList = new ArrayList<Question>();
		for(int j = 0 ; j < questions.length ; j++)
			questionsList.add(questions[j]);
		
		Map<Answer,Double> sumsMap = resultManager.getFrequencyFromSum(questionsList);
		Set<Answer> keys = sumsMap.keySet();
		assertEquals(keys.size(), 5);
		for(Answer key: keys){
			int row = -1;
			for (int i = 0; i < 5; i++)
				if(key.equals(answers[0][i])){
					row = i;
					break;
				}
				
			if(row == -1)
				fail();
			else
				assertEquals(sumsMap.get(key), (double)sums[row]/sumAll);
		}
		
		tm.commit(ts);
	}	
	
	/**
	 * This is statistics methods test.
	 */
	@Test
	public void testStatisticsManyQuestions() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		try {
			Question [] questions = new Question[4];
			
			int sum1 = 0, sum2 = 0, sum3 = 0;
			for(int p=0 ; p<questions.length ; p++){
				questions[p] = new Question();
				questionDAO.create(questions[p]);
	
				NumericAnswer[] numericAnswer = new NumericAnswer[10];
					
				for (int i = 0; i < numericAnswer.length; i++) {
					numericAnswer[i] = new NumericAnswer();
					numericAnswer[i].setValue(new Double((i + 1) * (p + 1)));
					numericAnswer[i].setQuestion(questions[p]);
					answerDAO.create(numericAnswer[i]);
	
					for (int j = 0; j <= i*p; j++) {
						SurveyResult result = new SurveyResult();
						result.setQuestion(questions[p]);
						result.setAnswer(numericAnswer[i]);
						surveyResultDAO.create(result);
					}
	
					sum1 +=  (i + 1) * (p + 1) ;
					sum2 += (i + 1) * (p + 1) * (i*p + 1);
					sum3 += (i*p + 1);
				}
			}
			double average = (double) sum1 / (double) questions.length / 10;
			double weightedAverage = (double) sum2 / (double) sum3;

			List<Question> questionsList = new ArrayList<Question>();
			for(int j = 0 ; j < questions.length ; j++)
				questionsList.add(questions[j]);
			
			assertEquals(resultManager.getMin(questionsList).intValue(), 1);
			assertEquals(resultManager.getMax(questionsList).intValue(), 40);
			assertEquals(resultManager.getAverage(questionsList).doubleValue(), average);
			assertEquals(resultManager.getWeightedAverage(questionsList).doubleValue(),
					weightedAverage);
		} catch (DAOException ex) {
			fail("Unexpected exception thrown. Message: " + ex.getMessage());
		}

		tm.commit(ts);
	}	
}
