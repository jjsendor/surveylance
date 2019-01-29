package pl.edu.agh.iosr.surveylance.service;

import java.util.Map;

import org.apache.tapestry5.json.JSONObject;

import pl.edu.agh.iosr.surveylance.dao.AnswerDAO;
import pl.edu.agh.iosr.surveylance.dao.ComponentDAO;
import pl.edu.agh.iosr.surveylance.dao.QuestionDAO;
import pl.edu.agh.iosr.surveylance.dao.SurveyDAO;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.User;

/**
 * Service used for synchronization with Google Gears local database.
 *
 * @author kuba
 *
 */
public interface SyncService {

	/**
	 * Sets {@link SurveyDAO} data access object which is used by this service.
	 *
	 * @param surveyDAO
	 *            data access object for survey
	 */
	public void setSurveyDAO(SurveyDAO surveyDAO);

	/**
	 * Sets {@link ComponentDAO} data access object which is used by this
	 * service.
	 *
	 * @param componentDAO
	 *            data access object for component
	 */
	public void setComponentDAO(ComponentDAO componentDAO);

	/**
	 * Sets {@link QuestionDAO} data access object which is used by this
	 * service.
	 *
	 * @param questionDAO
	 *            data access object for question
	 */
	public void setQuestionDAO(QuestionDAO questionDAO);

	/**
	 * Sets {@link AnswerDAO} data access object which is used by this service.
	 *
	 * @param answerDAO
	 *            data access object for answer
	 */
	public void setAnswerDAO(AnswerDAO answerDAO);

	/**
	 * Sets {@link SurveyRestorationService} used by this service.
	 *
	 * @param restorationService
	 *            survey restoration service
	 */
	public void setRestorationService(
			SurveyRestorationService restorationService);

	/**
	 * Compares list of surveys ids and modifications values from map and
	 * creates JSON object representing all changes that are between map and
	 * information stored in server database.
	 *
	 * @param user				user whose surveys are being compared
	 * @param modificationsMap	map with surveys ids and modifications values
	 *
	 * @return JSON object in form of:
	 * <pre>
	 * {
	 *     created: []	// array with all created surveys structures
	 *     removed: []	// array with all removed surveys ids
	 *     updated: []	// array with all updated surveys JSON objects
	 * }
	 * </pre>
	 */
	public JSONObject getSurveysModifications(User user,
			Map<Long, Integer> modificationsMap);

	/**
	 * Compares list of components ids and modifications values from map and
	 * creates JSON object representing all changes that are between map and
	 * information stored in server database.
	 *
	 * @param parent			parent component entity from which components
	 * 							are being compared
	 * @param modificationsMap	map with components ids and modifications values
	 *
	 * @return JSON object in form of:
	 * <pre>
	 * {
	 *     created: []	// array with all created components structures
	 *     removed: []	// array with all removed components ids
	 *     updated: []	// array with all updated components JSON objects
	 * }
	 * </pre>
	 */
	public JSONObject getComponentsModifications(Component parent,
			Map<Long, Integer> modificationsMap);

	/**
	 * Compares list of surveys ids and questions values from map and
	 * creates JSON object representing all changes that are between map and
	 * information stored in server database.
	 *
	 * @param parent			parent component entity from which questions are
	 * 							being compared
	 * @param modificationsMap	map with questions ids and modifications values
	 *
	 * @return JSON object in form of:
	 * <pre>
	 * {
	 *     created: []	// array with all created questions structures
	 *     removed: []	// array with all removed questions ids
	 *     updated: []	// array with all updated questions JSON objects
	 * }
	 * </pre>
	 */
	public JSONObject getQuestionsModifications(Component parent,
			Map<Long, Integer> modificationsMap);

	/**
	 * Compares list of surveys ids and answers values from map and
	 * creates JSON object representing all changes that are between map and
	 * information stored in server database.
	 *
	 * @param question			question entity from which answers are being
	 * 							compared
	 * @param modificationsMap	map with answers ids and modifications values
	 *
	 * @return JSON object in form of:
	 * <pre>
	 * {
	 *     created: []	// array with all created answers structures
	 *     removed: []	// array with all removed answers ids
	 *     updated: []	// array with all updated answers JSON objects
	 * }
	 * </pre>
	 */
	public JSONObject getAnswersModifications(Question question,
			Map<Long, Integer> modificationsMap);

}
