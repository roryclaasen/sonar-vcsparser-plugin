// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.Plugin.Context;
import org.sonar.api.utils.Version;

import dev.roryclaasen.vcsparser.authors.AuthorListConverter;
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

	private Version currentApiVersion = Version.create(6, 7, 4);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		when(context.getSonarQubeVersion()).thenReturn(currentApiVersion);

		when(environment.getEnvironmentVariable(anyString())).thenReturn(null);

		plugin = new VcsparserExtensionsPlugin();
		plugin.setEnvironment(environment);
		plugin.setFileReader(fileReader);
	}

	@ParameterizedTest
	@ValueSource(strings = { "6.7.4", "6.7.5", "6.8", "7" })
	void givenVcsparserExtentionsPlugin_whenDefineVersionOver_thenAddExtensions() {
		plugin.define(context);

		verify(context).addExtension(PluginMetrics.class);
		verify(context).addExtension(AuthorListConverter.class);
		verify(context).addExtension(PostProjectAnalysisHook.class);
		verify(context).addExtension(ComputeLinesFixedOverChangedMetric.class);
		verify(context).addExtension(ComputeNumAuthorsMetric.class);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "6", "6.7", "6.7.3" })
	void givenVcsparserExtentionsPlugin_whenDefineVersionBelow_thenDontExtensions(String version) {
		when(context.getSonarQubeVersion()).thenReturn(Version.parse(version));
		
		plugin.define(context);

		verify(context, times(0)).addExtension(PluginMetrics.class);
		verify(context, times(0)).addExtension(AuthorListConverter.class);
		verify(context, times(0)).addExtension(PostProjectAnalysisHook.class);
		verify(context, times(0)).addExtension(ComputeLinesFixedOverChangedMetric.class);
		verify(context, times(0)).addExtension(ComputeNumAuthorsMetric.class);
	}
}
