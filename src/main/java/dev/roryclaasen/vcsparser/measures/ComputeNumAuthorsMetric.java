// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.measures.PluginMetrics.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.google.common.collect.ObjectArrays;

import dev.roryclaasen.vcsparser.measures.PluginMetrics.MetricDetails;
import dev.roryclaasen.vcsparser.measures.authors.Author;
import dev.roryclaasen.vcsparser.measures.authors.AuthorData;

@ComputeEngineSide
public class ComputeNumAuthorsMetric implements MeasureComputer {

	private final Logger log = Loggers.get(ComputeNumAuthorsMetric.class);

	private String authorsData = "vcsparser_authors_data";

	private String[] numAuthors = MetricDetails.NUM_AUTHORS.getKeyAllDates();
	private String[] numAuthors10Perc = MetricDetails.NUM_AUTHORS_10_PERC.getKeyAllDates();

	public static final String DATE_FORMAT = "yyyy/MM/dd";
	private SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

	private static Map<String, Map<String, List<AuthorData>>> authorsCache = new HashMap<String, Map<String, List<AuthorData>>>();

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
		return defContext.newDefinitionBuilder()
				.setInputMetrics(authorsData)
				.setOutputMetrics(ObjectArrays.concat(numAuthors, numAuthors10Perc, String.class))
				.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		for (int i = 0; i < numAuthors.length; i++) {
			switch (context.getComponent().getType()) {
			case PROJECT:
			case MODULE:
			case DIRECTORY:
				computeChildMeasure(context, numAuthors[i]);
				break;
			case FILE:
				compute(context, authorsData, numAuthors[i]);
			default:
				break;
			}
		}
	}

	protected void computeChildMeasure(MeasureComputerContext context, String numAuthorsKey) {
		String currentKey = context.getComponent().getKey();
		String projectKey = currentKey.split(":", 2)[0];
		if (!authorsCache.containsKey(projectKey))
			return;

		Map<String, List<AuthorData>> projectCache = authorsCache.get(projectKey);

		List<String> authors = new ArrayList<String>();
		for (Entry<String, List<AuthorData>> entry : projectCache.entrySet()) {
			if (!entry.getKey().startsWith(currentKey))
				continue;
			List<AuthorData> authorEntry = entry.getValue();
			authorEntry = getAuthorsAfterDate(authorEntry, getMetricDateFromKey(numAuthorsKey).date());
			authors.addAll(getUniqueAuthors(authorEntry));
		}
		
		authors = getUniqueStrings(authors);
		if (authors.size() > 0)
			context.addMeasure(numAuthorsKey, authors.size());
	}

	protected void compute(MeasureComputerContext context, String authorsDataKey, String numAuthorsKey) {
		Measure authorsData = context.getMeasure(authorsDataKey);

		if (authorsData == null)
			return;

		String projectKey = context.getComponent().getKey().split(":", 2)[0];

		JSONArray authorListJson = new JSONArray(authorsData.getStringValue());
		List<AuthorData> authorList = processAuthors(authorListJson);
		if (authorList.size() == 0)
			return;

		if (!authorsCache.containsKey(projectKey))
			authorsCache.put(projectKey, new HashMap<String, List<AuthorData>>());

		authorsCache.get(projectKey).put(context.getComponent().getKey(), authorList);

		authorList = getAuthorsAfterDate(authorList, getMetricDateFromKey(numAuthorsKey).date());
		List<String> uniqueAuthors = getUniqueAuthors(authorList);
		if (uniqueAuthors.size() > 0)
			context.addMeasure(numAuthorsKey, uniqueAuthors.size());
	}

	private List<String> getUniqueStrings(List<String> stringList) {
		Map<String, Boolean> uniqueStrings = new HashMap<String, Boolean>();
		for (String item : stringList) {
			if (!uniqueStrings.containsKey(item))
				uniqueStrings.put(item, true);
		}
		return new ArrayList<String>(uniqueStrings.keySet());
	}

	private List<String> getUniqueAuthors(List<AuthorData> authorDataList) {
		Map<String, Boolean> uniqueAuthors = new HashMap<String, Boolean>();
		for (AuthorData authorData : authorDataList) {
			for (Author author : authorData.authors) {
				if (!uniqueAuthors.containsKey(author.name))
					uniqueAuthors.put(author.name, true);
			}
		}
		return new ArrayList<String>(uniqueAuthors.keySet());
	}

	private List<AuthorData> getAuthorsAfterDate(List<AuthorData> authorDataList, Date date) {
		List<AuthorData> datedAuthorDataList = new ArrayList<AuthorData>();
		for (AuthorData authorData : authorDataList) {
			if (authorData.date.after(date))
				datedAuthorDataList.add(authorData);
		}
		return datedAuthorDataList;
	}

	private List<AuthorData> processAuthors(JSONArray authorsArray) {
		try {
			List<AuthorData> authorDataList = new ArrayList<AuthorData>();
			for (Object object : authorsArray) {
				authorDataList.add(jsonObjectToAuthor((JSONObject) object));
			}
			return authorDataList;
		} catch (JSONException | ParseException e) {
			log.error("Unable to process authors", e);
			return null;
		}
	}

	private AuthorData jsonObjectToAuthor(JSONObject object) throws JSONException, ParseException {
		Date date = dateParser.parse(object.getString("Date"));
		List<Author> authors = new ArrayList<Author>();
		for (Object authorObj : (JSONArray) object.get("Authors")) {
			JSONObject authorJson = (JSONObject) authorObj;
			String name = authorJson.getString("Author");
			int changes = authorJson.getInt("NumberOfChanges");
			authors.add(new Author(name, changes));
		}
		return new AuthorData(date, authors);
	}
}
