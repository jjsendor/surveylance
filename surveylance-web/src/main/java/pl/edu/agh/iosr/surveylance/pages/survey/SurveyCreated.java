package pl.edu.agh.iosr.surveylance.pages.survey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import pl.edu.agh.iosr.surveylance.data.QuestionKind;
import pl.edu.agh.iosr.surveylance.data.QuestionType;
import pl.edu.agh.iosr.surveylance.entities.Answer;
import pl.edu.agh.iosr.surveylance.entities.Component;
import pl.edu.agh.iosr.surveylance.entities.NumericAnswer;
import pl.edu.agh.iosr.surveylance.entities.Question;
import pl.edu.agh.iosr.surveylance.entities.Survey;
import pl.edu.agh.iosr.surveylance.entities.TextAnswer;
import pl.edu.agh.iosr.surveylance.service.QuestionManager;
import pl.edu.agh.iosr.surveylance.service.ComponentManager;

@IncludeJavaScriptLibrary({
	"context:js/gears/gears_init.js",
	"context:js/gears/gears.js",
	"context:js/gears/survey.js",
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
	"context:js/model.js"
})
public class SurveyCreated {

	@Persist
	private Survey survey;

	@Persist
	private boolean created;

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public boolean isCreated() {
		if (created) {
			created = false;
			return true;
		}

		return false;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

}
