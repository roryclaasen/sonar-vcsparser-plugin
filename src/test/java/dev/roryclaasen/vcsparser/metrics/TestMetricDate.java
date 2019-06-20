// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class TestMetricDate {
	@Test
	void givenMetricDate_whenClassInitialize_thenCreateListOfDates() {
		MetricDate[] dates = MetricDate.values();

		assertEquals(6, dates.length);
	}

	@ParameterizedTest
	@EnumSource(MetricDate.class)
	void givenMetricDate_whenDate_thenDateMustBeInPast(MetricDate metricDate) {
		Date dateNow = new Date();

		Date date = metricDate.getDate();

		assertTrue(date.before(dateNow));
	}

	@ParameterizedTest
	@EnumSource(MetricDate.class)
	void givenMetricDate_whenDate_thenDateHasNoTime(MetricDate metricDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(metricDate.getDate());

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);

		assertNotEquals(0, year);
		assertNotEquals(0, month);
		assertNotEquals(0, day);
		assertEquals(0, hours);
		assertEquals(0, minutes);
		assertEquals(0, seconds);
	}
}
