// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.utils.log.Logger;

import dev.roryclaasen.vcsparser.LoggerCreator;

@ComputeEngineSide
public class JsonAuthorParser {
	private final Logger log;

	protected static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

	public JsonAuthorParser(LoggerCreator loggerCreator) {
		log = loggerCreator.get(AuthorListConverter.class);
	}

	public List<AuthorData> jsonStringArrayToAuthorDataList(String jsonStringArray) {
		return jsonArrayToAuthorDataList(new JSONArray(jsonStringArray));
	}

	public List<AuthorData> jsonArrayToAuthorDataList(JSONArray authorsArray) {
		try {
			List<AuthorData> authorDataList = new ArrayList<AuthorData>();
			for (Object object : authorsArray) {
				authorDataList.add(jsonObjectToAuthorData((JSONObject) object));
			}
			return authorDataList;
		} catch (JSONException | ParseException e) {
			log.error("Unable to process authors", e);
			return null;
		}
	}

	public AuthorData jsonObjectToAuthorData(JSONObject object) throws JSONException, ParseException {
		Date date = dateParser.parse(object.getString("date"));
		List<Author> authors = new ArrayList<Author>();
		for (Object authorObj : (JSONArray) object.get("authors")) {
			JSONObject authorJson = (JSONObject) authorObj;
			String name = authorJson.getString("Author");
			int changes = authorJson.getInt("NumberOfChanges");
			authors.add(new Author(name, changes));
		}
		return new AuthorData(date, authors);
	}
}
