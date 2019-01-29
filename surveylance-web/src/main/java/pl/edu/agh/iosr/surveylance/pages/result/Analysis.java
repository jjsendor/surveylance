package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import com.google.gdata.util.common.base.Pair;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class Analysis {
	@Inject
	private SurveyManager surveyManager;

	@Inject
	private SurveyResultManager surveyResultManager;

	@Property
	private Question question;

	@Property
	private Answer answer;

	@Property
	private List<Question> questions;

	private Pair<String, Pair<Integer, Double>> result;

	@SuppressWarnings("unused")
	@Property
	private Pair<Integer, Double> quantityWithFrequency;

	@Inject
	private Request request;

	private List<List<Question>> equalQuestions;

	@Property
	private List<Question> equalQuestionsList;

	@Property
	private List<Question> firstEqualQuestions;

	private Map<Question, Map<Answer, Integer>> quantitiesSum;
	private Map<Question, Map<Answer, Double>> frequenciesFromSum;

	@Property
	private List<Integer> trendNumber;

	@Property
	private Integer currentTrend;

	private Map<Question, Map<Answer, List<Integer>>> quantitiesTrend;
	private Map<Question, Map<Answer, List<Double>>> frequenciesTrend;
	private Map<Question, Survey> questionsSurveys;

	public Pair<String, Pair<Integer, Double>> getResult() {
		return result;
	}

	public void setResult(Pair<String, Pair<Integer, Double>> result) {
		this.result = result;
		quantityWithFrequency = result.getSecond();
	}

	public boolean isOpenQuestion() {
		return question.getKind() == QuestionKind.INPUT_TEXT;
	}

	public boolean isEqualQuestionsToAnalyse() {
		return !equalQuestionsList.isEmpty();
	}

	public boolean isNormalQuestionsToAnalyse() {
		return !questions.isEmpty();
	}

	public Survey getSurvey() {
		Survey answer = null;
		for (Question q : questionsSurveys.keySet())
			if (q.getId().equals(question.getId()))
				answer = questionsSurveys.get(q);

		return answer;
	}

	public Survey getNextSurvey() {
		Question nextQuestion = null;

		for (List<Question> tmpList : equalQuestions)
			for (Question tmpQ : tmpList)
				if (tmpQ.getId().equals(question.getId()))
					nextQuestion = tmpList.get(tmpList.indexOf(tmpQ) + 1);

		Survey answer = null;
		for (Question q : questionsSurveys.keySet())
			if (q.getId().equals(nextQuestion.getId()))
				answer = questionsSurveys.get(q);

		return answer;
	}

	void onActivate() {
		String surveysIds = request.getParameter("ids");
		if (surveysIds != null) {
			questions = new ArrayList<Question>();

			List<Survey> surveys = new ArrayList<Survey>();
			String[] idsStrings = surveysIds.split(",");

			for (String idString : idsStrings) {
				try {
					Long id = Long.valueOf(idString);
					surveys.add(surveyManager.getSurvey(id));
				} catch (NumberFormatException ex) {
					// do nothing
				}
			}

			if (surveys.size() > 1) {
				equalQuestions = surveyResultManager.getEqualQuestions(surveys);
				equalQuestionsList = new ArrayList<Question>();
				for (List<Question> tmpList : equalQuestions)
					for (Question tmpQuestion : tmpList)
						equalQuestionsList.add(tmpQuestion);

				questionsSurveys = new HashMap<Question, Survey>();
				trendNumber = new ArrayList<Integer>();
				int trend = 0;
				for (Survey survey : surveys) {
					if (trend != 0)
						trendNumber.add(trend);
					trend++;

					for (Question question : surveyResultManager
							.getQuestions(survey)) {
						boolean isEqual = false;

						for (Question tmpQuestion : equalQuestionsList)
							if (question.getId().equals(tmpQuestion.getId()))
								isEqual = true;

						if (!isEqual)
							questions.add(question);

						questionsSurveys.put(question, survey);
					}
				}

				firstEqualQuestions = new ArrayList<Question>();
				for (List<Question> tmpList : equalQuestions)
					firstEqualQuestions.add(tmpList.get(0));

				quantitiesSum = new HashMap<Question, Map<Answer, Integer>>();
				frequenciesFromSum = new HashMap<Question, Map<Answer, Double>>();
				quantitiesTrend = new HashMap<Question, Map<Answer, List<Integer>>>();
				frequenciesTrend = new HashMap<Question, Map<Answer, List<Double>>>();
				for (Question firstQuestion : firstEqualQuestions) {
					Map<Answer, Integer> qSum = surveyResultManager
							.getQuantitiesSum(equalQuestions
									.get(firstEqualQuestions
											.indexOf(firstQuestion)));
					quantitiesSum.put(firstQuestion, qSum);

					Map<Answer, Double> fSum = surveyResultManager
							.getFrequencyFromSum(equalQuestions
									.get(firstEqualQuestions
											.indexOf(firstQuestion)));
					frequenciesFromSum.put(firstQuestion, fSum);

					Map<Answer, List<Integer>> qTrend = surveyResultManager
							.getQuantityTrend(equalQuestions
									.get(firstEqualQuestions
											.indexOf(firstQuestion)));
					quantitiesTrend.put(firstQuestion, qTrend);

					Map<Answer, List<Double>> fTrend = surveyResultManager
							.getFrequencyTrend(equalQuestions
									.get(firstEqualQuestions
											.indexOf(firstQuestion)));
					frequenciesTrend.put(firstQuestion, fTrend);
				}
			} else {
				questions = surveyResultManager.getQuestions(surveys.get(0));
				equalQuestionsList = new ArrayList<Question>();
				firstEqualQuestions = new ArrayList<Question>();
			}
		} else {
			questions = new ArrayList<Question>();
			equalQuestionsList = new ArrayList<Question>();
			firstEqualQuestions = new ArrayList<Question>();
		}
	}

	public List<Answer> getAnswers() {
		return surveyResultManager.getPossibleAnswers(question);
	}

	public String getAnswerLiteralValue() {
		if (question.getType() == QuestionType.NUMERIC)
			return ((NumericAnswer) answer).getValue().toString();

		return ((TextAnswer) answer).getValue();
	}

	public String getFrequency() {
		Double frequency = surveyResultManager.getFrequency(question, answer) * 100;
		return surveyResultManager.twoDigitsFormat(frequency);
	}

	public String getFrequenciesFromSum() {
		Map<Answer, Double> sum = frequenciesFromSum.get(question);
		Double fSum = 0.0;
		for (Answer tmpAnswer : sum.keySet())
			if (tmpAnswer.getId().equals(answer.getId()))
				fSum = sum.get(tmpAnswer);

		return surveyResultManager.twoDigitsFormat(fSum * 100);

	}

	public Integer getQuantity() {
		return surveyResultManager.getQuantity(question, answer);
	}

	public Integer getQuantitiesSum() {
		Map<Answer, Integer> sum = quantitiesSum.get(question);
		Integer qSum = 0;
		for (Answer tmpAnswer : sum.keySet())
			if (tmpAnswer.getId().equals(answer.getId()))
				qSum = sum.get(tmpAnswer);

		return qSum;
	}

	/**
	 * returns two values - amount of span units from left side and absolute
	 * value of quantities trend
	 * 
	 * @return pair of amount of units and abs(trend)
	 */
	public Integer getQuantitiesTrend() {
		Map<Answer, List<Integer>> qTrend = quantitiesTrend.get(question);
		Integer trend = 0;
		for (Answer tmpAnswer : qTrend.keySet())
			if (tmpAnswer.getId().equals(answer.getId())) {
				List<Integer> trends = qTrend.get(tmpAnswer);
				trend = trends.get(currentTrend);
			}

		return trend;
	}

	public Double getFrequenciesTrend() {
		Map<Answer, List<Double>> fTrend = frequenciesTrend.get(question);
		Double trend = 0.0;
		for (Answer tmpAnswer : fTrend.keySet())
			if (tmpAnswer.getId().equals(answer.getId())) {
				List<Double> trends = fTrend.get(tmpAnswer);
				trend = new Double(trends.get(currentTrend) * 100);
			}

		return trend;
	}
	
	public String getFormattedFrequenciesTrend(){
		return surveyResultManager.twoDigitsFormat(getFrequenciesTrend());
	}

	public Pair<Integer, Integer> getBargraphQuantitiesTrend() {
		int trend = getQuantitiesTrend();
		
		int offset = trend < 0 ? 50 + trend : 50;

		return new Pair<Integer, Integer>(offset, Math.abs(trend));
	}

	public Pair<Double, String> getBargraphFrequenciesTrend() {
		double trend = getFrequenciesTrend();

		double offset = trend < 0 ? 50 + trend : 50;
		String length = surveyResultManager
				.twoDigitsFormat(Math.abs(trend) / 2);
		return new Pair<Double, String>(offset, length);
	}

	public Integer getAllAnswerQuantity() {
		return surveyResultManager.getAllAnswersQuantity(question);
	}

	public Integer getAllAnswerQuantityEqualQ() {
		return surveyResultManager.getAllAnswersQuantity(equalQuestions
				.get(firstEqualQuestions.indexOf(question)));
	}

	public List<Pair<String, Pair<Integer, Double>>> getTopResults() {
		return surveyResultManager.getTopTextResults(question, 5);
	}

	public List<Pair<String, Pair<Integer, Double>>> getTopResultsEqualQ() {
		return surveyResultManager.getTopTextResults(equalQuestions
				.get(firstEqualQuestions.indexOf(question)), 5);
	}
}