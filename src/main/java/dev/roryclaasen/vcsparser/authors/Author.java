// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

public class Author {
    public final String name;
    public final int numberOfChanges;

    public Author(String name, int numberOfChanges) {
        this.name = name;
        this.numberOfChanges = numberOfChanges;
    }
}
