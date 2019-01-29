/**
 * @namespace Downloads from server all information about surveys changes and
 * updates that information in local Google Gears database.
 *
 * @author kuba
 */

var sync = {
		/**
		 * Number of downloads actions.
		 */
		downloads: 0,
		/**
		 * Finishes downloading if all download actions are finished.
		 */
		finishDownloading: function() {
			if (sync.downloads == 0)
				dojox.off.sync.finishedDownloading();
		},
		/**
		 * Creates list with with persistent ids and modifications for all
		 * Gears entites in <code>gears_entities</code> array.
		 * 
		 * @returns	{Array}	list with objects holding persistent id and
		 * 					modifications
		 */
		modificationsList: function(gears_entities) {
			var list = new Array();
			
			for (var i = 0; i < gears_entities.length; i++) {
				list.push({
					id: gears_entities[i].persistentId,
					modifications: gears_entities[i].modifications
				});
			}
			
			return list;
		},
		/**
		 * @namespace Holds all methods related to downloading surveys updates
		 * from server.
		 */
		survey: {
			/**
			 * Creates list with persistent ids and modifications for all
			 * Gears surveys objects.
			 * 
			 * @returns	{Array}	list with objects holding persistent id and
			 * 					modifications
			 */
			modificationsList: function() {
				var gears_surveys = gears.survey.findAll();
				return sync.modificationsList(gears_surveys);
			},
			download: function() {
				sync.downloads++;
				dojo.xhrPost({
					url: CONTEXT + "survey/sync/",
					content: {
						ids: dojo.toJson(sync.survey.modificationsList())
					},
					load: function(response, ioArgs) {
						sync.downloads--;
						sync.survey.create(response.created);
						sync.survey.remove(response.removed);
						sync.survey.update(response.updated);

						sync.finishDownloading();

						return response;
					},
					error: function(response, ioArgs) {
						var message = response.message || response;
						message = "Unable to download surveys: " + message;
						dojox.off.sync.finishedDownloading(false, message);
						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * Creates all surveys using theirs JSON representation stored
			 * in <code>jsonSurveys</code> array.
			 * 
			 * @param {Array} jsonSurveys	array with JSON objects holding
			 * 								survey structure
			 */
			create: function(jsonSurveys) {
				console.log("Sync: Created surveys: " + jsonSurveys.length);
				for (var i = 0; i < jsonSurveys.length; i++)
					gears.survey.createFromJSON(jsonSurveys[i]);
			},
			/**
			 * Removes all surveys from local DB which persistent id is on
			 * <code>ids</code> list.
			 * 
			 * @param {Array} ids	array with surveys' persistent ids
			 */
			remove: function(ids) {
				console.log("Sync: Removed surveys: " + ids.length);
				for (var i = 0; i < ids.length; i++) {
					console.log("Sync: Removing survey with persistentId: " + ids[i]);
					var gears_survey = gears.survey.findByPersistentId(ids[i]);
					gears.survey.remove(gears_survey);
				}
			},
			update: function(jsonSurveys) {
				console.log("Sync: Updated surveys: " + jsonSurveys.length);
				for (var i = 0; i < jsonSurveys.length; i++) {
					var jsonSurvey = jsonSurveys[i];
					gears.survey.updateFromJSON(jsonSurvey);
					var gears_survey =
						gears.survey.findByPersistentId(jsonSurvey.id);
					sync.component.download(gears_survey.Component);
				}
			}
		},
		/**
		 * @namespace Holds all methods related to downloading components
		 * updates from server.
		 */
		component: {
			/**
			 * Creates list with persistent ids and modifications for all
			 * Gears components objects from given <code>gears_parent</code>
			 * component.
			 * 
			 * @param {gears.Component} gears_parent	parent component entity
			 * 
			 * @returns	{Array}	list with objects holding persistent id and
			 * 					modifications
			 */
			modificationsList: function(gears_parent) {
				var gears_components =
					gears.component.findByComponent(gears_parent);
				return sync.modificationsList(gears_components);
			},
			download: function(gears_component) {
				sync.downloads++;
				dojo.xhrPost({
					url: CONTEXT + "component/sync/"
						+ gears_component.persistentId,
					content: {
						ids: dojo.toJson(sync.component.modificationsList(
								gears_component))
					},
					load: function(response, ioArgs) {
						sync.downloads--;
						sync.component.create(response.created,
								gears_component);
						sync.component.remove(response.removed);
						sync.component.update(response.updated);

						sync.finishDownloading();

						return response;
					},
					error: function(response, ioArgs) {
						var message = response.message || response;
						message = "Unable to download components: " + message;
						dojox.off.sync.finishedDownloading(false, message);
						return response;
					},
					handleAs: "json"
				});
			},
			create: function(jsonComponents, gears_component) {
				console.log("Sync: Created components: " + jsonComponents.length);
				for (var i = 0; i < jsonComponents.length; i++) {
					gears.component.createFromJSON(
							jsonComponents[i], gears_component);
				}
			},
			remove: function(ids) {
				console.log("Sync: Removed components: " + ids.length);
				for (var i = 0; i < ids.length; i++) {
					console.log("Sync: Removing component with persistentId: " + ids[i]);
					var gears_component =
						gears.component.findByPersistentId(ids[i]);
					gears.component.remove(gears_component);
				}
			},
			update: function(jsonComponents) {
				console.log("Sync: Updated components: " + jsonComponents.length);
				for (var i = 0; i < jsonComponents.length; i++) {
					gears.component.updateFromJSON(jsonComponents[i]);
					var gears_component = gears.component.findByPersistentId(
							jsonComponents[i].id);
					
					if (jsonComponents[i].question)
						sync.question.download(gears_component);
					
					sync.component.download(gears_component);
				}
			}
		},
		/**
		 * @namespace Holds all methods related to downloading questions updates
		 * from server.
		 */
		question: {
			/**
			 * Creates object with persistent ids and modifications for 
			 * question asociated with given <code>gears_component</code>
			 * component.
			 * 
			 * @param {gears.Component} gears_component	component entity
			 * 
			 * @returns	{Array}	list with objects holding persistent id and
			 * 					modifications
			 */
			modificationsList: function(gears_component) {
				var gears_question = gears.question.findByComponent(gears_component);
				var gears_questions = new Array();
				gears_questions.push(gears_question);
				return sync.modificationsList(gears_questions).pop;
			},
			download: function(gears_component) {
				sync.downloads++;
				dojo.xhrPost({
					url: CONTEXT + "question/sync/"
						+ gears_component.persistentId,
					content: {
						ids: dojo.toJson(sync.question.modificationsList(
								gears_component))
					},
					load: function(response, ioArgs) {
						sync.downloads--;
						sync.question.update(response.updated);

						sync.finishDownloading();
	
						return response;
					},
					error: function(response, ioArgs) {
						var message = response.message || response;
						message = "Unable to download questions: " + message;
						dojox.off.sync.finishedDownloading(false, message);
						return response;
					},
					handleAs: "json"
				});
			},
			update: function(jsonQuestions) {
				console.log("Sync: Updated questions: " + jsonQuestions.length);
				var jsonQuestion = jsonQuestions[0];
				gears.question.updateFromJSON(jsonQuestion);
				var gears_question = gears.question.findByPersistentId(
						jsonQuestion.id);
				
				sync.answer.download(gears_question);
			}
		},
		/**
		 * @namespace Holds all methods related to downloading answers updates
		 * from server.
		 */
		answer: {
			/**
			 * Creates list with persistent ids and modifications for all
			 * Gears answers objects from given <code>gears_question</code>.
			 * 
			 * @param {gears.Question} gears_question	question entity
			 * 
			 * @returns	{Array}	list with objects holding persistent id and
			 * 					modifications
			 */
			modificationsList: function(gears_question) {
				var gears_answers = gears.answer.findByQuestion(gears_question);
				return sync.modificationsList(gears_answers);
			},
			download: function(gears_question) {
				sync.downloads++;
				dojo.xhrPost({
					url: CONTEXT + "answer/sync/"
						+ gears_question.persistentId,
					content: {
						ids: dojo.toJson(sync.answer.modificationsList(
								gears_question))
					},
					load: function(response, ioArgs) {
						sync.downloads--;
						sync.answer.create(response.created, gears_question);
						sync.answer.remove(response.removed);
						sync.answer.update(response.updated);

						sync.finishDownloading();

						return response;
					},
					error: function(response, ioArgs) {
						var message = response.message || response;
						message = "Unable to download answers: " + message;
						dojox.off.sync.finishedDownloading(false, message);
						return response;
					},
					handleAs: "json"
				});
			},
			create: function(jsonAnswers, gears_question) {
				console.log("Sync: Created answers: " + jsonAnswers.length);
				for (var i = 0; i < jsonAnswers.length; i++)
					gears.answer.createFromJSON(jsonAnswers[i], gears_question);
			},
			remove: function(ids) {
				console.log("Sync: Removed answers: " + ids.length);
				for (var i = 0; i < ids.length; i++) {
					console.log("Sync: Removing answer with persistentId: " + ids[i]);
					var gears_answer = gears.answer.findByPersistentId(ids[i]);
					gears.answer.remove(gears_answer);
				}
			},
			update: function(jsonAnswers) {
				console.log("Sync: Updated answers: " + jsonAnswers.length);
				for (var i = 0; i < jsonAnswers.length; i++)
					gears.answer.updateFromJSON(jsonAnswers[i]);
			}
		}
};

/**
 * Creates gears.Answer object from <code>jsonAnswer</code> and stores it
 * in Gears local database.
 *
 * @param	jsonAnswer	JSON representation of answer
 * @param	gears_question	{gears.Question}	answer's question
 */
gears.answer.createFromJSON = function(jsonAnswer, gears_question) {
	// store answer from jsonAnswer in local DB
	var gears_answer = new gears.Answer({
		persistentId: jsonAnswer.id,
		modifications: jsonAnswer.modifications,
		position: jsonAnswer.position,
		isText: jsonAnswer.isText,
		textValue: jsonAnswer.textValue,
		numericValue: jsonAnswer.numericValue,
		Question: gears_question
	});
	gears_answer.save();

	console.log("Sync: Answer " + jsonAnswer.id + " created from JSON");
};

/**
 * Creates gears.Question object from <code>jsonQuestion</code> and stores it
 * in Gears local database.
 *
 * @param	jsonQuestion	JSON representation of question
 * @param	gears_component	{gears.Component}	question's parent component
 */
gears.question.createFromJSON = function(jsonQuestion, gears_component) {
	// store question from jsonQuestion in local DB
	var gears_question = new gears.Question({
		persistentId: jsonQuestion.id,
		modifications: jsonQuestion.modifications,
		type: jsonQuestion.type,
		kind: jsonQuestion.kind,
		content: jsonQuestion.content,
		Component: gears_component
	});
	gears_question.save();

	console.log("Sync: Question " + jsonQuestion.id + " created from JSON");

	var jsonAnswers = jsonQuestion.answers;
	
	// store answers associated with jsonQuestion in local DB
	for (var i = 0; i < jsonAnswers.length; i++)
		gears.answer.createFromJSON(jsonAnswers[i], gears_question);
};

/**
 * Creates gears.Component object from <code>jsonComponent</code> and stores it
 * in Gears local database.
 *
 * @param	jsonComponent	JSON representation of component
 * @param	gears_parent	{gears.Component}	component's parent component
 */
gears.component.createFromJSON = function(jsonComponent, gears_parent) {
	// store component from jsonComponent in local DB
	var gears_component = new gears.Component({
		persistentId: jsonComponent.id,
		modifications: jsonComponent.modifications,
		position: jsonComponent.position,
		Component: gears_parent
	});
	gears_component.save();

	console.log("Sync: Component " + jsonComponent.id + " created from JSON");

	// store question object associated with jsonComponent in local DB
	if (jsonComponent.question)
		gears.question.createFromJSON(jsonComponent.question, gears_component);
	else {
		var jsonComponents = jsonComponent.components;
		
		// store all children components of jsonComponent in local DB 
		for (var i = 0; i < jsonComponents.length; i++)
			gears.component.createFromJSON(jsonComponents[i], gears_component);
	}
};

/**
 * Creates gears.Survey object from <code>jsonSurvey</code> and stores it
 * in Gears local database.
 *
 * @param	jsonSurvey	JSON representation of survey
 */
gears.survey.createFromJSON = function(jsonSurvey) {
	var jsonRootComponent = jsonSurvey.rootComponent;
	
	var rootComponent = new Component(
			null,
			jsonRootComponent.modifications,
			jsonRootComponent.position,
			null,
			jsonRootComponent.id);
	
	var survey = new Survey(
			jsonSurvey.name,
			jsonSurvey.modifications,
			jsonSurvey.description,
			jsonSurvey.expirationDate,
			rootComponent,
			jsonSurvey.id);
	
	// creates survey and rootComponent in local DB
	var gears_survey = gears.survey.create(survey);
	console.log("Sync: Survey " + jsonSurvey.id + " created from JSON");

	var jsonComponents = jsonRootComponent.components;
	
	// store each component from jsonSurvey rootComponent in DB
	for (var i = 0; i < jsonComponents.length; i++) {
		gears.component.createFromJSON(jsonComponents[i],
				gears_survey.Component);
	}
};

/**
 * Updates all attributes in gears.Answer object asociated with
 * <code>jsonAnswer</code> object.
 *
 * @param	jsonAnswer	JSON representation of answer
 */
gears.answer.updateFromJSON = function(jsonAnswer) {
	var gears_answer = gears.answer.findByPersistentId(jsonAnswer.id);

	gears_answer.persistentId = jsonAnswer.id;
	gears_answer.modifications = jsonAnswer.modifications;
	gears_answer.position = jsonAnswer.position;
	gears_answer.isText = jsonAnswer.isText;
	gears_answer.textValue = jsonAnswer.textValue;
	gears_answer.numericValue = jsonAnswer.numericValue;

	gears_answer.save();

	console.log("Sync: Answer " + jsonAnswer.id + " updated from JSON");
};

/**
 * Updates all attributes in gears.Question object asociated with
 * <code>jsonQuestion</code> object.
 *
 * @param	jsonQuestion	JSON representation of question
 */
gears.question.updateFromJSON = function(jsonQuestion) {
	var gears_question = gears.question.findByPersistentId(jsonQuestion.id);

	gears_question.modifications = jsonQuestion.modifications;
	gears_question.type = jsonQuestion.type;
	gears_question.kind = jsonQuestion.kind;
	gears_question.content = jsonQuestion.content;

	gears_question.save();

	console.log("Sync: Question " + jsonQuestion.id + " updated from JSON");
};

/**
 * Updates all attributes in gears.Component object asociated with
 * <code>jsonComponent</code> object.
 *
 * @param	jsonComponent	JSON representation of component
 */
gears.component.updateFromJSON = function(jsonComponent) {
	var gears_component = gears.component.findByPersistentId(jsonComponent.id);

	gears_component.modifications = jsonComponent.modifications;
	gears_component.position = jsonComponent.position;

	gears_component.save();

	console.log("Sync: Component " + jsonComponent.id + " updated from JSON");
};

/**
 * Updates all attributes in gears.Survey object asociated with
 * <code>jsonSurvey</code> object.
 *
 * @param	jsonSurvey	JSON representation of survey
 */
gears.survey.updateFromJSON = function(jsonSurvey) {
	var gears_survey = gears.survey.findByPersistentId(jsonSurvey.id);

	gears_survey.name = jsonSurvey.name;
	gears_survey.modifications = jsonSurvey.modifications;
	gears_survey.description = jsonSurvey.description;
	gears_survey.expirationDate = jsonSurvey.expirationDate;

	gears_survey.save();

	console.log("Sync: Survey " + jsonSurvey.id + " updated from JSON");
};

gears.survey.markModified = function(gears_survey) {
	gears_survey.modifications++;
	gears_survey.save();
};

gears.component.markModified = function(gears_component) {
	gears_component.modifications++;
	gears_component.save();

	var gears_parent = gears_component.Component;

	// sometimes Component was not null, but its object was only with one
	// property (rowid) and rowid was null
	if (gears_parent && gears_parent.rowid) {
		// very strange, but seems to work (without getting gears_parent again),
		// gears_parent is object only with one property - rowid
		gears_parent = new gears.Component(gears_parent.rowid);
		gears.component.markModified(gears_parent);
	}
	else {
		var gears_survey = gears.survey.findByRootComponent(gears_component);

		if (gears_survey)
			gears.survey.markModified(gears_survey);
	}
};

gears.question.markModified = function(gears_question) {
	gears_question.modifications++;
	gears_question.save();

	var gears_component = gears_question.Component;

	if (gears_component)
		gears.component.markModified(gears_component);
};

gears.answer.markModified = function(gears_answer) {
	gears_answer.modifications++;
	gears_answer.save();

	var gears_question = gears_answer.Question;

	if (gears_question)
		gears.question.markModified(gears_question);
};
