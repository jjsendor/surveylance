package pl.edu.agh.iosr.surveylance.service;

import java.util.List;
import java.util.Map;

import com.google.gdata.util.common.base.Pair;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyResultDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.SurveyResult;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * This service manages all possible actions for analyzing results.
 * 
 * @author michal
 * @author kornel
 */
public interface SurveyResultManager {

	/**
	 * Sets {@link ComponentDAO} data access object which is used by this
	 * service.
	 * 
	 * @param componentDAO
	 *            data access object for surveyResult
	 */
	public void setComponentDAO(ComponentDAO componentDAO);

	/**
	 * Sets {@link SurveyResultDAO} data access object which is used by this
	 * service.
	 * 
	 * @param surveyResultDAO
	 *            data access object for surveyResult
	 */
	public void setSurveyResultDAO(SurveyResultDAO surveyResultDAO);

	/**
	 * Sets {@link SurveyDAO} data access object which is used by this service.
	 * 
	 * @param surveyDAO
	 *            data access object for surveyResult
	 */
	public void setSurveyDAO(SurveyDAO surveyDAO);

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
	 * Sets {@link UserDAO} data access object to be used by this service.
	 * 
	 * @param userDAO
	 *            data access object for answers entities
	 */
	public void setUserDAO(UserDAO userDAO);

	/**
	 * This method gets all user's surveys.
	 * 
	 * @param user
	 *            user
	 * 
	 * @return list of user's surveys
	 */
	public List<Survey> getSurveys(User user);

	/**
	 * This method gets all user's surveys.
	 * 
	 * @param userId
	 *            user's id
	 * 
	 * @return list of user's surveys
	 */
	public List<Survey> getSurveys(long userId);

	/**
	 * This method gets all results for question in survey.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return list of results
	 */
	public List<SurveyResult> getResults(Question question);

	/**
	 * This method gets all results for question in survey.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return list of results
	 */
	public List<SurveyResult> getResults(long questionId);

	/**
	 * This method gets top n results. If n < 0 then all sorted results are
	 * returned.
	 * 
	 * @param allResults
	 *            results
	 * @param n
	 *            amount of results
	 * 
	 * @return list of n text results with amounts
	 */
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResultsFromResults(
			List<SurveyResult> allResults, int n);

	/**
	 * This method returns results for given {@link Form}.
	 * 
	 * @param allResults
	 *            {@link List} of {@link SurveyResult} objects
	 * @param form
	 *            {@link Form} object
	 * @return list of results
	 */
	public List<Pair<String, Pair<Integer, Double>>> getSingleTextResults(
			Question question, Form form);

	/**
	 * This method gets top n results with amounts for open question in survey.
	 * If n < 0 then all sorted results are returned.
	 * 
	 * @param question
	 *            question
	 * @param n
	 *            amount of results
	 * 
	 * @return list of n text results with amounts
	 */
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResults(
			Question question, int n);

	/**
	 * This method gets top n results with amounts for open question in survey.
	 * 
	 * @param questionId
	 *            question's Id
	 * @param n
	 *            amount of results
	 * 
	 * @return list of n text results with amounts
	 */
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResults(
			long questionId, int n);

	/**
	 * This method gets top n results with amounts for open questions in
	 * surveys. If n < 0 then all sorted results are returned.
	 * 
	 * @param questions
	 *            list of equal questions
	 * @param n
	 *            amount of results
	 * 
	 * @return list of n text results with amounts
	 */
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResults(
			List<Question> questions, int n);

	/**
	 * This method gets all questions from survey.
	 * 
	 * @param survey
	 *            survey
	 * 
	 * @return list of questions
	 */
	public List<Question> getQuestions(Survey survey);

	/**
	 * This method gets all questions from survey.
	 * 
	 * @param surveyId
	 *            survey's id
	 * 
	 * @return list of questions
	 */
	public List<Question> getQuestions(long surveyId);

	/**
	 * This method gets all answers for question in survey (answer must exist at
	 * least ones in SurveyResults).
	 * 
	 * @param question
	 *            question
	 * 
	 * @return list of answers
	 */
	public List<Answer> getAnswers(Question question);

