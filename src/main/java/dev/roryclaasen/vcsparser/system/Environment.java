// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.system;

public class Environment implements IEnvironment {
    @Override
    public String getEnvironmentVariable(String variableName) {
        return System.getenv(variableName);
    }
}
