// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.metrics.MetricKeyConverter.getAllDatesForMetric;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import com.google.common.collect.ObjectArrays;

import dev.roryclaasen.vcsparser.authors.Author;
import dev.roryclaasen.vcsparser.authors.AuthorData;
import dev.roryclaasen.vcsparser.authors.AuthorListConverter;
import dev.roryclaasen.vcsparser.authors.JsonAuthorParser;
import dev.roryclaasen.vcsparser.metrics.MetricDate;
import dev.roryclaasen.vcsparser.metrics.PluginMetric;

@ComputeEngineSide
public class ComputeNumAuthorsMetric implements MeasureComputer {
    private String authorsData = "vcsparser_authors_data";
    private String numChanges = "vcsparser_numchanges";

    private PluginMetric numAuthors = PluginMetric.NUM_AUTHORS;
    private PluginMetric numAuthors10Perc = PluginMetric.NUM_AUTHORS_10_PERC;

    protected static Map<String, Map<MetricDate, NavigableMap<String, Map<String, Boolean>>>> authorsCache = new HashMap<String, Map<MetricDate, NavigableMap<String, Map<String, Boolean>>>>();

    private double threshold = 10.0;

    private JsonAuthorParser jsonParser;
    private AuthorListConverter converter;

    public ComputeNumAuthorsMetric(JsonAuthorParser jsonParser, AuthorListConverter converter) {
        this.jsonParser = jsonParser;
        this.converter = converter;
    }

    public void removeProjectCache(String projectKey) {
        if (authorsCache.containsKey(projectKey))
            authorsCache.remove(projectKey);
    }

    protected void saveProjectCache(String projectKey, MetricDate date, String currentKey, Map<String, Boolean> authorMap) {
        if (!authorsCache.containsKey(projectKey))
            authorsCache.put(projectKey, new EnumMap<MetricDate, NavigableMap<String, Map<String, Boolean>>>(MetricDate.class));

        if (!authorsCache.get(projectKey).containsKey(date))
            authorsCache.get(projectKey).put(date, new TreeMap<String, Map<String, Boolean>>());

        if (!authorsCache.get(projectKey).get(date).containsKey(currentKey))
            authorsCache.get(projectKey).get(date).put(currentKey, new HashMap<String, Boolean>());

        Map<String, Boolean> dateCache = authorsCache.get(projectKey).get(date).get(currentKey);

        for (Entry<String, Boolean> entry : authorMap.entrySet()) {
            if (!dateCache.containsKey(entry.getKey()))
                dateCache.put(entry.getKey(), entry.getValue());
            else if (entry.getValue())
                dateCache.replace(entry.getKey(), true);
        }
    }

