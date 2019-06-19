// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

@ComputeEngineSide
public class AuthorUtils {
	private final Logger log = Loggers.get(AuthorUtils.class);

	protected static final String DATE_FORMAT = "yyyy/MM/dd";
	private SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);
	
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
			String name = authorJson.getString("author");
			int changes = authorJson.getInt("number_of_changes");
			authors.add(new Author(name, changes));
		}
		return new AuthorData(date, authors);
	}
	
	public List<Author> getAuthorListAfterDate(Collection<AuthorData> authorDataList, Date date) {
		List<Author> datedAuthorDataList = new ArrayList<Author>();
		for (AuthorData authorData : authorDataList) {
			if (authorData.date.after(date))
				datedAuthorDataList.addAll(authorData.authors);
		}
		return datedAuthorDataList;
	}

	public Map<String, Integer> getNumChangesPerAuthor(Map<String, Integer> numChanges, Collection<Author> authors) {
		for (Author author : authors) {
			if (!numChanges.containsKey(author.name))
				numChanges.put(author.name, author.numberOfChanges);
			else {
				int value = numChanges.get(author.name);
				value += author.numberOfChanges;
				numChanges.replace(author.name, value);
			}
		}
		return numChanges;
	}
}
