// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import org.sonar.api.Plugin;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import dev.roryclaasen.vcsparser.authors.AuthorUtils;
import dev.roryclaasen.vcsparser.measures.ComputeLinesFixedOverChangedMetric;
import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;
import dev.roryclaasen.vcsparser.metrics.PluginMetrics;
import dev.roryclaasen.vcsparser.system.Environment;
import dev.roryclaasen.vcsparser.system.FileReader;
import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

public class VcsparserExtensionsPlugin implements Plugin {
	private final Logger log = Loggers.get(VcsparserExtensionsPlugin.class);

	private IEnvironment environment;
	private IFileReader fileReader;

	public VcsparserExtensionsPlugin() {
		this.environment = new Environment();
		this.fileReader = new FileReader();
	}

	public void setEnvironment(IEnvironment environment) {
		this.environment = environment;
	}

	public void setFileReader(IFileReader fileReader) {
		this.fileReader = fileReader;
	}

	@Override
	public void define(Context context) {
		log.debug("Registering Vcsparser Extensions");

		PluginMetrics.loadAndAlter(environment, fileReader);
		context.addExtension(PluginMetrics.class);
		context.addExtension(AuthorUtils.class);
		context.addExtension(PostProjectAnalysisHook.class);
		context.addExtension(ComputeLinesFixedOverChangedMetric.class);
		context.addExtension(ComputeNumAuthorsMetric.class);
	}
}
