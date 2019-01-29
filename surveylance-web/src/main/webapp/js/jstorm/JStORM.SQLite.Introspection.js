JStORM.SQLite.Introspection = function(connName)
{
	this.sqliteMasterModel = new JStORM.Model({
		name:"sqlite_master",
		fields:
		{
			type:new JStORM.Field({type:"String"}),
			name:new JStORM.Field({type:"String"}),
			tbl_name:new JStORM.Field({type:"String"}),
			rootpage:new JStORM.Field({type:"Integer"}),
			sql:new JStORM.Field({type:"String"})
		},
		connection:connName
	});
	this.conn = this.sqliteMasterModel.getConnection();
};
JStORM.SQLite.Introspection.prototype = 
{
	doesTableExist:function()
	{
		var argsLength = arguments.length;
		var qmarks = [];
		//used instead of ["table"].concat(arguments) since it doesn`t work as expected
		var vals = ["table"];
		for(var i=0;i<argsLength;i++)
		{
			qmarks.push("?");
			vals.push(arguments[i]);	
		}
		var query = ["sqlite_master.type =? AND sqlite_master.name IN (",qmarks.join(","),")"].join("");
		vals = [query].concat(vals);
		return this.sqliteMasterModel.filter.apply(this.sqliteMasterModel,vals).count() == argsLength;
	}
};