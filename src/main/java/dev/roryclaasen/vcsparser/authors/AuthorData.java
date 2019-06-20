// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import java.util.Date;
import java.util.List;

public class AuthorData {
	public final Date date;
	public final List<Author> authors;

	public AuthorData(Date date, List<Author> authors) {
		this.date = date;
		this.authors = authors;
	}
}
