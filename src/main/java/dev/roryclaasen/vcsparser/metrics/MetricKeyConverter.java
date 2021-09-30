// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.metrics;

import java.util.ArrayList;
import java.util.List;

public final class MetricKeyConverter {
    private MetricKeyConverter() {}

    public static MetricDate getMetricDateFromKey(String key) {
        String suffix = key.substring(key.lastIndexOf('_'));
        for (MetricDate date : MetricDate.values()) {
            if (date.getSuffix().equals(suffix))
                return date;
        }
        return null;
    }

    public static String[] getAllDatesForMetric(String key) {
        List<String> keys = new ArrayList<String>();
        for (MetricDate date : MetricDate.values()) {
            keys.add(key + date.getSuffix());
        }
        return keys.toArray(new String[keys.size()]);
    }
}
