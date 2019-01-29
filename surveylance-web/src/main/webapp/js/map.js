// messages requiring localization
var MAP_LOAD_ERROR;
var MARKER_MESSAGE1;
var MARKER_MESSAGE2;
var MARKER_MESSAGE3;

var DEFAULT_X = 50.055816;
var DEFAULT_Y = 19.950485;

var map;
var marker;
var position;
var renderMarker = false;

var lat;
var lng;
var renderMarkers = false;

var surveyID;

var initMap = function() {
	if (GBrowserIsCompatible()) {
		map = new GMap2(document.getElementById("map_canvas"));
		position = new GLatLng(DEFAULT_X, DEFAULT_Y);
		map.setCenter(position, 8);
		map.setUIToDefault();

		if (renderMarker) {
			document.getElementById("survey_" + surveyID).xCoordinate.value = DEFAULT_X;
			document.getElementById("survey_" + surveyID).yCoordinate.value = DEFAULT_Y;
			showMarker();
		}
	} else {
		alert(MAP_LOAD_ERROR);
	}
};

function showMarker() {
	marker = new GMarker(position, {draggable: true});
	map.addOverlay(marker);
	marker.openInfoWindowHtml("<b>" + MARKER_MESSAGE1 + "<br>" + MARKER_MESSAGE2 + "</b>");

	GEvent.addListener(marker, "dragstart", function() {
		map.closeInfoWindow();
	});

	GEvent.addListener(marker, "dragend", function() {
		marker.openInfoWindowHtml("<b>" + MARKER_MESSAGE3 + "</b>");
		document.getElementById("survey_" + surveyID).xCoordinate.value = marker.getLatLng().lat();
		document.getElementById("survey_" + surveyID).yCoordinate.value = marker.getLatLng().lng();
	});
};