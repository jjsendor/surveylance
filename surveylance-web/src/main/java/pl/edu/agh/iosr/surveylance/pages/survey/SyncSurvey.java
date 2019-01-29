package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.service.SyncService;
import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

/**
 * Tapestry controller class responsible for synchronization of surveys.
 *
 * @author kuba
 */
@ContentType("application/json")
public class SyncSurvey {

	private static final Logger logger =
		Logger.getLogger(SyncSurvey.class);

	@Inject
	private Request request;

	@Inject
	private SyncService syncService;

	@ApplicationState(create = false)
	private UserSessionInfo userInfo;

	/**
	 * Compares surveys in local Gears database with one on server using
	 * {@link SyncService}.
	 * Parameters from request:
	 * <dl>
	 *   <dt>ids</dt>
	 *   <dd>list with surveys ids and theirs modifications values</dd>
	 * </dl>
	 *
	 * @return	JSONObject with list of modifications:
	 * <pre>
	 * {
	 *     created: [],	// created surveys JSON structures
	 *     removed: [],	// removed surveys ids
	 *     updated: []	// updated surveys JSON objects
	 * }
	 * </pre>
	 */
	Object onActivate() {
		String modificationsStr = request.getParameter("ids");
		JSONArray modifications = new JSONArray(modificationsStr);
		Map<Long, Integer> modificationsMap = new HashMap<Long, Integer>();

		for (int i = 0; i < modifications.length(); i++) {
			JSONObject modification = modifications.getJSONObject(i);
			modificationsMap.put(modification.getLong("id"),
					modification.getInt("modifications"));
		}

		JSONObject response = null;

		try {
			response = syncService.getSurveysModifications(
					userInfo.getUser(), modificationsMap);
		} catch (RuntimeException e) {
			logger.warn("Surveys were not synchronize.", e);
		}

		return new TextStreamResponse("application/json", response.toString());
	}

}
