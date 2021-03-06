package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
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

public class QuestionList {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Property
	private Survey survey;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private SurveyResultManager surveyResultManager;

	@Property
	private Question question;

	@Property
	private Answer answer;

	@SuppressWarnings("unused")
	@Property
	private List<Question> questions;

	private Pair<String, Pair<Integer, Double>> result;

	@SuppressWarnings("unused")
	@Property
	private Pair<Integer, Double> quantityWithFrequency;

	public boolean isOpenQuestion() {
		return question.getKind() == QuestionKind.INPUT_TEXT;
	}

	void onActivate(long surveyID) {
		survey = surveyManager.getSurvey(surveyID);
		questions = surveyResultManager.getQuestions(survey);
	}
	
	public String getPieContext(){
		return question.getId() + "_p";
	}
	
	public String getStatisticsContext(){
		return question.getId() + "_s";
	}

	public List<Answer> getAnswers() {
		return surveyResultManager.getPossibleAnswers(question);
	}

	public String getAnswerLiteralValue() {
		if (question.getType() == QuestionType.NUMERIC)
			return ((NumericAnswer) answer).getValue().toString();

		return ((TextAnswer) answer).getValue();
	}

	public Integer getFrequency() {
		Double frequency = surveyResultManager.getFrequency(question, answer) * 100;
		return frequency.intValue();
	}

	public Integer getQuantity() {
		return surveyResultManager.getQuantity(question, answer);
	}

	public Integer getAllAnswerQuantity() {
		return surveyResultManager.getAllAnswersQuantity(question);
	}

	public List<Pair<String, Pair<Integer, Double>>> getTopResults() {
		return surveyResultManager.getTopTextResults(question, 5);
	}

	public Pair<String, Pair<Integer, Double>> getResult() {
		return result;
	}

	public void setResult(Pair<String, Pair<Integer, Double>> result) {
		this.result = result;
		quantityWithFrequency = result.getSecond();
	}

	public List<Integer> getQuantities() {
		Map<Answer, Integer> quantitiesMap = surveyResultManager
				.getQuantity(question);

		List<Answer> sortedAnswers = getSortedAnswers(quantitiesMap);

		List<Integer> quantities = new ArrayList<Integer>();
		for (Iterator<Answer> it = sortedAnswers.iterator(); it.hasNext();) {
			Answer answer = it.next();
			Integer quantity = quantitiesMap.get(answer);

			quantities.add(quantity);
		}
		
		return quantities;
	}

	public List<String> getLabels() {

		Map<Answer, Integer> quantitiesMap = surveyResultManager
				.getQuantity(question);

		List<Answer> sortedAnswers = getSortedAnswers(quantitiesMap);
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

	private List<Answer> getSortedAnswers(Map<Answer, Integer> quantitiesMap) {
		List<Answer> sortedAnswers = new ArrayList<Answer>(quantitiesMap
				.keySet());
		
		final Map<Answer, Integer> quantities = new HashMap<Answer, Integer>(quantitiesMap);
		Collections.sort(sortedAnswers, new Comparator<Answer>() {

			@Override
			public int compare(Answer arg0, Answer arg1) {
				return quantities.get(arg1).compareTo(quantities.get(arg0));
			}

		});

		return sortedAnswers;
	}
	
	public List<String> getPieData(){
		List<String> pieData = new ArrayList<String>();
		
		Map<Answer, Integer> quantitiesMap = surveyResultManager
		.getQuantity(question);
		
		List<Answer> sortedAnswers = getSortedAnswers(quantitiesMap);
		
		for (Iterator<Answer> it = sortedAnswers.iterator(); it.hasNext();) {
			Answer answer = it.next();
			Integer quantity = quantitiesMap.get(answer);

			String value = null;
			if (question.getType() == QuestionType.NUMERIC)
				value = ((NumericAnswer) answer).getValue().toString();
			else
				value = ((TextAnswer) answer).getValue();

			pieData.add("{y: " + quantity + ", text: \"" + value + "\"}");

		}
		
		return pieData;
	}
	
}
