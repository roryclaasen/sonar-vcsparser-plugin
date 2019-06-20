// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import dev.roryclaasen.vcsparser.metrics.PluginMetric;

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

	private final String projectKey = "SomeProjectKey";
	private final String fileKey = ":path/to.file";
	private final String componentKey = projectKey + fileKey;
	private final String authorsDataKey = "SomeAuthorsDataKey";
	private final String numChangesKey = "SomeNumChangesKey_1d";
	private final String numAuthorsKey = "SomeNumAuthorsKey_1d";
	private final String numAuthors10PercKey = "SomeNumAuthors10PercKey_1d";
	private final String jsonArray = "[{},{},{}]";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		when(defContext.newDefinitionBuilder()).thenReturn(defBuilder);

		when(defBuilder.setInputMetrics(any(String.class))).thenReturn(defBuilder);
		when(defBuilder.setOutputMetrics(any(String.class))).thenReturn(defBuilder);
		when(defBuilder.build()).thenReturn(defComputer);

		when(component.getKey()).thenReturn(componentKey);

		when(context.getComponent()).thenReturn(component);
		when(context.getMeasure(anyString())).thenReturn(measure);

		when(measure.getIntValue()).thenReturn(10);
		when(measure.getStringValue()).thenReturn(jsonArray);

		authorChangesMap = new HashMap<String, Integer>();
		authorChangesMap.put("Author 1", 9);
		authorChangesMap.put("Author 2", 1);

		when(converter.getAuthorListAfterDate(anyList(), any(Date.class))).thenReturn(new ArrayList<Author>());
		when(converter.getNumChangesPerAuthor(anyMap(), anyList())).thenReturn(authorChangesMap);

		when(measure.getIntValue()).thenReturn(10);

		computer = new ComputeNumAuthorsMetric(jsonParser, converter);
		ComputeNumAuthorsMetric.authorsCache.clear();
	}

	@Test
	void givenComputeNumAuthorsMetric_whenRemoveProjectCacheAndKeyMissing_thenDoNotRemove() {
		ComputeNumAuthorsMetric.authorsCache.put("SomeOtherProjectKey", new HashMap<String, List<AuthorData>>());

		computer.removeProjectCache(componentKey);

		assertEquals(1, ComputeNumAuthorsMetric.authorsCache.size());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenRemoveProjectCacheAndKeyMatched_thenRemove() {
		ComputeNumAuthorsMetric.authorsCache.put(componentKey, new HashMap<String, List<AuthorData>>());

		computer.removeProjectCache(componentKey);

		assertEquals(0, ComputeNumAuthorsMetric.authorsCache.size());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenDefine_thenReturnMeasureComputerDefinition() {
		MeasureComputerDefinition defineComputer = computer.define(defContext);

		verify(defBuilder, times(1)).build();
		assertEquals(defComputer, defineComputer);
	}

	@Test
	void givenComputeNumAuthorsMetric_whenGetAuthorNumChangesAfterDateDict_thenReturnMap() {
		List<AuthorData> authorDataList = new ArrayList<AuthorData>();
		List<Author> authorList = new ArrayList<Author>();
		Map<String, Integer> authorChangesMap = new HashMap<String, Integer>();
		Date dateFrom = new Date();

		when(converter.getAuthorListAfterDate(anyList(), any(Date.class))).thenReturn(authorList);
		when(converter.getNumChangesPerAuthor(anyMap(), anyList())).thenReturn(authorChangesMap);

		Map<String, Integer> returnedMap = computer.getAuthorNumChangesAfterDateDict(authorDataList, dateFrom, authorChangesMap);

		assertEquals(authorChangesMap, returnedMap);
		verify(converter, times(1)).getAuthorListAfterDate(eq(authorDataList), eq(dateFrom));
		verify(converter, times(1)).getNumChangesPerAuthor(eq(authorChangesMap), eq(authorList));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsAndEmptyCollection_thenDontAdd() {
		List<Integer> authorNumChanges = new ArrayList<Integer>();

		computer.computeNumAuthors(context, numAuthorsKey, authorNumChanges);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputNumAuthorsAndCollectionHasItems_thenAddMeasure() {
		List<Integer> authorNumChanges = new ArrayList<Integer>();
		authorNumChanges.add(5);
		authorNumChanges.add(7);

		computer.computeNumAuthors(context, numAuthorsKey, authorNumChanges);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(2));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeNumAuthorsOver10PercAndMeasureNull_thenReturn() {
		when(context.getMeasure(numChangesKey)).thenReturn(null);

		computer.computeNumAuthorsOver10Perc(context, numChangesKey, numAuthors10PercKey, authorChangesMap.values());

		verify(measure, times(0)).getIntValue();
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeNumAuthorsOver10PercAndMeasureValueZero_thenReturn() {
		when(measure.getIntValue()).thenReturn(0);

		computer.computeNumAuthorsOver10Perc(context, numChangesKey, numAuthors10PercKey, authorChangesMap.values());

		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeNumAuthorsOver10PercAndListEmpty_thenDontAdd() {
		List<Integer> authorNumChanges = new ArrayList<Integer>();

		when(measure.getIntValue()).thenReturn(10);

		computer.computeNumAuthorsOver10Perc(context, numChangesKey, numAuthors10PercKey, authorNumChanges);

		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeNumAuthorsOver10PercAndHasItemsthenAddCountOverThreshold() {
		computer.computeNumAuthorsOver10Perc(context, numChangesKey, numAuthors10PercKey, authorChangesMap.values());

		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndAuthorsDataNull_thenReturn() {
		when(context.getMeasure(authorsDataKey)).thenReturn(null);

		computer.computeFileMeasure(context, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(measure, times(0)).getStringValue();
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndAuthorsDataListEmpty_thenReturn() {
		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(new ArrayList<AuthorData>());

		computer.computeFileMeasure(context, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		assertEquals(0, ComputeNumAuthorsMetric.authorsCache.size());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndCacheMissingKey_thenAddToCache() {
		List<AuthorData> authorDataList = Arrays.asList(new AuthorData(new Date(), new ArrayList<Author>()));
		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(authorDataList);

		computer.computeFileMeasure(context, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		assertEquals(1, ComputeNumAuthorsMetric.authorsCache.size());
		assertArrayEquals(authorDataList.toArray(), ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(componentKey).toArray());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndCacheHasKey_thenAddToCache() {
		List<AuthorData> authorDataList = Arrays.asList(new AuthorData(new Date(), new ArrayList<Author>()));
		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(authorDataList);
		ComputeNumAuthorsMetric.authorsCache.put(projectKey, new HashMap<String, List<AuthorData>>());

		computer.computeFileMeasure(context, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		assertEquals(1, ComputeNumAuthorsMetric.authorsCache.size());
		assertArrayEquals(authorDataList.toArray(), ComputeNumAuthorsMetric.authorsCache.get(projectKey).get(componentKey).toArray());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeFileMeasureAndAuthorsDatahasItems_thenAddMeasure() {
		List<AuthorData> authorDataList = Arrays.asList(new AuthorData(new Date(), new ArrayList<Author>()));
		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(authorDataList);

		computer.computeFileMeasure(context, authorsDataKey, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(2));
		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndMissingProjectInCache_thenReturn() {
		computer.computeChildMeasure(context, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndEntryDoesntStartWithCurrentKey_thenSkip() {
		Map<String, List<AuthorData>> fileMap = new HashMap<String, List<AuthorData>>();
		fileMap.put("SomeOtherProjectKey" + fileKey, new ArrayList<AuthorData>());

		ComputeNumAuthorsMetric.authorsCache.put(projectKey, fileMap);

		computer.computeChildMeasure(context, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(context, times(0)).addMeasure(eq(numAuthorsKey), anyInt());
		verify(context, times(0)).addMeasure(eq(numAuthors10PercKey), anyInt());
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeChildMeasureAndEntryStartsWithCurrentKey_thenAddMeasure() {
		Map<String, List<AuthorData>> fileMap = new HashMap<String, List<AuthorData>>();
		fileMap.put(componentKey, new ArrayList<AuthorData>());

		ComputeNumAuthorsMetric.authorsCache.put(projectKey, fileMap);

		computer.computeChildMeasure(context, numAuthorsKey, numAuthors10PercKey, numChangesKey);

		verify(converter, times(1)).getAuthorListAfterDate(eq(fileMap.get(componentKey)), any(Date.class));

		verify(context, times(1)).addMeasure(eq(numAuthorsKey), eq(2));
		verify(context, times(1)).addMeasure(eq(numAuthors10PercKey), eq(1));
	}

	@Test
	void givenComputeNumAuthorsMetric_whenComputeAndTypeFile_thenComputeFileMeasure() {
		when(component.getType()).thenReturn(Type.FILE);

		List<AuthorData> authorDataList = Arrays.asList(new AuthorData(new Date(), new ArrayList<Author>()));
		when(jsonParser.jsonStringArrayToAuthorDataList(jsonArray)).thenReturn(authorDataList);

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
		when(component.getType()).thenReturn(type);

		Map<String, List<AuthorData>> fileMap = new HashMap<String, List<AuthorData>>();
		fileMap.put(componentKey, new ArrayList<AuthorData>());

		ComputeNumAuthorsMetric.authorsCache.put(projectKey, fileMap);

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
