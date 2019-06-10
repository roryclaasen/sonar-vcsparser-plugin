// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.measures.Metric;

import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

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
		MockitoAnnotations.initMocks(this);

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
	void givenPluginMetrics_whenClassInitialize_thenCreateListOfDates() {
		PluginMetrics.Metrics.remove(someMetricKey);

		List<Pair<String, String>> dates = PluginMetrics.DatePairs;

		assertEquals(6, dates.size());
	}

	@Test
	void givenPluginMetrics_whenClassInitialize_thenCreateAndPutMetrics() {
		PluginMetrics.Metrics.remove(someMetricKey);

		int dateSize = PluginMetrics.DatePairs.size();
		int measureslength = PluginMetrics.MetricDetails.values().length;

		assertEquals(dateSize * measureslength, PluginMetrics.Metrics.size());
	}

	@Test
	void givenPluginMetrics_whenGetMetrics_thenReturnListOfMetrics() {
		PluginMetrics.Metrics.remove(someMetricKey);

		@SuppressWarnings("rawtypes") List<Metric> metrics = pluginMetrics.getMetrics();

		assertEquals(6, metrics.size());
		// assertEquals(PluginMetrics.Metrics.values(), metrics);
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

		PluginMetrics.loadAndAlter(environment, fileReader);
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
	
	@Test
	void givenPluginMetrics_whenGetAllDatesForMetric_thenReturnListOfKeys() {
		String someKey = "someKey";
		
		String[] keys = PluginMetrics.GetAllDatesForMetric(someKey);
		
		assertEquals(PluginMetrics.DatePairs.size(), keys.length);
	}
}
