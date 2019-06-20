// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.utils.log.Logger;

import dev.roryclaasen.vcsparser.LoggerCreator;

public class TestFileReader {
	@Mock
	private LoggerCreator loggerCreator;

	@Mock
	private Logger logger;

	private FileReader fileReader;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		when(loggerCreator.get(FileReader.class)).thenReturn(logger);

		fileReader = new FileReader(loggerCreator);
	}

	@Test
	void givenFileReader_whenReadingFileAndFileDoesNotExistShouldReturnNull() {
		String file = "this/file/does/not/exist.json";

		String jsonString = fileReader.readFile(file);

		assertNull(jsonString);
		verify(logger, times(1)).error(eq("Could not read file: " + file));
	}

	@Test
	void givenFileReader_whenReadingFileAndFileExistsShouldReturnContent() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File file = new File(classLoader.getResource("measures.example.json").getFile());

		String jsonString = fileReader.readFile(file.getAbsolutePath());

		assertNotNull(jsonString);
	}
}
