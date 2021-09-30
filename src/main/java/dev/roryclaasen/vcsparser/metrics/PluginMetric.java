// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static dev.roryclaasen.vcsparser.metrics.MetricKeyConverter.getAllDatesForMetric;
import static dev.roryclaasen.vcsparser.metrics.PluginMetrics.DEFAULT_DOMAIN;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;

public enum PluginMetric {
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

    private PluginMetric(String key, String name, String description, String domain, ValueType valueType, int direction, boolean qualitative) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.domain = domain;
        this.valueType = valueType;
        this.direction = direction;
        this.qualitative = qualitative;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDomain() {
        return domain;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isQualitative() {
        return qualitative;
    }

    public String[] getKeyAllDates() {
        return getAllDatesForMetric(this.key);
    }

    @SuppressWarnings("rawtypes")
    public Metric build(String keySuffix, String nameSuffix) {
        return new Metric.Builder(key + keySuffix, name + nameSuffix, valueType)
                .setDescription(description + nameSuffix)
                .setDomain(domain)
                .setDirection(direction)
                .setQualitative(qualitative)
                .create();
    }
}
