// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TestMetricKeyConverter {
	private final String someKeyWithoutDate = "Some_Metric_Key";
	
	@Test
	void givenMetricUtils_whenGetMetricDateFromKeyAndKeyContainsDate_thenReturnMetricDate() {
		MetricDate date = MetricDate.DAY_1;
		String key = someKeyWithoutDate + date.getSuffix();
		
		MetricDate keyDate = MetricKeyConverter.getMetricDateFromKey(key);
		
		assertEquals(date, keyDate);
	}
	
	@Test
	void givenMetricUtils_whenGetMetricDateFromKeyAndKeyMissingDate_thenReturnNull() {		
		MetricDate keyDate = MetricKeyConverter.getMetricDateFromKey(someKeyWithoutDate);
		
		assertNull(keyDate);
	}
	
	@Test
	void givenMetricUtils_whenGetMetricDateFromKeyAndKeyMissingDateAndUnderscores_thenThrow() {
		String key = "SomeMetricKeyWithoutUnderscores";
		
		assertThrows(IndexOutOfBoundsException.class, () -> MetricKeyConverter.getMetricDateFromKey(key));
	}
	
	@Test
	void givenMetricUtils_whenAllDateForMetirc_thenReturnListString() {		
		String[] keys = MetricKeyConverter.getAllDatesForMetric(someKeyWithoutDate);
		
		assertEquals(MetricDate.values().length, keys.length);
		for (String key : keys) {
			assertTrue(key.matches("^.+_\\d+\\w$"));
		}
	}
}
