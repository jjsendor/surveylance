/*
 * Bandoneon - another accordion-like effect
 */

dojo.require("dojo.fx");

function Bandoneon() {
	this.addPane = function(titleNodeId, paneNodeId) {
		var titleNode = dojo.byId(titleNodeId);
		var paneNode = dojo.byId(paneNodeId);
		
		var pane = new BandoneonPane(titleNode, paneNode);
		
		dojo.connect(titleNode, "onclick", pane, "togglePane");
	}
}

function BandoneonPane(titleNode, paneNode) {
	this.titleNode = titleNode;
	this.paneNode = paneNode;
	
	this.togglePane = function() {
		if (paneNode.style.display == "none") {
			dojo.fx.wipeIn({ node: paneNode.id, duration: 500 }).play();
			dojo.addClass(titleNode, "active");
		}
		else {
			dojo.fx.wipeOut({ node: paneNode.id, duration: 500 }).play();
			dojo.removeClass(titleNode, "active");
		}
	}
}
