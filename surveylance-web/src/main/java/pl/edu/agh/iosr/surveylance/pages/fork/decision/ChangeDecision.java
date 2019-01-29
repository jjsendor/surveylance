package pl.edu.agh.iosr.surveylance.pages.fork.decision;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that change's decision.
 * 
 * @author michal
 */
public class ChangeDecision {

	private static final Logger logger = Logger.getLogger(ChangeDecision.class);

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
	 * @param decisionId
	 *            id of decision
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
	Object onActivate(long decisionId) {
		String answerIdStr = request.getParameter("answerId");
		Long answerId = null;

		if (answerIdStr != null) {
			try {
				answerId = Long.valueOf(answerIdStr);
			} catch (NumberFormatException e) {
				logger.error("Exception while changing decision: "
						+ e.getMessage());
			}
		}

		String componentIdStr = request.getParameter("componentId");
		Long componentId = null;

		if (componentIdStr != null) {
			try {
				componentId = Long.valueOf(componentIdStr);
			} catch (NumberFormatException e) {
				logger.error("Exception while changing decision: "
						+ e.getMessage());
			}
		}

		Decision decision = surveyHierarchyManager.getDecision(decisionId);

		try {
			if (decision != null
					&& decision.getForkComponent() != null
					&& surveyManager.isComponentOwner(decision
							.getForkComponent().getParentComponent(), userInfo
							.getUser())) {
				if (answerId != null)
					surveyHierarchyManager.addAnswerToDecision(decisionId,
							answerId);

				if (componentId != null
						&& surveyManager.isComponentOwner(componentId, userInfo
								.getUser()))
					surveyHierarchyManager.addChoiceToDecision(decisionId,
							componentId);
			}
		} catch (DAOException e) {
			logger
					.error("Exception while changing decision: "
							+ e.getMessage());
		}

		JSONObject response = Helper.createJSONFromDecision(decision);
		return new TextStreamResponse("application/json", response.toString());
	}

}
