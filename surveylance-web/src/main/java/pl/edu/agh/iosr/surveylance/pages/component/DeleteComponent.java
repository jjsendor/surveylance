package pl.edu.agh.iosr.surveylance.pages.component;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * Controller class responsible for component deletion.
 * 
 * @author michal
 */
@ContentType("application/json")
public class DeleteComponent {

	private static final Logger logger = Logger
			.getLogger(DeleteComponent.class);

	@Inject
	private ComponentManager componentManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * Dispatches client request. Deletes component.
	 * 
	 * @param componentId
	 *            id of component to be deleted
	 * 
	 * @return JSON object response:
	 * 
	 *         <pre>
	 * {
	 *     deleted: {true/false} // was component deleted?
	 * }
	 * </pre>
	 */
	Object onActivate(long componentId) {
		Boolean deleted = false;

		try {
			if (surveyManager.isComponentOwner(componentId, userInfo.getUser())) {
					componentManager.deleteComponent(componentId);
					deleted = true;
			}
			else {
				logger.warn("User " + userInfo.getUser().getGoogleId()
						+ " has no rights to delete component with id "
						+ componentId);
			}
		} catch (RuntimeException e) {
			logger.warn("Component " + componentId + " was not deleted.",
					e);
		}

		JSONObject response = new JSONObject("{deleted: '" + deleted + "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
