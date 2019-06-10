// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class PluginMetrics implements Metrics {
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Metric> getMetrics() {
		return Arrays.asList();
	}
}
