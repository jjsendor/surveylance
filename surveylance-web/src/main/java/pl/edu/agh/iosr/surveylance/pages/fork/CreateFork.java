package pl.edu.agh.iosr.surveylance.pages.fork;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.exceptions.DAOException;
import pl.edu.agh.iosr.surveylance.entities.ForkComponent;
import pl.edu.agh.iosr.surveylance.service.ForkManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that creates fork.
 * 
 * @author michal
 */
@ContentType("application/json")
public class CreateFork {

	private static final Logger logger = Logger.getLogger(CreateFork.class);

	@Inject
	private ForkManager surveyHierarchyManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * This method creates fork and sets parent to it.
	 * 
	 * @param parentId
	 *            id of parent component
	 * 
	 * @return JSON object response in form of:
	 * 
	 *         <pre>
	 * {
	 *     id: fork.id,
	 *     modifications: fork.modifications,
	 *     parentId: fork.parent.id
	 * }
	 * </pre>
	 */
	Object onActivate(long parentId) {
		ForkComponent fork = null;

		if (surveyManager.isComponentOwner(parentId, userInfo.getUser())) {
			try {
				fork = surveyHierarchyManager.createFork();
				surveyHierarchyManager.addParentToFork(fork.getId(), parentId);
			} catch (DAOException e) {
				logger
						.error("Exception while creating fork: "
								+ e.getMessage());
			}
		}

		JSONObject response = null;

		if (fork != null) {
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append("{id: '");
			responseBuffer.append(fork.getId());
			responseBuffer.append("', modifications: '");
			responseBuffer.append(fork.getModifications());

			if (fork.getParentComponent() != null) {
				responseBuffer.append("', parentId: '");
				responseBuffer.append(parentId);
			}

			responseBuffer.append("'}");

			response = new JSONObject(responseBuffer.toString());
		} else {
			response = new JSONObject();
		}

		return new TextStreamResponse("application/json", response.toString());
	}

}
