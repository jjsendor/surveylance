package pl.edu.agh.iosr.surveylance.service.impl;

import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.DecisionDAO;
import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;

import java.util.List;

/**
 * This class creates hierarchy of components.
 *
 * @author michal
 */
public class ForkManagerImpl implements ForkManager {

	private ForkComponentDAO forkComponentDAO;
	private ComponentDAO componentDAO;
	private DecisionDAO decisionDAO;
	private AnswerDAO answerDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setForkComponentDAO(ForkComponentDAO forkComponentDAO) {
		this.forkComponentDAO = forkComponentDAO;
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
	public void setDecisionDAO(DecisionDAO decisionDAO) {
		this.decisionDAO = decisionDAO;
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
	public ForkComponent createFork() {
		ForkComponent fork = new ForkComponent();
		fork.setParentComponent(null);
		forkComponentDAO.create(fork);
		return fork;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addParentToFork(long forkId, long parentId) {
		ForkComponent fork = forkComponentDAO.findById(forkId, false);
		if (fork != null) {
			fork.setParentComponent(componentDAO.findById(parentId, false));
			forkComponentDAO.update(fork);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ForkComponent getFork(long forkId) {
		return forkComponentDAO.findById(forkId, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFork(long forkId) {
		ForkComponent fork = forkComponentDAO.findById(forkId, false);

		if (fork != null) {
			fork.setParentComponent(null);
			forkComponentDAO.delete(fork);
		}
	}

	/**
	 * This method add's question to decision.
	 *
	 * @param	decisionId	decision's id
	 * @param	answer			question answer
	 */
	private void setQuestionToDecision(long decisionId, Answer answer) {
		Decision decision = decisionDAO.findById(decisionId, false);
		decision.setAnswer(answer);
		decisionDAO.update(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NumericAnswer addNumericAnswerToDecision(long decisionId) {
		NumericAnswer answer = new NumericAnswer();
		answer.setQuestion(null);
		answerDAO.create(answer);

		setQuestionToDecision(decisionId, answer);

		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextAnswer addTextAnswerToDecision(
			long decisionId) {
		TextAnswer answer = new TextAnswer();
		answer.setQuestion(null);
		answerDAO.create(answer);

		setQuestionToDecision(decisionId, answer);

		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAnswerToDecision(long decisionId, long answerId) {
		Answer answer = answerDAO.findById(answerId, false);

		setQuestionToDecision(decisionId, answer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Answer getAnswerFromDecision(long decisionId) {
		return decisionDAO.findById(decisionId, false).getAnswer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAnswerFromDecision(long decisionId,
			boolean destroyAnswer) {
		Decision decision = decisionDAO.findById(decisionId, false);
		Answer answer = decision.getAnswer();
		decision.setAnswer(null);
		decisionDAO.update(decision);

		if (destroyAnswer && answer != null) {
			answer.setQuestion(null);

			answerDAO.delete(answer);
		}
	}

	/**
	 * This method add's choice to decision.
	 *
	 * @param	decisionId	decision's id
	 * @param	choice		choice component
	 */
	private void setQuestionToDecision(long decisionId, Component choice) {
		Decision decision = decisionDAO.findById(decisionId, false);
		decision.setComponent(choice);
		decisionDAO.update(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component addChoiceToDecision(long decisionId) {
		Component choice = new Component();
		choice.setParentComponent(null);
		componentDAO.create(choice);

		setQuestionToDecision(decisionId, choice);

		return choice;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChoiceToDecision(long decisionId, long choiceId) {
		Component choice = componentDAO.findById(choiceId, false);

		setQuestionToDecision(decisionId, choice);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getChoiceFromDecision(long decisionId) {
		return decisionDAO.findById(decisionId, false).getComponent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteChoiceFromDecision(long decisionId,
			boolean destroyChoice) {
		Decision decision = decisionDAO.findById(decisionId, false);
		Component choice = decision.getComponent();
		decision.setComponent(null);
		decisionDAO.update(decision);

		if (destroyChoice && choice != null) {
			choice.setParentComponent(null);
			componentDAO.delete(choice);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Decision getDecision(long decisionId) {
		return decisionDAO.findById(decisionId, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Decision addDecisionToFork(long forkId) {
		Decision decision = new Decision();
		decision.setAnswer(null);
		decision.setComponent(null);
		decision.setForkComponent(forkComponentDAO.findById(forkId, false));
		decisionDAO.create(decision);

		return decision;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDecisionToFork(long forkId, long decisionID) {
		Decision decision = decisionDAO.findById(decisionID, false);
		decision.setForkComponent(forkComponentDAO.findById(forkId, false));
		decisionDAO.update(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Decision> getDecisionsFromFork(long forkId) {
		return decisionDAO.findByForkComponent(
				forkComponentDAO.findById(forkId, false));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Decision> getDecisionsFromFork(long forkId,
			long choiceComponentID) {
		return decisionDAO.findByForkComponentAndComponent(
				forkComponentDAO.findById(forkId, false),
				componentDAO.findById(choiceComponentID, false));
	}

	/**
	 * This method deletes decision's from fork.
	 *
	 * @param	decisionId		decision's id
	 * @param	destroyDecision	should decision be destroyed
	 */
	private void deleteDecision(Decision decision, boolean destroyDecision) {
		decision.setForkComponent(null);

		if (destroyDecision && decision != null) {
			decision.setComponent(null);
			decision.setAnswer(null);
			decisionDAO.delete(decision);
		}
		else
			decisionDAO.update(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecisionFromFork(long forkId, long decisionId,
			boolean destroyDecision) {
		Decision decision = decisionDAO.findById(decisionId, false);

		if (decision != null && decision.getForkComponent() != null
				&& decision.getForkComponent().getId() == forkId) {
			deleteDecision(decision, destroyDecision);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecisionsFromFork(long forkId, long choiceComponentID,
			boolean destroyDecision) {
		List<Decision> decisions =
			getDecisionsFromFork(forkId, choiceComponentID);

		for (Decision decision : decisions)
			deleteDecision(decision, destroyDecision);
	}

	@Override
	public Component getComponentById(long componentId) {
		return componentDAO.findById(componentId, false);
	}

}
