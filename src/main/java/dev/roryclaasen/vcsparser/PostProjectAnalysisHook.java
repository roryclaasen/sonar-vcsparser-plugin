// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;

import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;

@ComputeEngineSide
public class PostProjectAnalysisHook implements PostProjectAnalysisTask {
	private ComputeNumAuthorsMetric computeNumAuthorsMetric;
		
	public PostProjectAnalysisHook(ComputeNumAuthorsMetric computeNumAuthorsMetric) {
		this.computeNumAuthorsMetric = computeNumAuthorsMetric;
	}
	
	@Override
	public void finished(ProjectAnalysis analysis) {
		String projectKey = analysis.getProject().getKey();
		computeNumAuthorsMetric.removeProjectCache(projectKey);
	}
}
