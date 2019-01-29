
dojo.require("dojo.fx");

function MessageBox(message, type) {
	if (type === undefined)
		type = "info";
	
	this.message = message;
	this.type = type;
	
	this.show = function() {
		var messageBoxNode = dojo.byId("message_box");
		
		if (messageBoxNode.style.display != "none")
			dojo.fx.wipeOut({ node: messageBoxNode.id, duration: 250 }).play();
		
		setTimeout("dojo.removeClass(dojo.byId(\"" + messageBoxNode.id + "\"), \"info\");", 500);
		setTimeout("dojo.removeClass(dojo.byId(\"" + messageBoxNode.id + "\"), \"warning\");", 500);
		setTimeout("dojo.removeClass(dojo.byId(\"" + messageBoxNode.id + "\"), \"error\");", 500);
		
		var messageIconPath = IMAGES_PATH + "/32x32/messagebox_" + type + ".png";
		var messageIcon = "<img title=\"" + type + "\" src=\"" + messageIconPath + "\" />";
		var messageHTML = messageIcon + "<span>" + message + "</span>";
		
		setTimeout("dojo.byId(\"" + messageBoxNode.id + "\").innerHTML = '" + messageHTML + "';", 500);
		
		setTimeout("dojo.addClass(dojo.byId(\"" + messageBoxNode.id + "\"), \"" + type + "\");", 500);
		setTimeout("dojo.fx.wipeIn({ node: \"" + messageBoxNode.id +
				"\", duration: 250 }).play();", 500);
		
	}
	
	this.hide = function() {
		var messageBoxNode = dojo.byId("message_box");
		
		if (messageBoxNode.style.display != "none")
			dojo.fx.wipeOut({ node: messageBoxNode.id, duration: 500 }).play();
	}
}