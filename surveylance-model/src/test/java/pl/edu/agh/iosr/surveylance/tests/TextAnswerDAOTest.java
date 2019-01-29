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
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;

/**
 * This class represents QuestionNumericAnswer class test.
 * 
 * @author kornel
 */
public class TextAnswerDAOTest extends GenericDAOTest {

	private AnswerDAO answerDAO;
	private QuestionDAO questionDAO;

	private Question question;

	/**
	 * Public constructor.
	 */
	public TextAnswerDAOTest() {
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
		TextAnswer textAnswer = new TextAnswer();
		textAnswer.setModifications(new Integer(0));
		textAnswer.setPosition(new Integer(0));
		textAnswer.setValue(new String("Text"));

		this.question = new Question();
		questionDAO.create(this.question);

		textAnswer.setQuestion(this.question);
		return textAnswer;
	}

	/** {@inheritDoc} */
	@Override
	protected void deleteRelatedEntites() {
		questionDAO.delete(this.question);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createNewEntityWithoutData() {
		TextAnswer textAnswer = new TextAnswer();
		textAnswer.setModifications(new Integer(0));
		return textAnswer;
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

		TextAnswer textAnswer = (TextAnswer) createNewEntityWithoutData();
		super.testFindByExampleEStringArray((Entity) textAnswer,
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
		((TextAnswer) entity).setModifications(new Integer(10));
		return entity;
	}

	/** {@inheritDoc} */
	@Override
	protected void compareEntities(Entity entity1, Entity entity2) {
		assertEquals(((TextAnswer) entity1).getId(), ((TextAnswer) entity2)
				.getId());
		assertEquals(((TextAnswer) entity1).getModifications(),
				((TextAnswer) entity2).getModifications());
		assertEquals(((TextAnswer) entity1).getPosition(), ((TextAnswer) entity2)
				.getPosition());
		assertEquals(((TextAnswer) entity1).getQuestion(),
				((TextAnswer) entity2).getQuestion());
		assertEquals(((TextAnswer) entity1).getValue(), ((TextAnswer) entity2)
				.getValue());
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
		TextAnswer ta1 = (TextAnswer) createNewEntityWithoutData();
		TextAnswer ta2 = (TextAnswer) createNewEntityWithoutData();
		ta1.setQuestion(question);
		ta2.setQuestion(question);

		questionDAO.create(question);

		int numberOfAnswersBeforeSave = answerDAO.findByQuestion(question)
				.size();
		answerDAO.create(ta1);
		answerDAO.create(ta2);
		int numberOfAnswersAfterSave = answerDAO.findByQuestion(question)
				.size();

		assertEquals(2, numberOfAnswersAfterSave - numberOfAnswersBeforeSave);

		answerDAO.delete(ta2);
		answerDAO.delete(ta1);
		questionDAO.delete(question);

		tm.commit(ts);
	}

}
