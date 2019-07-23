// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAuthorListConverter {

	private AuthorListConverter converter;

	@BeforeEach
	void setUp() {
		converter = new AuthorListConverter();
	}

	@Test
	void givenAuthorListConverter_whenGetAuthorListAfterDate_thenReturnFilteredList() {
		Author author1 = new Author("Some Author 1", 2);
		Author author2 = new Author("Some Author 2", 5);

		List<AuthorData> authorDataList = new ArrayList<AuthorData>();
		authorDataList.add(new AuthorData(LocalDate.now().atStartOfDay().minusDays(3), Arrays.asList(author1)));
		authorDataList.add(new AuthorData(LocalDate.now().atStartOfDay().minusDays(1), Arrays.asList(author2)));

		List<Author> authors = converter.getAuthorListAfterDate(authorDataList, LocalDate.now().atStartOfDay().minusDays(2));

		assertEquals(1, authors.size());
		assertEquals(author2, authors.get(0));
	}

	@Test
	void givenAuthorListConverter_whenGetAuthorListAfterDateAndEmptyAuthorDataColelction_thenReturnEmptyList() {
		List<Author> authors = converter.getAuthorListAfterDate(new ArrayList<AuthorData>(), LocalDate.now().atStartOfDay());

		assertEquals(0, authors.size());
	}

	@Test
	void givenAuthorListConverter_whenGetNumChangesPerAuthorAndAuthorsEmpty_thenReturnSameMap() {
		Map<String, Integer> authorsMap = new HashMap<String, Integer>();
		authorsMap.put("Some Author Name", 1);

		Map<String, Integer> resultMap = converter.getNumChangesPerAuthor(authorsMap, new ArrayList<Author>());

		assertEquals(authorsMap, resultMap);
	}

	@Test
	void givenAuthorListConverter_whenGetNumChangesPerAuthorAndAuthorsMissing_thenAddAuthorAndReturnMap() {
		Author author1 = new Author("Some Author 1", 2);

		Map<String, Integer> resultMap = converter.getNumChangesPerAuthor(new HashMap<String, Integer>(), Arrays.asList(author1));

		assertEquals(2, resultMap.get("Some Author 1"));
	}

	@Test
	void givenAuthorListConverter_whenGetNumChangesPerAuthorAndDiffrentAuthors_thenAddAuthorsAndReturnMap() {
		Author author1 = new Author("Some Author 1", 2);
		Author author2 = new Author("Some Author 2", 5);

		Map<String, Integer> resultMap = converter.getNumChangesPerAuthor(new HashMap<String, Integer>(), Arrays.asList(author1, author2));

		assertEquals(2, resultMap.size());
		assertEquals(2, resultMap.get("Some Author 1"));
		assertEquals(5, resultMap.get("Some Author 2"));
	}

	@Test
	void givenAuthorListConverter_whenGetNumChangesPerAuthorAndAuthorExists_thenAddAppendAuthorAndReturnMap() {
		Map<String, Integer> authorsMap = new HashMap<String, Integer>();
		authorsMap.put("Some Author 1", 1);

		Author author1 = new Author("Some Author 1", 2);

		Map<String, Integer> resultMap = converter.getNumChangesPerAuthor(authorsMap, Arrays.asList(author1));

		assertEquals(3, resultMap.get("Some Author 1"));
	}
}
