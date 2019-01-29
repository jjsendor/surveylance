package pl.edu.agh.iosr.surveylance.service.impl;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.service.ModificationService;

/**
 * Implementation of {@link ModificationService} interface.
 *
 * @author kuba
 *
 */
public class ModificationServiceImpl implements ModificationService {

	private SurveyDAO surveyDAO;
	private ComponentDAO componentDAO;
	private QuestionDAO questionDAO;
	private AnswerDAO answerDAO;

	@Override
	public void setSurveyDAO(SurveyDAO surveyDAO) {
		this.surveyDAO = surveyDAO;
	}

	@Override
	public void setComponentDAO(ComponentDAO componentDAO) {
		this.componentDAO = componentDAO;
	}

	@Override
	public void setQuestionDAO(QuestionDAO questionDAO) {
		this.questionDAO = questionDAO;
	}

	@Override
	public void setAnswerDAO(AnswerDAO answerDAO) {
		this.answerDAO = answerDAO;
	}

	@Override
	public void markModified(Survey survey) {
		int modifications = survey.getModifications();
		survey.setModifications(++modifications);
		surveyDAO.update(survey);
	}

	@Override
	public void markModified(Component component) {
		int modifications = component.getModifications();
		component.setModifications(++modifications);
		componentDAO.update(component);

		Component parent = component.getParentComponent();

		if (parent != null)
			markModified(parent);
		else {
			Survey survey = surveyDAO.findByRootComponent(component);

			if (survey != null)
				markModified(survey);
		}
	}

	@Override
	public void markModified(Question question) {
		int modifications = question.getModifications();
		question.setModifications(++modifications);
		questionDAO.update(question);

		Component component = question.getParentComponent();

		if (component != null)
			markModified(component);
	}

	@Override
	public void markModified(Answer answer) {
		int modifications = answer.getModifications();
		answer.setModifications(++modifications);
		answerDAO.update(answer);

		Question question = answer.getQuestion();

		if (question != null)
			markModified(question);
	}

}
