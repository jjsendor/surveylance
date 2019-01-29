package pl.edu.agh.iosr.surveylance.pages.fork.decision;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that create's decision.
 * 
 * @author michal
 */
@ContentType("application/json")
public class CreateDecision {

	private static final Logger logger = Logger.getLogger(CreateDecision.class);

	@Inject
	private Request request;

	@Inject
	private ForkManager surveyHierarchyManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * This method creates decision. Parameters from request:
	 * <dl>
	 * <dt>answerId</dt>
	 * <dd>- <em>optional</em> - id of answer that activate's decision</dd>
	 * 
	 * <dt>componentId</dt>
	 * <dd>- <em>optional</em> - id of activated component</dd>
	 * </dl>
	 * 
	 * @param forkId
	 *            id of fork component
	 * 
	 * @return JSON object response in form of:
	 * 
	 *         <pre>
	 * {
	 *     id: decision.id,
	 *     forkId: decision.fork.id,
	 *     choiceId: decision.choice.id,
	 *     answerId: decision.answer.id
	 * }
	 * </pre>
	 */
	Object onActivate(long forkId) {
		String answerIdStr = request.getParameter("answerId");
		Long answerId = null;

		if (answerIdStr != null) {
			try {
				answerId = Long.valueOf(answerIdStr);
			} catch (NumberFormatException e) {
				logger.error("Exception while creating decision: "
						+ e.getMessage());
			}
		}

		String componentIdStr = request.getParameter("componentId");
		Long componentId = null;

		if (componentIdStr != null) {
			try {
				componentId = Long.valueOf(componentIdStr);
			} catch (NumberFormatException e) {
				logger.error("Exception while creating decision: "
						+ e.getMessage());
			}
		}

		Decision decision = null;

		try {
			ForkComponent fork = surveyHierarchyManager.getFork(forkId);

			if (fork != null
					&& fork.getParentComponent() != null
					&& surveyManager.isComponentOwner(
							fork.getParentComponent(), userInfo.getUser())) {
				decision = surveyHierarchyManager.addDecisionToFork(forkId);

				if (answerId != null) {
					surveyHierarchyManager.addAnswerToDecision(
							decision.getId(), answerId);
				}

				if (componentId != null
						&& surveyManager.isComponentOwner(componentId, userInfo
								.getUser())) {
					surveyHierarchyManager.addChoiceToDecision(
							decision.getId(), componentId);
				}
			}
		} catch (DAOException e) {
			logger
					.error("Exception while creating decision: "
							+ e.getMessage());
		}

		JSONObject response = Helper.createJSONFromDecision(decision);
		return new TextStreamResponse("application/json", response.toString());
	}

}
