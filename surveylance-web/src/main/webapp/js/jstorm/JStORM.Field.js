JStORM.Field = function(options)
{
	this.columnName = options.columnName;
	this.relatedColumnName = options.relatedColumnName;
	this.relatedModel = function()
	{
		return JStORM._models[options.relatedModel];
	};
	this.relationType = options.relationType;
	this.isRelation = !!this.relationType;
	this.type = this.isRelation ? "Integer" : options.type;
	this.maxLength = options.maxLength;
	this.isPrimaryKey = options.isPrimaryKey;
	this.allowNull = options.allowNull;
};

JStORM.Field.prototype.getSqlType = function()
{
	 
	return JStORM.Field.TypeToSql[this.type](this.maxLength);
};

JStORM.Field.TypeToSql =
{
	"Integer":function()
	{
		return 'INTEGER';
	},
	"Float":function()
	{
		return 'REAL';
	},
	"String":function(maxLength)
	{
		return 'VARCHAR(' + maxLength +')';
	}
};