    @Override
    public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
        return defContext.newDefinitionBuilder()
                .setInputMetrics(ObjectArrays.concat(new String[] { authorsData }, getAllDatesForMetric(numChanges), String.class))
                .setOutputMetrics(ObjectArrays.concat(numAuthors.getKeyAllDates(), numAuthors10Perc.getKeyAllDates(), String.class))
                .build();
    }

    @Override
    public void compute(MeasureComputerContext context) {
        for (MetricDate date : MetricDate.values()) {
            String numAuthorsKey = numAuthors.getKey() + date.getSuffix();
            String numAuthors10PercKey = numAuthors10Perc.getKey() + date.getSuffix();
            String numChangesKey = numChanges + date.getSuffix();

            switch (context.getComponent().getType()) {
            case PROJECT:
            case MODULE:
            case DIRECTORY:
                computeChildMeasure(context, date, numAuthorsKey, numAuthors10PercKey);
                break;
            case FILE:
                computeFileMeasure(context, date, authorsData, numAuthorsKey, numAuthors10PercKey, numChangesKey);
                break;
            default:
                break;
            }
        }
    }

    protected void computeChildMeasure(MeasureComputerContext context, MetricDate date, String numAuthorsKey, String numAuthors10PercKey) {
        String currentKey = context.getComponent().getKey();
        String projectKey = currentKey.split(":", 2)[0];

        if (!authorsCache.containsKey(projectKey))
            return;

        if (!authorsCache.get(projectKey).containsKey(date))
            return;

        NavigableMap<String, Map<String, Boolean>> projectCache = authorsCache.get(projectKey).get(date);
        Map<String, Map<String, Boolean>> validMaps = projectCache.subMap(currentKey, currentKey + Character.MAX_VALUE);

        Map<String, Boolean> authorMap = new HashMap<String, Boolean>();
        for (Entry<String, Map<String, Boolean>> fileEntry : validMaps.entrySet()) {
            for (Entry<String, Boolean> aurthorEntry : fileEntry.getValue().entrySet()) {
                if (!authorMap.containsKey(aurthorEntry.getKey()))
                    authorMap.put(aurthorEntry.getKey(), aurthorEntry.getValue());
                else if (aurthorEntry.getValue())
                    authorMap.replace(aurthorEntry.getKey(), true);
            }
        }

        computeNumAuthors(context, numAuthorsKey, authorMap.values());
        computeNumAuthorsOver10Perc(context, numAuthors10PercKey, authorMap.values());
    }

    protected void computeFileMeasure(MeasureComputerContext context, MetricDate date, String authorsDataKey, String numAuthorsKey, String numAuthors10PercKey, String numChangesKey) {
        String currentKey = context.getComponent().getKey();
        String projectKey = currentKey.split(":", 2)[0];

        Measure authorsDataMeasure = context.getMeasure(authorsDataKey);
        Measure numChangesMeasure = context.getMeasure(numChangesKey);

        if (authorsDataMeasure == null)
            return;

        List<AuthorData> authorDataList = jsonParser.jsonStringArrayToAuthorDataList(authorsDataMeasure.getStringValue());
        if (authorDataList.isEmpty())
            return;

        int numChangesValue = (numChangesMeasure == null) ? 0 : numChangesMeasure.getIntValue();
        Map<String, Boolean> authorIsOver = getAuthorIsOverAfterDateDict(authorDataList, date.getDate(), numChangesValue);

        computeNumAuthors(context, numAuthorsKey, authorIsOver.values());
        computeNumAuthorsOver10Perc(context, numAuthors10PercKey, authorIsOver.values());

        saveProjectCache(projectKey, date, currentKey, authorIsOver);
    }

    protected Map<String, Boolean> getAuthorIsOverAfterDateDict(Collection<AuthorData> authorDataList, LocalDateTime dateFrom, int numChangesValue) {
        List<Author> authorList = converter.getAuthorListAfterDate(authorDataList, dateFrom);
        Map<String, Integer> authors = converter.getNumChangesPerAuthor(new HashMap<String, Integer>(), authorList);

        Map<String, Boolean> authorsOverMap = new HashMap<String, Boolean>();
        for (Entry<String, Integer> entry : authors.entrySet()) {
            if (numChangesValue == 0)
                authorsOverMap.put(entry.getKey(), false);
            else {
                double perc = (entry.getValue() * 100D) / numChangesValue;
                authorsOverMap.put(entry.getKey(), perc > threshold);
            }
        }
        return authorsOverMap;
    }

    protected void computeNumAuthors(MeasureComputerContext context, String numAuthorsKey, Collection<Boolean> authorIsOver) {
        if (!authorIsOver.isEmpty())
            context.addMeasure(numAuthorsKey, authorIsOver.size());
    }

    protected void computeNumAuthorsOver10Perc(MeasureComputerContext context, String numAuthors10PercKey, Collection<Boolean> authorIsOver) {
        if (authorIsOver.isEmpty())
            return;
        int count = 0;
        for (boolean isOver : authorIsOver) {
            if (isOver)
                count++;
        }
        if (count != 0)
            context.addMeasure(numAuthors10PercKey, count);
    }
}
