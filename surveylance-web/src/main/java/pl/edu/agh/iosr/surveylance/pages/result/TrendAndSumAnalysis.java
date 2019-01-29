package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.google.gdata.util.common.base.Pair;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class TrendAndSumAnalysis {
	
	private static final Logger logger = Logger.getLogger(TrendAndSumAnalysis.class);

	@Inject
	private QuestionManager questionManager;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private SurveyResultManager surveyResultManager;

	@Property
	private List<Question> firstQuestions;

	private List<List<Question>> questions;

	@Property
	private Question question;

	@Property
	private List<Survey> surveys;

	@Property
	private Survey survey;

	@Property
	private Answer answer;

	private Map<Question, Map<Answer, Integer>> quantitiesSum;
	private Map<Question, Map<Answer, Double>> frequenciesFromSum;
	private Map<Question, Map<Answer, List<Integer>>> quantitiesTrend;
	private Map<Question, Map<Answer, List<Double>>> frequenciesTrend;

	@Property
	private Pair<Integer, Double> quantityWithFrequency;

	private Pair<String, Pair<Integer, Double>> result;

	@SuppressWarnings("unused")
	@Property
	private boolean quantity = false;

	@SuppressWarnings("unused")
	@Property
	private boolean frequency = false;

	@SuppressWarnings("unused")
	@Property
	private boolean sum = false;

	public List<Answer> getAnswers() {
		return surveyResultManager.getPossibleAnswers(question);
	}

	public boolean isClosedQuestion() {
		return question.getKind() != QuestionKind.INPUT_TEXT;
	}

	void onActivate(String ids) {
		String[] typeAndIds = ids.split("t");
		String[] equalStrings = typeAndIds[0].split("e");

		firstQuestions = new ArrayList<Question>();
		questions = new ArrayList<List<Question>>();
		for (String equalString : equalStrings) {
			String[] idsStrings = equalString.split("q");

			List<Question> tmpQuestions = new ArrayList<Question>();
			boolean first = true;
			for (String idString : idsStrings)
				try {
					Long id = Long.valueOf(idString);
					Question q = questionManager.getQuestion(id);
					tmpQuestions.add(q);
					if (first) {
						first = false;
						firstQuestions.add(q);
					}
				} catch (NumberFormatException ex) {
					// do nothing
				}
			questions.add(tmpQuestions);
		}

		quantitiesSum = new HashMap<Question, Map<Answer, Integer>>();
		frequenciesFromSum = new HashMap<Question, Map<Answer, Double>>();
		quantitiesTrend = new HashMap<Question, Map<Answer, List<Integer>>>();
		frequenciesTrend = new HashMap<Question, Map<Answer, List<Double>>>();
		for (Question firstQuestion : firstQuestions) {
			List<Question> tmpList = questions.get(firstQuestions
					.indexOf(firstQuestion));

			Map<Answer, Integer> qSum = surveyResultManager
					.getQuantitiesSum(tmpList);
			quantitiesSum.put(firstQuestion, qSum);

			Map<Answer, Double> fSum = surveyResultManager
					.getFrequencyFromSum(tmpList);
			frequenciesFromSum.put(firstQuestion, fSum);

			Map<Answer, List<Integer>> qTrend = surveyResultManager
					.getQuantityTrend(tmpList);
			quantitiesTrend.put(firstQuestion, qTrend);

			Map<Answer, List<Double>> fTrend = surveyResultManager
					.getFrequencyTrend(tmpList);
			frequenciesTrend.put(firstQuestion, fTrend);
		}

		surveys = new ArrayList<Survey>();
		for (Question q : questions.get(0))
			surveys.add(surveyManager.getSurvey(q));

		for (int i = 0; i < typeAndIds[1].length(); i++)
			if (typeAndIds[1].charAt(i) == 'q')
				quantity = true;
			else if (typeAndIds[1].charAt(i) == 'f')
				frequency = true;
			else if (typeAndIds[1].charAt(i) == 's')
				sum = true;
	}

	public Integer getQuantitiesTrend() {
		Map<Answer, List<Integer>> qTrend = quantitiesTrend.get(question);
		Integer trend = 0;
		for (Answer tmpAnswer : qTrend.keySet())
			if (tmpAnswer.getId().equals(answer.getId())) {
				List<Integer> trends = qTrend.get(tmpAnswer);
				trend = trends.get(surveys.indexOf(survey));
			}

		return trend;
	}

	public Double getFrequenciesTrend() {
		Map<Answer, List<Double>> fTrend = frequenciesTrend.get(question);
		Double trend = 0.0;
		for (Answer tmpAnswer : fTrend.keySet())
			if (tmpAnswer.getId().equals(answer.getId())) {
				List<Double> trends = fTrend.get(tmpAnswer);
				trend = new Double(trends.get(surveys.indexOf(survey)) * 100);
			}

		return trend;
	}

	public String getFormattedFrequenciesTrend() {
		return surveyResultManager.twoDigitsFormat(getFrequenciesTrend());
	}

	public String getAnswerLiteralValue() {
		if (question.getType() == QuestionType.NUMERIC)
			return ((NumericAnswer) answer).getValue().toString();

		return ((TextAnswer) answer).getValue();
	}

	public String getFrequenciesFromSum() {
		Map<Answer, Double> sum = frequenciesFromSum.get(question);
		Double fSum = 0.0;
		for (Answer tmpAnswer : sum.keySet())
			if (tmpAnswer.getId().equals(answer.getId()))
				fSum = sum.get(tmpAnswer);

		return surveyResultManager.twoDigitsFormat(fSum * 100);

	}

	public Integer getQuantitiesSum() {
		Map<Answer, Integer> sum = quantitiesSum.get(question);
		Integer qSum = 0;
		for (Answer tmpAnswer : sum.keySet())
			if (tmpAnswer.getId().equals(answer.getId()))
				qSum = sum.get(tmpAnswer);

		return qSum;
	}

	public Integer getAllAnswerQuantity() {
		return surveyResultManager.getAllAnswersQuantity(questions
				.get(firstQuestions.indexOf(question)));
	}

	public List<Pair<String, Pair<Integer, Double>>> getTopResults() {
		return surveyResultManager.getTopTextResults(questions
				.get(firstQuestions.indexOf(question)), 5);
	}

	public Pair<String, Pair<Integer, Double>> getResult() {
		return result;
	}

	public void setResult(Pair<String, Pair<Integer, Double>> result) {
		this.result = result;
		quantityWithFrequency = result.getSecond();
	}

	public String getQuantityWithFrequencySecond() {
		return surveyResultManager
				.twoDigitsFormat(100 * quantityWithFrequency.second);
	}

	public List<Integer> getQuantitiesTrends() {
		Map<Answer, List<Integer>> qTrend = quantitiesTrend.get(question);

		Map<Answer, Integer> answersTrends = new HashMap<Answer, Integer>();

		for (Answer a : qTrend.keySet()) {
			answersTrends.put(a, qTrend.get(a).get(surveys.indexOf(survey)));
		}

		List<Answer> sortedAnswers = getSortedAnswers(qTrend.keySet());

		List<Integer> trends = new ArrayList<Integer>();
		for (Iterator<Answer> it = sortedAnswers.iterator(); it.hasNext();) {
			Answer answer = it.next();
			Integer quantity = answersTrends.get(answer);

			trends.add(quantity);
		}
		return trends;
	}
	
	public List<Double> getFrequenciesTrends() {
		Map<Answer, List<Double>> fTrend = frequenciesTrend.get(question);

		Map<Answer, Double> answersTrends = new HashMap<Answer, Double>();

		for (Answer a : fTrend.keySet()) {
			answersTrends.put(a, fTrend.get(a).get(surveys.indexOf(survey)));
		}

		List<Answer> sortedAnswers = getSortedAnswers(fTrend.keySet());

		List<Double> trends = new ArrayList<Double>();
		for (Iterator<Answer> it = sortedAnswers.iterator(); it.hasNext();) {
			Answer answer = it.next();
			Double frequency = answersTrends.get(answer);

			trends.add(frequency);
		}
		return trends;
	}

	public List<String> getLabels() {

		Map<Answer, List<Integer>> qTrend = quantitiesTrend.get(question);

		List<Answer> sortedAnswers = getSortedAnswers(qTrend.keySet());
		int counter = 1;

		List<String> labels = new ArrayList<String>();
		for (Iterator<Answer> it = sortedAnswers.iterator(); it.hasNext();) {
			Answer answer = it.next();

			String value = null;
			if (question.getType() == QuestionType.NUMERIC)
				value = ((NumericAnswer) answer).getValue().toString();
			else
				value = ((TextAnswer) answer).getValue();

			labels.add("{value: " + counter + ", text: \"" + value + "\"}");

			counter++;

		}
		return labels;
	}
	
	public List<Integer> getQuantitiesSums() {
		Map<Answer, Integer> sum = quantitiesSum.get(question);
		
		List<Answer> sortedAnswers = getSortedAnswers(getAnswers());
		List<Integer> quantitiesSums = new ArrayList<Integer>();
		
		for(Answer answer : sortedAnswers){
			for(Answer answerInSum : sum.keySet())
				if(answerInSum.getId().equals(answer.getId()))
					quantitiesSums.add(sum.get(answerInSum));
		}
		logger.info("sums: " + quantitiesSums);
		return quantitiesSums;
	}

	private List<Answer> getSortedAnswers(Collection<Answer> answers) {
		List<Answer> sortedAnswers = new ArrayList<Answer>(answers);
		Collections.sort(sortedAnswers, new Comparator<Answer>() {

			@Override
			public int compare(Answer arg0, Answer arg1) {
				return arg0.getId().compareTo(arg1.getId());
			}

		});

		return sortedAnswers;
	}
}
