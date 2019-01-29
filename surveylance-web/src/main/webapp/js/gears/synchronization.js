// messages requiring localization
var SYNCHRONIZATION_COMPLETED;
var SYNCHRONIZATION_ERROR;

/* -----------------------------------------------------------------------------
 * Offline to online synchronization API
 * -----------------------------------------------------------------------------
 */

function answerToJSON(answer) {
	return {
		persistentId: answer.persistentId,
		gearsId: answer.gearsId,
		modifications: answer.modifications,
		position: answer.position,
		value: answer.value
	};
}

function questionToJSON(question) {
	var answers = question.answers;
	var jsonAnswers = new Array();

	for (var i = 0; i < answers.length; i++) {
		var answer = answers[i];
		var jsonAnswer = answerToJSON(answer);
		jsonAnswers.push(jsonAnswer);
	}

	return {
		persistentId: question.persistentId,
		gearsId: question.gearsId,
		modifications: question.modifications,
		content: question.content,
		type: question.type,
		kind: question.kind,
		answers: jsonAnswers
	};
}

function componentToJSON(component) {
	var question = component.question;

	var jsonQuestion = null;
	var jsonComponents = null;

	if (question != null) {
		jsonQuestion = questionToJSON(question);
	}
	else {
		var components = component.childs;
		jsonComponents = new Array();

		for (var i = 0; i < components.length; i++) {
			var child = components[i];
			var jsonChildComponent = componentToJSON(child);
			jsonComponents.push(jsonChildComponent);
		}
	}

	return {
		persistentId: component.persistentId,
		gearsId: component.gearsId,
		modifications: component.modifications,
		position: component.position,
		components: jsonComponents,
		question: jsonQuestion
	};
}

/**
 * Creates containing survey structure which then should be used in
 * dojo.toJson() method to get string representation of this object in JSON
 * format.
 */
function surveyToJSON(survey) {
	return {
		persistentId: survey.persistentId,
		gearsId: survey.gearsId,
		modifications: survey.modifications,
		name: survey.name,
		description: survey.description,
		expirationDate: survey.expirationDate,
		rootComponent: componentToJSON(survey.rootComponent)
	};
}

function restoreAnswerPersistentId(jsonAnswer) {
	var gears_answer = new gears.Answer(jsonAnswer.gearsId);
	gears_answer.persistentId = jsonAnswer.persistentId;
	gears_answer.save();
}

function restoreQuestionPersistentId(jsonQuestion) {
	var jsonAnswers = jsonQuestion.answers;

	for (var i = 0; i < jsonAnswers.length; i++) {
		var jsonAnswer = jsonAnswers[i];
		restoreAnswerPersistentId(jsonAnswer);
	}

	var gears_question = new gears.Question(jsonQuestion.gearsId);
	gears_question.persistentId = jsonQuestion.persistentId;
	gears_question.save();
}

function restoreComponentPersistentId(jsonComponent) {
	var question = component.question;

	var jsonQuestion = jsonComponent.question;
	var jsonComponents = jsonComponent.components;

	if (jsonQuestion != null) {
		restoreQuestionPersistentId(jsonQuestion);
	}
	else {
		for (var i = 0; i < jsonComponents.length; i++) {
			var jsonChildComponent = jsonComponents[i];
			restoreComponentPersistentId(jsonChildComponent);
		}
	}

	var gears_component = new gears.Component(jsonComponent.gearsId);
	gears_component.persistentId = jsonComponent.persistentId;
	gears_component.save();
}

function restoreSurveyPersistentId(jsonSurvey) {
	restoreComponentPersistentId(jsonSurvey.rootComponent);

	var gears_survey = new gears.Survey(jsonSurvey.gearsId);
	gears_survey.persistentId = jsonSurvey.persistentId;
	gears_survey.save();
}

/**
 * Calls synchronization controller by XHR with POST parameter "survey"
 * which is string with survey structure in JSON.
 */
