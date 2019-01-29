package pl.edu.agh.iosr.surveylance.pages.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.service.SyncService;

/**
 * Tapestry controller class responsible for synchronization of components.
 *
 * @author kuba
 */
@ContentType("application/json")
public class SyncComponent {

	private static final Logger logger =
		Logger.getLogger(SyncComponent.class);

	@Inject
	private Request request;

	@Inject
	private SyncService syncService;

	@Inject
	private ComponentDAO componentDAO;

	/**
	 * Compares components in local Gears database with one on server using
	 * {@link SyncService}.
	 * Parameters from request:
	 * <dl>
	 *   <dt>ids</dt>
	 *   <dd>list with components ids and theirs modifications values</dd>
	 * </dl>
	 *
	 * @param	parentId	components' parent component id
	 *
	 * @return	JSONObject with list of modifications:
	 * <pre>
	 * {
	 *     created: [],	// created components JSON structures
	 *     removed: [],	// removed components ids
	 *     updated: []	// updated components JSON objects
	 * }
	 * </pre>
	 */
	Object onActivate(long parentId) {
		String modificationsStr = request.getParameter("ids");
		JSONArray modifications = new JSONArray(modificationsStr);
		Map<Long, Integer> modificationsMap = new HashMap<Long, Integer>();

		for (int i = 0; i < modifications.length(); i++) {
			JSONObject modification = modifications.getJSONObject(i);
			modificationsMap.put(modification.getLong("id"),
					modification.getInt("modifications"));
		}

		Component parent = componentDAO.findById(parentId, false);
		JSONObject response = null;

		try {
			response = syncService.getComponentsModifications(
					parent, modificationsMap);
		} catch (RuntimeException e) {
			logger.warn("Components were not synchronize.", e);
		}

		return new TextStreamResponse("application/json", response.toString());
	}

}
