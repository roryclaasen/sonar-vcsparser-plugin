// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class FileReader implements IFileReader {

	private final Logger log = Loggers.get(FileReader.class);

	@SuppressWarnings("resource")
	@Override
	public String readFile(String fileName) {
		try {
			InputStream stream = new FileInputStream(fileName);
			InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
			return new BufferedReader(reader).lines().collect(Collectors.joining());
		} catch (FileNotFoundException e) {
			log.error("Could not read file: " + fileName);
			return null;
		}
	}
}