function gears_synchronizeSurvey() {
	var jsonSurvey = surveyToJSON(survey);
	console.debug("Survey in JSON format:\n" + dojo.toJson(jsonSurvey));

	dojo.xhrPost({
		url: "../../survey/synchronize/" + survey.persistentId,
		content: {
			survey: dojo.toJson(jsonSurvey)
		},
		load: function(response, ioArgs) {
			console.debug("synchronized: " + response.synchronized);

			if (response.synchronized) {
				console.debug(dojo.toJson(response.survey));
				restoreSurveyPersistentId(response.survey);
				new MessageBox(SYNCHRONIZATION_COMPLETED).show();
			}
			else
				new MessageBox(SYNCHRONIZATION_ERROR, "error").show();

			return response;
		},
		error: function(response, ioArgs) {
			console.debug("ERROR: HTTP status code is ", ioArgs.xhr.status);

			if (ioArgs.xhr.status != 200)
				new MessageBox(SYNCHRONIZATION_ERROR, "error").show();

			return response;
		},
		handleAs: "json"
	});
}

/* -----------------------------------------------------------------------------
 * Online to offline synchronization API
 * -----------------------------------------------------------------------------
 */

gears.answer.restoreFromJSON = function(jsonAnswer, gears_question) {
	// Save jsonAnswer to DB
	var gears_answer = new gears.Answer({
		persistentId: jsonAnswer["persistentId"],
		modifications: jsonAnswer["modifications"],
		position: jsonAnswer["position"],
		isText: jsonAnswer["isText"],
		textValue: jsonAnswer["textValue"],
		numericValue: jsonAnswer["numericValue"],
		Question: gears_question
	});
	gears_answer.save();
}

gears.question.restoreFromJSON = function(jsonQuestion, gears_component) {
	// Save jsonQuestion to DB.
	var gears_question = new gears.Question({
		persistentId: jsonQuestion["persistentId"],
		modifications: jsonQuestion["modifications"],
		type: jsonQuestion["type"],
		kind: jsonQuestion["kind"],
		content: jsonQuestion["content"],
		Component: gears_component
	});
	gears_question.save();

	// Restore answers associated with jsonQuestion to DB.
	for (var i = 0; i < jsonQuestion["answers"].length; i++) {
		var jsonAnswer = jsonQuestion["answers"][i];
		gears.answer.restoreFromJSON(jsonAnswer, gears_question);
	}
}

gears.component.restoreFromJSON = function(jsonComponent, gears_parentComponent) {
	// Save jsonComponent to DB.
	var gears_component = new gears.Component({
		persistentId: jsonComponent["persistentId"],
		modifications: jsonComponent["modifications"],
		position: jsonComponent["position"],
		Component: gears_parentComponent
	});
	gears_component.save();

	// Restore question object associated with jsonComponent object.
	if (jsonComponent["question"]) {
		gears.question.restoreFromJSON(jsonComponent["question"],
				gears_component);
	}
	
	// Restore children of jsonComponent component.
	for (var i = 0; i < jsonComponent["components"].length; i++) {
		var jsonChildComponent = jsonComponent["components"][i];
		gears.component.restoreFromJSON(jsonChildComponent, gears_component);
	}
	
	// Return ID of new survey's root component.
	return gears_component.rowid;
}

gears.survey.restoreFromJSON = function(jsonSurvey) {
	// Remove old survey object from DB.
	gears.component.removeById(survey.rootComponent.gearsId);
	
	// Restore jsonSurvey structure to DB.
	var rootComponentID = gears.component.restoreFromJSON(
			jsonSurvey["rootComponent"], null);
	
	// Update survey object in DB.
	var gears_survey = gears.survey.findByPersistentId(
			jsonSurvey["persistentId"]);
	gears_survey.Component = rootComponentID;
	gears_survey.save();
}

function synchronizeSurvey() {
	console.debug("Downloading survey from server");

	dojo.xhrPost({
		url: "../../survey/synchronize/" + survey.persistentId,
		load: function(response, ioArgs) {
			console.debug("synchronized: " + response.synchronized);

			if (response.synchronized) {
				console.debug("received survey:\n"
						+ dojo.toJson(response.survey));
				gears.survey.restoreFromJSON(response.survey);
				new MessageBox(SYNCHRONIZATION_COMPLETED).show();
			}
			else
				new MessageBox(SYNCHRONIZATION_ERROR, "error").show();

			return response;
		},
		error: function(response, ioArgs) {
			console.debug("ERROR: HTTP status code is ", ioArgs.xhr.status);

			if (ioArgs.xhr.status != 200)
				new MessageBox(SYNCHRONIZATION_ERROR, "error").show();

			return response;
		},
		handleAs: "json"
	});
}
