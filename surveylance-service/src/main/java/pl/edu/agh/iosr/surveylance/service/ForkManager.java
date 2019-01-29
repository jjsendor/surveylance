package pl.edu.agh.iosr.surveylance.service;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ForkComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.DecisionDAO;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;

import java.util.List;

/**
 * This interface creates hierarchy of components.
 * 
 * @author michal
 */
public interface ForkManager {

	/**
	 * This method sets forkComponentDAO.
	 * 
	 * @param forkComponentDAO
	 *            fork component's DAO
	 */
	public void setForkComponentDAO(ForkComponentDAO forkComponentDAO);

	/**
	 * This method sets componentDAO.
	 * 
	 * @param componentDAO
	 *            component's DAO
	 */
	public void setComponentDAO(ComponentDAO componentDAO);

	/**
	 * This method sets decisionDAO.
	 * 
	 * @param decisionDAO
	 *            decision's DAO
	 */
	public void setDecisionDAO(DecisionDAO decisionDAO);

	/**
	 * This method sets answerDAO.
	 * 
	 * @param answerDAO
	 *            answer's DAO
	 */
	public void setAnswerDAO(AnswerDAO answerDAO);

	/**
	 * This method creates new fork.
	 * 
	 * @return new fork component
	 */
	public ForkComponent createFork();

	/**
	 * This method adds parent to fork.
	 * 
	 * @param forkId
	 *            fork's ID
	 * 
	 * @param parentId
	 *            parent's ID
	 */
	public void addParentToFork(long forkId, long parentId);

	/**
	 * This method gets fork by ID.
	 * 
	 * @param forkId
	 *            fork's ID
	 * 
	 * @return fork component
	 */
	public ForkComponent getFork(long forkId);

	/**
	 * This method deletes fork.
	 * 
	 * @param forkId
	 *            fork's ID
	 */
	public void deleteFork(long forkId);

	/**
	 * This method creates new numeric question's answer and adds it to
	 * decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @return numeric question's answer
	 */
	public NumericAnswer addNumericAnswerToDecision(long decisionId);

	/**
	 * This method creates new text question's answer and adds it to decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @return text question's answer
	 */
	public TextAnswer addTextAnswerToDecision(long decisionId);

	/**
	 * This method adds answer to decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @param answerId
	 *            question's answer's id
	 */
	public void addAnswerToDecision(long decisionId, long answerId);

	/**
	 * This method gets question's answer from decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @return question's answer
	 */
	public Answer getAnswerFromDecision(long decisionId);

	/**
	 * This method deletes question's answers from decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @param destroyAnswer
	 *            should answer be destroyed
	 */
	public void deleteAnswerFromDecision(long decisionId, boolean destroyAnswer);

	/**
	 * This method creates new component and adds it to decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @return question's answer
	 */
	public Component addChoiceToDecision(long decisionId);

	/**
	 * This method adds choice to decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @param choiceId
	 *            choice's id
	 */
	public void addChoiceToDecision(long decisionId, long choiceId);

	/**
	 * This method get choice from decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @return choice (component)
	 */
	public Component getChoiceFromDecision(long decisionId);

	/**
	 * This method deletes choice from decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @param destroyChoice
	 *            should choice (component) be destroyed
	 */
	public void deleteChoiceFromDecision(long decisionId, boolean destroyChoice);

	/**
	 * This method get's decision.
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @return decision
	 */
	public Decision getDecision(long decisionId);

	/**
	 * This method adds decision to fork.
	 * 
	 * @param forkId
	 *            fork's id
	 * 
	 * @return decision
	 */
	public Decision addDecisionToFork(long forkId);

	/**
	 * This method adds decision to fork.
	 * 
	 * @param forkId
	 *            fork's id
	 * @param decisionID
	 *            decision's id
	 */
	public void addDecisionToFork(long forkId, long decisionID);

	/**
	 * This method lists all decisions in this fork.
	 * 
	 * @param forkId
	 *            fork's id
	 * 
	 * @return list of decisions
	 */
	public List<Decision> getDecisionsFromFork(long forkId);

	/**
	 * This method lists all decisions connected with choice.
	 * 
	 * @param forkId
	 *            fork's id
	 * 
	 * @param choiceComponentID
	 *            choice components's id
	 * 
	 * @return list of decisions
	 */
	public List<Decision> getDecisionsFromFork(long forkId,
			long choiceComponentID);

	/**
	 * This method deletes decision's from fork.
	 * 
	 * @param forkId
	 *            fork's id
	 * 
	 * @param decisionId
	 *            decision's id
	 * 
	 * @param destroyDecision
	 *            should decision be destroyed
	 */
	public void deleteDecisionFromFork(long forkId, long decisionId,
			boolean destroyDecision);

	/**
	 * This method deletes decision's connected with one choice from fork.
	 * 
	 * @param forkId
	 *            fork's id
	 * 
	 * @param choiceComponentID
	 *            choice components's id
	 * 
	 * @param destroyDecision
	 *            should decision be destroyed
	 */
	public void deleteDecisionsFromFork(long forkId, long choiceComponentID,
			boolean destroyDecision);

	/**
	 * This methods returns component for given id
	 * 
	 * @param componentId
	 *            component's Id
	 * 
	 * @return relevant component object
	 */
	public Component getComponentById(long componentId);

}
