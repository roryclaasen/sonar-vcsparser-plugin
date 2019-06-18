// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
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

	public static final String CONFIG_ENV_VARIABLE = "SONAR_VCSPARSER_JSONDATA";
	public static final String DEFAULT_DOMAIN = "Code Churn";

	public enum MetricDetails {
		LINES_FIXED_OVER_CHANGED("vcsparser_linesfixedoverchanged", "Lines fixed/changed", "Lines fixed/changed", DEFAULT_DOMAIN, ValueType.PERCENT, Metric.DIRECTION_NONE, false),
		NUM_AUTHORS("vcsparser_numauthors", "Number of authors", "Number of authors", DEFAULT_DOMAIN, ValueType.INT, Metric.DIRECTION_NONE, false),
		NUM_AUTHORS_10_PERC("vcsparser_numauthors10perc", "Number of authors over 10% contrib", "Number of authors over 10% contrib", DEFAULT_DOMAIN, ValueType.INT, Metric.DIRECTION_NONE, false);

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

	private static Date MinusDate(int year, int month, int day) {
		Date today = new Date(); // TODO make the end of the day
		today = DateUtils.addDays(today, -day);
		today = DateUtils.addMonths(today, -month);
		today = DateUtils.addYears(today, -year);
		return today;
	}

	public enum MetricDates {
		YEAR_1("_1y", " (1 year)", MinusDate(1, 0, 0)),
		MONTH_6("_6m", " (6 months)", MinusDate(0, 6, 0)),
		MONTH_3("_3m", " (3 months)", MinusDate(0, 3, 0)),
		DAY_30("_30d", " (30 days)", MinusDate(0, 0, 30)),
		DAY_7("_7d", " (7 days)", MinusDate(0, 0, 7)),
		DAY_1("_1d", " (1 days)", MinusDate(0, 0, 1));

		private String suffix;
		private String description;
		private Date date;

		private MetricDates(String suffix, String description, Date date) {
			this.suffix = suffix;
			this.description = description;
			this.date = date;
		}
		
		public Date date() {
			return date;
		}
	}

	public static MetricDates getMetricDateFromKey(String key) {
		String suffix = key.substring(key.lastIndexOf('_'));
		for(MetricDates date: MetricDates.values()) {
			if (date.suffix.equals(suffix))
				return date;
		}
		return null;
	}

	public static String[] getAllDatesForMetric(String key) {
		List<String> keys = new ArrayList<String>();
		for (MetricDates date : MetricDates.values()) {
			keys.add(key + date.suffix);
		}
		return keys.toArray(new String[keys.size()]);
	}

	static {
		Metrics = new HashMap<String, Metric>();
		for (MetricDetails details : MetricDetails.values()) {
			for (MetricDates date : MetricDates.values()) {
				Metrics.put(details.key + date.suffix, createMetric(details, date.suffix, date.description));
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
