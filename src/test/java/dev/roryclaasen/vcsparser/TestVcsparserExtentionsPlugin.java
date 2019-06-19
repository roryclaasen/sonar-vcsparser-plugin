// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.Plugin.Context;

import dev.roryclaasen.vcsparser.authors.AuthorUtils;
import dev.roryclaasen.vcsparser.measures.ComputeLinesFixedOverChangedMetric;
import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;
import dev.roryclaasen.vcsparser.metrics.PluginMetrics;
import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

public class TestVcsparserExtentionsPlugin {
	private VcsparserExtensionsPlugin plugin;

	@Mock
	private Context context;

	@Mock
	private IEnvironment environment;

	@Mock
	private IFileReader fileReader;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		when(environment.getEnvironmentVariable(anyString())).thenReturn(null);

		plugin = new VcsparserExtensionsPlugin();
		plugin.setEnvironment(environment);
		plugin.setFileReader(fileReader);
	}

	@Test
	void givenVcsparserExtentionsPlugin_whenDefine_thenAddExtensions() {
		plugin.define(context);

		verify(context).addExtension(PluginMetrics.class);
		verify(context).addExtension(AuthorUtils.class);
		verify(context).addExtension(PostProjectAnalysisHook.class);
		verify(context).addExtension(ComputeLinesFixedOverChangedMetric.class);
		verify(context).addExtension(ComputeNumAuthorsMetric.class);
	}
}
