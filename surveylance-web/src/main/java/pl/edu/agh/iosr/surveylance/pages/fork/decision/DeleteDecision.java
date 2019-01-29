package pl.edu.agh.iosr.surveylance.pages.fork.decision;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.Decision;
import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that deletes decision.
 * 
 * @author michal
 */
@ContentType("application/json")
public class DeleteDecision {

	private static final Logger logger = Logger.getLogger(DeleteDecision.class);

	@Inject
	private ForkManager surveyHierarchyManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * This method deletes decision.
	 * 
	 * @param decisionId
	 *            id of deleted decision
	 * 
	 * @return JSON object response in form of:
	 * 
	 *         <pre>
	 * {
	 *     deleted: true/false	// was decision deleted?
	 * }
	 * </pre>
	 */
	Object onActivate(long decisionId) {
		Boolean deleted = false;

		try {
			Decision decision = surveyHierarchyManager.getDecision(decisionId);

			if (decision != null
					&& decision.getForkComponent() != null
					&& surveyManager.isComponentOwner(decision
							.getForkComponent().getParentComponent(), userInfo
							.getUser())) {
				surveyHierarchyManager.deleteDecisionFromFork(decision
						.getForkComponent().getId(), decisionId, true);
			}

			deleted = true;
		} catch (DAOException e) {
			logger
					.error("Exception while deleting decision: "
							+ e.getMessage());
		}

		JSONObject response = new JSONObject("{deleted: '" + deleted + "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
