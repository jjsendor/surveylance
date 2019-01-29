JStORM.Query = function(modelClass)
{
	this.modelClass = modelClass;
	this._meta = modelClass._meta;
	this._sql = modelClass._sql;
	this._whereCaluse = [];
	this._orderBy = [];
	this._params = [];
	this._limit = 0;
	this._offset = 0;
	this._result = null;
};

JStORM.Query.prototype = 
{
	////////////////////////////////////////////////
	/// functions that return new modified query ///
	////////////////////////////////////////////////
	filter:function(whereClause)
	{
		var clone = this._clone();
		clone._whereCaluse.push("("+whereClause+")");
		this._extendArrayFromArg(clone._params,arguments,1);
		return clone;
	},
	orderBy:function()
	{
		var clone = this._clone();
		this._extendArrayFromArg(clone._orderBy,arguments);
		return clone;
	},
	limit:function(limit)
	{
		var clone = this._clone();
		clone._limit = limit;
		return clone;
	},
	offset:function(offset)
	{
		var clone = this._clone();
		clone._offset = offset;
		return clone;
	},
	/*selectRelated:function()
	{
		
		
	},*/
	////////////////////////////////////
	/// functions that execute sql ////
	//////////////////////////////////
	count:function()
	{
		return this._executeScalar(this._sql.getCountSql(this._whereCaluse));
	},
	remove:function()
	{
		this._executeNonQuery(this._sql.getDeleteSql(this._whereCaluse));
	},
	first:function()
	{
		var first = this.next();
		this.close();
		return first;
	},
	next:function()
	{
		if(!this._result)
		{
			//execute the query
			this._result = this._execute(this._sql.getSelectSql(
				this._whereCaluse,this._orderBy,this._limit,this._offset
			));
		}
		
		if (this._result.next())
			return this.modelClass._newFromResultSet(this._result,this._meta.tableName);
		else {
			this.close();
			return false;
		}
	},
	close:function()
	{
		if (this._result)
		{
			this._result.close();
			//make result null if we want to run the query again
			this._result = null;
		}
	},
	each:function(fn,bind)
	{
		var current;
		while(current = this.next())fn.apply(bind || this,[current]);
	},
	toArray:function()
	{
		var arr = [];
		this.each(arr.push,arr);
		return arr;
	},
	///////////////////////////////////
	/// private functions          ///
	/////////////////////////////////
	_getConnection:function()
	{
		return this.modelClass.getConnection()
	},
	_execute:function(sql)
	{
		return this._getConnection().execute(sql,this._getParams());
	},
	_executeScalar:function(sql)
	{
		return this._getConnection().executeScalar(sql,this._getParams());
	},
	_executeNonQuery:function(sql)
	{
		return this._getConnection().executeNonQuery(sql,this._getParams());
	},
	_extendArrayFromArg:function(arr,args,offset)
	{
		arr.push.apply(arr,Array.prototype.slice.apply(args,[offset ? offset : 0]));
	},
	_getParams:function()
	{
		var params = [];
		for(var i=0,l=this._params.length;i<l;i++)
			params.push(typeof(this._params[i]) == "function" ? this._params[i]() : this._params[i]);
		return params;
	},
	_clone:function()
	{
		var clone = new JStORM.Query(this.modelClass);
		clone._whereCaluse = this._whereCaluse; 
		clone._orderBy = this._orderBy;
		clone._params = this._params;
		clone._limit = this._limit;
		clone._offset = this._offset;
		return clone;
	}
};
