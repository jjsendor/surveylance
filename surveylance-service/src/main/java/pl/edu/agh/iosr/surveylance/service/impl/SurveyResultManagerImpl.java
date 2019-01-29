package pl.edu.agh.iosr.surveylance.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gdata.util.common.base.Pair;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyResultDAO;
import pl.edu.agh.iosr.surveylance.dao.UserDAO;
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

/**
 * Implements {@link SurveyResultManager} interface.
 * 
 * @author michal
 * @author kornel
 */
public class SurveyResultManagerImpl implements SurveyResultManager {

	private ComponentDAO componentDAO = null;
	private AnswerDAO answerDAO = null;
	private QuestionDAO questionDAO = null;
	private SurveyDAO surveyDAO = null;
	private SurveyResultDAO surveyResultDAO = null;
	private UserDAO userDAO = null;

	Logger logger = Logger.getLogger(this.getClass());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComponentDAO(ComponentDAO componentDAO) {
		this.componentDAO = componentDAO;
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
	public void setQuestionDAO(QuestionDAO questionDAO) {
		this.questionDAO = questionDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSurveyDAO(SurveyDAO surveyDAO) {
		this.surveyDAO = surveyDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSurveyResultDAO(SurveyResultDAO surveyResultDAO) {
		this.surveyResultDAO = surveyResultDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Survey> getSurveys(User user) {
		return surveyDAO.findByOwner(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Survey> getSurveys(long userId) {
		User user = userDAO.findById(userId, false);
		return getSurveys(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SurveyResult> getResults(Question question) {
		return surveyResultDAO.findByQuestion(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SurveyResult> getResults(Question question, Form form) {
		return surveyResultDAO.findByQuestionAndForm(question, form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SurveyResult> getResults(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getResults(question);
	}

	@Override
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResultsFromResults(
			List<SurveyResult> allResults, int n) {
		Map<String, Integer> textResults = new HashMap<String, Integer>();

		for (SurveyResult res : allResults) {
			String textAnswer = res.getTextAnswer();
			if (textResults.containsKey(textAnswer)) {
				textResults.put(textAnswer, textResults.get(textAnswer) + 1);
			} else {
				textResults.put(textAnswer, 1);
			}

		}

		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(
				textResults.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});

		if (n >= 0 && entries.size() >= n)
			entries = entries.subList(0, n);

		List<Pair<String, Pair<Integer, Double>>> topTextResults = new ArrayList<Pair<String, Pair<Integer, Double>>>();

		for (Entry<String, Integer> entry : entries) {
			Integer quantity = entry.getValue();
			Double frequency = (double) quantity / allResults.size();
			Pair<Integer, Double> newPair = new Pair<Integer, Double>(quantity,
					frequency);
			Pair<String, Pair<Integer, Double>> topTextPair = new Pair<String, Pair<Integer, Double>>(
					entry.getKey(), newPair);
			topTextResults.add(topTextPair);

		}

		return topTextResults;
	}

	/** {@inheritDoc} */
	@Override
	public List<Pair<String, Pair<Integer, Double>>> getSingleTextResults(
			Question question, Form form) {
		List<SurveyResult> allResults = surveyResultDAO.findByQuestionAndForm(
				question, form);
		return getTopTextResultsFromResults(allResults, 1);
	}

	@Override
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResults(
			Question question, int n) {
		List<SurveyResult> allResults = surveyResultDAO
				.findByQuestion(question);
		return getTopTextResultsFromResults(allResults, n);
	}

	@Override
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResults(
			long questionId, int n) {
		Question question = questionDAO.findById(questionId, false);
		return getTopTextResults(question, n);
	}

	@Override
	public List<Pair<String, Pair<Integer, Double>>> getTopTextResults(
			List<Question> questions, int n) {
		List<SurveyResult> allResults = getResults(questions);
		return getTopTextResultsFromResults(allResults, n);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Question> getQuestions(Survey survey) {
		List<Question> all = questionDAO.findAll();
		List<Question> questions = new ArrayList<Question>();

		if (survey != null)
			for (Iterator<Question> it = all.iterator(); it.hasNext();) {
				Question question = it.next();
				if (question.getParentComponent() != null) {
					Component root = componentDAO.findRootComponent(question
							.getParentComponent());
					Survey persistentSurvey = surveyDAO
							.findByRootComponent(root);
					if (root != null
							&& persistentSurvey != null
							&& survey.getId().longValue() == persistentSurvey
									.getId().longValue()) {
						questions.add(question);
					}
				}
			}

		return questions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Question> getQuestions(long surveyId) {
		Survey survey = surveyDAO.findById(surveyId, false);
		return getQuestions(survey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getAnswers(Question question) {
		Set<Answer> answers = new HashSet<Answer>();

		List<SurveyResult> results = getResults(question);

		for (Iterator<SurveyResult> it = results.iterator(); it.hasNext();)
			answers.add(it.next().getAnswer());

		return new ArrayList<Answer>(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getAnswers(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getAnswers(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getPossibleAnswers(Question question) {
		return answerDAO.findByQuestion(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getPossibleAnswers(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getPossibleAnswers(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SurveyResult createResult(Form form, Question question, Answer answer) {
		SurveyResult result = new SurveyResult();

		result.setForm(form);
		result.setQuestion(question);
		result.setAnswer(answer);

		surveyResultDAO.create(result);

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SurveyResult createResult(Form form, Question question,
			String answerValue) {
		SurveyResult result = new SurveyResult();

		result.setForm(form);
		result.setQuestion(question);
		result.setTextAnswer(answerValue);

		surveyResultDAO.create(result);

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMinFromAnswers(List<Answer> answers) {
		Double min = null;
		for (Iterator<Answer> it = answers.iterator(); it.hasNext();) {
			Answer next = it.next();
			if (next instanceof NumericAnswer) {
				NumericAnswer numeric = (NumericAnswer) next;
				if (min == null)
					min = numeric.getValue();
				else if (numeric.getValue() < min)
					min = numeric.getValue();
			}
		}

		return min;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMin(Question question) {
		List<Answer> answers = getAnswers(question);

		return getMinFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMin(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getMin(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMin(List<Question> questions) {
		List<Answer> answers = getAnswers(questions);

		return getMinFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMaxFromAnswers(List<Answer> answers) {
		Double max = null;
		for (Iterator<Answer> it = answers.iterator(); it.hasNext();) {
			Answer next = it.next();
			if (next instanceof NumericAnswer) {
				NumericAnswer numeric = (NumericAnswer) next;
				if (max == null)
					max = numeric.getValue();
				else if (numeric.getValue() > max)
					max = numeric.getValue();
			}
		}

		return max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMax(Question question) {
		List<Answer> answers = getAnswers(question);

		return getMaxFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMax(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getMax(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMax(List<Question> questions) {
		List<Answer> answers = getAnswers(questions);

		return getMaxFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getAverageFromAnswers(List<Answer> answers) {
		int sum = 0;
		int counter = 0;
		for (Iterator<Answer> it = answers.iterator(); it.hasNext();) {
			Answer next = it.next();
			if (next instanceof NumericAnswer) {
				NumericAnswer numeric = (NumericAnswer) next;
				sum += numeric.getValue();
				counter++;
			}
		}

		return (double) sum / (double) counter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getAverage(Question question) {
		List<Answer> answers = getAnswers(question);

		return getAverageFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getAverage(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getAverage(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getAverage(List<Question> questions) {
		List<Answer> answers = getAnswers(questions);

		return getAverageFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getWeightedAverageFromResults(List<SurveyResult> results) {
		int sum = 0;
		int counter = 0;
		for (Iterator<SurveyResult> it = results.iterator(); it.hasNext();) {
			Answer next = it.next().getAnswer();
			if (next instanceof NumericAnswer) {
				NumericAnswer numeric = (NumericAnswer) next;
				sum += numeric.getValue();
				counter++;
			}
		}

		return (double) sum / (double) counter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getWeightedAverage(Question question) {
		List<SurveyResult> results = getResults(question);

		return getWeightedAverageFromResults(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getWeightedAverage(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getWeightedAverage(question);
	}

	@Override
	public Double getWeightedAverage(List<Question> questions) {
		List<SurveyResult> results = getResults(questions);

		return getWeightedAverageFromResults(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedianFromAnswers(List<Answer> answers) {
		List<Double> values = new ArrayList<Double>();
		for (Iterator<Answer> it = answers.iterator(); it.hasNext();) {
			Answer next = it.next();
			if (next instanceof NumericAnswer)
				values.add(((NumericAnswer) next).getValue());
		}

		Collections.sort(values);
		int medianPosition = values.size() / 2;
		if (2 * medianPosition != values.size())
			medianPosition++;

		return values.get(medianPosition - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedian(Question question) {
		List<Answer> answers = getAnswers(question);

		return getMedianFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedian(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getMedian(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedian(List<Question> questions) {
		List<Answer> answers = getAnswers(questions);

		return getMedianFromAnswers(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedianWithRepeatingsFromResults(List<SurveyResult> results) {
		List<Double> values = new ArrayList<Double>();
		for (Iterator<SurveyResult> it = results.iterator(); it.hasNext();) {
			Answer next = it.next().getAnswer();
			if (next instanceof NumericAnswer)
				values.add(((NumericAnswer) next).getValue());
		}

		Collections.sort(values);
		int medianPosition = values.size() / 2;
		if (2 * medianPosition != values.size())
			medianPosition++;

		return values.get(medianPosition - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedianWithRepeatings(Question question) {
		List<SurveyResult> results = getResults(question);

		return getMedianWithRepeatingsFromResults(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedianWithRepeatings(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getMedianWithRepeatings(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getMedianWithRepeatings(List<Question> questions) {
		List<SurveyResult> results = getResults(questions);

		return getMedianWithRepeatingsFromResults(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getQuantity(Question question, Answer answer) {
		List<SurveyResult> results = getResults(question);
		int quantity = 0;

		if (answer != null)
			for (Iterator<SurveyResult> it = results.iterator(); it.hasNext();)
				if (answer.getId().longValue() == it.next().getAnswer().getId()
						.longValue())
					quantity++;

		return quantity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getQuantity(long questionId, long answerId) {
		Question question = questionDAO.findById(questionId, false);
		Answer answer = answerDAO.findById(answerId, false);
		return getQuantity(question, answer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Integer> getQuantity(Question question) {
		List<SurveyResult> results = getResults(question);
		Map<Answer, Integer> quantities = new HashMap<Answer, Integer>();

		for (Iterator<SurveyResult> it = results.iterator(); it.hasNext();) {
			Answer answer = it.next().getAnswer();
			if (answer != null) {
				Integer quantity = quantities.get(answer);

				if (quantity == null)
					quantities.put(answer, 1);
				else
					quantities.put(answer, quantity + 1);
			}
		}

		return quantities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Integer> getQuantity(Question question, Form form) {
		List<SurveyResult> results = getResults(question, form);
		Map<Answer, Integer> quantities = new HashMap<Answer, Integer>();

		for (Iterator<SurveyResult> it = results.iterator(); it.hasNext();) {
			Answer answer = it.next().getAnswer();
			if (answer != null) {
				Integer quantity = quantities.get(answer);

				if (quantity == null)
					quantities.put(answer, 1);
				else
					quantities.put(answer, quantity + 1);
			}
		}

		return quantities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Integer> getQuantity(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getQuantity(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAllAnswersQuantity(Question question) {
		List<SurveyResult> results = getResults(question);
		return results.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAllAnswersQuantity(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getAllAnswersQuantity(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAllAnswersQuantity(List<Question> questions) {
		List<SurveyResult> results = getResults(questions);
		return results.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getMostPopularAnswers(Question question) {
		Map<Answer, Integer> quantities = getQuantity(question);
		List<Answer> maxs = new ArrayList<Answer>();

		for (Iterator<Answer> it = quantities.keySet().iterator(); it.hasNext();) {
			Answer answer = it.next();
			if (maxs.size() == 0)
				maxs.add(answer);
			else if (quantities.get(answer) > quantities.get(maxs.get(0))) {
				maxs = new ArrayList<Answer>();
				maxs.add(answer);
			}
		}

		return maxs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getMostPopularAnswers(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getMostPopularAnswers(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getLeastPopularAnswers(Question question) {
		Map<Answer, Integer> quantities = getQuantity(question);
		List<Answer> mins = new ArrayList<Answer>();

		for (Iterator<Answer> it = quantities.keySet().iterator(); it.hasNext();) {
			Answer answer = it.next();
			if (mins.size() == 0)
				mins.add(answer);
			else if (quantities.get(answer) < quantities.get(mins.get(0))) {
				mins = new ArrayList<Answer>();
				mins.add(answer);
			} else if (quantities.get(answer) == quantities.get(mins.get(0)))
				mins.add(answer);
		}

		return mins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getLeastPopularAnswers(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getLeastPopularAnswers(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getFrequency(Question question, Answer answer) {
		return (double) getQuantity(question, answer)
				/ (double) getAllAnswersQuantity(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getFrequency(long questionId, long answerId) {
		Question question = questionDAO.findById(questionId, false);
		Answer answer = answerDAO.findById(answerId, false);
		return getFrequency(question, answer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Double> getFrequency(Question question) {
		Map<Answer, Double> frequencies = new HashMap<Answer, Double>();
		Map<Answer, Integer> quantities = getQuantity(question);
		double allAnswersQuantity = (double) getAllAnswersQuantity(question);

		Set<Answer> answers = quantities.keySet();
		for (Iterator<Answer> it = answers.iterator(); it.hasNext();) {
			Answer answer = it.next();
			frequencies.put(answer, (double) quantities.get(answer)
					/ allAnswersQuantity);
		}

		return frequencies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Double> getFrequency(long questionId) {
		Question question = questionDAO.findById(questionId, false);
		return getFrequency(question);
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
	public boolean areAnswersEqual(QuestionType type, Answer answer1,
			Answer answer2) {
		if (type.equals(QuestionType.NUMERIC))
			if (((NumericAnswer) answer1).getValue().equals(
					((NumericAnswer) answer2).getValue()))
				return true;
			else
				return false;
		else if (((TextAnswer) answer1).getValue().equals(
				((TextAnswer) answer2).getValue()))
			return true;
		else
			return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean areAnswersEqual(QuestionType type, long answerId1,
			long answerId2) {
		Answer answer1 = answerDAO.findById(answerId1, false);
		Answer answer2 = answerDAO.findById(answerId2, false);
		return areAnswersEqual(type, answer1, answer2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean areQuestionsEqual(Question question1, Question question2) {
		if (!question1.getType().equals(question2.getType()))
			return false;
		else if (!question1.getKind().equals(question2.getKind()))
			return false;
		else if (!question1.getContent().equals(question2.getContent()))
			return false;

		if (!question1.getKind().equals(QuestionKind.INPUT_TEXT)) {
			List<Answer> answers1 = getPossibleAnswers(question1);
			List<Answer> answers2 = getPossibleAnswers(question2);

			for (Answer answer1 : answers1) {
				boolean isOk = false;

				for (Answer answer2 : answers2)
					if (areAnswersEqual(question1.getType(), answer1, answer2))
						isOk = true;

				if (!isOk)
					return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean areQuestionsEqual(long questionId1, long questionId2) {
		Question question1 = questionDAO.findById(questionId1, false);
		Question question2 = questionDAO.findById(questionId2, false);
		return areQuestionsEqual(question1, question2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<List<Question>> getEqualQuestions(List<Survey> surveys) {
		List<List<Question>> answer = new ArrayList<List<Question>>();
		List<List<Question>> questions = new ArrayList<List<Question>>();
		for (Survey survey : surveys)
			questions.add(getQuestions(survey));

		if (questions.size() > 0) {
			List<Question> firstSurvey = questions.get(0);
			questions.remove(0);

			for (Question model : firstSurvey) {
				List<Question> equalQuestions = new ArrayList<Question>();
				equalQuestions.add(model);
				boolean allOk = true;

				for (List<Question> survey : questions) {
					boolean questionFound = false;
					for (Question q : survey)
						if (areQuestionsEqual(model, q)) {
							questionFound = true;
							equalQuestions.add(q);
							break;
						}

					if (!questionFound) {
						allOk = false;
						break;
					}
				}

				if (allOk)
					answer.add(equalQuestions);
			}
		}

		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<List<Answer>> getPossibleAnswers(List<Question> questions) {
		List<List<Answer>> answers = new ArrayList<List<Answer>>();

		if (questions.size() > 0) {
			Question question1 = questions.get(0);
			List<Answer> answers1 = getPossibleAnswers(question1);

			List<List<Answer>> tmpAnswers = new ArrayList<List<Answer>>();
			for (int i = 1; i < questions.size(); i++)
				tmpAnswers.add(getPossibleAnswers(questions.get(i)));

			for (Answer answer : answers1) {
				List<Answer> oneAnswer = new ArrayList<Answer>();
				oneAnswer.add(answer);

				for (List<Answer> answersFromOne : tmpAnswers)
					for (Answer answerFromOne : answersFromOne)
						if (areAnswersEqual(question1.getType(), answer,
								answerFromOne)) {
							oneAnswer.add(answerFromOne);
							break;
						}

				answers.add(oneAnswer);
			}
		}

		return answers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Answer> getAnswers(List<Question> questions) {
		Set<Answer> answers = new HashSet<Answer>();

		for (int i = 0; i < questions.size(); i++) {
			List<Answer> tmp = getAnswers(questions.get(i));
			for (Answer ans : tmp)
				answers.add(ans);
		}

		return new ArrayList<Answer>(answers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SurveyResult> getResults(List<Question> questions) {
		Set<SurveyResult> results = new HashSet<SurveyResult>();

		for (int i = 0; i < questions.size(); i++) {
			List<SurveyResult> tmp = getResults(questions.get(i));
			for (SurveyResult res : tmp)
				results.add(res);
		}

		return new ArrayList<SurveyResult>(results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, List<Integer>> getQuantityTrend(List<Question> questions) {
		Map<Answer, List<Integer>> trends = new HashMap<Answer, List<Integer>>();
		List<List<Answer>> answers = getPossibleAnswers(questions);

		for (List<Answer> answer : answers)
			if (answer.size() > 0) {
				List<Integer> trend = new ArrayList<Integer>();
				Answer firstAnswer = answer.get(0);

				for (int i = 0; i < answer.size(); i++)
					trend.add(getQuantity(questions.get(i), answer.get(i)));

				trends.put(firstAnswer, trend);
			}

		return trends;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, List<Double>> getFrequencyTrend(List<Question> questions) {
		Map<Answer, List<Double>> trends = new HashMap<Answer, List<Double>>();
		List<List<Answer>> answers = getPossibleAnswers(questions);

		for (List<Answer> answer : answers)
			if (answer.size() > 0) {
				List<Double> trend = new ArrayList<Double>();
				Answer firstAnswer = answer.get(0);

				for (int i = 0; i < answer.size(); i++)
					trend.add(getFrequency(questions.get(i), answer.get(i)));

				trends.put(firstAnswer, trend);
			}

		return trends;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Integer> getQuantitiesSum(List<Question> questions) {
		Map<Answer, Integer> sums = new HashMap<Answer, Integer>();
		List<List<Answer>> answers = getPossibleAnswers(questions);

		for (List<Answer> answer : answers)
			if (answer.size() > 0) {
				Answer firstAnswer = answer.get(0);
				int sum = getQuantity(questions.get(0), firstAnswer);

				for (int i = 1; i < answer.size(); i++)
					sum += getQuantity(questions.get(i), answer.get(i));

				sums.put(firstAnswer, sum);
			}

		return sums;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Answer, Double> getFrequencyFromSum(List<Question> questions) {
		Map<Answer, Integer> sums = new HashMap<Answer, Integer>();
		List<List<Answer>> answers = getPossibleAnswers(questions);

		for (List<Answer> answer : answers)
			if (answer.size() > 0) {
				Answer firstAnswer = answer.get(0);
				int sum = getQuantity(questions.get(0), firstAnswer);

				for (int i = 1; i < answer.size(); i++)
					sum += getQuantity(questions.get(i), answer.get(i));

				sums.put(firstAnswer, sum);
			}

		int sumAll = 0;
		Set<Answer> keys = sums.keySet();
		for (Answer key : keys)
			sumAll += sums.get(key);

		Map<Answer, Double> answer = new HashMap<Answer, Double>();
		for (Answer key : keys)
			answer.put(key, (double) sums.get(key) / sumAll);

		return answer;
	}

	@Override
	public String twoDigitsFormat(Double val) {
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(val).replace(',', '.');
	}

}
