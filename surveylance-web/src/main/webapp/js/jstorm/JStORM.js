var JStORM = 
{
	_models:{},
	connections:{},
	debug:true,
	version:'0.3beta',
	getConnectionParams:function(connName)
	{
		return JStORM.connections[connName];
	},
	getConnection:function(connName)
	{
		var connParams = JStORM.getConnectionParams(connName);
		var conn = new JStORM[connParams.PROVIDER].Connection();
		conn.open(connParams);
		conn.transactionDepth = 0;
		return conn;
	},
	getIntrospection:function(connName)
	{
		var connParams = JStORM.getConnectionParams(connName);
		return new JStORM[connParams.DIALECT].Introspection(connName);
	},
	extend:function(src, add)
	{
		if (!add)
		{
			add = src;
			src = this;
		}
		for (var property in add) src[property] = add[property];
		return src;
	},
	curry:function(fn, scope) {
	    var scope = scope || window;
	    var args = [];
	    for (var i=2, len = arguments.length; i < len; ++i) {
	        args.push(arguments[i]);
	    };
	    return function() {
		    fn.apply(scope, args);
	    };
	},
	each:function(arr,fn)
	{
		for(var i=0,l=arr.length;i<l;i++)
			fn(arr[i]);
	}
};
JStORM.SQLite = {};
JStORM.MySQL = {};

