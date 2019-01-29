JStORM.Gears = {};
JStORM.Gears.Connection = function(){};

JStORM.Gears.Connection.prototype = 
{
	execute:function(sql,params)
	{
		if(params)
			return new JStORM.Gears.ResultSet(this.conn.execute(sql,params));
		else
			return new JStORM.Gears.ResultSet(this.conn.execute(sql));
	},
	executeNonQuery:function(sql,params)
	{
		if(params)
			new JStORM.Gears.ResultSet(this.conn.execute(sql,params)).close();
		else
			new JStORM.Gears.ResultSet(this.conn.execute(sql)).close();	
	},
	executeScalar:function(sql,params)
	{
		return new JStORM.Gears.ResultSet(this.conn.execute(sql,params)).getScalar();
	},
	getLastInsertId:function()
	{
		return this.conn.lastInsertRowId;
	},
	open:function(connParam)
	{
		this.conn = google.gears.factory.create('beta.database');
		this.conn.open(connParam.HOST);
	},
	close:function()
	{
		this.conn.close();
	}
};

JStORM.Gears.ResultSet = function(resultSet)
{
	this.result = resultSet;
	this.first = true;
};

JStORM.Gears.ResultSet.prototype =
{
	next:function()
	{
		if(this.first)
			this.first = false;
		else
			this.result.next();
			
		return this.result.isValidRow();
	},
	close:function()
	{
		if(this.result.close)
			this.result.close();
	},
	getByFieldName:function(fieldName)
	{
		return this.result.fieldByName(fieldName);
	},
	getByFieldPos:function(fieldPos)
	{
		return this.result.field(fieldPos);
	},
	getScalar:function()
	{
		var ret = this.result.isValidRow() ? this.result.field(0) : null;
		this.close();
		return ret;
	}
};
