// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.sonar.api.measures.Metric;

import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class TestPluginMetrics {
    @Mock
    private IEnvironment environment;

    @Mock
    private IFileReader fileReader;

    private PluginMetrics pluginMetrics;

    private final String SomeFilePath = "some/file/path.json";

    private final String someMetricKey = "some_metric_key";
    private final String someMetricName = "Some Name";
    private final String someMetricDescription = "Some Description";
    private final String someMetricDomain = "Some Domain";

    @BeforeEach
    void setUp() {
        when(environment.getEnvironmentVariable(anyString())).thenReturn(SomeFilePath);

        pluginMetrics = new PluginMetrics();

        PluginMetrics.Metrics.put(someMetricKey, new Metric.Builder(someMetricKey, someMetricName, Metric.ValueType.INT)
                .setDescription(someMetricDescription)
                .setDomain(someMetricDomain)
                .create());
    }

    @AfterEach
    void cleanUp() {
        PluginMetrics.Metrics.remove(someMetricKey);
    }

    @Test
    void givenPluginMetrics_whenClassInitialize_thenCreateAndPutMetrics() {
        PluginMetrics.Metrics.remove(someMetricKey);

        int dateSize = MetricDate.values().length;
        int measureslength = PluginMetric.values().length;

        assertEquals(dateSize * measureslength, PluginMetrics.Metrics.size());
    }

    @Test
    void givenPluginMetrics_whenGetMetrics_thenReturnListOfMetrics() {
        PluginMetrics.Metrics.remove(someMetricKey);

        @SuppressWarnings("rawtypes") List<Metric> metrics = pluginMetrics.getMetrics();

        assertEquals(18, metrics.size());
        assertIterableEquals(PluginMetrics.Metrics.values(), metrics);
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndGetEnvironmentVariableNull_thenDoNotThrow() {
        when(environment.getEnvironmentVariable(anyString())).thenReturn(null);

        PluginMetrics.loadAndAlter(environment, fileReader);

        verify(fileReader, times(0)).readFile(anyString());
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndGetEnvironmentVariableEmpty_thenDoNotThrow() {
        when(environment.getEnvironmentVariable(anyString())).thenReturn("");

        PluginMetrics.loadAndAlter(environment, fileReader);

        verify(fileReader, times(0)).readFile(anyString());
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndReadFileNull_thenDoNotThrow() {
        PluginMetrics.loadAndAlter(environment, fileReader);
        when(fileReader.readFile(SomeFilePath)).thenReturn(null);

        verify(fileReader, times(1)).readFile(SomeFilePath);
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndNoMetricWithKey_thenDoNotThrow() {
        when(fileReader.readFile(SomeFilePath)).thenReturn("{\"metrics\":[{\"key\":\"metric_key_not_in_map\"}]}");

        try {
            PluginMetrics.loadAndAlter(environment, fileReader);
            assertTrue(Boolean.TRUE);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndHasName_thenChangeMetric() {
        String name = "A new Name";
        when(fileReader.readFile(SomeFilePath)).thenReturn("{\"metrics\":[{\"key\":\"" + someMetricKey + "\",\"name\":\"" + name + "\"}]}");

        PluginMetrics.loadAndAlter(environment, fileReader);

        assertEquals(PluginMetrics.Metrics.get(someMetricKey).getName(), name);
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndHasDescription_thenChangeMetric() {
        String description = "A new Description";
        when(fileReader.readFile(SomeFilePath)).thenReturn("{\"metrics\":[{\"key\":\"" + someMetricKey + "\",\"description\":\"" + description + "\"}]}");

        PluginMetrics.loadAndAlter(environment, fileReader);

        assertEquals(PluginMetrics.Metrics.get(someMetricKey).getDescription(), description);
    }

    @Test
    void givenPluginMetrics_whenLoadAndAlterAndHasDomain_thenChangeMetric() {
        String domain = "A new Domain";
        when(fileReader.readFile(SomeFilePath)).thenReturn("{\"metrics\":[{\"key\":\"" + someMetricKey + "\",\"domain\":\"" + domain + "\"}]}");

        PluginMetrics.loadAndAlter(environment, fileReader);

        assertEquals(PluginMetrics.Metrics.get(someMetricKey).getDomain(), domain);
    }
}
