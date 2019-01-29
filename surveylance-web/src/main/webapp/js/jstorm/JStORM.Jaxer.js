//Jaxer support almost identical api to Google Gears
JStORM.Jaxer = {};
JStORM.Jaxer.Connection = function(){};
JStORM.Jaxer.Connection.prototype = new JStORM.Gears.Connection;

JStORM.Jaxer.Connection.prototype.open = function(connParameters)
{
	if(connParameters.DIALECT == "MySQL")
		this.conn = new Jaxer.DB.MySQL.Connection(connParameters);
	else if(connParameters.DIALECT == "SQLite")
		this.conn = new Jaxer.DB.SQLite.Connection(connParameters);
	else
		throw new Error("not supported dialect")
	this.conn.open();
};

JStORM.Jaxer.Connection.prototype.getLastInsertId = function()
{
	return this.conn.lastInsertId;
};
