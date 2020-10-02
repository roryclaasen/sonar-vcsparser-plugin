// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import org.sonar.api.Plugin;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.log.Logger;

import dev.roryclaasen.vcsparser.authors.AuthorListConverter;
import dev.roryclaasen.vcsparser.authors.JsonAuthorParser;
import dev.roryclaasen.vcsparser.measures.ComputeLinesFixedOverChangedMetric;
import dev.roryclaasen.vcsparser.measures.ComputeNumAuthorsMetric;
import dev.roryclaasen.vcsparser.metrics.PluginMetrics;
import dev.roryclaasen.vcsparser.system.Environment;
import dev.roryclaasen.vcsparser.system.FileReader;
import dev.roryclaasen.vcsparser.system.IEnvironment;
import dev.roryclaasen.vcsparser.system.IFileReader;

public class VcsparserExtensionsPlugin implements Plugin {
	private final Logger log;

	private IEnvironment environment;
	private IFileReader fileReader;

	public VcsparserExtensionsPlugin() {
		LoggerCreator loggerCreator = new LoggerCreator();
		this.log = loggerCreator.get(VcsparserExtensionsPlugin.class);
		this.environment = new Environment();
		this.fileReader = new FileReader(loggerCreator);
	}

	public void setEnvironment(IEnvironment environment) {
		this.environment = environment;
	}

	public void setFileReader(IFileReader fileReader) {
		this.fileReader = fileReader;
	}

	@Override
	public void define(Context context) {
		Version sonar = context.getSonarQubeVersion();
		Version required = Version.create(6, 7, 4);
		
		if (sonar.isGreaterThanOrEqual(required)) {
			log.debug("Registering Vcsparser Extensions");
	
			PluginMetrics.loadAndAlter(environment, fileReader);
	
			context.addExtension(LoggerCreator.class);
			context.addExtension(PluginMetrics.class);
			context.addExtension(JsonAuthorParser.class);
			context.addExtension(AuthorListConverter.class);
	
			context.addExtension(ComputeLinesFixedOverChangedMetric.class);
			context.addExtension(ComputeNumAuthorsMetric.class);
	
			context.addExtension(PostProjectAnalysisHook.class);
		} else {
			log.debug("Vcsparser needs SonarQube to be " + required.toString() + " or greater. Found " + sonar.toString());
		}
	}
}
