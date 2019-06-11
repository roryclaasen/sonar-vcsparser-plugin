// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;
import org.sonar.api.measures.Metrics;

import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

@SuppressWarnings("rawtypes")
public class PluginMetrics implements Metrics {
	protected static final Map<String, Metric> Metrics;
	public static final List<Pair<String, String>> DatePairs;

	public static final String CONFIG_ENV_VARIABLE = "SONAR_VCSPARSER_JSONDATA";
	public static final String DEFAULT_DOMAIN = "Code Churn";

	public enum MetricDetails {
		LINES_FIXED_OVER_CHANGED("vcsparser_linesfixedoverchanged", "Lines fixed/changed", "Lines fixed/changed", DEFAULT_DOMAIN, ValueType.PERCENT, Metric.DIRECTION_NONE, false);

		private String key;
		private String name;
		private String description;
		private String domain;
		private ValueType valueType;
		private int direction;
		private boolean qualitative;

		private MetricDetails(String key, String name, String description, String domain, ValueType valueType, int direction, boolean qualitative) {
			this.key = key;
			this.name = name;
			this.description = description;
			this.domain = domain;
			this.valueType = valueType;
			this.direction = direction;
			this.qualitative = qualitative;
		}

		public String[] getKeyAllDates() {
			return getAllDatesForMetric(this.key);
		}
	}

	public static String[] getAllDatesForMetric(String key) {
		List<String> keys = new ArrayList<String>();
		for (Pair<String, String> date : DatePairs) {
			keys.add(key + date.getLeft());
		}
		return keys.toArray(new String[keys.size()]);
	}

	static {
		DatePairs = new ArrayList<Pair<String, String>>();
		DatePairs.add(Pair.of("_1y", " (1 year)"));
		DatePairs.add(Pair.of("_6m", " (6 months)"));
		DatePairs.add(Pair.of("_3m", " (3 months)"));
		DatePairs.add(Pair.of("_30d", " (30 days)"));
		DatePairs.add(Pair.of("_7d", " (7 days)"));
		DatePairs.add(Pair.of("_1d", " (1 day)"));

		Metrics = new HashMap<String, Metric>();
		for (MetricDetails details : MetricDetails.values()) {
			for (Pair<String, String> date : DatePairs) {
				Metrics.put(details.key + date.getLeft(), createMetric(details, date.getLeft(), date.getRight()));
			}
		}
	}

	private static Metric createMetric(MetricDetails details, String keySuffix, String nameSuffix) {
		return new Metric.Builder(details.key + keySuffix, details.name + nameSuffix, details.valueType)
				.setDescription(details.description + nameSuffix)
				.setDomain(details.domain)
				.setDirection(details.direction)
				.setQualitative(details.qualitative)
				.create();
	}

	public static void loadAndAlter(IEnvironment environment, IFileReader fileReader) {
		String environmentVariable = environment.getEnvironmentVariable(CONFIG_ENV_VARIABLE);

		if (environmentVariable == null)
			return;
		if (environmentVariable.trim().length() == 0)
			return;

		String jsonString = fileReader.readFile(environmentVariable);
		if (jsonString != null)
			parseJson(jsonString);
	}

	private static void parseJson(String jsonString) {
		JSONObject jsonData = new JSONObject(jsonString);
		JSONArray metricsJson = jsonData.getJSONArray("metrics");
		for (int i = 0; i < metricsJson.length(); i++) {
			JSONObject metric = metricsJson.getJSONObject(i);
			alterMetric(metric);
		}
	}

	private static void alterMetric(JSONObject jsonObject) {
		String key = jsonObject.getString("key");
		if (!Metrics.containsKey(key))
			return;
		Metric metric = Metrics.get(key);

		if (jsonObject.has("name"))
			metric.setName(jsonObject.getString("name"));

		if (jsonObject.has("description"))
			metric.setDescription(jsonObject.getString("description"));

		if (jsonObject.has("domain"))
			metric.setDomain(jsonObject.getString("domain"));
	}

	@Override
	public List<Metric> getMetrics() {
		return new ArrayList<Metric>(Metrics.values());
	}
}
