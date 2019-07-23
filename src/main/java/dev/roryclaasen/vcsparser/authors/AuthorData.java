// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import java.time.LocalDateTime;
import java.util.List;

public class AuthorData {
	public final LocalDateTime date;
	public final List<Author> authors;

	public AuthorData(LocalDateTime date, List<Author> authors) {
		this.date = date;
		this.authors = authors;
	}
}
