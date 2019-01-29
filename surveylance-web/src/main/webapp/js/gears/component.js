
/* -----------------------------------------------------------------------------
 * Service layer helper functions
 * -----------------------------------------------------------------------------
 */

// TODO move this function to gears.component
function adjustComponents(gears_parent, position) {
	var gears_components = gears.component.findByComponent(gears_parent);
	console.debug("Adjusting position " + position + " for parent with gearsId = "
			+ gears_parent.rowid);
	for (var i = 0; i < gears_components.length; i++) {
		var gears_component = gears_components[i];
		console.debug("Component: gearsId = " + gears_component.rowid);
		if (gears_component.position >= position) {
			console.debug("Moving up from " + gears_component.position);
			gears_component.position++;
			gears_component.modifications++;	// mark modified
			gears_component.save();
		}
	}
}

// TODO move this function to gears.component
function readjustComponents(gears_parent, position) {
	var gears_components = gears.component.findByComponent(gears_parent);
	console.debug("Readjusting position " + position + " for parent with gearsId = "
			+ gears_parent.rowid);
	for (var i = 0; i < gears_components.length; i++) {
		var gears_component = gears_components[i];
		console.debug("Component: gearsId = " + gears_component.rowid);
		if (gears_component.position > position) {
			console.debug("Moving down from " + gears_component.position);
			gears_component.position--;
			gears_component.modifications++;	// mark modified
			gears_component.save();
		}
	}
}

/* -----------------------------------------------------------------------------
 * Service layer (using Gears ORM)
 * -----------------------------------------------------------------------------
 */

/**
 * @namespace Component Gears DAO.
 */
gears.component = {
		create: function(component) {
			console.log("Creating component for parent with gearsId: "
					+ component.parent.gearsId);
			var gears_parent = new gears.Component(component.parent.gearsId);

			if (component.position)
				adjustComponents(gears_parent, component.position);

			var gears_component = new gears.Component({
				persistentId: component.persistentId,
				modifications: component.modifications,
				position: component.position,
				Component: gears_parent
			});

			gears_component.save();

			gears.component.markModified(gears_parent);

			component.gearsId = gears_component.rowid;
			console.debug("Gears ORM: Component created (persistentId: "
					+ component.persistentId + ", gearsId: " + component.gearsId + ")");

			return gears_component;
		},
		remove: function(gears_component) {
			console.debug("Gears ORM: Deleting component (gearsId: "
					+ gears_component.rowid + ")");
			var gears_components =
				gears.component.findByComponent(gears_component);

			// cascade deletion
			for (var i = 0; i < gears_components.length; i++) {
				if (gears_components[i].question)
					gears.question.remove(gears_components[i].Question);
				else
					gears.component.remove(gears_components[i]);
			}

			if (gears_component.position) {
				readjustComponents(gears_component.Component,
						gears_component.position);
			}

			gears.component.markModified(gears_component.Component);

			gears_component.Component = null;
			gears_component.remove();
		},
		removeById: function(id) {
			console.debug("Gears ORM: Removing component by gearsId: " + id);
			var gears_component = new gears.Component(id);

			if (gears_component)
				gears.component.remove(gears_component);
		},
		move: function(component) {
			console.debug("Gears ORM: Moving component (gearsId: "
					+ component.gearsId + ")");
			var gears_component = new gears.Component(component.gearsId);

			console.debug("Gears ORM: Moving from posistion "
					+ gears_component.position + " to position " + component.position);
			if (gears_component.position != null)
				readjustComponents(gears_component.Component, gears_component.position);
			if (component.position != null)
				adjustComponents(gears_component.Component, component.position);

			gears_component.position = component.position;
			gears_component.save();

			gears.component.markModified(gears_component);
		},
		findAll: function() {
			return gears.Component.all().toArray();
		},
		findById: function(id) {
			return new gears.Component(id);
		},
		findByPersistentId: function(persistentId) {
			return gears.Component.filter(
					"Component.persistentId = ?", persistentId).first();
		},
		findByComponent: function(gears_parent) {
			return gears.Component.filter(
					"Component.Component = ?", gears_parent.rowid).toArray();
		}
}
