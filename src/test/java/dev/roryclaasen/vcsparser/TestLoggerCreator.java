// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.utils.log.Logger;

public class TestLoggerCreator {
	private LoggerCreator loggerCreator;

	@BeforeEach
	void setUp() {
		loggerCreator = new LoggerCreator();
	}
	
	@Test
	void givenLoggerCreator_whenGet_thenReturnLogger() {
		Object log = loggerCreator.get(TestLoggerCreator.class);
		
		assertNotNull(log);
		assertTrue(log instanceof Logger);
	}
}