	/**
	 * This method gets all answers for question in survey (answer must exist at
	 * least ones in SurveyResults).
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return list of answers
	 */
	public List<Answer> getAnswers(long questionId);

	/**
	 * This method gets all possible answers for question in survey (answer
	 * needn't exist in SurveyResults, answer should only exists in database
	 * with question column set for suitable value).
	 * 
	 * @param question
	 *            question
	 * 
	 * @return list of answers
	 */
	public List<Answer> getPossibleAnswers(Question question);

	/**
	 * This method gets all possible answers for question in survey (answer
	 * needn't exist in SurveyResults, answer should only exists in database
	 * with question column set for suitable value).
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return list of answers
	 */
	public List<Answer> getPossibleAnswers(long questionId);

	/**
	 * This method gets minimum from all answers for this question Creates
	 * result for given question by asociating it with specified answer. This
	 * method is working for closed-answer questions only.
	 * 
	 * @param form
	 *            survey form for which this result is answer
	 * @param question
	 *            question entity for which result is created
	 * @param answer
	 *            answer which seleceted as question answer
	 * 
	 * @return created result entity
	 */
	public SurveyResult createResult(Form form, Question question, Answer answer);

	/**
	 * Creates result for given question by asociating it with specified answer.
	 * This method is working for open-answer questions only.
	 * 
	 * @param form
	 *            survey form for which this result is answer
	 * @param question
	 *            question entity for which result is created
	 * @param answerValue
	 *            literal value of answer
	 * 
	 * @return created result entity
	 */
	public SurveyResult createResult(Form form, Question question,
			String answerValue);

	/**
	 * This method gets minimum from all answers
	 * 
	 * @param answers
	 *            list of answers
	 * 
	 * @return minimum from answers
	 */
	public Double getMinFromAnswers(List<Answer> answers);

	/**
	 * This method gets minimum from all answers for this question
	 * 
	 * @param question
	 *            question
	 * 
	 * @return minimum from answers
	 */
	public Double getMin(Question question);

	/**
	 * This method gets minimum from all answers for this question.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return minimum from answers
	 */
	public Double getMin(long questionId);

	/**
	 * This method gets minimum from all answers for this questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return minimum from answers
	 */
	public Double getMin(List<Question> questions);

	/**
	 * This method gets maximum from all answers
	 * 
	 * @param answers
	 *            list of answers
	 * 
	 * @return maximum from answers
	 */
	public Double getMaxFromAnswers(List<Answer> answers);

	/**
	 * This method gets maximum from all answers for this question.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return maximum from answers
	 */
	public Double getMax(Question question);

	/**
	 * This method gets maximum from all answers for this question.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return maximum from answers
	 */
	public Double getMax(long questionId);

	/**
	 * This method gets maximum from all answers for this questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return maximum from answers
	 */
	public Double getMax(List<Question> questions);

	/**
	 * This method gets average from all answers
	 * 
	 * @param answers
	 *            list of answers
	 * 
	 * @return average from answers
	 */
	public Double getAverageFromAnswers(List<Answer> answers);

	/**
	 * This method gets average from all answers for this question.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return average from answers
	 */
	public Double getAverage(Question question);

	/**
	 * This method gets average from all answers for this question.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return average from answers
	 */
	public Double getAverage(long questionId);

	/**
	 * This method gets average from all answers
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return average from answers
	 */
	public Double getAverage(List<Question> questions);

	/**
	 * This method gets weighted-average from all results
	 * 
	 * @param results
	 *            list of survey's results
	 * 
	 * @return weighted-average from results
	 */
	public Double getWeightedAverageFromResults(List<SurveyResult> results);

	/**
	 * This method gets weighted-average from all answers for this question.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return weighted-average from answers
	 */
	public Double getWeightedAverage(Question question);

	/**
	 * This method gets weighted-average from all answers for this question.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return weighted-average from answers
	 */
	public Double getWeightedAverage(long questionId);

