
/* -----------------------------------------------------------------------------
 * Service layer helper functions
 * -----------------------------------------------------------------------------
 */

// TODO move to gears.answer namespace
function adjustAnswers(gears_question, position) {
	var gears_answers = gears.answer.findByQuestion(gears_question);
	console.debug("Adjusting position " + position + " for question with gearsId = "
			+ gears_question.rowid);
	for (var i = 0; i < gears_answers.length; i++) {
		var gears_answer = gears_answers[i];
		console.debug("Answer: gearsId = " + gears_answer.gearsId);
		if (gears_answer.position >= position) {
			console.debug("Moving up from " + gears_answer.position);
			gears_answer.position++;
			gears_answer.save();
		}
	}
}

//TODO move to gears.answer namespace
function readjustAnswers(gears_question, position) {
	var gears_answers = gears.answer.findByQuestion(gears_question);
	console.debug("Readjusting position " + position + " for question with gearsId = "
			+ gears_question.rowid);
	for (var i = 0; i < gears_answers.length; i++) {
		var gears_answer = gears_answers[i];
		console.debug("Answer: gearsId = " + gears_answer.gearsId);
		if (gears_answer.position > position) {
			console.debug("Moving down from " + gears_answer.position);
			gears_answer.position--;
			gears_answer.save();
		}
	}
}


/**
 * @namespace Answer Gears DAO.
 */
gears.answer = {
		create: function(answer) {
			var question = answer.question;
			var gears_question = new gears.Question(question.gearsId);

			if (answer.position)
				adjustAnswers(gears_question, answer.position);

			var gears_answer = new gears.Answer({
				persistentId: answer.persistentId,
				modifications: answer.modifications,
				position: answer.position,
				isText: (question.type == "text" ? 1 : 0),
				textValue: (question.type == "text" && answer.value ? answer.value.toString() : null),
				numericValue: (question.type == "numeric" && answer.value ? parseInt(answer.value) : null),
				Question: gears_question
			});

			gears_answer.save();

			gears.question.markModified(gears_question);

			answer.gearsId = gears_answer.rowid;
			console.debug("Gears ORM: Answer created (persistentId: "
					+ answer.persistentId + ", gearsId: " + answer.gearsId + ")");
			
			return gears_answer;
		},
		remove: function(gears_answer) {
			console.debug("Gears ORM: Deleting answer (gearsId: "
					+ gears_answer.rowid + ")");

			if (gears_answer.position) 
				readjustAnswers(gears_answer.Question, gears_answer.position);

			gears.question.markModified(gears_answer.Question);

			gears_answer.Question = null;
			gears_answer.remove();
		},
		removeById: function(id) {
			console.debug("Gears ORM: Removing answer by gearsId: " + id);
			var gears_answer = new gears.Answer(id);

			if (gears_answer)
				gears.answer.remove(gears_answer);
		},
		update: function(answer) {
			console.debug("Gears ORM: Updating answer (gearsId: "
					+ answer.gearsId + ")");
			var gears_answer = new gears.Answer(answer.gearsId);

			console.debug("Gears ORM: answer's question type = "
					+ gears_answer.Question.type);

			if (answer.value) {
				if (gears_answer.Question.type == "text")
					gears_answer.textValue = answer.value.toString();
				else if (gears_answer.Question.type == "numeric")
					gears_answer.numericValue = parseInt(answer.value);
			}

			gears_answer.save();

			gears.answer.markModified(gears_answer);
		},
		findAll: function() {
			return gears.Answer.all().toArray();
		},
		findById: function(id) {
			return new gears.Answer(id);
		},
		findByPersistentId: function(persistentId) {
			return gears.Answer.filter(
					"Answer.persistentId = ?", persistentId).first();
		},
		findByQuestion: function(gears_question) {
			return gears.Answer.filter(
					"Answer.Question = ?", gears_question.rowid).toArray();
		}
}
