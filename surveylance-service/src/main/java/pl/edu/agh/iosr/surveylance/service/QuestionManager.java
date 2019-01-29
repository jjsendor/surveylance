package pl.edu.agh.iosr.surveylance.service;

import java.util.List;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.service.exceptions.AnswerDoesNotExistException;
import pl.edu.agh.iosr.surveylance.service.exceptions.QuestionDoesNotExistException;

/**
 * Manages common actions on questions (adding, modificating, removing).
 * 
 * @author kuba
 */
public interface QuestionManager {

	/**
	 * Sets {@link QuestionDAO} data access object to be used by this service.
	 * 
	 * @param questionDAO
	 *            data access object for questions entites
	 */
	public void setQuestionDAO(QuestionDAO questionDAO);

	/**
	 * Sets {@link AnswerDAO} data access object to be used by this service.
	 * 
	 * @param answerDAO
	 *            data access object for answers entities
	 */
	public void setAnswerDAO(AnswerDAO answerDAO);

	/**
	 * Sets {@link SurveyManager} object which is used by this service.
	 * 
	 * @param componentManager
	 */
	public void setComponentManager(ComponentManager componentManager);

	/**
	 * Sets {@link ModificationService} object which is used by this service.
	 * 
	 * @param modificationService
	 */
	public void setModificationService(ModificationService modificationService);

	/**
	 * Creates given question in database. Also parent component for that
	 * question is created.
	 * 
	 * @param question
	 *            question entity which is persist
	 * @param parent
	 *            parent component for component that contains created question;
	 *            this is not component that directly contains question - it's
	 *            only parent component for question parent component (one which
	 *            is created by this method)
	 * @param position
	 *            position on which question's parent component is created
	 */
	public void createQuestion(Question question, Component parent, int position);

	/**
	 * Creates given question in database. Also parent component for that
	 * question is created.
	 * 
	 * @param question
	 *            question entity which is persist
	 * @param parentId
	 *            parent component id for component that contains created
	 *            question; this is not component that directly contains
	 *            question - it's only parent component for question parent
	 *            component (one which is created by this method)
	 * @param position
	 *            position on which question's parent component is created
	 */
	public void createQuestion(Question question, long parentId, int position);

	/**
	 * Gets question with given id.
	 * 
	 * @param questionId
	 *            id of the question which is returned
	 * @return question entity with id <code>questionId</code> or
	 *         <code>null</code> if there is no question with that id
	 */
	public Question getQuestion(long questionId);

	/**
	 * Gets question asociated with given parent component - one that has no
	 * other child components, but is only used to contain question (and by that
	 * question position in component order).
	 * 
	 * @param parent
	 *            parent component of question that is returned
	 * @return question entity or <code>null</code>
	 */
	public Question getQuestion(Component parent);

	/**
	 * Sets content for question with given id.
	 * 
	 * @param questionId
	 *            id of the question
	 * @param content
	 *            question content to be set
	 */
	public void setQuestionContent(long questionId, String content)
			throws QuestionDoesNotExistException;

	/**
	 * Deletes given question in database.
	 * 
	 * @param question
	 *            question entity which is deleted
	 */
	public void deleteQuestion(Question question);

	/**
	 * Deletes question with given id in database.
	 * 
	 * @param questionId
	 *            id of the question which is deleted
	 */
	public void deleteQuestion(long questionId)
			throws QuestionDoesNotExistException;

	/**
	 * Creates new answer for specified question. Newly created answer is put on
	 * last position on answers list for that question.
	 * 
	 * @param question
	 *            question to which answer is being added
	 * 
	 * @return created answer entity
	 */
	public Answer createAnswer(Question question);

	/**
	 * Creates new answer for given question.
	 * 
	 * @param question
	 *            question to which answer is being added
	 * @param position
	 *            position on which answer is created
	 * 
	 * @return created answer entity
	 */
	public Answer createAnswer(Question question, int position);

	/**
	 * Gets answer with given id.
	 * 
	 * @param answerId
	 *            id of the answer
	 * 
	 * @return answer entity
	 */
	public Answer getAnswer(long answerId);

	/**
	 * Gets answer which is on specified position on given question answers
	 * list.
	 * 
	 * @param question
	 *            question entity
	 * @param position
	 *            position of answer on question answers list
	 * 
	 * @return answer entity
	 */
	public Answer getAnswer(Question question, int position);

	/**
	 * Gets answer which is on specified position on given question answers
	 * list.
	 * 
	 * @param questionId
	 *            id of question
	 * @param position
	 *            position of answer on question answers list
	 * 
	 * @return answer entity
	 */
	public Answer getAnswer(long questionId, int position);

	/**
	 * Gets list of all answers for given question.
	 * 
	 * @param question
	 *            question for which answers are listed
	 * 
	 * @return list of answers
	 */
	public List<Answer> getAnswers(Question question);

	/**
	 * Sets value of specified answer by trying to parse given literal value.
	 * For text question type this value is set untouched. For numeric question
	 * type <code>literalValue</code> is parsed to integer.
	 * 
	 * @param answer
	 *            answer entity which value is being set
	 * @param literalValue
	 *            literal value to be set
	 */
	public void setAnswer(Answer answer, String literalValue);

	/**
	 * Sets value of specified answer. Object type is dependent from question
	 * type.
	 * 
	 * @param answer
	 *            answer entity which value is being set
	 * @param value
	 *            answer's value to be set
	 */
	public void setAnswer(Answer answer, Object value);

	/**
	 * Sets value of specified answer by trying to parse given literal value.
	 * For text question type this value is set untouched. For numeric question
	 * type <code>literalValue</code> is parsed to integer.
	 * 
	 * @param answerId
	 *            id of answer which value is set
	 * @param literalValue
	 *            literal value to be set
	 * 
	 * @throws AnswerDoesNotExistException
	 *             if answer with given id does not exist in database.
	 */
	public void setAnswer(long answerId, String literalValue)
			throws AnswerDoesNotExistException;

	/**
	 * Sets value for answer with given id.
	 * 
	 * @param answerId
	 *            id of answer which value is set
	 * @param value
	 *            answer's value to be set
	 * 
	 * @throws AnswerDoesNotExistException
	 *             if answer with given id does not exist in database.
	 */
	public void setAnswer(long answerId, Object value)
			throws AnswerDoesNotExistException;

	/**
	 * Deletes given answer in database.
	 * 
	 * @param answer
	 *            answer entity which is deleted
	 */
	public void deleteAnswer(Answer answer);

	/**
	 * Deletes answer with given id in database.
	 * 
	 * @param answerId
	 *            id of answer which is deleted
	 * 
	 * @throws AnswerDoesNotExistException
	 *             if answer with given id does not exist in database.
	 */
	public void deleteAnswer(long answerId) throws AnswerDoesNotExistException;

}
