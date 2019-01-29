package pl.edu.agh.iosr.surveylance.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.User;
import pl.edu.agh.iosr.surveylance.service.SurveyRestorationService;
import pl.edu.agh.iosr.surveylance.service.SyncService;

/**
 * {@link SyncService} implementation.
 *
 * @author kuba
 *
 */
public class SyncServiceImpl implements SyncService {

	private static final String CREATED = "created";
	private static final String REMOVED = "removed";
	private static final String UPDATED = "updated";

	private SurveyDAO surveyDAO;
	private ComponentDAO componentDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;
	
	private SurveyRestorationService restorationService;

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
	public void setComponentDAO(ComponentDAO componentDAO) {
		this.componentDAO = componentDAO;
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
	public void setAnswerDAO(AnswerDAO answerDAO) {
		this.answerDAO = answerDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRestorationService(
			SurveyRestorationService restorationService) {
		this.restorationService = restorationService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getSurveysModifications(User user,
			Map<Long, Integer> modificationsMap) {
		Set<Long> idsSet = new HashSet<Long>(modificationsMap.keySet());
		List<Survey> surveys = surveyDAO.findByOwner(user);
		List<Survey> createdSurveys = new ArrayList<Survey>();
		List<Survey> updatedSurveys = new ArrayList<Survey>();

		for (Survey survey : surveys) {
			if (idsSet.contains(survey.getId())) {
				idsSet.remove(survey.getId());

				if (!survey.getModifications().equals(
						modificationsMap.get(survey.getId()))) {
					updatedSurveys.add(survey);
				}
			}
			else
				createdSurveys.add(survey);
		}

		// now idsSet contains deleted surveys ids and createdSurveys list
		// contains created surveys
		JSONArray created = new JSONArray();

		for (Survey survey : createdSurveys)
			created.put(restorationService.surveyToJSON(survey));

		JSONArray removed = new JSONArray();

		for (Long id : idsSet)
			removed.put(id);

		JSONArray updated = new JSONArray();

		for (Survey survey : updatedSurveys)
			updated.put(restorationService.surveyJSONObject(survey));

		JSONObject modifications = new JSONObject();

		modifications.put(CREATED, created);
		modifications.put(REMOVED, removed);
		modifications.put(UPDATED, updated);

		return modifications;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getComponentsModifications(Component parent,
			Map<Long, Integer> modificationsMap) {
		Set<Long> idsSet = new HashSet<Long>(modificationsMap.keySet());
		List<Component> components = componentDAO.findByParent(parent);
		List<Component> createdComponents = new ArrayList<Component>();
		List<Component> updatedComponents = new ArrayList<Component>();

		for (Component component : components) {
			if (idsSet.contains(component.getId())) {
				idsSet.remove(component.getId());

				if (!component.getModifications().equals(
						modificationsMap.get(component.getId()))) {
					updatedComponents.add(component);
				}
			}
			else
				createdComponents.add(component);
		}

		// now idsSet contains deleted components ids and createdComponents list
		// contains created components
		JSONArray created = new JSONArray();

		for (Component component : createdComponents)
			created.put(restorationService.componentToJSON(component));

		JSONArray removed = new JSONArray();

		for (Long id : idsSet)
			removed.put(id);

		JSONArray updated = new JSONArray();

		for (Component component : updatedComponents)
			updated.put(restorationService.componentJSONObject(component));

		JSONObject modifications = new JSONObject();

		modifications.put(CREATED, created);
		modifications.put(REMOVED, removed);
		modifications.put(UPDATED, updated);

		return modifications;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getQuestionsModifications(Component parent,
			Map<Long, Integer> modificationsMap) {
		Set<Long> idsSet = new HashSet<Long>(modificationsMap.keySet());
		List<Question> questions = new ArrayList<Question>(1);
		questions.add(questionDAO.findByParent(parent));
		List<Question> createdQuestions = new ArrayList<Question>();
		List<Question> updatedQuestions = new ArrayList<Question>();

		for (Question question : questions) {
			if (idsSet.contains(question.getId())) {
				idsSet.remove(question.getId());

				if (!question.getModifications().equals(
						modificationsMap.get(question.getId()))) {
					updatedQuestions.add(question);
				}
			}
			else
				createdQuestions.add(question);
		}

		// now idsSet contains deleted questions ids and createdQuestions list
		// contains created questions
		JSONArray created = new JSONArray();

		for (Question question : createdQuestions)
			created.put(restorationService.questionToJSON(question));

		JSONArray removed = new JSONArray();

		for (Long id : idsSet)
			removed.put(id);

		JSONArray updated = new JSONArray();

		for (Question question : updatedQuestions)
			updated.put(restorationService.questionJSONObject(question));

		JSONObject modifications = new JSONObject();

		modifications.put(CREATED, created);
		modifications.put(REMOVED, removed);
		modifications.put(UPDATED, updated);

		return modifications;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getAnswersModifications(Question question,
			Map<Long, Integer> modificationsMap) {
		Set<Long> idsSet = new HashSet<Long>(modificationsMap.keySet());
		List<Answer> answers = answerDAO.findByQuestion(question);
		List<Answer> createdAnswers = new ArrayList<Answer>();
		List<Answer> updatedAnswers = new ArrayList<Answer>();

		for (Answer answer : answers) {
			if (idsSet.contains(answer.getId())) {
				idsSet.remove(answer.getId());

				if (!answer.getModifications().equals(
						modificationsMap.get(answer.getId()))) {
					updatedAnswers.add(answer);
				}
			}
			else
				createdAnswers.add(answer);
		}

		// now idsSet contains deleted answers ids and createdAnswers list
		// contains created answers
		JSONArray created = new JSONArray();

		for (Answer answer : createdAnswers)
			created.put(restorationService.answerToJSON(answer));

		JSONArray removed = new JSONArray();

		for (Long id : idsSet)
			removed.put(id);

		JSONArray updated = new JSONArray();

		for (Answer answer : updatedAnswers)
			updated.put(restorationService.answerJSONObject(answer));

		JSONObject modifications = new JSONObject();

		modifications.put(CREATED, created);
		modifications.put(REMOVED, removed);
		modifications.put(UPDATED, updated);

		return modifications;
	}

}
