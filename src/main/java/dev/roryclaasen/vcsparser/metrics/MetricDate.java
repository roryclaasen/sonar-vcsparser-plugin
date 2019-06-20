// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public enum MetricDate {
	YEAR_1("_1y", " (1 year)", MinusDate(1, 0, 1)),
	MONTH_6("_6m", " (6 months)", MinusDate(0, 6, 1)),
	MONTH_3("_3m", " (3 months)", MinusDate(0, 3, 1)),
	DAY_30("_30d", " (30 days)", MinusDate(0, 0, 31)),
	DAY_7("_7d", " (7 days)", MinusDate(0, 0, 8)),
	DAY_1("_1d", " (1 days)", MinusDate(0, 0, 1));

	private String suffix;
	private String description;
	private Date date;

	private MetricDate(String suffix, String description, Date date) {
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

	public Date getDate() {
		return date;
	}

	private static Date MinusDate(int year, int month, int day) {
		Date today = new Date();
		today = DateUtils.addDays(today, -day);
		today = DateUtils.addMonths(today, -month);
		today = DateUtils.addYears(today, -year);
		return today;
	}
}
