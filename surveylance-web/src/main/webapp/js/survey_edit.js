dojo.require("dojo.dnd.Source");
dojo.require("dijit.InlineEditBox");
dojo.require("dijit.TitlePane");
dojo.require("dojo.parser");
dojo.require("dojox.off._common");
dojo.require("dojox.off.ui");
dojo.require("dojox.off.sync");

var rootComponent;
var survey;

var createdComponent;
var activeComponentsContainer;

var wipedOut = new Array();
var questions = new Array();

// messages requiring localization
var AJAX_ERROR;
var COMPONENT_DELETE_CONFIRM;
var ORDER_COMPONENT_GO_UP;
var ANSWER_EDIT_PROMPT;
var ANSWER_ADD;
var ANSWER_DELETE;
var DND_PROMPT;
var ORDER_COMPONENT_PROMPT;
var QUESTION_NEW;
var QUESTION_EDIT_PROMPT;

function wipe(id) {
	var animArgs = {
		node: id,
		duration: 500 // ms to run animation
	};
	
	if (wipedOut[id])
		dojo.fx.wipeIn(animArgs).play();
	else
		dojo.fx.wipeOut(animArgs).play();

	wipedOut[id] = !wipedOut[id];
}

function getComponentId(componentNode) {
	var id = componentNode.id;
	id = id.substr(id.indexOf("_") + 1);
	id = id.substr(id.indexOf("_") + 1);
	
	return id;
}

function handleAJAXError(ioArgs) {
	if (ioArgs.xhr.status != 200) {
		console.debug("ERROR: HTTP status code is ", ioArgs.xhr.status);
		var errorMessage = new MessageBox(AJAX_ERROR, "error");
		errorMessage.show();
	}
}

/* -----------------------------------------------------------------------------
 * Gears: restoring survey's components
 * -----------------------------------------------------------------------------
 */

// TODO move to gears.component namespace
function gears_restoreComponent(gears_component) {
	console.debug("Gears: restoring component with gearsId = "
			+ gears_component.rowid);
	console.debug("gears_component.Component.rowid = " + gears_component.Component.rowid);
	if (gears_component.Component.rowid) {
		parent = components[gears_component.Component.rowid];
		console.debug("gears.question.findByComponent(" + gears_component + ")");
		var gears_question = gears.question.findByComponent(gears_component);
		console.debug("gears_question = " + gears_question);
		if (gears_question) {
			console.debug("Creating component with id " + gears_component.rowid
					+ " for question with id " + gears_question.rowid
					+ ", parent id: " + gears_question.Component.rowid);
			type = ["question", gears_question.type, gears_question.kind];
			
			parent.dndSource.insertNodes(false, [{
				id: gears_component.rowid,
				type: type,
				content: gears_question.content
			}]);
			
			question = addComponent(type, parent.dndSource);
			question.gearsId = gears_question.rowid;
			question.parent.gearsId = gears_component.rowid;
			
			question.content = gears_question.content;
			
			//restoring answers
			var gears_answers = gears.answer.findByQuestion(gears_question);
			
			for (var i = 0; i < gears_answers.length; i++) {
				var gears_answer = gears_answers[i];
				var answer = new Answer(
					gears_answer.modifications,
					gears_answer.position,
					gears_answer.isText
							? gears_answer.textValue
							: gears_answer.numericValue
				);
				
				answer.gearsId = gears_answer.rowid;
				
				addAnswer(question, answer);
			}
			
			return;	// do not iterate through child components
		}
		else {
			console.debug("Creating component with id "
					+ gears_component.rowid + ", parent id: "
					+ gears_component.Component.rowid);
			type = ["component", "order"];
			parent.dndSource.insertNodes(false, [{
				id: gears_component.rowid,
				type: type,
				position: gears_component.position
			}]);
			
			component = addComponent(type, parent.dndSource);
			component.gearsId = gears_component.rowid;
			
			components[component.gearsId] = component;
		}
	}
	
	var gears_components = gears.component.findByComponent(gears_component).sort(
			gears_sortComponents);
	console.debug("Restoring child components [length = "
			+ gears_components.length + "]");
	for (var i = 0; i < gears_components.length; i++)
		gears_restoreComponent(gears_components[i]);
}

function gears_sortComponents(gears_component1, gears_component2) {
	return gears_component1.position - gears_component2.position;
}

/* -----------------------------------------------------------------------------
 * XHR actions
 * -----------------------------------------------------------------------------
 */

/**
 * @namespace Holds XHR requests.
 */
