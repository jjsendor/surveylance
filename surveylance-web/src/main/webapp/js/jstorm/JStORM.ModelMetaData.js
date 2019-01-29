JStORM.ModelMetaData = function(modelClass,options)
{
	this.modelClass = modelClass;
	this.tableName = options.name;
	this.connName = options.connection;
	this.fields = [];
	this.relations = [];
	//go over field
	for(fieldName in options.fields)
	{
		var field = options.fields[fieldName];
		field.fieldName = fieldName;
		field.columnName = field.columnName ? field.columnName : fieldName;
		field.sqlType = field.getSqlType();
		if(field.isPrimaryKey)
		{
			this.pk = field;
		}
		else
		{
			if(field.isRelation)
				this.relations.push(field);
			else
				this.fields.push(field);
		}
	}
	//if there is no primary key,add rowid
	if(!this.pk)
	{
		this.pk = new JStORM.Field({columnName:"rowid",isPrimaryKey:true,type:"Integer"});
		this.pk.fieldName = "rowid";
		this.pk.sqlType = "INTEGER NOT NULL PRIMARY KEY ";
		if(JStORM.getConnectionParams(this.connName).DIALECT == "MySQL")
			this.pk.sqlType += " AUTO_INCREMENT ";
	}
};

