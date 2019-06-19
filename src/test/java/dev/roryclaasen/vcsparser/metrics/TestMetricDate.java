// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestMetricDate {
	@Test
	void givenMetricDate_whenClassInitialize_thenCreateListOfDates() {
		MetricDate[] dates = MetricDate.values();

		assertEquals(6, dates.length);
	}
	
	// TODO Write test to test date
}
