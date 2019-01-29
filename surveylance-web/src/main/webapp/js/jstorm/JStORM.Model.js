JStORM.Model = function(options)
{
	if(!options || !options.name || !options.fields || !options.connection)
		throw new Error("no name/fields/connection were supplied");
		
	var model = function(valuesOrRowID)
	{
		var instance = this;
		if(valuesOrRowID)
		{
			if(typeof(valuesOrRowID) == "number")
			{
				this.setPkValue(valuesOrRowID);
				this.refresh();
				return;
			}
			else
			{
				JStORM.extend(this,valuesOrRowID);
			}
		}
		JStORM.each(model._meta.relations,function(relation)
		{
			if(relation.relationType == "ManyToOne")
			{
				instance[relation.fieldName] = new JStORM.Query(relation.relatedModel())
					.filter(relation.relatedColumnName + " = ?",function(){return instance.getPkValue()}) 
			}
		});
	};
	
	model.prototype =
	{
		save:function()
		{
			return this._updateInsert(this._isPkSet(this));
		},
		remove:function()
		{
			model.removeByPk(this.getPkValue());
		},
		refresh:function()
		{
			if(this._isPkSet())
				JStORM.extend(this,model.getByPk(this.rowid));
			return this;
		},
		getPkValue:function()
		{
			return this[model._meta.pk.fieldName];
		},
		setPkValue:function(pkValue)
		{
			this[model._meta.pk.fieldName] = pkValue;
			return this;
		},
		_isPkSet:function()
		{
			return !!this[model._meta.pk.fieldName];
		},
		_updateInsert:function(update)
		{
			var query = update ? model._sql.getUpdateSql() : model._sql.getInsertSql();

			var values = [],self = this;
			JStORM.each(model._meta.fields,function(field)
			{
				var value = self[field.fieldName];
				if(typeof(value) == "undefined")
					values.push(null);
				else
					values.push(value);
			});
			JStORM.each(model._meta.relations,function(relation)
			{
				var value = self[relation.fieldName];
				if(relation.relationType == "OneToMany")
				{
					if(typeof(value) == "number" || (!value && relation.allowNull))
						values.push(value);
					else if(value.getPkValue)
						values.push(value.getPkValue());
					else
						throw new Error("value of related model can be a model instance,or a id(integer)");
				}
			});			
			if(update)values.push(this.getPkValue());
			var conn = model.getConnection();
			conn.executeNonQuery(query,values);			
			if(!update)this.rowid = conn.getLastInsertId();
			return this;
		}
	};
	//add static methods
	JStORM.extend(model,this);
	//add events support
	JStORM.extend(model,new JStORM.Events);
	for(var i in model.prototype)//add events to save,remove,refresh
		if(i.charAt(0) != "_")//wrap only public functions
			model.prototype[i] = JStORM.Events.wrapFunction(model.prototype[i],i.charAt(0).toUpperCase()+i.substring(1),model);

	//add meta data
	model._meta = new JStORM.ModelMetaData(model,options);
	//add sql generator
	model._sql = new JStORM.Sql(model._meta);
	//add name to model
	model.name = options.name;
	//add getBy and removeBy for each fields
	//for(var i=0,l = model._meta.fields.length;i<l;i++)
	//{
	//	var field = model._meta.fields[i];
	//	model["getBy" + field.fieldName] = JStORM.curry(model.getByFieldValue,model,field.fieldName);
	//	model["removeBy" + field.fieldName] = JStORM.curry(model.removeByFieldValue,model,field.fieldName);
	//}
	
	//register model
	JStORM._models[options.name] = model;
	return model;
};
JStORM.Model.prototype = {
	all: function(){
		return new JStORM.Query(this);
	},
	filter: function(sql){
		var query = this.all();
		return query.filter.apply(query, arguments);
	},
	remove: function(sql){
		var query = this.all();
		query.filter.apply(query, arguments).remove();
	},
	getByFieldValue: function(fieldName, fieldValue){
		return this.filter(this._meta.tableName + "." + fieldName + " = ?", fieldValue).first();
	},
	getByPk: function(pkValue){
		return this.getByFieldValue(this._meta.pk.fieldName, pkValue);
	},
	removeByFieldValue: function(fieldName, fieldValue){
		return this.filter(this._meta.tableName + "." + fieldName + " = ?", fieldValue).remove();
	},
	removeByPk: function(pkValue){
		this.removeByFieldValue(this._meta.pk.fieldName, pkValue);
	},
	getConnection: function(){
		if (!this._connection) {
			this._connection = JStORM.getConnection(this._meta.connName);
		}
		return this._connection;
	},
	dropTable: function(){
		this.getConnection().executeNonQuery(this._sql.getDropTableSql());
	},
	createTable: function(){
		this.getConnection().executeNonQuery(this._sql.getCreateTableSql());
	},
	_newFromResultSet: function(result, relationPrefix){
		var cls = this;
		//create a new instance of the this class
		var instance = new cls();
		
		//every model has a primary key,set the value from the result
		instance.setPkValue(result.getByFieldName(relationPrefix + "_" + this._meta.pk.columnName));
		
		JStORM.each(cls._meta.fields, function(field){
			instance[field.fieldName] = result.getByFieldName(relationPrefix + "_" + field.fieldName)
		});
		
		JStORM.each(cls._meta.relations, function(relation){
			if (relation.relationType == "OneToMany") {
				var relatedModel = relation.relatedModel();
				if (relatedModel == cls) 
					instance[relation.fieldName] = new relatedModel().setPkValue(result.getByFieldName(relationPrefix + "_" + relation.columnName));
				else 
					instance[relation.fieldName] = relatedModel._newFromResultSet(result, relationPrefix + "_" + relation.columnName)
			}
		});
		return instance;
	},
	transaction: function(fn, bind){
		var conn = this.getConnection();
		try {
			if (conn.transactionDepth <= 0) 
				conn.execute("BEGIN");
			conn.transactionDepth++;
			fn.apply(bind || this, []);
		} 
		catch (e) {
			conn.transactionDepth = 0;
			conn.execute("ROLLBACK");
			throw e;
		}
		conn.transactionDepth = Math.max(0, conn.transactionDepth - 1);
		if (conn.transactionDepth <= 0) 
			conn.execute("COMMIT");
		
	}
	/*
	 TODO: load,
	 */
};
