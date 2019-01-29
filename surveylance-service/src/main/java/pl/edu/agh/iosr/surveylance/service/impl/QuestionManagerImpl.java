package pl.edu.agh.iosr.surveylance.service.impl;

import java.util.List;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.ModificationService;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.exceptions.AnswerDoesNotExistException;
import pl.edu.agh.iosr.surveylance.service.exceptions.QuestionDoesNotExistException;

/**
 * Implements {@link QuestionManager} interface.
 * 
 * @author kuba
 */
public class QuestionManagerImpl implements QuestionManager {

	private ComponentManager componentManager;
	private ModificationService modificationService;

	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;

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
	@Override
	public void setComponentManager(ComponentManager componentManager) {
		this.componentManager = componentManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setModificationService(
			ModificationService modificationService) {
		this.modificationService = modificationService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createQuestion(Question question, Component parent,
			int position) {
		Component component =
			componentManager.createComponent(parent, position);

		question.setParentComponent(component);
		questionDAO.create(question);
		// do not mark modified parent component!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createQuestion(Question question, long parentId, int position) {
		Component component =
			componentManager.createComponent(parentId, position);

		question.setParentComponent(component);
		questionDAO.create(question);
		// do not mark modified parent component!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Question getQuestion(long questionId) {
		return questionDAO.findById(questionId, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Question getQuestion(Component parent) {
		return questionDAO.findByParent(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQuestionContent(long questionId, String content)
			throws QuestionDoesNotExistException {
		Question question = getQuestion(questionId);

		if (question != null) {
			question.setContent(content);
			questionDAO.update(question);

			modificationService.markModified(question);
		}
		else {
			throw new QuestionDoesNotExistException("Question with id "
					+ questionId + " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQuestion(Question question) {
		Component component = question.getParentComponent();

		if (component != null) {
			question.setParentComponent(null);
			componentManager.deleteComponent(component);
		}

		for (Answer answer : getAnswers(question))
			deleteAnswer(answer);

		questionDAO.delete(question);
		// do not mark modified parent component!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQuestion(long questionId)
			throws QuestionDoesNotExistException {
		Question question = getQuestion(questionId);

		if (question != null)
			deleteQuestion(question);
		else {
			throw new QuestionDoesNotExistException("Question with id "
					+ questionId + " does not exist in database");
		}
	}

	private int getMaxAnswerPosition(Question question) {
		return answerDAO.findByQuestion(question).size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Answer createAnswer(Question question) {
		int position = getMaxAnswerPosition(question);

		return createAnswer(question, position);
	}

	private void adjustPosition(Question question, int position) {
		List<Answer> answers = answerDAO.findByQuestion(question);

		for (Answer answer : answers) {
			if (answer.getPosition() != null
					&& answer.getPosition() >= position) {
				answer.setPosition(answer.getPosition() + 1);
				answerDAO.update(answer);
			}
		}
	}

	private void readjustPosition(Question question, int position) {
		List<Answer> answers = answerDAO.findByQuestion(question);

		for (Answer answer : answers) {
			if (answer.getPosition() != null
					&& answer.getPosition() > position) {
				answer.setPosition(answer.getPosition() - 1);
				answerDAO.update(answer);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Answer createAnswer(Question question, int position) {
		Answer answer = null;

		if (question.getType() == QuestionType.NUMERIC)
			answer = new NumericAnswer();
		else if (question.getType() == QuestionType.TEXT)
			answer = new TextAnswer();

		int maxPosition = getMaxAnswerPosition(question);

		if (maxPosition < position)
			position = maxPosition;
		else
			adjustPosition(question, position); // adjust only if position is
		// is less then maxPosition

		answer.setPosition(position);
		answer.setQuestion(question);

		answerDAO.create(answer);

		modificationService.markModified(question);

		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Answer getAnswer(long answerId) {
		return answerDAO.findById(answerId, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Answer getAnswer(Question question, int position) {
		List<Answer> answers = getAnswers(question);

		for (Answer answer : answers) {
			if (answer.getPosition() != null
					&& answer.getPosition() == position)
				return answer;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Answer getAnswer(long questionId, int position) {
		Question question = questionDAO.findById(questionId, false);
		return getAnswer(question, position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getAnswers(Question question) {
		return answerDAO.findByQuestion(question);
	}

	private void setNumericAnswer(NumericAnswer answer, Double number) {
		answer.setValue(number);
	}

	private void setTextAnswer(TextAnswer answer, String text) {
		answer.setValue(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnswer(Answer answer, String literalValue) {
		Question question = answer.getQuestion();

		if (question.getType() == QuestionType.NUMERIC)
			setNumericAnswer((NumericAnswer) answer, new Double(literalValue));
		else if (question.getType() == QuestionType.TEXT)
			setTextAnswer((TextAnswer) answer, literalValue);

		answerDAO.update(answer);

		modificationService.markModified(answer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnswer(Answer answer, Object value) {
		Question question = answer.getQuestion();

		if (question.getType() == QuestionType.NUMERIC)
			setNumericAnswer((NumericAnswer) answer, (Double) value);
		else if (question.getType() == QuestionType.TEXT)
			setTextAnswer((TextAnswer) answer, (String) value);

		answerDAO.update(answer);

		modificationService.markModified(answer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnswer(long answerId, String literalValue)
			throws AnswerDoesNotExistException {
		Answer answer = getAnswer(answerId);

		if (answer != null)
			setAnswer(answer, literalValue);
		else {
			throw new AnswerDoesNotExistException("Answer with id " + answerId
					+ " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnswer(long answerId, Object value)
			throws AnswerDoesNotExistException {
		Answer answer = getAnswer(answerId);

		if (answer != null)
			setAnswer(answer, value);
		else {
			throw new AnswerDoesNotExistException("Answer with id " + answerId
					+ " does not exist in database");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAnswer(Answer answer) {
		if (answer.getPosition() != null)
			readjustPosition(answer.getQuestion(), answer.getPosition());

		modificationService.markModified(answer.getQuestion());

		answer.setQuestion(null);
		answerDAO.delete(answer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAnswer(long answerId) throws AnswerDoesNotExistException {
		Answer answer = getAnswer(answerId);

		if (answer != null)
			deleteAnswer(answer);
		else {
			throw new AnswerDoesNotExistException("Answer with id " + answerId
					+ " does not exist in database");
		}
	}

}
