// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        LocalDateTime dateNow = LocalDate.now().atStartOfDay();

        LocalDateTime date = metricDate.getDate();

        assertTrue(date.isBefore(dateNow));
    }
}
