// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.sonar.api.Plugin.Context;
import org.sonar.api.utils.Version;

import dev.roryclaasen.vcsparser.authors.AuthorListConverter;
import dev.roryclaasen.vcsparser.measures.ComputeLinesFixedOverChangedMetric;
import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;
import dev.roryclaasen.vcsparser.metrics.PluginMetrics;
import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class TestVcsparserExtentionsPlugin {
	private VcsparserExtensionsPlugin plugin;

	@Mock
	private Context context;

	@Mock
	private IEnvironment environment;

	@Mock
	private IFileReader fileReader;

	private Version currentApiVersion = Version.create(8, 9, 2);

	@BeforeEach
	void setUp() {
		when(context.getSonarQubeVersion()).thenReturn(currentApiVersion);

		when(environment.getEnvironmentVariable(anyString())).thenReturn(null);

		plugin = new VcsparserExtensionsPlugin();
		plugin.setEnvironment(environment);
		plugin.setFileReader(fileReader);
	}

	@ParameterizedTest
	@ValueSource(strings = { "8.9.2", "8.9.3", "9" })
	void givenVcsparserExtentionsPlugin_whenDefineVersionOver_thenAddExtensions() {
		plugin.define(context);

		verify(context).addExtension(PluginMetrics.class);
		verify(context).addExtension(AuthorListConverter.class);
		verify(context).addExtension(PostProjectAnalysisHook.class);
		verify(context).addExtension(ComputeLinesFixedOverChangedMetric.class);
		verify(context).addExtension(ComputeNumAuthorsMetric.class);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "8", "8.8", "8.9.1" })
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
