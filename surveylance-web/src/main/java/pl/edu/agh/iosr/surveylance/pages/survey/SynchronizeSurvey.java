package pl.edu.agh.iosr.surveylance.pages.survey;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.SurveyRestorationService;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * Controller class for surveys synchronizations purposes.
 * 
 * @author kuba
 */
@ContentType("application/json")
public class SynchronizeSurvey {

	private static final Logger logger = Logger
			.getLogger(SynchronizeSurvey.class);

	@Inject
	private Request request;

	@Inject
	private SurveyRestorationService restorationService;

	@Inject
	private SurveyManager surveyManager;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * Synchronizes survey. Parameters from request:
	 * <dl>
	 * <dt>survey</dt>
	 * <dd>survey structure in JSON format</dd>
	 * </dl>
	 * 
	 * @param surveyId
	 *            id of survey
	 * 
	 * @return JSONObject with info about synchronization:
	 * 
	 *         <pre>
	 * {
	 *     synchronized: true/false, // was survey synchronized successfully?
	 * }
	 * </pre>
	 */
	Object onActivate(long surveyId) {
		String jsonSurveyStr = request.getParameter("survey");

		boolean isSynchronized = false;
		JSONObject jsonSurvey = null;

		if (surveyManager.isSurveyOwner(surveyId, userInfo.getUser())) {
			if (jsonSurveyStr == null) {
				// server -> Gears synchronization
				jsonSurvey = restorationService.surveyToJSON(surveyId);
				isSynchronized = true;
			} else {
				// Gears -> server synchronization
				try {
					jsonSurvey = new JSONObject(jsonSurveyStr);
					jsonSurvey = restorationService.restoreSurvey(surveyId,
							jsonSurvey);
					isSynchronized = true;
				} catch (RuntimeException e) {
					logger.error("Syntax error in source string", e);
				}
			}
		} else {
			logger.info("User " + userInfo.getUser().getGoogleId()
					+ " has no rights to synchronize survey with id "
					+ surveyId);
		}

		// TODO: send also survey, this time with persistentIds
		JSONObject response = new JSONObject();
		response.put("synchronized", isSynchronized);
		response.put("survey", jsonSurvey);

		return new TextStreamResponse("application/json", response.toString());
	}

}
