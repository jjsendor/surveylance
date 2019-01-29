/**
 * Survey helper class.
 *
 * @param	rootComponent
 * @param	name
 * @param	description
 * @param	expirationDate
 * @param	persistentId
 */
function Survey(name, modifications, description, expirationDate,
		rootComponent, persistentId) {
	if (name === undefined)
		name = null;
	
	if (modifications === undefined)
		modifications = null;
	
	if (description === undefined)
		description = null;
	
	if (expirationDate === undefined)
		expirationDate = null;
	
	if (rootComponent === undefined)
		rootComponent = null;
	
	if (persistentId === undefined)
		persistentId = null;
	
	this.persistentId = persistentId;
	this.gearsId = null;

	this.name = name;
	this.modifications = modifications;
	this.description = description;
	this.expirationDate = expirationDate;
	this.rootComponent = rootComponent;
}

/**
 * Component helper class.
 *
 * @param	id
 * @param	modifications
 * @param	position
 * @param	parent
 * @param	persistentId
 */
function Component(id, modifications, position, parent, persistentId) {
	if (id === undefined)
		id = null;
	
	if (modifications === undefined)
		modifications = null;
	
	if (position === undefined)
		position = null;
	
	if (parent === undefined)
		parent = null;
	
	if (persistentId === undefined)
		persistentId = null;
	
	this.id = id;
	this.persistentId = persistentId;
	this.gearsId = null;

	this.modifications = modifications;
	this.position = position;
	this.parent = parent;
	this.childs = new Array();
	this.question = null;
	this.dndSource = null;
	
	this.findChildById = function(childId) {
		var components = dojo.filter(this.childs, function (item) {
			return item.id == childId;
		});
		
		if (components.length == 0)
			return null;
		
		return components.pop();
	};
	
	this.addChild = function(child) {
		this.childs.push(child);
		child.parent = this;
	};
	
	this.removeChild = function(child) {
		this.childs = dojo.filter(this.childs, function (item) {
			return item != child;
		});
		child.parent = null;
	};
}

/**
 * Question helper class.
 *
 * @param	parent
 * @param	type
 * @param	kind
 * @param	modifications
 * @param	persistentId
 */
function Question(parent, type, kind, modifications, persistentId) {
	if (parent === undefined)
		parent = null;
	
	if (type === undefined)
		type = null;
	
	if (kind === undefined)
		kind = null;
	
	if (modifications === undefined)
		modifications = null;
	
	if (persistentId === undefined)
		persistentId = null;
	
	this.id = null;
	this.persistentId = persistentId;
	this.gearsId = null;

	this.parent = parent;
	this.type = type;
	this.kind = kind;
	this.modifications = modifications;
	this.content = null;
	this.answersListNode = null;
	this.answers = new Array();
	
	this.addAnswer = function(answer) {
		this.answers.push(answer);
		answer.question = this;
	}
	
	this.removeAnswer = function(answer) {
		this.answers = dojo.filter(this.answers, function (item) {
			return item != answer;
		});
		answer.question = null;
	};
}

/**
 * Answer helper class.
 *
 * @param	modifications
 * @param	position
 * @param	value
 * @param	persistentId
 */
function Answer(modifications, position, value, persistentId) {
	if (modifications === undefined)
		modifications = null;
	
	if (position === undefined)
		position = null;
	
	if (value === undefined)
		value = null;
	
	if (persistentId === undefined)
		persistentId = null;
	
	this.id = null;
	this.persistentId = persistentId;
	this.gearsId = null;

	this.question = null;
	this.position = position;
	this.modifications = modifications;
	this.value = value;
}