	/**
	 * This method gets weighted-average from all answers
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return weighted-average from answers
	 */
	public Double getWeightedAverage(List<Question> questions);

	/**
	 * This method gets median from all answers
	 * 
	 * @param answers
	 *            list of answers
	 * 
	 * @return median from answers
	 */
	public Double getMedianFromAnswers(List<Answer> answers);

	/**
	 * This method gets median from all answers for this question.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return median from answers
	 */
	public Double getMedian(Question question);

	/**
	 * This method gets median from all answers for this question.
	 * 
	 * @param questionId
	 *            question's
	 * 
	 * @return median from answers
	 */
	public Double getMedian(long questionId);

	/**
	 * This method gets median from all answers for this questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return median from answers
	 */
	public Double getMedian(List<Question> questions);

	/**
	 * This method gets median from all results
	 * 
	 * @param results
	 *            list of survey's results
	 * 
	 * @return median from results
	 */
	public Double getMedianWithRepeatingsFromResults(List<SurveyResult> results);

	/**
	 * This method gets median from all answers for this question if answer is
	 * repeated more than once it's included more times.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return median from answers
	 */
	public Double getMedianWithRepeatings(Question question);

	/**
	 * This method gets median from all answers for this question if answer is
	 * repeated more than once it's included more times.
	 * 
	 * @param questionId
	 *            question's
	 * 
	 * @return median from answers
	 */
	public Double getMedianWithRepeatings(long questionId);

	/**
	 * This method gets median from all answers for this questions if answer is
	 * repeated more than once it's included more times
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return median from answers
	 */
	public Double getMedianWithRepeatings(List<Question> questions);

	/**
	 * This method gets answer's number.
	 * 
	 * @param question
	 *            question
	 * 
	 * @param answer
	 *            answer
	 * 
	 * @return answer's number
	 */
	public Integer getQuantity(Question question, Answer answer);

	/**
	 * This method gets answer's number.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @param answerId
	 *            answer's id
	 * 
	 * @return answer's number
	 */
	public Integer getQuantity(long questionId, long answerId);

	/**
	 * This method gets answers' number.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return map of answers' number
	 */
	public Map<Answer, Integer> getQuantity(Question question);

	/**
	 * This method gets answers' number.
	 * 
	 * @param question
	 *            question
	 * @param form
	 *            {@link Form} object
	 * 
	 * @return map of answers' number
	 */
	public Map<Answer, Integer> getQuantity(Question question, Form form);

	/**
	 * This method gets answers' number.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return map of answers' number
	 */
	public Map<Answer, Integer> getQuantity(long questionId);

	/**
	 * This method gets all answers' number (sum).
	 * 
	 * @param question
	 *            question
	 * 
	 * @return all answers' number (sum)
	 */
	public Integer getAllAnswersQuantity(Question question);

	/**
	 * This method gets all answers' number (sum).
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return all answers' number (sum)
	 */
	public Integer getAllAnswersQuantity(long questionId);

	/**
	 * This method gets all answers' number (sum).
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return all answers' number (sum)
	 */
	public Integer getAllAnswersQuantity(List<Question> questions);

	/**
	 * This method gets most popular answers for this question.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return list of most popular answer
	 */
	public List<Answer> getMostPopularAnswers(Question question);

	/**
	 * This method gets most popular answers for this question (identified by
	 * id).
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return list of most popular answers
	 */
	public List<Answer> getMostPopularAnswers(long questionId);

	/**
	 * This method gets least popular answers for this question.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return list of least popular answers
	 */
	public List<Answer> getLeastPopularAnswers(Question question);

	/**
	 * This method gets least popular answers for this question (identified by
	 * id).
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return list of least popular answers
	 */
	public List<Answer> getLeastPopularAnswers(long questionId);

	/**
	 * This method gets answer's frequency.
	 * 
	 * @param question
	 *            question
	 * 
	 * @param answer
	 *            answer
	 * 
	 * @return answer's frequency
	 */
	public Double getFrequency(Question question, Answer answer);

