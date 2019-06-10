// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.measures.Metric;

public class TestPluginMetrics {
	private PluginMetrics pluginMetrics;

	@BeforeEach
	void setUp() {
		pluginMetrics = new PluginMetrics();
	}

	@Test
	void givenBugMetrics_whenGetMetrics_thenReturnListOfMetrics() {
		@SuppressWarnings("rawtypes") List<Metric> metrics = pluginMetrics.getMetrics();

		assertEquals(6, metrics.size());
	}
}
