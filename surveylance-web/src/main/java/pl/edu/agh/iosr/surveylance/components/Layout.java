package pl.edu.agh.iosr.surveylance.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.service.data.UserSessionInfo;

@IncludeStylesheet("context:css/main.css")
@IncludeJavaScriptLibrary({
	"context:js/dojo/dojo.js",
	"context:js/gears/gears_init.js",
	"context:js/gears/gears.js",
	"context:js/jstorm/JStORM.js",
	"context:js/jstorm/JStORM.Query.js",
	"context:js/jstorm/JStORM.Gears.js",
	"context:js/jstorm/JStORM.Sql.js",
	"context:js/jstorm/JStORM.Field.js",
	"context:js/jstorm/JStORM.ModelMetaData.js",
	"context:js/jstorm/JStORM.Model.js",
	"context:js/jstorm/JStORM.Events.js",
	"context:js/jstorm/JStORM.SQLite.Introspection.js",
	"context:js/jstorm/gears_model.js",
	"context:js/gears/survey.js",
	"context:js/gears/component.js",
	"context:js/gears/question.js",
	"context:js/gears/answer.js",
	"context:js/gears/sync.js",
	"context:js/model.js",
	"context:js/message_box.js"
})
public class Layout {

	@ApplicationState(create = false)
	@Property
	private UserSessionInfo userInfo;

	@Inject
	@Path("context:/")
	private Asset relativeContext;

	@Inject
	@Path("context:manifest.json")
	private Asset gearsManifest;

	@Inject
	@Path("context:images/")
	private Asset imagesPath;

	@Inject
	@Path("context:version.js")
	private Asset versionJS;

	public boolean isInitGears() {
		if (userInfo != null && !userInfo.isInitGears()) {
			userInfo.initGears();
			return true;
		}

		return false;
	}

	public String getRelativeContext() {
		return relativeContext.toClientURL();
	}

	public String getGearsManifestPath() {
		return gearsManifest.toClientURL();
	}

	public String getImagesPath() {
		return imagesPath.toClientURL();
	}

	public String getVersionJSPath() {
		return versionJS.toClientURL();
	}

}
