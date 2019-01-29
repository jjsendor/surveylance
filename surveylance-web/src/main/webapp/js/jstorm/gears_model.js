JStORM.connections = {
	"default": {
		PROVIDER: "Gears",
		DIALECT: "SQLite",
		PATH: "surveylance"
	}
};

gears.Author = new JStORM.Model({
	name: "Author",
	fields: {
		googleID: new JStORM.Field({
			type: "String",
			maxLength: 25
		})
	},
	connection: "default"
});

gears.Survey = new JStORM.Model({
	name: "Survey",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		name: new JStORM.Field({
			type: "String",
			maxLength: 25
		}),
		modifications: new JStORM.Field({
			type: "Integer"
		}),
		description: new JStORM.Field({
			type: "String",
			maxLength: 255
		}),
		expirationDate: new JStORM.Field({
			type: "String",
			maxLength: 25
		}),
		author: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Author",
			allowNull: true
		}),
		Component: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Component",
			allowNull: true
		})
	},
	connection : "default"
});

gears.Component = new JStORM.Model({
	name: "Component",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		modifications: new JStORM.Field({
			type: "Integer",
		}),
		position: new JStORM.Field({
			type: "Integer",
		}),
		Component: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Component",
			allowNull: true
		}) 
	},
	connection: "default"
});

gears.Question = new JStORM.Model({
	name: "Question",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		modifications: new JStORM.Field({
			type: "Integer"
		}),
		type: new JStORM.Field({
			type: "String",
			maxLength: 10
		}),
		kind: new JStORM.Field({
			type: "String",
			maxLength: 10
		}),
		content: new JStORM.Field({
			type: "String",
			maxLength: 255
		}),
		Component: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Component",
			allowNull: true
		})
	},
	connection: "default"
});

gears.Answer = new JStORM.Model({
	name: "Answer",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		modifications: new JStORM.Field({
			type: "Integer",
		}),
		position: new JStORM.Field({
			type: "Integer",
		}),
		isText: new JStORM.Field({
			type: "Integer",
		}),
		textValue: new JStORM.Field({
			type: "String",
			maxLength: 255
		}),
		numericValue: new JStORM.Field({
			type: "Integer",
		}),
		Question: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Question",
			allowNull: true
		})
	},
	connection: "default"
});

gears.Fork = new JStORM.Model({
	name: "ForkComponent",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		modifications: new JStORM.Field({
			type: "Integer",
		}),
		Component: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Component",
			allowNull: true
		}),
		Question: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Question",
			allowNull: true
		})
	},
	connection: "default"
});

gears.Decision = new JStORM.Model({
	name: "Decision",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		ForkComponent: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "ForkComponent",
			allowNull: true
		}),
		Component: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Component",
			allowNull: true
		}),
		Answer: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Answer",
			allowNull: true
		})
	},
	connection: "default"
});

gears.SurveyResult = new JStORM.Model({
	name: "SurveyResult",
	fields: {
		persistentId: new JStORM.Field({
			type: "Integer",
		}),
		respondentID: new JStORM.Field({
			type: "Integer",
		}),
		textAnswer: new JStORM.Field({
			type: "String",
			maxLength: 255
		}),
		Question: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Question",
			allowNull: true
		}),
		Answer: new JStORM.Field({
			relationType: "OneToMany",
			relatedModel: "Answer",
			allowNull: true
		})
	},
	connection: "default"
});

gears.createDbTables = function() {
	gears.Author.createTable();
	gears.Component.createTable();
	gears.Survey.createTable();
	gears.Question.createTable();
	gears.Fork.createTable();
	gears.Answer.createTable();
	gears.Decision.createTable();
	gears.SurveyResult.createTable();
};
