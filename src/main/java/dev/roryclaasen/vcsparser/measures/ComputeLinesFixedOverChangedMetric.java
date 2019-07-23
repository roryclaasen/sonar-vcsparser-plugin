// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.metrics.MetricKeyConverter.getAllDatesForMetric;

import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import com.google.common.collect.ObjectArrays;

import dev.roryclaasen.vcsparser.metrics.PluginMetric;

@ComputeEngineSide
public class ComputeLinesFixedOverChangedMetric implements MeasureComputer {
	private String[] linesChanged = getAllDatesForMetric("vcsparser_lineschanged");
	private String[] linesChangedFixed = getAllDatesForMetric("vcsparser_lineschanged_fixes");

	private String[] linesFixedOverChanged = PluginMetric.LINES_FIXED_OVER_CHANGED.getKeyAllDates();

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
		return defContext.newDefinitionBuilder()
				.setInputMetrics(ObjectArrays.concat(linesChanged, linesChangedFixed, String.class))
				.setOutputMetrics(linesFixedOverChanged)
				.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		for (int i = 0; i < linesChanged.length; i++) {
			compute(context, linesChanged[i], linesChangedFixed[i], linesFixedOverChanged[i]);
		}
	}

	protected void compute(MeasureComputerContext context, String linesChangedKey, String linesChangedFixedKey, String linesFixedOverChangedKey) {
		Measure linesChangedMeasure = context.getMeasure(linesChangedKey);
		Measure linesChangedFixedMeasure = context.getMeasure(linesChangedFixedKey);

		if (linesChangedMeasure == null)
			return;

		int linesChangedValue = linesChangedMeasure.getIntValue();
		int linesChangedFixedValue = (linesChangedFixedMeasure != null) ? linesChangedFixedMeasure.getIntValue() : 0;

		if (linesChangedValue == 0)
			return;

		double value = (linesChangedFixedValue * 100.0) / linesChangedValue;
		context.addMeasure(linesFixedOverChangedKey, value);
	}
}
