// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

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
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("measures.json").getFile());

		String jsonString = fileReader.readFile(file.getAbsolutePath());

		assertNotNull(jsonString);
	}
}
