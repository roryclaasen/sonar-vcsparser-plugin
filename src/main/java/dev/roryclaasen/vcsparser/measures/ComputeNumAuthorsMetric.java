// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.metrics.MetricUtils.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import com.google.common.collect.ObjectArrays;

import dev.roryclaasen.vcsparser.authors.Author;
import dev.roryclaasen.vcsparser.authors.AuthorData;
import dev.roryclaasen.vcsparser.authors.AuthorUtils;
import dev.roryclaasen.vcsparser.metrics.PluginMetric;

@ComputeEngineSide
public class ComputeNumAuthorsMetric implements MeasureComputer {
	private String authorsData = "vcsparser_authors_data";
	private String[] numChanges = getAllDatesForMetric("vcsparser_numchanges");

	private String[] numAuthors = PluginMetric.NUM_AUTHORS.getKeyAllDates();
	private String[] numAuthors10Perc = PluginMetric.NUM_AUTHORS_10_PERC.getKeyAllDates();

	protected static Map<String, Map<String, List<AuthorData>>> authorsCache = new HashMap<String, Map<String, List<AuthorData>>>();

	private double threshold = 10.0;

	private AuthorUtils authorUtils;

	public ComputeNumAuthorsMetric(AuthorUtils authorUtils) {
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
				.setOutputMetrics(ObjectArrays.concat(numAuthors, numAuthors10Perc, String.class))
				.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		for (int i = 0; i < numAuthors10Perc.length; i++) {
			switch (context.getComponent().getType()) {
			case PROJECT:
			case MODULE:
			case DIRECTORY:
				computeChildMeasure(context, numAuthors[i], numAuthors10Perc[i], numChanges[i]);
				break;
			case FILE:
				computeFileMeasure(context, authorsData, numAuthors[i], numAuthors10Perc[i], numChanges[i]);
			default:
				break;
			}
		}
	}

	protected void computeChildMeasure(MeasureComputerContext context, String numAuthorsKey, String numAuthors10PercKey, String numChangesKey) {
		String currentKey = context.getComponent().getKey();
		String projectKey = currentKey.split(":", 2)[0];

		if (!authorsCache.containsKey(projectKey))
			return;

		Map<String, List<AuthorData>> projectCache = authorsCache.get(projectKey);

		Date date = getMetricDateFromKey(numAuthorsKey).getDate();
		Map<String, Integer> numChangesDict = new HashMap<String, Integer>();
		for (Entry<String, List<AuthorData>> entry : projectCache.entrySet()) {
			if (!entry.getKey().startsWith(currentKey))
				continue;

			numChangesDict = getAuthorNumChangesAfterDateDict(entry.getValue(), date, numChangesDict);
		}

		computeNumAuthors(context, numAuthorsKey, numChangesDict.values());
		computeNumAuthorsOver10Perc(context, numChangesKey, numAuthors10PercKey, numChangesDict.values());
	}

	protected void computeFileMeasure(MeasureComputerContext context, String authorsDataKey, String numAuthorsKey, String numAuthors10PercKey, String numChangesKey) {
		String currentKey = context.getComponent().getKey();
		String projectKey = currentKey.split(":", 2)[0];
		
		Measure authorsDataMeasure = context.getMeasure(authorsDataKey);

		if (authorsDataMeasure == null)
			return;

		List<AuthorData> authorDataList = authorUtils.jsonStringArrayToAuthorDataList(authorsDataMeasure.getStringValue());
		if (authorDataList.size() == 0)
			return;

		if (!authorsCache.containsKey(projectKey))
			authorsCache.put(projectKey, new HashMap<String, List<AuthorData>>());
		
		authorsCache.get(projectKey).put(currentKey, authorDataList);

		Collection<Integer> authorNumChanges = getAuthorNumChangesAfterDateDict(authorDataList, getMetricDateFromKey(numAuthorsKey).getDate(), new HashMap<String, Integer>()).values();

		computeNumAuthors(context, numAuthorsKey, authorNumChanges);
		computeNumAuthorsOver10Perc(context, numChangesKey, numAuthors10PercKey, authorNumChanges);
	}

	protected Map<String, Integer> getAuthorNumChangesAfterDateDict(Collection<AuthorData> authorDataList, Date dateFrom, Map<String, Integer> currentMap) {
		List<Author> authorList = authorUtils.getAuthorListAfterDate(authorDataList, dateFrom);
		return authorUtils.getNumChangesPerAuthor(currentMap, authorList);
	}

	protected void computeNumAuthors(MeasureComputerContext context, String numAuthorsKey, Collection<Integer> authorNumChanges) {
		if (authorNumChanges.size() > 0)
			context.addMeasure(numAuthorsKey, authorNumChanges.size());
	}

	protected void computeNumAuthorsOver10Perc(MeasureComputerContext context, String numChangesKey, String numAuthors10PercKey, Collection<Integer> authorNumChanges) {
		Measure numChangesMeasure = context.getMeasure(numChangesKey);
		if (numChangesMeasure == null)
			return;

		int numChanges = numChangesMeasure.getIntValue();
		if (numChanges == 0)
			return;

		int authorsOver = 0;
		for (Integer item : authorNumChanges) {
			double perc = (item * 100D) / numChanges;
			if (perc > threshold)
				authorsOver++;
		}
		if (authorsOver > 0)
			context.addMeasure(numAuthors10PercKey, authorsOver);
	}
}