	/**
	 * This method gets answer's frequency.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @param answerId
	 *            answer's id
	 * 
	 * @return answer's frequency
	 */
	public Double getFrequency(long questionId, long answerId);

	/**
	 * This method gets answers' frequency.
	 * 
	 * @param question
	 *            question
	 * 
	 * @return map of answers' frequency
	 */
	public Map<Answer, Double> getFrequency(Question question);

	/**
	 * This method gets answers' frequency.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return map of answers' frequency
	 */
	public Map<Answer, Double> getFrequency(long questionId);

	/**
	 * This method gets question from id.
	 * 
	 * @param questionId
	 *            question's id
	 * 
	 * @return question
	 */
	public Question getQuestion(long questionId);

	/**
	 * This method checks identity of answers
	 * 
	 * @param type
	 *            question type
	 * 
	 * @param answer1
	 *            first answer
	 * 
	 * @param answer2
	 *            second answer
	 * 
	 * @return true if answers are equal
	 */
	public boolean areAnswersEqual(QuestionType type, Answer answer1,
			Answer answer2);

	/**
	 * This method checks identity of answers
	 * 
	 * @param type
	 *            question type
	 * 
	 * @param answer1Id
	 *            first answer Id
	 * 
	 * @param answer2Id
	 *            second answer Id
	 * 
	 * @return true if answers are equal
	 */
	public boolean areAnswersEqual(QuestionType type, long answerId1,
			long answerId2);

	/**
	 * This method checks identity of questions
	 * 
	 * @param question1
	 *            first question
	 * 
	 * @param question2
	 *            second question
	 * 
	 * @return true if questions are equal
	 */
	public boolean areQuestionsEqual(Question question1, Question question2);

	/**
	 * This method checks identity of questions
	 * 
	 * @param questionId1
	 *            first question's id
	 * 
	 * @param questionId2
	 *            second question's id
	 * 
	 * @return true if questions are equal
	 */
	public boolean areQuestionsEqual(long questionId1, long questionId2);

	/**
	 * This method gets all equal questions from surveys
	 * 
	 * @param surveys
	 *            list of surveys
	 * 
	 * @return list of equal questions' list from all surveys (each element of
	 *         list is list on witch all questions are equal)
	 */
	public List<List<Question>> getEqualQuestions(List<Survey> surveys);

	/**
	 * This method gets all answers for equal questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return list of answers' list (each element of list is list of answers
	 *         for single question)
	 */
	public List<List<Answer>> getPossibleAnswers(List<Question> questions);

	/**
	 * This method gets list with all answers for questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return list of answers
	 * 
	 */
	public List<Answer> getAnswers(List<Question> questions);

	/**
	 * This method gets list with all results for questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return list of results
	 * 
	 */
	public List<SurveyResult> getResults(List<Question> questions);

	/**
	 * This method gets list with all results for questions
	 * 
	 * @param question
	 *            {@link Question} object
	 * @param form
	 *            {@link Form} object
	 * 
	 * @return list of results
	 * 
	 */
	public List<SurveyResult> getResults(Question question, Form form);

	/**
	 * This method gets quantity trends for equal questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return map with quantity trends for answers
	 */
	public Map<Answer, List<Integer>> getQuantityTrend(List<Question> questions);

	/**
	 * This method gets frequency trends for equal questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return map with frequency trends for answers
	 */
	public Map<Answer, List<Double>> getFrequencyTrend(List<Question> questions);

	/**
	 * This method gets sum of quantities for equal questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return sum of quantities
	 */
	public Map<Answer, Integer> getQuantitiesSum(List<Question> questions);

	/**
	 * This method gets average of frequencies for equal questions
	 * 
	 * @param questions
	 *            list of questions
	 * 
	 * @return average of frequencies
	 */
	public Map<Answer, Double> getFrequencyFromSum(List<Question> questions);

	/**
	 * This method converts Double format to String with two digits precision
	 * (format ##.##)
	 * 
	 * @param val
	 *            value to convert
	 * @return converted string
	 */
	public String twoDigitsFormat(Double val);
}
