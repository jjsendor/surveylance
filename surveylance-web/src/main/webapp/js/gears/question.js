
/* -----------------------------------------------------------------------------
 * Service layer (using Gears ORM)
 * -----------------------------------------------------------------------------
 */

/**
 * @namespace Question Gears DAO.
 */
gears.question = {
		create: function(question) {
			var component = question.parent;
			var gears_parent = gears.component.create(component);
			component.gearsId = gears_parent.rowid;
			
			var gears_question = new gears.Question({
				persistentId: question.persistentId,
				modifications: question.modifications,
				type: question.type,
				kind: question.kind,
				content: question.content,
				Component: gears_parent
			});
			
			gears_question.save();
			// do not mark anything modified!
			
			question.gearsId = gears_question.rowid;
			console.debug("Gears ORM: Question created (persistentId: "
					+ question.persistentId + ", gearsId: "
					+ question.gearsId + ")");
			
			return gears_question;
		},
		remove: function(gears_question) {
			console.debug("Gears ORM: Deleting question (gearsId: "
					+ gears_question.rowid + ")");
			// delete parent component
			var gears_component = gears_question.Component;
			gears.component.markModified(gears_component.Component);
			gears_component.Question = null;
			gears_component.remove();
			gears_question.Component = null;

			var gears_answers = gears.answer.findByQuestion(gears_question);

			// delete all answers for this question
			for (var i = 0; i < gears_answers.length; i++)
				gears.answer.remove(gears_answers[i]);

			gears_question.remove();
		},
		removeById: function(id) {
			console.debug("Gears ORM: Removing question by gearsId: " + id);
			var gears_question = new gears.Question(id);

			if (gears_question)
				gears.question.remove(gears_question);
		},
		update: function(question) {
			console.debug("Gears ORM: Updating question (gearsId: "
					+ question.gearsId + ")");
			var gears_question = new gears.Question(question.gearsId);
			gears_question.content = question.content;
			gears_question.save();

			gears.question.markModified(gears_question);
		},
		findAll: function() {
			return gears.Question.all().toArray();
		},
		findById: function(id) {
			return new gears.Question(id);
		},
		findByPersistentId: function(persistentId) {
			return gears.Question.filter(
					"Question.persistentId = ?", persistentId).first();
		},
		findByComponent: function(gears_component) {
			var gears_questions = gears.Question.filter(
					"Question.Component = ?", gears_component.rowid);
			if (gears_questions)
				return gears_questions.first();
			
			return null;
		}
}
