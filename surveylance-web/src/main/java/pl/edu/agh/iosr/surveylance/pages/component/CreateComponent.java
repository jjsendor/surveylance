package pl.edu.agh.iosr.surveylance.pages.component;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * This is controller that creates components.
 *
 * @author michal
 */
@ContentType("application/json")
public class CreateComponent {

	private static final Logger logger =
		Logger.getLogger(CreateComponent.class);

	@Inject
	private Request request;

	@Inject
	private ComponentManager componentManager;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * This method creates component.
	 * Parameters from request:
	 * <dl>
	 *   <dt>position</dt>
	 *   <dd><em>optional</em> - position on which new component is added</dd>
	 * </dl>
	 *
	 * @param	parentId	id of parent component
	 *
	 * @return	JSONObject with info about component:
	 * <pre>
	 * {
	 *     id: component.id,
	 *     modifications: component.modifications,
	 *     position: component.position,
	 *     parentId: component.parentComponent.id
	 * }
	 * </pre>
	 */
	Object onActivate(long parentId) {
		long componentId = -1;
		int componentPosition = -1;
		String positionStr = request.getParameter("position");

		try {
			if (surveyManager.isComponentOwner(parentId, userInfo.getUser())) {
				Component component = null;

				try {
					int position = Integer.valueOf(positionStr);
					component = componentManager.createComponent(parentId,
							position);
				} catch (NumberFormatException e) {
					logger.warn("Exception while creating component.", e);
					component = componentManager.createComponent(parentId);
				}

				componentId = component.getId();
				componentPosition = component.getPosition();
			}
			else {
				logger.warn("User " + userInfo.getUser().getGoogleId()
						+ " has no rights to create component for parent with id "
						+ parentId);
			}
		} catch (RuntimeException e) {
			logger.warn("Component was not created.", e);
		}

		JSONObject response = new JSONObject("{id: '" + componentId
				+ "', position: '" + componentPosition
				+ "'}");

		return new TextStreamResponse("application/json", response.toString());
	}

}
