package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.FormDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyResultDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.SurveyResult;

public class SurveyResultDAOTest extends GenericDAOTest {

	private SurveyResultDAO surveyResultDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;
	private FormDAO formDAO;

	private Question question;
	private Answer answer;
	private Form form;

	/**
	 * Public constructor.
	 */
	public SurveyResultDAOTest() {
		surveyResultDAO = (SurveyResultDAO) applicationContext
				.getBean("surveyResultDAO");
		questionDAO = (QuestionDAO) applicationContext.getBean("questionDAO");
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");
		formDAO = (FormDAO) applicationContext.getBean("formDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(SurveyResult.class, surveyResultDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super.testFindById((Entity) new SurveyResult(),
				(GenericDAO) surveyResultDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((SurveyResult) entity1).getId(), ((SurveyResult) entity2)
				.getId());
		assertEquals(((SurveyResult) entity1).getAnswer(),
				((SurveyResult) entity2).getAnswer());
		assertEquals(((SurveyResult) entity1).getQuestion(),
				((SurveyResult) entity2).getQuestion());
		assertEquals(((SurveyResult) entity1).getTextAnswer(),
				((SurveyResult) entity2).getTextAnswer());
		assertEquals(((SurveyResult) entity1).getForm(),
				((SurveyResult) entity2).getForm());
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		this.answer = new NumericAnswer();
		((NumericAnswer) this.answer).setValue(new Double(2));
		this.answer.setModifications(new Integer(3));

		this.question = new Question();
		this.question.setType(QuestionType.NUMERIC);
		this.question.setKind(QuestionKind.RADIO);

		this.form = new Form();
		this.form.setHash("#%#%$#%");
		this.form.setSubmitted(new Boolean(true));

		SurveyResult surveyResult = new SurveyResult();
		surveyResult.setAnswer(answer);
		surveyResult.setQuestion(question);
		surveyResult.setTextAnswer("Murzyn");
		surveyResult.setForm(form);

		questionDAO.create(question);
		answerDAO.create(answer);
		formDAO.create(form);
		return surveyResult;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		SurveyResult surveyResult = new SurveyResult();
		return surveyResult;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		questionDAO.delete(question);
		answerDAO.delete(answer);
		formDAO.delete(form);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[3];
		excludedProperties[0] = "question";
		excludedProperties[1] = "answer";
		excludedProperties[2] = "textAnswer";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		Question question = new Question();
		((SurveyResult) entity).setQuestion(question);
		return entity;
	}

	/**
	 * This method tests findByExample() (with excluded properties parameter)
	 * method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindByExampleEStringArray() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		SurveyResult result = new SurveyResult();
		Question question = new Question();
		question.setModifications(new Integer(0));
		questionDAO.create(question);
		result.setQuestion(question);
		super.testFindByExampleEStringArray((Entity) result,
				(GenericDAO) surveyResultDAO);
		questionDAO.delete(question);

		tm.commit(ts);
	}

	/**
	 * This method tests findByExample() (without excluded properties parameter)
	 * method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindByExampleE() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		super.testFindByExampleE(this.createNewEntityWithData(),
				(GenericDAO) surveyResultDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists((Entity) new SurveyResult(),
				(GenericDAO) surveyResultDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate(createNewEntityWithoutData(),
				(GenericDAO) surveyResultDAO);
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate(createNewEntityWithoutData(),
				(GenericDAO) surveyResultDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete(createNewEntityWithoutData(),
				(GenericDAO) surveyResultDAO);
	}

	/**
	 * This method tests findByQuestion() method
	 */
	@Test
	public void testFindByQuestion() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Question question = new Question();
		questionDAO.create(question);

		int sizeBefore = surveyResultDAO.findByQuestion(question).size();

		SurveyResult surveyResult1 = new SurveyResult();
		SurveyResult surveyResult2 = new SurveyResult();

		surveyResult1.setQuestion(question);
		surveyResult2.setQuestion(question);

		surveyResultDAO.create(surveyResult1);
		surveyResultDAO.create(surveyResult2);

		int sizeAfter = surveyResultDAO.findByQuestion(question).size();

		assertEquals(sizeBefore + 2, sizeAfter);

		surveyResultDAO.delete(surveyResult1);
		surveyResultDAO.delete(surveyResult2);
		questionDAO.delete(question);

		tm.commit(ts);
	}

}
