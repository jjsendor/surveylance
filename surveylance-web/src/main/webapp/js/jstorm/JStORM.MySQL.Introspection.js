JStORM.MySQL.Introspection = function(connName)
{
	this.conn = JStORM.getConnection(connName);
};
JStORM.MySQL.Introspection.prototype = 
{
	doesTableExist:function()
	{
		var result = this.conn.execute("SHOW TABLES");
		var tableNames = {};
		while (result.next())
			tableNames[result.getByFieldPos(0)] = true;
			
		for(var i=0,l=arguments.length;i<l;i++)
			if(!tableNames[arguments[i].toLowerCase()])
				return false;
		return true;
	}
};