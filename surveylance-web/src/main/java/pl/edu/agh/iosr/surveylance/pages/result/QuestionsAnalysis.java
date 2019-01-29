package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class QuestionsAnalysis {
	
	@Inject
	private QuestionManager questionManager;	
	
	@Inject
	private SurveyManager surveyManager;	
	
	@Inject
	private SurveyResultManager surveyResultManager;
	
	@Property
	private List<Question> questions;
	
	@Property
	private Question question;	
	
	@Property
	private Answer answer;	
	
	private Map<Question, Survey> surveys;
	
	@SuppressWarnings("unused")
	@Property
	private boolean manySurveys = false;

	@Property
	private Pair<Integer, Double> quantityWithFrequency;
	
	private Pair<String, Pair<Integer, Double>> result;
	
	public Pair<String, Pair<Integer, Double>> getResult() {
		return result;
	}
	
	public String getPieContext(){
		return question.getId() + "_p";
	}
	
	public String getStatisticsContext(){
		return question.getId() + "_s";
	}
	
	public void setResult(Pair<String, Pair<Integer, Double>> result) {
		this.result = result;
		quantityWithFrequency = result.getSecond();
	}	
	
	public Survey getSurvey(){
		return surveys.get(question);
	}
	
	void onActivate(String ids){		
		String[] idsStrings = ids.split("q");
		
		questions = new ArrayList<Question>();
		for (String idString : idsStrings) {
			try {
				Long id = Long.valueOf(idString);
				questions.add(questionManager.getQuestion(id));
			} catch (NumberFormatException ex) {
				// do nothing
			}
		}	
		
		surveys = new HashMap<Question, Survey>();
		for(Question q: questions)
			surveys.put(q, surveyManager.getSurvey(q));
		
		Survey first = surveys.get(questions.get(0));
		for(Question q: questions)
			if(!surveys.get(q).getId().equals(first.getId()))
				manySurveys = true;
	}
	
	public boolean isOpenQuestion() {
		return question.getKind() == QuestionKind.INPUT_TEXT;
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
	
	public Integer getQuantity() {
		return surveyResultManager.getQuantity(question, answer);
	}
	
	public Integer getAllAnswerQuantity() {
		return surveyResultManager.getAllAnswersQuantity(question);
	}
	
	public List<Pair<String, Pair<Integer, Double>>> getTopResults() {
		return surveyResultManager.getTopTextResults(question, 5);
	}	
	
	public String getQuantityWithFrequencySecond(){
		return surveyResultManager.twoDigitsFormat(100*quantityWithFrequency.second);
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
}
