// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.measures.PluginMetrics.getAllDatesForMetric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition.Builder;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinitionContext;

public class TestComputeLinesFixedOverChangedMetric {
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

	private ComputeLinesFixedOverChangedMetric computer;

	private final String linesChangedKey = "SomeLinesChangedKey";
	private final String linesChangedFixedKey = "SomeLinesFixedKey";
	private final String linesFixedOverChangedKey = "SomeLinesFixedOverChangedKey";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		when(defContext.newDefinitionBuilder()).thenReturn(defBuilder);

		when(defBuilder.setInputMetrics(any(String.class))).thenReturn(defBuilder);
		when(defBuilder.setOutputMetrics(any(String.class))).thenReturn(defBuilder);
		when(defBuilder.build()).thenReturn(defComputer);

		when(context.getMeasure(anyString())).thenReturn(measure);

		when(measure.getIntValue()).thenReturn(0);

		computer = new ComputeLinesFixedOverChangedMetric();
	}

	@Test
	void givenLinesFixedOverChangedComputer_whenDefine_thenReturnMeasureComputerDefinition() {
		MeasureComputerDefinition defineComputer = computer.define(defContext);

		verify(defBuilder, times(1)).build();
		assertEquals(defComputer, defineComputer);
	}

	@Test
	void givenLinesFixedOverChangedComputer_whenComputeLinesChangedNull_thenDoNotAddMeasure() {
		when(context.getMeasure(linesChangedKey)).thenReturn(null);

		computer.compute(context, linesChangedKey, linesChangedFixedKey, linesFixedOverChangedKey);

		verify(measure, times(0)).getIntValue();
		verify(context, times(0)).addMeasure(eq(linesChangedFixedKey), anyInt());
	}

	@Test
	void givenLinesFixedOverChangedComputer_whenComputeLinesChangedZero_thenDoNotAddMeasure() {
		computer.compute(context, linesChangedKey, linesChangedFixedKey, linesFixedOverChangedKey);

		verify(context, times(0)).addMeasure(eq(linesChangedFixedKey), anyInt());
	}

	@Test
	void givenLinesFixedOverChangedComputer_whenLinesChangedFixedNull_thenLinesChangedFixedZero() {
		when(context.getMeasure(linesChangedFixedKey)).thenReturn(null);
		when(measure.getIntValue()).thenReturn(2);

		computer.compute(context, linesChangedKey, linesChangedFixedKey, linesFixedOverChangedKey);

		verify(context, times(1)).addMeasure(eq(linesFixedOverChangedKey), eq(0.0));
	}

	@Test
	void givenLinesFixedOverChangedComputer_whenCompute_thenAddMeasure() {
		when(measure.getIntValue()).thenReturn(2, 4);

		computer.compute(context, linesChangedKey, linesChangedFixedKey, linesFixedOverChangedKey);

		verify(context, times(1)).addMeasure(eq(linesFixedOverChangedKey), eq((4 * 100) / 2.0));
	}

	@Test
	void givenLinesFixedOverChangedComputer_whenCompute_LoopThroughDates() {
		String[] linesChanged = getAllDatesForMetric("vcsparser_lineschanged");

		computer.compute(context);

		for (String key : linesChanged) {
			verify(context, times(1)).getMeasure(key);
		}
	}
}
