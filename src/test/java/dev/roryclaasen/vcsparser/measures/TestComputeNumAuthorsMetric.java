// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Component.Type;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition.Builder;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinitionContext;

import dev.roryclaasen.vcsparser.authors.Author;
import dev.roryclaasen.vcsparser.authors.AuthorData;
import dev.roryclaasen.vcsparser.authors.AuthorListConverter;
import dev.roryclaasen.vcsparser.authors.JsonAuthorParser;
import dev.roryclaasen.vcsparser.metrics.MetricDate;
import dev.roryclaasen.vcsparser.metrics.PluginMetric;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class TestComputeNumAuthorsMetric {
	@Mock
	private Builder defBuilder;

	@Mock
	private MeasureComputerDefinitionContext defContext;

	@Mock
	private MeasureComputerDefinition defComputer;

	@Mock
	private MeasureComputerContext context;

	@Mock
	private Measure measure;

	@Mock
	private Component component;

	@Mock
	private JsonAuthorParser jsonParser;

	@Mock
	private AuthorListConverter converter;

	private ComputeNumAuthorsMetric computer;

	private Map<String, Integer> authorChangesMap;
	private Map<String, Boolean> authorOverMap;

	private final String projectKey = "SomeProjectKey";
	private final String fileKey = ":path/to.file";
	private final String componentKey = projectKey + fileKey;
	private final MetricDate metricDate = MetricDate.DAY_1;
	private final String authorsDataKey = "SomeAuthorsDataKey";
	private final String numChangesKey = "SomeNumChangesKey_1d";
	private final String numAuthorsKey = "SomeNumAuthorsKey_1d";
	private final String numAuthors10PercKey = "SomeNumAuthors10PercKey_1d";
	private final int numChanges = 10;
	private final String jsonArray = "[{},{},{}]";

	@BeforeEach
	void setUp() {
		when(defContext.newDefinitionBuilder()).thenReturn(defBuilder);

		when(defBuilder.setInputMetrics(any(String.class))).thenReturn(defBuilder);
		when(defBuilder.setOutputMetrics(any(String.class))).thenReturn(defBuilder);
		when(defBuilder.build()).thenReturn(defComputer);

		when(component.getKey()).thenReturn(componentKey);

		when(context.getComponent()).thenReturn(component);
		when(context.getMeasure(anyString())).thenReturn(measure);

		when(measure.getIntValue()).thenReturn(numChanges);
		when(measure.getStringValue()).thenReturn(jsonArray);

		authorChangesMap = new HashMap<String, Integer>();
		authorChangesMap.put("Author 1", 9);
		authorChangesMap.put("Author 2", 1);

		authorOverMap = new HashMap<String, Boolean>();
		authorOverMap.put("Author 1", true);
		authorOverMap.put("Author 2", false);

		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(Arrays.asList(new AuthorData(LocalDate.now().atStartOfDay(), new ArrayList<Author>())));

		when(converter.getAuthorListAfterDate(anyList(), any(LocalDateTime.class))).thenReturn(new ArrayList<Author>());
		when(converter.getNumChangesPerAuthor(anyMap(), anyList())).thenReturn(authorChangesMap);

		computer = new ComputeNumAuthorsMetric(jsonParser, converter);
		ComputeNumAuthorsMetric.authorsCache.clear();
	}

	private void addAuthorToCache(String project, MetricDate date, String file, String name, boolean value) {
		ComputeNumAuthorsMetric.authorsCache.putIfAbsent(project, new EnumMap<MetricDate, NavigableMap<String, Map<String, Boolean>>>(MetricDate.class));
		ComputeNumAuthorsMetric.authorsCache.get(project).putIfAbsent(date, new TreeMap<String, Map<String, Boolean>>());
		ComputeNumAuthorsMetric.authorsCache.get(project).get(date).putIfAbsent(file, new HashMap<String, Boolean>());
		ComputeNumAuthorsMetric.authorsCache.get(project).get(date).get(file).putIfAbsent(name, value);
	}

	private void addAuthorToCache(String name, boolean value) {
		addAuthorToCache(projectKey, metricDate, componentKey, name, value);
	}

	@Test
	void givenComputeNumAuthorsMetric_whenRemoveProjectCacheAndKeyMissing_thenDoNotRemove() {
		ComputeNumAuthorsMetric.authorsCache.put("SomeOtherProjectKey", new EnumMap<MetricDate, NavigableMap<String, Map<String, Boolean>>>(MetricDate.class));

		computer.removeProjectCache(projectKey);

		assertEquals(1, ComputeNumAuthorsMetric.authorsCache.size());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenRemoveProjectCacheAndKeyMatched_thenRemove() {
		ComputeNumAuthorsMetric.authorsCache.put(projectKey, new EnumMap<MetricDate, NavigableMap<String, Map<String, Boolean>>>(MetricDate.class));

		computer.removeProjectCache(projectKey);

		assertEquals(0, ComputeNumAuthorsMetric.authorsCache.size());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenSaveProjectCacheAndNoExistingProject_thenPutMapsAndAddAuthorMap() {
		computer.saveProjectCache(projectKey, metricDate, componentKey, authorOverMap);

		assertTrue(ComputeNumAuthorsMetric.authorsCache.containsKey(projectKey));
		assertTrue(ComputeNumAuthorsMetric.authorsCache.get(projectKey).containsKey(metricDate));
		assertTrue(ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(metricDate).containsKey(componentKey));
		assertEquals(2, ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(metricDate).get(componentKey).size());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenSaveProjectCacheAndExistingAuthorFalseAndInputTrue_thenSetTrue() {
		addAuthorToCache("Author 1", false);

		Map<String, Boolean> authorMap = new HashMap<String, Boolean>();
		authorMap.put("Author 1", true);

		computer.saveProjectCache(projectKey, metricDate, componentKey, authorMap);

		assertTrue(ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(metricDate).get(componentKey).get("Author 1"));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenSaveProjectCacheAndExistingAuthorTrueandInputFalse_thenDoNotSet() {
		addAuthorToCache("Author 1", true);

		Map<String, Boolean> authorMap = new HashMap<String, Boolean>();
		authorMap.put("Author 1", false);

		computer.saveProjectCache(projectKey, metricDate, componentKey, authorMap);

		assertTrue(ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(metricDate).get(componentKey).get("Author 1"));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenSaveProjectCacheAndExistingAuthorTrueAndInputTrue_thenSetTrue() {
		addAuthorToCache("Author 1", true);

		Map<String, Boolean> authorMap = new HashMap<String, Boolean>();
		authorMap.put("Author 1", true);

		computer.saveProjectCache(projectKey, metricDate, componentKey, authorMap);

		assertTrue(ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(metricDate).get(componentKey).get("Author 1"));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenSaveProjectCacheAndExistingAuthorFalseAndInputFalse_thenDoNotSet() {
		addAuthorToCache("Author 1", false);

		Map<String, Boolean> authorMap = new HashMap<String, Boolean>();
		authorMap.put("Author 1", false);

		computer.saveProjectCache(projectKey, metricDate, componentKey, authorMap);

		assertFalse(ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(metricDate).get(componentKey).get("Author 1"));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenDefine_thenReturnMeasureComputerDefinition() {
		MeasureComputerDefinition defineComputer = computer.define(defContext);

		verify(defBuilder, times(1)).build();
		assertEquals(defComputer, defineComputer);
	}

	@Test
	void givenComputeNumAuthorsMetric_whenGetAuthorIsOverAfterDateDict_thenReturnMap() {
		List<AuthorData> authorDataList = new ArrayList<AuthorData>();
		List<Author> authorList = new ArrayList<Author>();
		LocalDateTime dateFrom = LocalDate.now().atStartOfDay();

		when(converter.getAuthorListAfterDate(anyList(), any(LocalDateTime.class))).thenReturn(authorList);
		when(converter.getNumChangesPerAuthor(anyMap(), anyList())).thenReturn(authorChangesMap);

		Map<String, Boolean> returnedMap = computer.getAuthorIsOverAfterDateDict(authorDataList, dateFrom, numChanges);

		verify(converter, times(1)).getAuthorListAfterDate(eq(authorDataList), eq(dateFrom));
		verify(converter, times(1)).getNumChangesPerAuthor(anyMap(), eq(authorList));
		assertEquals(authorOverMap, returnedMap);
	}

	@Test
	void givenComputeNumAuthorsMetric_whenGetAuthorIsOverAfterDateDictAndNumChangesZero_thenReturnMapEntriesFalse() {
		List<AuthorData> authorDataList = new ArrayList<AuthorData>();
		List<Author> authorList = new ArrayList<Author>();
		LocalDateTime dateFrom = LocalDate.now().atStartOfDay();

		when(converter.getAuthorListAfterDate(anyList(), any(LocalDateTime.class))).thenReturn(authorList);
		when(converter.getNumChangesPerAuthor(anyMap(), anyList())).thenReturn(authorChangesMap);

		Map<String, Boolean> returnedMap = computer.getAuthorIsOverAfterDateDict(authorDataList, dateFrom, 0);

		for (Entry<String, Boolean> entry : returnedMap.entrySet()) {
			assertFalse(entry.getValue());
		}
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsAndEmptyCollection_thenDontAdd() {
		List<Boolean> authorIsOver = new ArrayList<Boolean>();

		computer.computeNumAuthors(context, numAuthorsKey, authorIsOver);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsAndCollectionHasItems_thenAddMeasure() {
		List<Boolean> authorIsOver = new ArrayList<Boolean>();
		authorIsOver.add(false);
		authorIsOver.add(true);

		computer.computeNumAuthors(context, numAuthorsKey, authorIsOver);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(2));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsOver10PercAndEmptyCollection_thenDontAdd() {
		List<Boolean> authorIsOver = new ArrayList<Boolean>();

		computer.computeNumAuthorsOver10Perc(context, numAuthors10PercKey, authorIsOver);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsOver10PercAndCountZero_thenDontAdd() {
		List<Boolean> authorIsOver = new ArrayList<Boolean>();
		authorIsOver.add(false);

		computer.computeNumAuthorsOver10Perc(context, numAuthors10PercKey, authorIsOver);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsOver10PercAndCountGTZero_thenAddMeasure() {
		List<Boolean> authorIsOver = new ArrayList<Boolean>();
		authorIsOver.add(true);

		computer.computeNumAuthorsOver10Perc(context, numAuthors10PercKey, authorIsOver);

		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndAuthorsDataNull_thenReturn() {
		when(context.getMeasure(authorsDataKey)).thenReturn(null);

		computer.computeFileMeasure(context, metricDate, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(measure, times(0)).getStringValue();
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndAuthorsDataEmpty_thenReturn() {
		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(Arrays.asList());

		computer.computeFileMeasure(context, metricDate, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		assertEquals(0, ComputeNumAuthorsMetric.authorsCache.size());
		verify(measure, times(0)).getIntValue();
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndNumChangesMeasureNull_thenUseZero() {
		when(context.getMeasure(numChangesKey)).thenReturn(null);

		computer.computeFileMeasure(context, metricDate, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(measure, times(0)).getIntValue();
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndNumChangesMeasureNotNull_thenUseMeasure() {
		computer.computeFileMeasure(context, metricDate, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(measure, times(1)).getIntValue();
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndMissingProjectInCache_thenReturn() {
		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndMissingDateProjectInCache_thenReturn() {
		ComputeNumAuthorsMetric.authorsCache.putIfAbsent(projectKey, new EnumMap<MetricDate, NavigableMap<String, Map<String, Boolean>>>(MetricDate.class));


		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndIncorrectSubMapFilter_thenDoNotAddMeasure() {
		addAuthorToCache(projectKey, metricDate, "A Bad Component Key", "Author 1", true);

		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasure_thenAddMeasures() {
		addAuthorToCache("Author 1", true);

		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(1));
		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndAuthorFalse_thenAddNumAuthorsKeyMeasure() {
		addAuthorToCache("Author 1", false);

		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(1));
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndSameAuthorFalse_thenAddNumAuthorsKeyMeasure() {
		addAuthorToCache("Author 1", false);
		addAuthorToCache(projectKey, metricDate, componentKey + ".ext", "Author 1", false);

		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(1));
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndSameAuthorFalseThenTrue_thenMeasures() {
		addAuthorToCache("Author 1", false);
		addAuthorToCache(projectKey, metricDate, componentKey + ".ext", "Author 1", true);

		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(1));
		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndSameAuthorTrueThenFalse_thenMeasures() {
		addAuthorToCache("Author 1", true);
		addAuthorToCache(projectKey, metricDate, componentKey + ".ext", "Author 1", false);

		computer.computeChildMeasure(context, metricDate, numAuthorsKey, numAuthors10PercKey);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(1));
		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeAndTypeFile_thenComputeFileMeasure() {
		when(component.getType()).thenReturn(Type.FILE);

		computer.compute(context);

		for (String key : PluginMetric.NUM_AUTHORS.getKeyAllDates()) {
			verify(context, times(1)).addMeasure(eq(key), eq(2));
		}
		for (String key : PluginMetric.NUM_AUTHORS_10_PERC.getKeyAllDates()) {
			verify(context, times(1)).addMeasure(eq(key), eq(1));
		}
	}

	@ParameterizedTest
	@EnumSource(value = Type.class, names = { "PROJECT", "MODULE", "DIRECTORY" })
	void givenComputeNumAuthorsMetric_whenComputeAndTypeSupportsChild_thenComputeChildMeasure(Type type) {
		for (MetricDate date : MetricDate.values()) {
			addAuthorToCache(projectKey, date, componentKey, "Author 1", true);
			addAuthorToCache(projectKey, date, componentKey, "Author 2", false);
		}

		when(component.getType()).thenReturn(type);

		computer.compute(context);

		for (String key : PluginMetric.NUM_AUTHORS.getKeyAllDates()) {
			verify(context, times(1)).addMeasure(eq(key), eq(2));
		}
		for (String key : PluginMetric.NUM_AUTHORS_10_PERC.getKeyAllDates()) {
			verify(context, times(1)).addMeasure(eq(key), eq(1));
		}
	}

	@ParameterizedTest
	@EnumSource(value = Type.class, names = { "VIEW", "SUBVIEW" })
	void givenComputeNumAuthorsMetric_whenComputeAndTypeInValid_thenDoNotCompute(Type type) {
		when(component.getType()).thenReturn(type);

		computer.compute(context);

		verify(context, times(0)).addMeasure(anyString(), anyInt());
	}
}