var xhr = {
		/**
		 * @namespace Holds XHR requests related to component.
		 */
		component: {
			/**
			 * XHR call that creates new component.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.component.CreateComponent.java
			 */
			create: function(component, actionLog) {
				dojo.xhrPost({
					url: "../../component/create/" + component.parent.persistentId,
					content: { position: component.position },
					load: function(response, ioArgs) {
						if (response.id > -1) {
							component.persistentId = response.id;
							component.position = response.position;

							if (gears.available() && component.gearsId) {
								var gears_component =
									new gears.Component(component.gearsId);
								gears_component.persistentId = component.persistentId;
								gears_component.position = component.position;
								gears_component.save();
							}
						}
						else {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to create component with gearsId "
								+ component.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * XHR call that removes component.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.component.DeleteComponent.java
			 */
			remove: function(component, actionLog) {
				dojo.xhrPost({
					url: "../../component/delete/" + component.persistentId,
					load: function(response, ioArgs) {
						console.debug("deleted: " + response.deleted);

						if (!response.deleted) {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to delete component with gearsId "
								+ component.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * XHR call that changes component position.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.component.MoveComponent.java
			 */
			move: function(component, actionLog) {
				dojo.xhrPost({
					url: "../../component/move/" + component.persistentId,
					content: { position: component.position },
					load: function(response, ioArgs) {
						console.debug("position: " + response.position);

						if (response.position > -1) {
							component.position = response.position;
						}
						else {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to move component with gearsId "
								+ component.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			}
		},
		/**
		 * @namespace Holds XHR requests related to question.
		 */
		question: {
			/**
			 * XHR call that creates new question.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.question.CreateQuestion.java
			 */
			create: function(question, actionLog) {
				var component = question.parent;

				dojo.xhrPost({
					url: "../../question/create/" + question.parent.parent.persistentId,
					content: {
						position: question.parent.position,
						type: question.type,
						kind: question.kind
					},
					load: function(response, ioArgs) {
						if (response.question.id > -1 && response.component.id) {
							question.persistentId = response.question.id;
							question.modifications = response.question.modifications;

							component.persistentId = response.component.id;
							component.modifications = response.question.modifications;
							component.position = response.component.position;

							if (gears.available() && question.gearsId) {
								var gears_question = new gears.Question(question.gearsId);
								gears_question.persistentId = question.persistentId;
								gears_question.modifications = question.persistentId;
								gears_question.save();

								var gears_component = gears_question.Component;
								gears_component.persistentId = component.persistentId;
								gears_component.modifications = component.modifications;
								gears_component.position = component.position;
								gears_component.save();
							}

							console.debug("parent id: " + response.component.id
									+ "\nparent modifications: " + response.component.modifications
									+ "\nparent position: " + response.component.position
									+ "\nparent parentId: " + response.component.parentId
									+ "\nquestion id: " + response.question.id
									+ "\nquestion modifications: " + response.question.modifications
									+ "\nquestion type: " + response.question.type
									+ "\nquestion kind: " + response.question.kind);
						}
						else {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to create question with gearsId "
								+ question.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * XHR call that removes question.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.question.DeleteQuestion.java
			 */
			remove: function(question, actionLog) {
				dojo.xhrPost({
					url: "../../question/delete/" + question.persistentId,
					load: function(response, ioArgs) {
						console.debug("delete: " + response.deleted);

						if (!response.deleted) {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to delete question with gearsId "
								+ question.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * XHR call that updates question.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.question.UpdateQuestion.java
			 */
			update: function(question, actionLog) {
				dojo.xhrPost({
					url: "../../question/update/" + question.persistentId,
					content: {
						content: question.content
					},
					load: function(response, ioArgs) {
						console.debug("updated: " + response.updated);

						if (!response.updated) {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();
						
						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to update question with gearsId "
								+ question.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			}
		},
		/**
		 * @namespace Holds XHR requests related to answer.
		 */
		answer: {
			/**
			 * XHR call that creates new answer.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.answer.CreateAnswer.java
			 */
			create: function(answer, actionLog) {
				var question = answer.question;

				dojo.xhrPost({
					url: "../../answer/create/" + question.persistentId,
					load: function(response, ioArgs) {
						if (response.id > -1) {
							answer.persistentId = response.id;
							answer.position = response.position;

							if (gears.available() && answer.gearsId) {
								var gears_answer = new gears.Answer(answer.gearsId);
								gears_answer.persistentId = answer.persistentId;
								gears_answer.position = answer.position;
								gears_answer.save();
							}

							console.debug("id: " + response.id
									+ "\nposition: " + response.position);
						}
						else {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to create answer with gearsId "
								+ answer.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * XHR call that removes answer.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.answer.DeleteAnswer.java
			 */
			remove: function(answer, actionLog) {
				dojo.xhrPost({
					url: "../../answer/delete/" + answer.persistentId,
					load: function(response, ioArgs) {
						console.debug("delete: " + response.deleted);

						if (!response.deleted) {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to delete answer with gearsId "
								+ answer.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			},
			/**
			 * XHR call that updates answer.
			 *
			 * @see pl.edu.agh.iosr.surveylance.pages.answer.UpdateAnswer.java
			 */
			update: function(answer, actionLog) {
				dojo.xhrPost({
					url: "../../answer/update/" + answer.persistentId,
					content: {
						value: answer.value
					},
					load: function(response, ioArgs) {
						console.debug("updated: " + response.updated);

						if (!response.updated) {
							if (dojox.off.sync.actions.isReplaying) {
								// TODO log "component not created message"
							}
						}

						if (dojox.off.sync.actions.isReplaying)
							actionLog.continueReplay();

						return response;
					},
					error: function(response, ioArgs) {
						handleAJAXError(ioArgs);

						if (dojox.off.sync.actions.isReplaying) {
							var msg = "Unable to update answer with gearsId "
								+ answer.gearsId + ".";
							actionLog.haltReplay(msg);
						}

						return response;
					},
					handleAs: "json"
				});
			}
		}
};

dojo.connect(dojox.off.ui, "onLoad", this, function() {
	dojo.connect(dojox.off.sync, "onSync", this,
		function(type) {
			switch (type) {
			case "start":
				//console.log("survey/edit Dojo Sync - start");
				//new MessageBox("syncing has started", "info").show();
				break;
			case "refreshFiles":
				//console.log("survey/edit Dojo Sync - refreshFiles");
				//new MessageBox("syncing will begin refreshing offline file cache", "info").show();
				break;
			case "upload":
				//console.log("survey/edit Dojo Sync - upload");
				//new MessageBox("syncing will begin uploading", "info").show();
				break;
			case "download":
				//console.log("survey/edit Dojo Sync - download");
				//new MessageBox("syncing will begin downloading", "info").show();
				//dojox.off.sync.finishedDownloading();
				break;
			case "finished":
				//console.log("survey/edit Dojo Sync - finished");
				//new MessageBox(SYNCHRONIZATION_COMPLETED).show();
				break;
			case "cancel":
				//console.log("survey/edit Dojo Sync - cancel");
				//new MessageBox("syncing canceled", "info").show();
				break;
			}
		}
	);

	dojo.connect(dojox.off.sync.actions, "onReplay", this,
		function(action, actionLog){
			switch (action.name) {
			// TODO extract to method: replay.component.create
			case "create component":
				var component = action.data;
				var parent = component.parent;

				if (!parent) {
					console.log("Replaying: create component aborted (no parent)");
					// quit current replay action and continue replaying
					actionLog.continueReplay();
					return;
				}

				if (!parent.persistentId) {
					var gears_parent = new gears.Component(parent.gearsId);

					if (!gears_parent.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					parent.persistentId = gears_parent.persistentId;
				}

				xhr.component.create(component, actionLog);
				break;
			// TODO extract to method: replay.component.remove
			case "delete component":
				var component = action.data;

				if (!component.persistentId) {
					var gears_component = new gears.Component(component.gearsId);

					if (!gears_component.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					component.persistentId = gears_component.persistentId;
				}
				
				xhr.component.remove(component, actionLog);
				break;
			// TODO extract to method: replay.component.move
			case "move component":
				var component = action.data;

				if (!component.persistentId) {
					var gears_component = new gears.Component(component.gearsId);

					if (!gears_component.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					component.persistentId = gears_component.persistentId;
				}

				xhr.component.move(component, actionLog);
				break;
			// TODO extract to method: replay.question.create
			case "create question":
				var question = action.data;
				var component = question.parent;
				var parent = component.parent;

				if (!parent) {
					// quit current replay action and continue replaying
					actionLog.continueReplay();
					return;
				}

				if (!parent.persistentId) {
					var gears_parent = new gears.Component(parent.gearsId);

					if (!gears_parent.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					parent.persistentId = gears_parent.persistentId;
				}
				
				xhr.question.create(question, actionLog);
				break;
			// TODO extract to method: replay.question.remove
			case "delete question":
				var question = action.data;

				if (!question.persistentId) {
					var gears_question = new gears.Question(question.gearsId);

					if (!gears_question.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					question.persistentId = gears_question.persistentId;
				}

				xhr.question.remove(question, actionLog);
				break;
			// TODO extract to method: replay.question.update
			case "update question":
				var question = action.data;

				if (!question.persistentId) {
					var gears_question = new gears.Question(question.gearsId);

					if (!gears_question.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					question.persistentId = gears_question.persistentId;
				}
				
				xhr.question.update(question, actionLog);
				break;
			// TODO extract to method: replay.answer.create
			case "create answer":
				var answer = action.data;
				var question = answer.question;

				if (!question) {
					// quit current replay action and continue replaying
					actionLog.continueReplay();
					return;
				}

				if (!question.persistentId) {
					var gears_question = new gears.Question(question.gearsId);

					if (!gears_question.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					question.persistentId = gears_question.persistentId;
				}

				xhr.answer.create(answer, actionLog);
				break;
			// TODO extract to method: replay.answer.remove
			case "delete answer":
				var answer = action.data;

				if (!answer.persistentId) {
					var gears_answer = new gears.Answer(answer.gearsId);

					if (!gears_answer.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					answer.persistentId = gears_answer.persistentId;
				}
				
				xhr.answer.remove(answer, actionLog);
				break;
			// TODO extract to method: replay.answer.update
			case "update answer":
				var answer = action.data;

				if (!answer.persistentId) {
					var gears_answer = new gears.Answer(answer.gearsId);

					if (!gears_answer.persistentId) {
						// quit current replay action and continue replaying
						actionLog.continueReplay();
						return;
					}

					answer.persistentId = gears_answer.persistentId;
				}

				xhr.answer.update(answer, actionLog);
				break;
			default:
				break;
			}
		}
	);
});

/* -----------------------------------------------------------------------------
 * Dispatchers (for XHR and Gears)
 * -----------------------------------------------------------------------------
 */

/**
 * Persists order component creation by updating Gears and/or calling server by
 * XHR.
 * 
 * @param	{Object} component	component object
 * 
 * @see		component.js
 */
function createOrderComponent(component) {
	if (gears.available())
		gears.component.create(component);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.component.create(component);
	else {
		// register offline action
		var actionData = new Component();

		if (component.parent) {
			actionData.parent = new Component();
			actionData.parent.persistentId = component.parent.persistentId;
			actionData.parent.gearsId = component.parent.gearsId;
		}

		actionData.gearsId = component.gearsId;
		actionData.position = component.position;

		var action = { name: "create component", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"create component\" registered.");
	}
}

/**
 * Persists order component deletion by updating Gears and/or calling server by
 * XHR.
 * 
 * @param	{Object} component	component object
 * 
 * @see		component.js
 */
function deleteOrderComponent(component) {
	console.debug("Deleting order component with persistent id "
			+ component.persistentId)
	if (gears.available())
		gears.component.removeById(component.gearsId);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.component.remove(component);
	else {
		// register offline action
		var actionData = new Component();

		actionData.gearsId = component.gearsId;
		actionData.persistentId = component.persistentId;

		var action = { name: "delete component", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"delete component\" registered.");
	}
}

/**
 * Persists order component movement by updating Gears and/or calling server by
 * XHR.
 * 
 * @param	{Object} component	component object
 * 
 * @see		component.js
 */
function moveOrderComponent(component) {
	if (gears.available())
		gears.component.move(component);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.component.move(component);
	else {
		// register offline action
		var actionData = new Component();

		actionData.gearsId = component.gearsId;
		actionData.persistentId = component.persistentId;
		actionData.position = component.position;

		var action = { name: "move component", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"move component\" registered.");
	}
}

function createForkComponent(component) {
	if (!gears.available() || !gears.isServedFromLocalStore()) {
	}
	if (gears.available()) {
		
	}
	//TODO
}

/**
 * Persists question creation by updating Gears and/or calling server by XHR.
 * 
 * @param	{Object} question	question object
 * 
 * @see		question.js
 */
function createQuestion(question) {
	if (gears.available())
		gears.question.create(question);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.question.create(question);
	else {
		// register offline action
		var actionData = new Question();

		if (question.parent) {
			var component = new Component();
			component.gearsId = question.parent.gearsId;
			component.position = question.parent.position;

			if (question.parent.parent) {
				var parent = new Component();
				parent.persistentId = question.parent.parent.persistentId;
				parent.gearsId = question.parent.parent.gearsId;
				component.parent = parent;
			}

			actionData.parent = component;
		}

		actionData.gearsId = question.gearsId;
		actionData.position = question.position;
		actionData.type = question.type;
		actionData.kind = question.kind;

		var action = { name: "create question", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"create question\" registered.");
	}
}

/**
 * Persists question deletion by updating Gears and/or calling server by XHR.
 * 
 * @param	{Object} question	question object
 * 
 * @see		question.js
 */
function deleteQuestion(question) {
	console.debug("Deleting question component with persistent id "
			+ question.persistentId);

	if (gears.available())
		gears.question.removeById(question.gearsId);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.question.remove(question);
	else {
		// register offline action
		var actionData = new Question();

		actionData.gearsId = question.gearsId;
		actionData.persistentId = question.persistentId;

		var action = { name: "delete question", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"delete question\" registered.");
	}
}

/**
 * Persists question update by updating Gears and/or calling server by XHR.
 * 
 * @param	{Object} question	question object
 * 
 * @see		question.js
 */
function updateQuestion(question) {
	if (gears.available())
		gears.question.update(question);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.question.update(question);
	else {
		// register offline action
		var actionData = new Question();

		actionData.gearsId = question.gearsId;
		actionData.persistentId = question.persistentId;
		actionData.content = question.content;

		var action = { name: "update question", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"update question\" registered.");
	}
}

/**
 * Persists answer creation by updating Gears and/or calling server by XHR.
 * 
 * @param	{Object} answer	answer object
 * 
 * @see		answer.js
 */
function createAnswer(answer) {
	if (gears.available())
		gears.answer.create(answer);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.answer.create(answer);
	else {
		// register offline action
		var actionData = new Answer();

		if (answer.question) {
			actionData.question = new Question();
			actionData.question.persistentId = answer.question.persistentId;
			actionData.question.gearsId = answer.question.gearsId;
		}

		actionData.gearsId = answer.gearsId;

		var action = { name: "create answer", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"create answer\" registered.");
	}
}

/**
 * Persists answer deletion by updating Gears and/or calling server by XHR.
 * 
 * @param	{Object} answer	answer object
 * 
 * @see		answer.js
 */
function deleteAnswer(answer) {
	if (gears.available())
		gears.answer.removeById(answer.gearsId);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.answer.remove(answer);
	else {
		// register offline action
		var actionData = new Answer();

		actionData.gearsId = answer.gearsId;
		actionData.persistentId = answer.persistentId;

		var action = { name: "delete answer", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"delete answer\" registered.");
	}
}

/**
 * Persists answer update by updating Gears and/or calling server by XHR.
 * 
 * @param	{Object} answer	answer object
 * 
 * @see		answer.js
 */
function updateAnswer(answer) {
	if (gears.available())
		gears.answer.update(answer);

	if (!gears.available() || !gears.isServedFromLocalStore())
		xhr.answer.update(answer);
	else {
		// register offline action
		var actionData = new Answer();

		actionData.gearsId = answer.gearsId;
		actionData.persistentId = answer.persistentId;
		actionData.value = answer.value;

		var action = { name: "update answer", data: actionData };
		dojox.off.sync.actions.add(action);

		console.log("Replay \"update answer\" registered.");
	}
}

/* -----------------------------------------------------------------------------
 * GUI helpers
 * -----------------------------------------------------------------------------
 */

/**
 * Adds component (either order component or question) and calls XHR/Gears
 * request to persist creation. This function is called after onDrop event
 * on dojo.dnd.Source objects (order component list).
 * 
 * This function calls addOrderComponent or addQuestion functions when
 * adding order component or question respectively.
 * 
 * @param	{Object} types	array of types describing created component
 * 							(e.g. question type and kind)
 */
function addComponent(types, target) {
	var componentNode = createdComponent;
	
	if (!target)
		target = dojo.dnd.manager().target;
	
	var position = target.getAllNodes().indexOf(componentNode);
	var tempId = getComponentId(componentNode);
	
	var component = new Component(tempId, 0, position);
	target.component.addChild(component);
	
	// remove d'n'd help info
	if (dojo.hasClass(target.node, "empty")) {
		dojo.removeClass(target.node, "empty");
		dojo.query(".dnd_info", target.node).style("display", "none");
	}
	
	if (dojo.indexOf(types, "component") != -1) {
		if (dojo.indexOf(types, "order") != -1) 
			return addOrderComponent(component, componentNode, target);
		else if (dojo.indexOf(types, "fork") != -1) {
		//			alert("funkcja nie jest jeszcze aktywna")
		//			return { node: null, data: null, type: null };
		//TODO implement fork component creator here
		}
	}
	else if (dojo.indexOf(types, "question") != -1) {
		var type, kind;
		console.debug("addComponent - question, types: " + types
				+ ", kinds: " + componentNode.className);
		
		if (dojo.indexOf(types, "numeric") != -1)
			type = "numeric";
		else if (dojo.indexOf(types, "text") != -1)
			type = "text";
		
		if (dojo.hasClass(componentNode, "radio"))
			kind = "radio";
		else if (dojo.hasClass(componentNode, "checkbox"))
			kind = "checkbox";
		else if (dojo.hasClass(componentNode, "input_text"))
			kind = "input_text";
		
		console.debug("addComponent - question, type: " + type
				+ ", kind: " + kind);
		
		return addQuestion(component, componentNode, target, type, kind);
	}
}

/**
 * Global function for removing component (order component or question).
 * If removing only order component, question parameter should be left
 * undefined.
 * If removing question, both component and question parameters should be
 * specified - removing question and order component in which this question
 * is nested (this order component in fact contains no other order component
 * elements, which means it have no order components children).
 * 
 * @param	{Object} component	order component to be remove
 * @param	{Object} question		question component to be removed (optional)
 */
function removeComponent(component, question) {
	if (confirm(COMPONENT_DELETE_CONFIRM)) {
		// remove component from parent order component components list
		var parent = component.parent;
		var parentNode = parent.dndSource.parent;
		parentNode.removeChild(dojo.byId("component_item_" + component.id));
		
		if (parentNode.childNodes.length == 0) 
			dojo.addClass(parent.dndSource.node, "empty");
		
		parent.removeChild(component);
		
		if (question) {
			console.debug("removing question");
			removeQuestion(question);
		}
		else {
			console.debug("removing order component");
			removeOrderComponent(component);
			deleteOrderComponent(component);
		}
	}
}

/**
 * This function is called by addComponent function when adding order component.
 * 
 * @param	component		adding order component object
 * @param	componentNode	DOM node representing this order component in parent
 * 							child nodes list
 * @param	target			dojo.dnd.Source object on which component was
 * 							dropped
 * 
 * @return	component		added component
 */
function addOrderComponent(component, componentNode, target) {
	console.debug("adding order component with id: " + component.id);
	
	var parentComponentLink = dojo.doc.createElement("a");
	dojo.addClass(parentComponentLink, "parent_link")
	parentComponentLink.href = "#";
	parentComponentLink.innerHTML = ORDER_COMPONENT_GO_UP;
	
	dojo.connect(parentComponentLink, "onclick", function() {
		focusComponent(target.node);
	});
	
	var componentsList = dojo.doc.createElement("ol");
	componentsList.id = "components_list_" + component.id;
	
	var componentsContainer = dojo.doc.createElement("div");
	componentsContainer.id = "component_" + component.id;
	dojo.addClass(componentsContainer, "empty");
	dojo.addClass(componentsContainer, "component_order_container");
	componentsContainer.style.display = "none";
	
	componentsContainer.appendChild(parentComponentLink);
	componentsContainer.appendChild(componentsList);
	
	var components = dojo.byId("components");
	components.appendChild(componentsContainer);
	
	createDropZone(component);
	
	// connect order component delete link
	dojo.query("a.remove", componentNode).connect("onclick", function() {
		removeComponent(component);
	});
	
	return component;
}

/**
 * Function used by removeComponent function when removing order component.
 * This function don't call XHR/Gears - this is provided in removeComponent
 * function. It is only responsible for removing dojo.dnd.Source object
 * connected with component being removed and to remove nested order components
 * and question components elements in this component by calling itself.
 * 
 * @param {Object} component	order component which is removed
 */
function removeOrderComponent(component) {
	console.debug("removing order component with id: " + component.id);
	var parentNode = component.dndSource.parent;
	
	dojo.forEach(component.childs, function(childComponent) {
		parentNode.removeChild(dojo.byId("component_item_" + childComponent.id));
		
		if (childComponent.question)
			removeQuestion(childComponent.question)
		else		
			removeOrderComponent(childComponent);
	});
	
	// remove order component components list
	var componentsWrapper = dojo.byId("components");
	componentsWrapper.removeChild(component.dndSource.node);
	component.dndSource.destroy();
}

function moveComponent(componentNode) {
	var target = dojo.dnd.manager().target;
	var position = target.getAllNodes().indexOf(componentNode);
	var componentId = getComponentId(componentNode);
	var component = target.component.findChildById(componentId);
	
	console.debug("component with id " + componentId
			+ " moved from position " + component.position + " to " + position);
	
	component.position = position;
	
	moveOrderComponent(component);
}

function focusComponent(componentNode) {
	var componentId = getComponentId(componentNode);
	console.debug("focus component with id: " + componentNode.id);
	
	if (activeComponentsContainer)
		activeComponentsContainer.style.display = "none";
	
	activeComponentsContainer = dojo.byId("component_" + componentId);
	activeComponentsContainer.style.display = "";
}

function addForkComponent(target, forkComponentNode) {
	//TODO implement adding fork here
}

function addQuestion(component, componentNode, target, type, kind) {
	console.debug("Adding question in component with id: " + component.id)
	
	var question = new Question(component, type, kind, 0);
	component.question = question;
	
	var componentItem = target.getItem(componentNode.id);
	var questionEditBox = componentItem.data.questionEditBox;
	
	// connect "onblur" event on question content inline edit box
	questionEditBox.onChange = function(value) {
		editQuestion(question, value);
	}
	
	// connect question component delete link
	dojo.query("a.remove", componentNode).connect("onclick", function() {
		removeComponent(component, question);
	});
	
	if (kind == "checkbox" || kind == "radio") {
		question.answersListNode = componentItem.data.answersList;
	
		var addAnswerButton = componentItem.data.addAnswerButton;
		dojo.connect(addAnswerButton, "onclick", function() {
			addAnswer(question);
		});
	}
	
	return question;
}

function removeQuestion(question) {
	console.debug("removing question with id: " + question.id)
	deleteQuestion(question);
}

function editQuestion(question, value) {
	console.debug("Updating content for question " + question.persistentId
			+ ", new value = " + value);
	question.content = value;
	updateQuestion(question);
}

function addAnswer(question, answer) {
	console.debug("Adding answer for question with type " + question.type);
	var answersList = question.answersListNode;
	var type = question.kind;
	
	var create = true;
	if (answer)
		create = false;
	else
		answer = new Answer();
	
	
	if (!answer.position)
		answer.position = question.answers.length;	// add answer at the end
	
	question.addAnswer(answer);
	
	var answerItem = dojo.doc.createElement("li");
	answer.node = answerItem;
	var answerInput = dojo.doc.createElement("input");
	answerInput.type = type;
	answerItem.appendChild(answerInput);
	var answerLabel = dojo.doc.createElement("label");
	answerItem.appendChild(answerLabel);
	
	var answerEditBox = new dijit.InlineEditBox({
		value: (answer.value ? new String(answer.value) : ANSWER_EDIT_PROMPT),
		autoSave: true,
		width: "80%"
	}, answerLabel);
	
	answerEditBox.onChange = function(value) {
		editAnswer(answer, value);
	}
	
	var removeAnswerButton = dojo.doc.createElement("button");
	answerItem.appendChild(removeAnswerButton);
	dojo.addClass(removeAnswerButton, "removeAnswer");
	
	var removeImage = dojo.doc.createElement("img");
	removeImage.src = "../../images/buttons/remove.png";
	removeImage.alt = "-";
	removeAnswerButton.appendChild(removeImage);
	
	var removeSpan = dojo.doc.createElement("span");
	removeSpan.appendChild(dojo.doc.createTextNode(ANSWER_DELETE));
	removeAnswerButton.appendChild(removeSpan);
	
	dojo.connect(removeAnswerButton, "onclick", function() {
		removeAnswer(answer);
	});
	
	answersList.appendChild(answerItem);
	
	if (create)
		createAnswer(answer);
}

function removeAnswer(answer) {
	var answerNode = answer.node;
	answerNode.parentNode.removeChild(answerNode);
	answer.question.removeAnswer(answer);
	deleteAnswer(answer);
}

function editAnswer(answer, value) {
	console.debug("Updating content for answer " + answer.persistentId
			+ ", new value = " + value);
	answer.value = value;
	updateAnswer(answer);
}

//function blurAllQuestions() {
//	var questions = dojo.query(".question");
//	
//	for (i in questions)
//		dojo.removeClass(questions[i], "active");;
//}
//
//function focusQuestion(questionNode) {
//	dojo.addClass(questionNode, "active");
//}

//function addQuestion(target, questionNode) {
//	var questions = dojo.query(".question .dijitTitlePane .dijitTitlePaneTitle .dijitTitlePaneTextNode");
//	
//	for (var i = 0; i < questions.length; i++)
//		questions[i].innerHTML = "Pytanie " + (i + 1) + ".";
//	
//	blurAllQuestions();
//	focusQuestion(questionNode)
//	//dojo.fadeIn({ node: questionNode.id }).play();
//}

/* -----------------------------------------------------------------------------
 * d'n'd creators
 * -----------------------------------------------------------------------------
 */

function toolboxCreator(data, hint) {
	var node = dojo.doc.createElement("li");
	dojo.forEach(data.type, function(className) { dojo.addClass(node, className); });
	node.id = dojo.dnd.getUniqueId();
	node.innerHTML = "<a href=\"#\" title=\"" + DND_PROMPT + "\"><img src=\"../../images/" + data.image + "\" />" + data.title + "</a>";
	
	return { node: node, data: data, type: ["component"] };
}

/**
 * Creator of components, used by dnd drop event on components list.
 * @param data
 * @param hint
 * @return
 */
function componentCreator(data, hint) {
	var componentNode = dojo.doc.createElement("li");
	
	createdComponent = componentNode;
	
	var tempId;
	
	if (data.id)
		tempId = data.id;
	else
		tempId = dojo.dnd.getUniqueId();
		
	componentNode.id = "component_item_" + tempId;
	
	if (dojo.indexOf(data.type, "component") != -1) {
		if (dojo.indexOf(data.type, "order") != -1)
			return orderComponentCreator(componentNode, data, hint);
		else if (dojo.indexOf(data.type, "fork") != -1) {
			alert("funkcja nie jest jeszcze aktywna")
			return { node: null, data: null, type: null };
			//TODO implement fork component creator here
		}
	}
	else if (dojo.indexOf(data.type, "question") != -1)
		return questionCreator(componentNode, data, hint);
}

function orderComponentCreator(componentNode, data, hint) {
	dojo.addClass(componentNode, "order_component");
	
	var componentItem = dojo.doc.createElement("div");
	
	var removeLink = dojo.doc.createElement("a");
	removeLink.href = "#";
	dojo.addClass(removeLink, "remove");
	
	var componentLink = dojo.doc.createElement("a");
	componentLink.href = "#";
	componentLink.innerHTML = ORDER_COMPONENT_PROMPT;
	dojo.connect(componentLink, "onclick", function() {
		focusComponent(componentNode);
	});

	componentItem.appendChild(removeLink);
	componentItem.appendChild(componentLink);
	componentNode.appendChild(componentItem);

	return { node: componentNode, data: data, type: data.type };
}

function questionCreator(componentNode, data, hint) {
	dojo.forEach(data.type, function(className) {
		dojo.addClass(componentNode, className);
	});
	
	var questionWrapper = dojo.doc.createElement("div");
	componentNode.appendChild(questionWrapper);
	
	var questionPane = new dijit.TitlePane(
		{ title: QUESTION_NEW, open: true }, questionWrapper);
	
	console.debug(questionPane.domNode);
	console.debug(dojo.query(".dijitTitlePaneTitle", questionPane.domNode));
	
	var questionPaneTitle =
			dojo.query(".dijitTitlePaneTitle", questionPane.domNode).pop();
	var removeLink = dojo.doc.createElement("a");
	removeLink.href = "#";
	dojo.addClass(removeLink, "remove");
	questionPaneTitle.appendChild(removeLink);
	
	var questionPaneContent = dojo.doc.createElement("div");
	questionPane.setContent(questionPaneContent);
	
	var questionPara = dojo.doc.createElement("p");
	questionPaneContent.appendChild(questionPara);
	var questionEditBox = new dijit.InlineEditBox({
		value: (data.content
				? data.content : QUESTION_EDIT_PROMPT),
		autoSave: true
	}, questionPara);
	
	data.questionEditBox = questionEditBox;
	
	if (dojo.indexOf(data.type, "radio") != -1) {
		var answersListData = createAnswerList(questionPaneContent, "radio");
		data.answersList = answersListData.answersList;
		data.addAnswerButton = answersListData.addAnswerButton;
	}
	else if (dojo.indexOf(data.type, "checkbox") != -1) {
		var answersListData = createAnswerList(questionPaneContent, "checkbox");
		data.answersList = answersListData.answersList;
		data.addAnswerButton = answersListData.addAnswerButton;
	}
	else if (dojo.indexOf(data.type, "input_text") != -1) {
		var questionAnswerTextBox = dojo.doc.createElement("input");
		questionAnswerTextBox.type = "text";
		questionPaneContent.appendChild(questionAnswerTextBox);
	}
	else
		return null;

	return { node: componentNode, data: data, type: data.type };
}

function createAnswerList(questionPaneContentNode, type) {
	var answersList = dojo.doc.createElement("ol");
	questionPaneContentNode.appendChild(answersList);
	dojo.addClass(answersList, "answers_list");

	var addAnswerButton = dojo.doc.createElement("button");
	questionPaneContentNode.appendChild(addAnswerButton);
	dojo.addClass(addAnswerButton, "addAnswer");
	
	var addImage = dojo.doc.createElement("img");
	addImage.src = "../../images/buttons/add.png";
	addImage.alt = "+";
	addAnswerButton.appendChild(addImage);
	
	var addSpan = dojo.doc.createElement("span");
	addSpan.appendChild(dojo.doc.createTextNode(ANSWER_ADD));
	addAnswerButton.appendChild(addSpan);
	
	return { answersList: answersList, addAnswerButton: addAnswerButton };
}

/**
 * Creates dnd drop zone (dojo.dnd.Source) for specified component.
 * @param	component	component for which drop zone is created
 * @return
 */
function createDropZone(component) {
	var componentDrop = new dojo.dnd.Source("component_" + component.id, {
		creator: componentCreator, accept: ["component", "question"]
	});
	componentDrop.parent = dojo.byId("components_list_" + component.id);
	
	dojo.connect(componentDrop, "onDrop", function(source, nodes, isCopy, target) {
		var node = nodes[0];
		
		if (source == dojo.dnd.manager().target)
			moveComponent(node);
		
		switch (source.node.id) {
		case "toolbox_questions_numeric":
			// call XHR/Gears handler
			createQuestion(addComponent(["question", "numeric"]));
			break;
		case "toolbox_questions_text":
			// call XHR/Gears handler
			createQuestion(addComponent(["question", "text"]));
			break;
		case "toolbox_components":
			if (dojo.hasClass(node, "order")) {
				// call XHR/Gears handler
				createOrderComponent(addComponent(["component", "order"]));
			}
			else if (dojo.hasClass(node, "fork"))
				addComponent(["component", "fork"]);
			break;
//		case "toolbox_user":
//			break;
//		case "toolbox_rest":
//			break;
		default:
			break;
		}
	});
	
	componentDrop.component = component;
	component.dndSource = componentDrop;
}

var initDnd = function() {
	var toolboxQuestionsNumeric =
			new dojo.dnd.Source("toolbox_questions_numeric", {
				creator: toolboxCreator, copyOnly: true, selfAccept: false
			}
	);
	var toolboxQuestionsText =
			new dojo.dnd.Source("toolbox_questions_text", {
				creator: toolboxCreator, copyOnly: true, selfAccept: false
			}
	);
	/*for (i in toolboxBasic.node.childNodes) {
		toolboxBasic.node.childNodes[i].id = dojo.dnd.getUniqueId();
		dojo.addClass(toolboxBasic.node.childNodes[i], "dojoDndItem");
	}*/
	var toolboxComponents = new dojo.dnd.Source("toolbox_components", {
		creator: toolboxCreator, copyOnly: true, selfAccept: false
	});
	/* TODO
	var toolboxUser = new dojo.dnd.Source("toolbox_user", {
		creator: toolboxCreator, copyOnly: true, selfAccept: false
	});
	var toolboxRest = new dojo.dnd.Source("toolbox_rest", {
		creator: toolboxCreator, copyOnly: true, selfAccept: false
	});
	*/
	toolboxQuestionsNumeric.insertNodes(false, [{
			title: "pytanie jednokrotnego wyboru",
			image: "32x32/radio.png",
			type: ["question", "numeric", "radio"]
		}, {
			title: "pytanie wielokrotnego wyboru",
			image: "32x32/checkbox.png",
			type: ["question", "numeric", "checkbox"]
		}, {
			title: "pytanie otwarte",
			image: "32x32/editclear.png",
			type: ["question", "numeric", "input_text"]
	}]);
	
	toolboxQuestionsText.insertNodes(false, [{
			title: "pytanie jednokrotnego wyboru",
			image: "32x32/radio.png",
			type: ["question", "text", "radio"]
		}, {
			title: "pytanie wielokrotnego wyboru",
			image: "32x32/checkbox.png",
			type: ["question", "text", "checkbox"]
		}, {
			title: "pytanie otwarte",
			image: "32x32/editclear.png",
			type: ["question", "text", "input_text"]
	}]);
	
	toolboxComponents.insertNodes(false, [{
			title: "komponent grupujący",
			type: ["component", "order"]
		}/*,{
			title: "komponent rozgałęziający",
			type: ["component", "fork"]
	}*/]);
	
//	toolboxBasic.forInItems(function(item, id, map) {
//		dojo.addClass(id, item.type[0]);
//	});
//
//	toolboxComponents.forInItems(function(item, id, map) {
//		dojo.addClass(id, item.type[0]);
//	});
	
	createDropZone(rootComponent);
	activeComponentsContainer = dojo.byId("component_" + rootComponent.id);
};
