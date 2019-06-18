// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.measures.authors;

import java.util.Date;
import java.util.List;

public class AuthorData {
	public Date date;
	public List<Author> authors;
	
	public AuthorData(Date date, List<Author> authors) {
		this.date = date;
		this.authors = authors;
	}
}
