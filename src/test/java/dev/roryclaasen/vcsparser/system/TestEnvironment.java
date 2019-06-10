// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class TestEnvironment {
	private Environment environment;

	@BeforeEach
	void setUp() {
		environment = new Environment();
	}

	@Test
	void givenEnvironment_whenGettingExistingVariableShouldReturnValue() {
		assertNotNull(environment.getEnvironmentVariable("PATH"));
	}
}
