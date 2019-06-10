// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures;

import static dev.roryclaasen.vcsparser.measures.PluginMetrics.GetAllDatesForMetric;
import static dev.roryclaasen.vcsparser.measures.PluginMetrics.MetricDetails;

import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import com.google.common.collect.ObjectArrays;

@ComputeEngineSide
public class LinesFixedOverChangedComputer implements MeasureComputer {

	private String[] linesChanged = GetAllDatesForMetric("vcsparser_lineschanged");
	private String[] linesChangedFixed = GetAllDatesForMetric("vcsparser_lineschanged_fixes");

	private String[] linesFixedOverChanged = MetricDetails.linesFixedOverChanged.getKeyAllDates();

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
			Measure linesChanged = context.getMeasure(this.linesChanged[i]);
			Measure linesChangedFixed = context.getMeasure(this.linesChangedFixed[i]);

			if (linesChanged == null)
				continue;

			int linesChangedValue = linesChanged.getIntValue();
			int linesChangedFixedValue = (linesChangedFixed != null) ? linesChangedFixed.getIntValue() : 0;

			if (linesChangedValue == 0)
				continue;

			double value = (linesChangedFixedValue * 100.0) / linesChangedValue;
			context.addMeasure(linesFixedOverChanged[i], value);
		}
	}
}
