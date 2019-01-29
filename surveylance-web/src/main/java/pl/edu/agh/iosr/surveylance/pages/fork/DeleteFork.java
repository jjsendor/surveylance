package pl.edu.agh.iosr.surveylance.pages.fork;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that deletes fork.
 * 
 * @author michal
 */
public class DeleteFork {

	private static final Logger logger = Logger.getLogger(DeleteFork.class);

	@Inject
	private ForkManager surveyHierarchyManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * This method deletes fork.
	 * 
	 * @param forkId
	 *            id of deleted fork
	 * 
	 * @return JSON object response:
	 * 
	 *         <pre>
	 * {
	 *     deleted: {true/false} // was fork deleted?
	 * }
	 * </pre>
	 */
	Object onActivate(long forkId) {
		Boolean answer = false;
		ForkComponent fork = surveyHierarchyManager.getFork(forkId);

		if (fork != null
				&& fork.getParentComponent() != null
				&& surveyManager.isComponentOwner(fork.getParentComponent(),
						userInfo.getUser())) {
			try {
				surveyHierarchyManager.deleteFork(forkId);
				answer = true;
			} catch (DAOException e) {
				logger.error("Exception while deleting fork: " + e);
			}
		}

		JSONObject response = new JSONObject("{deleted: '" + answer + "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
