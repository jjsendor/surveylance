package pl.edu.agh.iosr.surveylance.pages.survey;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Form;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.SurveyManager;
import pl.edu.agh.iosr.surveylance.service.SurveyResultManager;

public class SubmitSurvey {

	@Property
	private Form form;

	@SuppressWarnings("unused")
	@Property
	private boolean submitted;

	@SuppressWarnings("unused")
	@Property
	private String answerValue;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private SurveyManager surveyManager;

	@Inject
	private QuestionManager questionManager;

	@Inject
	private SurveyResultManager resultManager;

	private static final String X_COORDINATE_LABEL = "xCoordinate";

	private static final String Y_COORDINATE_LABEL = "yCoordinate";

	private static final Logger logger = Logger.getLogger(SubmitSurvey.class);

	void onActivate(String hashCode) {
		this.form = surveyManager.getFormByHash(hashCode);

		this.submitted = true;

		if (!form.getSubmitted()) {
			this.submitted = false;
			submitAnswers();
			surveyManager.invalidateInvitation(form);
		}
	}

	@SuppressWarnings("unchecked")
	private void submitAnswers() {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();

		Enumeration<String> parameterNames = request.getParameterNames();

		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();

			try {
				Question question = questionManager.getQuestion(Long
						.valueOf(parameterName));

				if (question.getKind() == QuestionKind.INPUT_TEXT) {
					String answerValue = request.getParameter(parameterName);
					resultManager.createResult(form, question, answerValue);
				} else {
					String[] parameterValues = request
							.getParameterValues(parameterName);

					for (String parameterValue : parameterValues) {
						Answer answer = questionManager.getAnswer(Long
								.valueOf(parameterValue));
						resultManager.createResult(form, question, answer);
					}
				}
			} catch (NumberFormatException ex) {
				if (parameterName.contains(X_COORDINATE_LABEL))
					this.form.setX(Double.valueOf(request
							.getParameter(parameterName)));
				if (parameterName.contains(Y_COORDINATE_LABEL))
					this.form.setY(Double.valueOf(request
							.getParameter(parameterName)));
			}
		}
	}

}
