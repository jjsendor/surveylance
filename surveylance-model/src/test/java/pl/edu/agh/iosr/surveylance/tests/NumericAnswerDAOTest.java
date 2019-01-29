package pl.edu.agh.iosr.surveylance.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.GenericDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Entity;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;

/**
 * This class represents QuestionNumericAnswer class test.
 * 
 * @author kornel
 */
public class NumericAnswerDAOTest extends GenericDAOTest {

	private AnswerDAO answerDAO;
	private QuestionDAO questionDAO;

	private Question question;

	/**
	 * Public constructor.
	 */
	public NumericAnswerDAOTest() {
		answerDAO = (AnswerDAO) applicationContext.getBean("answerDAO");
		questionDAO = (QuestionDAO) applicationContext.getBean("questionDAO");
	}

	/**
	 * This method tests getEntityClass() method.
	 */
	@Test
	public void testGetEntityClass() {
		assertEquals(Answer.class, answerDAO.getEntityClass());
	}

	/**
	 * This method tests findById() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		super
				.testFindById(createNewEntityWithoutData(),
						(GenericDAO) answerDAO);
	}

	/**
	 * This method tests findAll() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		super.testFindAll(createNewEntityWithoutData(), (GenericDAO) answerDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getExcludedProperties() {
		String[] excludedProperties = new String[2];
		excludedProperties[0] = "modifications";
		excludedProperties[1] = "value";
		return excludedProperties;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithData() {
		NumericAnswer numericAnswer = new NumericAnswer();
		numericAnswer.setModifications(new Integer(0));
		numericAnswer.setPosition(new Integer(0));
		numericAnswer.setValue(new Double(0));

		this.question = new Question();
		questionDAO.create(this.question);

		numericAnswer.setQuestion(this.question);
		return numericAnswer;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		questionDAO.delete(this.question);
	}

	/**
	 * This method returns instance of QuestionNumericAnswer class without any
	 * data.
	 * 
	 * @return instance of QuestionNumericAnswer class without data
	 */
	@Override
	protected Entity createNewEntityWithoutData() {
		NumericAnswer numericAnswer = new NumericAnswer();
		numericAnswer.setModifications(new Integer(0));
		return numericAnswer;
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

		NumericAnswer numericAnswer = (NumericAnswer) createNewEntityWithoutData();
		super.testFindByExampleEStringArray((Entity) numericAnswer,
				(GenericDAO) answerDAO);

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
				(GenericDAO) answerDAO);

		tm.commit(ts);
	}

	/**
	 * This method tests exists() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExists() {
		super.testExists(createNewEntityWithoutData(), (GenericDAO) answerDAO);
	}

	/**
	 * This method tests create() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreate() {
		super.testCreate((Entity) createNewEntityWithoutData(),
				(GenericDAO) answerDAO);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity getUpdatedEntity(Entity entity) {
		((NumericAnswer) entity).setModifications(new Integer(19));
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((NumericAnswer) entity1).getId(),
				((NumericAnswer) entity2).getId());
		assertEquals(((NumericAnswer) entity1).getModifications(),
				((NumericAnswer) entity2).getModifications());
		assertEquals(((NumericAnswer) entity1).getPosition(),
				((NumericAnswer) entity2).getPosition());
		assertEquals(((NumericAnswer) entity1).getQuestion(),
				((NumericAnswer) entity2).getQuestion());
		assertEquals(((NumericAnswer) entity1).getValue(),
				((NumericAnswer) entity2).getValue());
	}

	/**
	 * This method tests update() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {
		super.testUpdate((Entity) createNewEntityWithoutData(),
				(GenericDAO) answerDAO);
	}

	/**
	 * This method tests delete() method.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		super.testDelete((Entity) createNewEntityWithoutData(),
				(GenericDAO) answerDAO);
	}

	/**
	 * This method tests findByQuestion() method.
	 */
	@Test
	public void testFindByQuestion() {
		HibernateTransactionManager tm = (HibernateTransactionManager) applicationContext
				.getBean("transactionManager");
		TransactionStatus ts = tm
				.getTransaction(new DefaultTransactionDefinition());

		Question question = new Question();

		NumericAnswer na1 = (NumericAnswer) createNewEntityWithoutData();
		NumericAnswer na2 = (NumericAnswer) createNewEntityWithoutData();
		na1.setQuestion(question);
		na2.setQuestion(question);

		questionDAO.create(question);

		int numberOfAnswersBeforeSave = answerDAO.findByQuestion(question)
				.size();
		answerDAO.create(na1);
		answerDAO.create(na2);
		int numberOfAnswersAfterSave = answerDAO.findByQuestion(question)
				.size();

		assertEquals(2, numberOfAnswersAfterSave - numberOfAnswersBeforeSave);

		answerDAO.delete(na2);
		answerDAO.delete(na1);
		questionDAO.delete(question);

		tm.commit(ts);
	}

}
