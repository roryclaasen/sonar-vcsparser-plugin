// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestFileReader {
	private FileReader fileReader;

	@BeforeEach
	void setUp() {
		fileReader = new FileReader();
	}

	@Test
	void givenFileReader_whenReadingFileAndFileDoesNotExistShouldReturnEmptyString() {
		String jsonString = fileReader.readFile("");

		assertNull(jsonString);
	}

	@Test
	void givenFileReader_whenReadingFileAndFileExistsShouldReturnContent() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File file = new File(classLoader.getResource("measures.example.json").getFile());

		String jsonString = fileReader.readFile(file.getAbsolutePath());

		assertNotNull(jsonString);
	}
}
