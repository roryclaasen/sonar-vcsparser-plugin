// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.Plugin.Context;

import dev.roryclaasen.vcsparser.measures.PluginMetrics;

public class TestVcsparserExtentionsPlugin {
	private VcsparserExtensionsPlugin plugin;

	@Mock
	private Context context;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		plugin = new VcsparserExtensionsPlugin();
	}

	@Test
	void givenBugPredictionPlugin_whenDefine_thenAddExtensions() {
		plugin.define(context);

		verify(context).addExtension(PluginMetrics.class);
	}
}
