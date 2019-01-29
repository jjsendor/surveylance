package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.google.gdata.util.common.base.Pair;

import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class OpenAnswerList {

	@Property
	private Question question;

	@SuppressWarnings("unused")
	@Property
	private List<Pair<String, Pair<Integer, Double>>> results;

	@Property
	private Pair<Integer, Double> quantityWithFrequency;

	private Pair<String, Pair<Integer, Double>> result;

	@Inject
	private SurveyResultManager surveyResultManager;

	void onActivate(long questionId) {
		question = surveyResultManager.getQuestion(questionId);

		results = surveyResultManager.getTopTextResults(question, -1);

	}

	public Pair<String, Pair<Integer, Double>> getResult() {
		return result;
	}

	public void setResult(Pair<String, Pair<Integer, Double>> result) {
		this.result = result;
		quantityWithFrequency = result.getSecond();
	}

	public String getQuantityWithFrequencySecond(){
		return surveyResultManager.twoDigitsFormat(100*quantityWithFrequency.second);
	}
}
