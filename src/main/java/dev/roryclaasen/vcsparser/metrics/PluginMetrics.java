// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

@SuppressWarnings("rawtypes")
public class PluginMetrics implements Metrics {
    protected static final Map<String, Metric> Metrics;

    public static final String CONFIG_ENV_VARIABLE = "SONAR_VCSPARSER_JSONDATA";
    public static final String DEFAULT_DOMAIN = "Code Churn";

    static {
        Metrics = new HashMap<String, Metric>();
        for (PluginMetric details : PluginMetric.values()) {
            for (MetricDate date : MetricDate.values()) {
                Metrics.put(details.getKey() + date.getSuffix(), details.build(date.getSuffix(), date.getDescription()));
            }
        }
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
