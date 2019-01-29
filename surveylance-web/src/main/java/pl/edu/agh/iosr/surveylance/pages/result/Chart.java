package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class Chart {
	
	private static final Logger logger = Logger.getLogger(Chart.class); 
	
	@Inject
	private Request request;

	@SuppressWarnings("unused")
	@Property
	private String question;

	@Property
	private List<String> answers;

	@Property
	private List<String> labels;

	@Property
	private List<String> pieData;

	@Property
	private List<Integer> quantities;

	@Property
	private List<Double> frequencies;

	@SuppressWarnings("unused")
	@Property
	private String answer;

	@SuppressWarnings("unused")
	@Property
	private Integer quantity;

	@SuppressWarnings("unused")
	@Property
	private Double frequency;

	@Property
	private boolean numeric;

	@SuppressWarnings("unused")
	@Property
	private Double min;

	@SuppressWarnings("unused")
	@Property
	private Double max;

	@SuppressWarnings("unused")
	@Property
	private Double average;

	@SuppressWarnings("unused")
	@Property
	private Double weightedAverage;

	@SuppressWarnings("unused")
	@Property
	private Double median;

	@SuppressWarnings("unused")
	@Property
	private Integer allAnswersQuantity;

	@Property
	private List<String> leastPopular;

	@Property
	private List<String> mostPopular;

	@Inject
	private SurveyResultManager surveyResultManager;
	
	@SuppressWarnings("unused")
	@Property
	private boolean pie = false;
	
	@SuppressWarnings("unused")
	@Property
	private boolean statistics = false;
	
	void onActivate(String context) {
		
		String[] contexts = context.split("_");
		
		long questionId = Integer.parseInt(contexts[0]);
		
		String chartType = contexts[1];
		
		if(chartType == null || chartType.charAt(0) == 'p')
			pie = true;
		else if(chartType.charAt(0) == 's')
			statistics = true;
		
		
		Question q = surveyResultManager.getQuestion(questionId);
		question = q.getContent();
		Map<Answer, Integer> quantitiesMap = surveyResultManager
				.getQuantity(questionId);
		Map<Answer, Double> frequenciesMap = surveyResultManager
				.getFrequency(questionId);
		if (q.getType() == QuestionType.NUMERIC) {
			numeric = true;
			min = surveyResultManager.getMedianWithRepeatings(questionId);
			max = surveyResultManager.getMax(questionId);
			average = surveyResultManager.getAverage(questionId);
			weightedAverage = surveyResultManager
					.getWeightedAverage(questionId);
			median = surveyResultManager.getMedianWithRepeatings(questionId);
		} else
			numeric = false;

		allAnswersQuantity = surveyResultManager
				.getAllAnswersQuantity(questionId);

		List<Answer> leastPopularAnsw = surveyResultManager
				.getLeastPopularAnswers(questionId);
		leastPopular = new ArrayList<String>();
		if (numeric)
			for (Iterator<Answer> it = leastPopularAnsw.iterator(); it
					.hasNext();)
				leastPopular.add(((NumericAnswer) it.next()).getValue()
						.toString());
		else
			for (Iterator<Answer> it = leastPopularAnsw.iterator(); it
					.hasNext();)
				leastPopular.add(((TextAnswer) it.next()).getValue());

		List<Answer> mostPopularAnsw = surveyResultManager
				.getMostPopularAnswers(questionId);
		mostPopular = new ArrayList<String>();
		if (numeric)
			for (Iterator<Answer> it = mostPopularAnsw.iterator(); it.hasNext();)
				mostPopular.add(((NumericAnswer) it.next()).getValue()
						.toString());
		else
			for (Iterator<Answer> it = mostPopularAnsw.iterator(); it.hasNext();)
				mostPopular.add(((TextAnswer) it.next()).getValue());

		answers = new ArrayList<String>();
		labels = new ArrayList<String>();
		pieData = new ArrayList<String>();
		quantities = new ArrayList<Integer>();
		frequencies = new ArrayList<Double>();
		int counter = 1;

		List<Answer> sortedAnswers = new ArrayList<Answer>(quantitiesMap
				.keySet());
		Collections.sort(sortedAnswers, new Comparator<Answer>() {

			@Override
			public int compare(Answer arg0, Answer arg1) {
				if (numeric)
					return new Double(((NumericAnswer) arg0).getValue())
							.compareTo(((NumericAnswer) arg1).getValue());
				else
					return ((TextAnswer) arg0).getValue().compareToIgnoreCase(
							((TextAnswer) arg1).getValue());
			}

		});

		for (Iterator<Answer> it = sortedAnswers.iterator(); it.hasNext();) {
			Answer answer = it.next();
			Integer quantity = quantitiesMap.get(answer);
			Double frequency = frequenciesMap.get(answer);

			String value = null;
			if (numeric)
				value = ((NumericAnswer) answer).getValue().toString();
			else
				value = ((TextAnswer) answer).getValue();

			answers.add(value);
			labels.add("{value: " + counter + ", text: \"" + value + "\"}");
			pieData.add("{y: " + quantity + ", text: \"" + value + "\"}");
			counter++;

			quantities.add(quantity);
			frequencies.add(frequency);
			logger.info("freeq " + frequencies);
		}

	}
}
