package pl.edu.agh.iosr.surveylance.pages.component;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that moves components.
 * 
 * @author michal
 */
@ContentType("application/json")
public class MoveComponent {

	private static final Logger logger = Logger.getLogger(MoveComponent.class);

	@Inject
	private Request request;

	@Inject
	private ComponentManager positionManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * This method moves component. Parameters from request:
	 * <dl>
	 * <dt>position</dt>
	 * <dd>new position of component</dd>
	 * </dl>
	 * 
	 * @param componentId
	 *            id of moved component
	 * 
	 * @return JSON object response:
	 * 
	 *         <pre>
	 * {
	 *     position: component.position	// new position of component,
	 *     								// -1 for error
	 * }
	 * </pre>
	 */
	Object onActivate(long componentId) {
		int position = -1;
		String positionStr = request.getParameter("position");

		try {
			if (surveyManager.isComponentOwner(componentId, userInfo.getUser())) {
					position = Integer.valueOf(positionStr);
					position = positionManager.moveComponent(componentId,
							position);
			}
			else {
				logger.warn("User " + userInfo.getUser().getGoogleId()
						+ " has no rights to move component with id "
						+ componentId);
			}
		} catch (RuntimeException e) {
			logger.warn("Component " + componentId + " was not moved.", e);
		}

		JSONObject response = new JSONObject("{position: '" + position + "'}");
		return new TextStreamResponse("application/json", response.toString());
	}

}
