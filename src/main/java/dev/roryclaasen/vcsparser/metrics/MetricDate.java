// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum MetricDate {
    YEAR_1("_1y", " (1 year)", minusDate(1, 0, 1)),
    MONTH_6("_6m", " (6 months)", minusDate(0, 6, 1)),
    MONTH_3("_3m", " (3 months)", minusDate(0, 3, 1)),
    DAY_30("_30d", " (30 days)", minusDate(0, 0, 31)),
    DAY_7("_7d", " (7 days)", minusDate(0, 0, 8)),
    DAY_1("_1d", " (1 days)", minusDate(0, 0, 1));

    private String suffix;
    private String description;
    private LocalDateTime date;

    private MetricDate(String suffix, String description, LocalDateTime date) {
        this.suffix = suffix;
        this.description = description;
        this.date = date;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    private static LocalDateTime minusDate(int year, int month, int day) {
        LocalDateTime date = LocalDate.now().atStartOfDay();
        date = date.minusDays(day);
        date = date.minusMonths(month);
        date = date.minusYears(year);
        return date;
    }
}
