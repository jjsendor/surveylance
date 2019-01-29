JStORM.Events = function()
{
	this.$events = {};
}
JStORM.Events.prototype = 
{
	addListener:function(name,fn)
	{
		this.$events[name] = this.$events[name] || [];
		this.$events[name].push(fn);
		return this;	
	},
	removeListener:function(name,fn)
	{
		if(this.$events[name])
			this.$events[name].remove(fn);
		return this;
	},
	fireEvent:function(name,args,bind)
	{
		var listeners = this.$events[name];
		if(listeners)
			for(var i=0,ln=listeners.length;i<ln;i++)
				listeners[i].apply(bind || this,args || [])
		return this;
	}
};

JStORM.Events.wrapFunction=function(fn,name,object)
{
	return function()
	{
		object.fireEvent("onBefore"+name,[this]);
		var ret = fn.apply(this,arguments);
		object.fireEvent("onAfter"+name,[this]);
		return ret;
	};
};