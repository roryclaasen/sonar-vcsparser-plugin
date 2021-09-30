// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.sonar.api.measures.Metric;

public class TestPluginMetric {
    private final String keySuffix = "SomeKeySuffix";
    private final String nameSuffix = "SomeNameSuffix";

    @SuppressWarnings("rawtypes")
    @ParameterizedTest
    @EnumSource(PluginMetric.class)
    void givenPluginMetric_whenBuild_thenCreateMetricCorrectly(PluginMetric metric) {
        Metric builtMetric = metric.build(keySuffix, nameSuffix);

        assertEquals(metric.getKey() + keySuffix, builtMetric.getKey());
        assertEquals(metric.getName() + nameSuffix, builtMetric.getName());
        assertEquals(metric.getDescription() + nameSuffix, builtMetric.getDescription());
        assertEquals(metric.getDomain(), builtMetric.getDomain());
        assertEquals(metric.getValueType(), builtMetric.getType());
        assertEquals(metric.getDirection(), builtMetric.getDirection());
        assertEquals(metric.isQualitative(), builtMetric.getQualitative());
    }
}
