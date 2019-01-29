dojo.require("dojo.dnd.Source");

function surveyItemCreator(data, hint) {
	var surveyListItem = dojo.doc.createElement("li");
	var surveyNameSpan = dojo.doc.createElement("span");
	surveyNameSpan.textContent = data.surveyName;
	surveyListItem.appendChild(surveyNameSpan);

	return { node: surveyListItem, data: data, type: data.type };
}

var surveyChooserSource;	// dnd source

function addToSurveyChooser(surveyId, surveyName) {
	surveyChooserSource.insertNodes(false, [{
		surveyId: surveyId,
		surveyName: surveyName
	}]);
}

var initDnd = function() {
	surveyChooserSource = new dojo.dnd.Source("surveys_chooser",
		{
			creator: surveyItemCreator,
			copyOnly: true,
			selfAccept: true
		}
	);
};

dojo.addOnLoad(initDnd);

function goToCampaignAnalysis() {
	var ids = [];

	surveyChooserSource.getAllNodes().forEach(function(node) {
		ids.push(surveyChooserSource.getItem(node.id).data.surveyId);
	});

	window.location = "AnalysisMainPage?ids=" + ids.join(",");
}
