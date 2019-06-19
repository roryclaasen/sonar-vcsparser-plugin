// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;

import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;
import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsOver10PercMetric;

@ComputeEngineSide
public class PostProjectAnalysisHook implements PostProjectAnalysisTask {
	private ComputeNumAuthorsMetric computeNumAuthorsMetric;
	private ComputeNumAuthorsOver10PercMetric computeNumAuthorsOver10PercMetric;
		
	public PostProjectAnalysisHook(ComputeNumAuthorsMetric computeNumAuthorsMetric, ComputeNumAuthorsOver10PercMetric computeNumAuthorsOver10PercMetric) {
		this.computeNumAuthorsMetric = computeNumAuthorsMetric;
		this.computeNumAuthorsOver10PercMetric = computeNumAuthorsOver10PercMetric;
	}
	
	@Override
	public void finished(ProjectAnalysis analysis) {
		String projectKey = analysis.getProject().getKey();
		computeNumAuthorsMetric.removeProjectCache(projectKey);
		computeNumAuthorsOver10PercMetric.removeProjectCache(projectKey);
	}
}
