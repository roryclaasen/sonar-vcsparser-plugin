// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import org.sonar.api.Plugin;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import dev.roryclaasen.vcsparser.measures.PluginMetrics;

public class VcsparserExtensionsPlugin implements Plugin {

	private final Logger log = Loggers.get(VcsparserExtensionsPlugin.class);
	
	@Override
	public void define(Context context) {
		log.debug("Registering SonarQube Bug Prediction");

		context.addExtension(PluginMetrics.class);
	}
}
