// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask.ProjectAnalysis;
import org.sonar.api.ce.posttask.Project;

import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;

public class TestPostProjectAnalysisHook {
	@Mock
	private ProjectAnalysis projectAnalysis;
	
	@Mock
	private Project project;
	
	@Mock
	private ComputeNumAuthorsMetric computeNumAuthorsMetric;
	
	private static String SOME_PROJECT_KEY = "SomeProjectKey";
	
	private PostProjectAnalysisHook projectAnalysisHook;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		
		when(projectAnalysis.getProject()).thenReturn(project);
		
		when(project.getKey()).thenReturn(SOME_PROJECT_KEY);

		projectAnalysisHook = new PostProjectAnalysisHook(computeNumAuthorsMetric);
	}
	
	@Test
	void givenPostProjectAnalysisHook_whenFinished_thenCallCleanCache() {		
		projectAnalysisHook.finished(projectAnalysis);
		
		verify(computeNumAuthorsMetric, times(1)).removeProjectCache(SOME_PROJECT_KEY);
	}
}
