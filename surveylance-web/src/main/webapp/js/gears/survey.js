/**
 * @namespace Survey Gears DAO.
 */
gears.survey = {
		create: function(survey) {
			console.debug("Gears ORM: Creating survey");
			var rootComponent = survey.rootComponent;

			var gears_rootComponent = new gears.Component({
				persistentId: rootComponent.persistentId,
				modifications: rootComponent.modifications,
				position: rootComponent.position,
				Component: rootComponent.parent
			}).save();

			var gears_survey = new gears.Survey({
				persistentId: survey.persistentId,
				name: survey.name,
				modifications: survey.modifications,
				description: survey.description,
				expirationDate: survey.expirationDate,
				Component: gears_rootComponent
			}).save();

			return gears_survey;
		},
		remove: function(gears_survey) {
			console.debug("Gears ORM: Deleting survey (gearsId "
					+ gears_survey.rowid + ")");
			// delete rootComponent and all component underneath it recursively
			gears.component.remove(gears_survey.rootComponent);
			gears_survey.Component = null;
			gears_survey.remove();
		},
		removeById: function(id) {
			var gears_survey = new gears.Survey(id);

			if (gears_survey)
				gears.survey.remove(gears_survey);
		},
		findAll: function() {
			return gears.Survey.all().toArray();
		},
		findById: function(id) {
			return new gears.Survey(id);
		},
		findByPersistentId: function(persistentId) {
			return gears.Survey.filter(
					"Survey.persistentId = ?", persistentId).first();
		},
		findByRootComponent: function(gears_component) {
			return gears.Survey.filter(
					"Survey.Component = ?", gears_component.rowid).first();
		}
};
