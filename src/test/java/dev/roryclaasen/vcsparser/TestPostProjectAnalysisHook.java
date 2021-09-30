// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask.ProjectAnalysis;
import org.sonar.api.ce.posttask.Project;

import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
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
