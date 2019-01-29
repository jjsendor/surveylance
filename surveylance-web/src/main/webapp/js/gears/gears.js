var MANIFEST_FILE;

var gears = {
	// This function checks if Google Gears are enabled.
	available: function() {
		return window.google && google.gears
			&& google.gears.factory.hasPermission;
	},
	localServer: null,
	managedStore: null,
	dynamicStore: null,
	STORE_NAME: 'SurveylanceStore',
	// Called after logging to prepare application to work in offline mode.
	initializeResourceStore: function() {
		// Check if the user has Gears installed.
		if (window.google && google.gears) {
			// Create the LocalServer module instance.
			try {
				gears.localServer =
					google.gears.factory.create('beta.localserver');
			} catch (ex) {
				console.debug("Exception while obtaining local server");
				// TODO externalize it, remove alert invocation and localize this
				// message
				//alert('Using application in offline mode is not possible without Google Gears. Cannot obtain local store');
				return;
			}
			
			// Open or create a managed resource store.
			gears.managedStore
					= gears.localServer.openManagedStore(gears.STORE_NAME);
			
			if (!gears.managedStore) {
				gears.managedStore =
					gears.localServer.createManagedStore(gears.STORE_NAME);
				
				// The manifest describes what will go into the store.
				gears.managedStore.manifestUrl = MANIFEST_FILE;
				
				// Download the initial version.
				gears.managedStore.checkForUpdate();
			}
			
			// Enable managed resource store.
			//gears.managedStore.enabled = true;

			// If the store is empty (no currentVersion attribute), it means that
			// the next update will download the initial version of resources. We
			// show appropriate alerts only in this situation, because we don't want
			// to bother the user each time we update the software.
			if (!gears.managedStore.currentVersion) {
				gears.managedStore.oncomplete = function() {
					// TODO externalize it, remove alert invocation and localize
					// this message
					//alert('Surveylance is ready for offline use.');
					gears.managedStore.oncomplete = null; // Show it only once.
				};
				gears.managedStore.onerror = function(error) {
					// TODO externalize it, remove alert invocation and localize
					// this message
					alert('Error when installing offline access: ' + error.message);
					gears.managedStore.onerror = null; // Show it only once.
				};
			}

			// Open or create a dynamic resource store.
			gears.dynamicStore = gears.localServer.openStore(gears.STORE_NAME);
			if (!gears.dynamicStore) {
				gears.dynamicStore =
					gears.localServer.createStore(gears.STORE_NAME);
			}
			
			// Enable dynamic resource store.
			//gears.dynamicStore.enabled = true;
		}
		else {
			// TODO externalize it, remove alert invocation and localize this
			// message
			//alert('Using application in offline mode is not possible without Google Gears.');
		}
	},
	// This function checks if content is served from local store.
	isServedFromLocalStore: function() {
		return gears.managedStore.enabled;
	},
	// This function enables serving content from local store.
	serveFromLocalStore: function() {
		gears.managedStore.enabled = true;
		gears.dynamicStore.enabled = true;
	},
	// This function disables serving content from local store.
	serveFromServer: function() {
		gears.managedStore.enabled = false;
		gears.dynamicStore.enabled = false;
	},
	// This function loads pages to serving content from local store.
	loadPagesToOfflineUse: function(pages) {
		try {
			for (var i = 0; i < pages.length; i++) {
				gears.dynamicStore.remove(pages[i]);
			}
		} catch (ex) {
			// No handler required.
		}
		try {
			gears.dynamicStore.capture(pages, gears.captureCallback);
		} catch (ex) {
			// TODO externalize it, remove alert invocation and localize this
			// message
			alert('Error when installing offline access.');
		}
	},
	// This is callback function for capture() function.
	captureCallback: function(url, success, captureId) {
		if (!success) {
			// TODO externalize it, remove alert invocation and localize this
			// message
			alert("Capturing page " + url + " failed.");
		}
	},
	goOnline: function() {
//		dojo.byId("gears_online_info").style.display = "inline";
//		dojo.byId("gears_offline_info").style.display = "none";
		gears.serveFromServer();
//		console.log("Working in on-line mode");
//		refresh();
	},
	goOffline: function() {
//		dojo.byId("gears_online_info").style.display = "none";
//		dojo.byId("gears_offline_info").style.display = "inline";
		gears.serveFromLocalStore();
//		console.log("Working in off-line mode");
//		refresh();
	}
}

function refresh() {
	window.location.href = window.location.pathname;
}
