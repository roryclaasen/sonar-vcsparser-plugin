// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.sonar.api.utils.log.Logger;

import dev.roryclaasen.vcsparser.LoggerCreator;

public class FileReader implements IFileReader {
    private final Logger log;

    public FileReader(LoggerCreator loggerCreator) {
        log = loggerCreator.get(FileReader.class);
    }

    @Override
    public String readFile(String fileName) {
        try (InputStream stream = new FileInputStream(fileName);
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader)) {
            return br.lines().collect(Collectors.joining());
        } catch (Exception e) {
            log.error("Could not read file: " + fileName);
            return null;
        }
    }
}
