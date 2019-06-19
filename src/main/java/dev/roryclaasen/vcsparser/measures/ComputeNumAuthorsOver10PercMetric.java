// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.measures.PluginMetrics.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import com.google.common.collect.ObjectArrays;

import dev.roryclaasen.vcsparser.authors.Author;
import dev.roryclaasen.vcsparser.authors.AuthorData;
import dev.roryclaasen.vcsparser.authors.AuthorUtils;
import dev.roryclaasen.vcsparser.measures.PluginMetrics.MetricDetails;

@ComputeEngineSide
public class ComputeNumAuthorsOver10PercMetric implements MeasureComputer {
	private String authorsData = "vcsparser_authors_data";
	private String[] numChanges = getAllDatesForMetric("vcsparser_numchanges");

	private String[] numAuthors10Perc = MetricDetails.NUM_AUTHORS_10_PERC.getKeyAllDates();

	private static Map<String, Map<String, List<AuthorData>>> authorsCache = new HashMap<String, Map<String, List<AuthorData>>>();

	private double threshold = 10.0;

	private AuthorUtils authorUtils;

	public ComputeNumAuthorsOver10PercMetric(AuthorUtils authorUtils) {
		this.authorUtils = authorUtils;
	}

	public void removeProjectCache(String projectKey) {
		if (authorsCache.containsKey(projectKey))
			authorsCache.remove(projectKey);
	}

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
		return defContext.newDefinitionBuilder()
				.setInputMetrics(ObjectArrays.concat(new String[] { authorsData }, numChanges, String.class))
				.setOutputMetrics(numAuthors10Perc)
				.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		for (int i = 0; i < numAuthors10Perc.length; i++) {
			switch (context.getComponent().getType()) {
			case PROJECT:
			case MODULE:
			case DIRECTORY:
				computeChildMeasure(context, numAuthors10Perc[i], numChanges[i]);
				break;
			case FILE:
				compute(context, authorsData, numAuthors10Perc[i], numChanges[i]);
			default:
				break;
			}
		}
	}

	protected void computeChildMeasure(MeasureComputerContext context, String numAuthorsKey, String numChangesKey) {
		Measure numChangesMeasure = context.getMeasure(numChangesKey);
		if (numChangesMeasure == null)
			return;
		
		int numChanges = numChangesMeasure.getIntValue();
		if (numChanges == 0)
			return;
		
		String currentKey = context.getComponent().getKey();
		String projectKey = currentKey.split(":", 2)[0];
		if (!authorsCache.containsKey(projectKey))
			return;

		Map<String, List<AuthorData>> projectCache = authorsCache.get(projectKey);
				
		Date date = getMetricDateFromKey(numAuthorsKey).date();
		Map<String, Integer> numChangesDict = new HashMap<String, Integer>();
		for (Entry<String, List<AuthorData>> entry : projectCache.entrySet()) {
			if (!entry.getKey().startsWith(currentKey))
				continue;
			List<Author> authorEntry = authorUtils.getAuthorListAfterDate(entry.getValue(), date);
			numChangesDict = authorUtils.getNumChangesPerAuthor(numChangesDict, authorEntry);
		}

		int authorsOver = 0;
		for (Integer item : numChangesDict.values()) {
			double perc = (item * 100D) / numChanges;
			if (perc >= threshold)
				authorsOver++;
		}
		if (authorsOver > 0)
			context.addMeasure(numAuthorsKey, authorsOver);
	}

	protected void compute(MeasureComputerContext context, String authorsDataKey, String numAuthorsKey, String numChangesKey) {
		Measure authorsDataMeasure = context.getMeasure(authorsDataKey);
		Measure numChangesMeasure = context.getMeasure(numChangesKey);

		if (authorsDataMeasure == null || numChangesMeasure == null)
			return;

		int numChanges = numChangesMeasure.getIntValue();
		if (numChanges == 0)
			return;

		String projectKey = context.getComponent().getKey().split(":", 2)[0];

		JSONArray authorListJson = new JSONArray(authorsDataMeasure.getStringValue());
		List<AuthorData> authorDataList = authorUtils.jsonArrayToAuthorDataList(authorListJson);
		if (authorDataList.size() == 0)
			return;

		if (!authorsCache.containsKey(projectKey))
			authorsCache.put(projectKey, new HashMap<String, List<AuthorData>>());

		authorsCache.get(projectKey).put(context.getComponent().getKey(), authorDataList);

		List<Author> authorList = authorUtils.getAuthorListAfterDate(authorDataList, getMetricDateFromKey(numAuthorsKey).date());
		Collection<Integer> authorNumChanges = authorUtils.getNumChangesPerAuthor(new HashMap<String, Integer>(), authorList).values();
		
		int authorsOver = 0;
		for (Integer item : authorNumChanges) {
			double perc = (item * 100D) / numChanges;
			if (perc >= threshold)
				authorsOver++;
		}
		if (authorsOver > 0)
			context.addMeasure(numAuthorsKey, authorsOver);
	}
}
