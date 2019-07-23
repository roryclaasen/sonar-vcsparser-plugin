// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sonar.api.ce.ComputeEngineSide;

@ComputeEngineSide
public class AuthorListConverter {
	public List<Author> getAuthorListAfterDate(Collection<AuthorData> authorDataList, LocalDateTime date) {
		List<Author> datedAuthorDataList = new ArrayList<Author>();
		for (AuthorData authorData : authorDataList) {
			if (authorData.date.isAfter(date))
				datedAuthorDataList.addAll(authorData.authors);
		}
		return datedAuthorDataList;
	}

	public Map<String, Integer> getNumChangesPerAuthor(Map<String, Integer> numChanges, Collection<Author> authors) {
		for (Author author : authors) {
			if (!numChanges.containsKey(author.name))
				numChanges.put(author.name, author.numberOfChanges);
			else {
				int value = numChanges.get(author.name);
				value += author.numberOfChanges;
				numChanges.replace(author.name, value);
			}
		}
		return numChanges;
	}
}
