package pl.edu.agh.iosr.surveylance.pages.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class AnalysisMainPage {

	@Inject
	private Request request;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private SurveyResultManager surveyResultManager;

	@Property
	private Question question;

	@Property
	private Survey survey;

	@Property
	private Map<Survey, List<Question>> normalQuestions;

	@Property
	private List<Question> equalQuestionsList;

	private List<List<Question>> equalQuestions;

	@Property
	private List<Survey> surveys;

	public boolean isClosedQuestion() {
		return question.getKind() != QuestionKind.INPUT_TEXT;
	}

	public boolean isEqualQuestionsToAnalyse() {
		return !equalQuestionsList.isEmpty();
	}

	public boolean isNormalQuestionsToAnalyse() {
		return !normalQuestions.isEmpty();
	}

	public List<Question> getQuestions() {
		return normalQuestions.get(survey);
	}

	public Long getEqualQuestion() {
		return equalQuestions.get(equalQuestionsList.indexOf(question)).get(
				surveys.indexOf(survey)).getId();
	}

	private void analyzeSurveys() {
		equalQuestionsList = new ArrayList<Question>();
		normalQuestions = new HashMap<Survey, List<Question>>();

		if (surveys.size() > 1) {
			equalQuestions = surveyResultManager.getEqualQuestions(surveys);

			for (List<Question> tmpList : equalQuestions)
				for (Question tmpQuestion : tmpList)
					equalQuestionsList.add(tmpQuestion);

			for (Survey survey : surveys) {
				List<Question> tmpList = new ArrayList<Question>();

				for (Question question : surveyResultManager
						.getQuestions(survey)) {
					boolean isEqual = false;

					for (Question tmpQuestion : equalQuestionsList)
						if (question.getId().equals(tmpQuestion.getId()))
							isEqual = true;

					if (!isEqual)
						tmpList.add(question);
				}

				if (!tmpList.isEmpty())
					normalQuestions.put(survey, tmpList);
			}

			equalQuestionsList.clear();

			for (List<Question> tmpList : equalQuestions)
				equalQuestionsList.add(tmpList.get(0));
		} else {
			normalQuestions.put(surveys.get(0), surveyResultManager
					.getQuestions(surveys.get(0)));
		}
	}

	void onActivate() {
		surveys = new ArrayList<Survey>();
		String surveysIds = request.getParameter("ids");

		if (surveysIds != null) {
			String[] idsStrings = surveysIds.split(",");

			for (String idString : idsStrings) {
				try {
					Long id = Long.valueOf(idString);
					surveys.add(surveyManager.getSurvey(id));
				} catch (NumberFormatException ex) {
					// do nothing
				}
			}

			analyzeSurveys();
		}
	}

	void onActivate(long surveyId) {
		Survey survey = surveyManager.getSurvey(surveyId);

		if (survey != null) {
			surveys = new ArrayList<Survey>(1);
			surveys.add(survey);
			analyzeSurveys();
		}
	}

	public String getAllNormalQuestions() {
		StringBuffer ids = new StringBuffer();

		for (Survey s : surveys)
			for (Question q : normalQuestions.get(s)) {
				ids.append(q.getId());
				ids.append("q");
			}

		String normalQuestions;

		if (ids.length() > 0)
			normalQuestions = ids.substring(0, ids.length() - 1);
		else
			normalQuestions = "";

		return normalQuestions;
	}

	public String getNormalQuestionsFromSurvey() {
		StringBuffer ids = new StringBuffer();

		for (Question q : normalQuestions.get(survey)) {
			ids.append(q.getId());
			ids.append("q");
		}

		String normalQuestions;

		if (ids.length() > 0)
			normalQuestions = ids.substring(0, ids.length() - 1);
		else
			normalQuestions = "";

		return normalQuestions;
	}

	public String getAllEqualQuestions() {
		StringBuffer ids = new StringBuffer();

		for (List<Question> tmpList : equalQuestions)
			for (Question q : tmpList) {
				ids.append(q.getId());
				ids.append("q");
			}

		String equalQuestions;

		if (ids.length() > 0)
			equalQuestions = ids.substring(0, ids.length() - 1);
		else
			equalQuestions = "";

		return equalQuestions;
	}

	public String getEqualQuestions() {
		StringBuffer ids = new StringBuffer();

		for (Question q : equalQuestions.get(equalQuestionsList
				.indexOf(question))) {
			ids.append(q.getId());
			ids.append("q");
		}

		String equalQuestions;

		if (ids.length() > 0)
			equalQuestions = ids.substring(0, ids.length() - 1);
		else
			equalQuestions = "";

		return equalQuestions;
	}

	public String getQuantityTrendQuestions() {
		StringBuffer ids = new StringBuffer();

		for (Question q : equalQuestions.get(equalQuestionsList
				.indexOf(question))) {
			ids.append(q.getId());
			ids.append("q");
		}

		if (ids.length() > 0)
			ids.deleteCharAt(ids.length() - 1);
		ids.append("t");
		ids.append("q");

		return ids.toString();
	}

	public String getFrequencyTrendQuestions() {
		StringBuffer ids = new StringBuffer();

		for (Question q : equalQuestions.get(equalQuestionsList
				.indexOf(question))) {
			ids.append(q.getId());
			ids.append("q");
		}

		if (ids.length() > 0)
			ids.deleteCharAt(ids.length() - 1);
		ids.append("t");
		ids.append("f");

		return ids.toString();
	}

	public String getSumQuestions() {
		StringBuffer ids = new StringBuffer();

		for (Question q : equalQuestions.get(equalQuestionsList
				.indexOf(question))) {
			ids.append(q.getId());
			ids.append("q");
		}

		if (ids.length() > 0)
			ids.deleteCharAt(ids.length() - 1);
		ids.append("t");
		ids.append("s");

		return ids.toString();
	}

	public String getTrendAndSumQuestions() {
		StringBuffer ids = new StringBuffer();

		for (Question q : equalQuestions.get(equalQuestionsList
				.indexOf(question))) {
			ids.append(q.getId());
			ids.append("q");
		}

		if (ids.length() > 0)
			ids.deleteCharAt(ids.length() - 1);
		ids.append("t");
		ids.append("q");
		ids.append("f");
		ids.append("s");

		return ids.toString();
	}

	public String getAllTrendAndSumQuestions() {
		StringBuffer ids = new StringBuffer();

		for (List<Question> tmpList : equalQuestions) {
			for (Question q : tmpList) {
				ids.append(q.getId());
				ids.append("q");
			}

			if (ids.length() > 0)
				ids.deleteCharAt(ids.length() - 1);
			ids.append("e");
		}

		if (ids.length() > 0)
			ids.deleteCharAt(ids.length() - 1);
		ids.append("t");
		ids.append("q");
		ids.append("f");
		ids.append("s");

		return ids.toString();
	}

}
