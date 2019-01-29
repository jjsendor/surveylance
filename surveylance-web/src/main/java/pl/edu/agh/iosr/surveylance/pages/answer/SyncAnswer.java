package pl.edu.agh.iosr.surveylance.pages.answer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.service.SyncService;

/**
 * Tapestry controller class responsible for synchronization of answers.
 *
 * @author kuba
 */
@ContentType("application/json")
public class SyncAnswer {

	private static final Logger logger =
		Logger.getLogger(SyncAnswer.class);

	@Inject
	private Request request;

	@Inject
	private SyncService syncService;

	@Inject
	private QuestionDAO questionDAO;

	/**
	 * Compares answers in local Gears database with one on server using
	 * {@link SyncService}.
	 * Parameters from request:
	 * <dl>
	 *   <dt>ids</dt>
	 *   <dd>list with answers ids and theirs modifications values</dd>
	 * </dl>
	 *
	 * @return	JSONObject with list of modifications:
	 * <pre>
	 * {
	 *     created: [],	// created answers JSON structures
	 *     removed: [],	// removed answers ids
	 *     updated: []	// updated answers JSON objects
	 * }
	 * </pre>
	 */
	Object onActivate(long questionId) {
		String modificationsStr = request.getParameter("ids");
		JSONArray modifications = new JSONArray(modificationsStr);
		Map<Long, Integer> modificationsMap = new HashMap<Long, Integer>();

		for (int i = 0; i < modifications.length(); i++) {
			JSONObject modification = modifications.getJSONObject(i);
			modificationsMap.put(modification.getLong("id"),
					modification.getInt("modifications"));
		}

		Question question = questionDAO.findById(questionId, false);
		JSONObject response = null;

		try {
			response = syncService.getAnswersModifications(
					question, modificationsMap);
		} catch (RuntimeException e) {
			logger.warn("Answers were not synchronize.", e);
		}

		return new TextStreamResponse("application/json", response.toString());
	}

}
