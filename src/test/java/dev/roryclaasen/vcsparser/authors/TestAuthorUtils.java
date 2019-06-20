// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.utils.log.Logger;

import dev.roryclaasen.vcsparser.LoggerCreator;

public class TestAuthorUtils {
	@Mock
	private LoggerCreator loggerCreator;

	@Mock
	private Logger logger;

	private SimpleDateFormat dateParser;
	private AuthorUtils authorUtils;

	@BeforeEach
	void SetUp() {
		MockitoAnnotations.initMocks(this);

		when(loggerCreator.get(AuthorUtils.class)).thenReturn(logger);

		dateParser = new SimpleDateFormat(AuthorUtils.DATE_FORMAT);
		authorUtils = new AuthorUtils(loggerCreator);
	}

	private JSONObject createJsonAuthorData(String date, JSONObject... authors) {
		JSONObject authorData = new JSONObject();
		authorData.put("date", date);
		authorData.put("authors", new JSONArray(authors));
		return authorData;
	}

	private JSONObject createJsonAuthor(String name, int numChanges) {
		JSONObject author = new JSONObject();
		author.put("author", name);
		author.put("number_of_changes", numChanges);
		return author;
	}

	@Test
	void givenAuthorUtils_whenJsonStringArrayToAuthorDataList_thenReturnListAuthorData() {
		JSONArray jsonAuthorDataArray = new JSONArray();
		jsonAuthorDataArray.put(createJsonAuthorData("2019/06/19", createJsonAuthor("Some Author Name", 1)));

		List<AuthorData> result = authorUtils.jsonStringArrayToAuthorDataList(jsonAuthorDataArray.toString());

		assertEquals(1, result.size());
	}

	@Test
	void givenAuthorUtils_whenJsonArrayToAuthorDataList_thenReturnListAuthorData() {
		JSONArray jsonAuthorDataArray = new JSONArray();
		jsonAuthorDataArray.put(createJsonAuthorData("2019/06/19", createJsonAuthor("Some Author Name", 1)));

		List<AuthorData> result = authorUtils.jsonArrayToAuthorDataList(jsonAuthorDataArray);

		assertEquals(1, result.size());
	}

	@Test
	void givenAuthorUtils_whenJsonArrayToAuthorDataListThrow_thenReturnNull() {
		JSONObject someBadJsonObject = new JSONObject();

		JSONArray jsonAuthorDataArray = new JSONArray();
		jsonAuthorDataArray.put(someBadJsonObject);

		List<AuthorData> result = authorUtils.jsonArrayToAuthorDataList(jsonAuthorDataArray);

		assertNull(result);
	}

	@Test
	void givenAuthorUtils_whenJsonObjectToAuthorData_thenReturnAuthorData() throws JSONException, ParseException {
		JSONObject jsonAuthorData = createJsonAuthorData("2019/06/19", createJsonAuthor("Some Author Name", 1));

		AuthorData authorData = authorUtils.jsonObjectToAuthorData(jsonAuthorData);

		assertEquals("2019/06/19", dateParser.format(authorData.date));
		assertEquals(1, authorData.authors.size());
		Author author = authorData.authors.get(0);
		assertEquals("Some Author Name", author.name);
		assertEquals(1, author.numberOfChanges);
	}

	@Test
	void givenAuthorUtils_whenJsonObjectToAuthorDataAndDateWrongFormat_thenThrowParseException() {
		JSONObject jsonAuthorData = createJsonAuthorData("2019-06-19", createJsonAuthor("Some Author Name", 1));

		assertThrows(ParseException.class, () -> authorUtils.jsonObjectToAuthorData(jsonAuthorData));
	}

	@Test
	void givenAuthorUtils_whenJsonObjectToAuthorDataAndMissingJsonEntries_thenThrowJSONException() {
		JSONObject someBadJsonObject = new JSONObject();

		assertThrows(JSONException.class, () -> authorUtils.jsonObjectToAuthorData(someBadJsonObject));
	}

	@Test
	void givenAuthorUtils_whenGetAuthorListAfterDate_thenReturnFilteredList() {
		Author author1 = new Author("Some Author 1", 2);
		Author author2 = new Author("Some Author 2", 5);

		List<AuthorData> authorDataList = new ArrayList<AuthorData>();
		authorDataList.add(new AuthorData(DateUtils.addDays(new Date(), -3), Arrays.asList(author1)));
		authorDataList.add(new AuthorData(DateUtils.addDays(new Date(), -1), Arrays.asList(author2)));

		List<Author> authors = authorUtils.getAuthorListAfterDate(authorDataList, DateUtils.addDays(new Date(), -2));

		assertEquals(1, authors.size());
		assertEquals(author2, authors.get(0));
	}

	@Test
	void givenAuthorUtils_whenGetAuthorListAfterDateAndEmptyAuthorDataColelction_thenReturnEmptyList() {
		List<Author> authors = authorUtils.getAuthorListAfterDate(new ArrayList<AuthorData>(), new Date());

		assertEquals(0, authors.size());
	}

	@Test
	void givenAuthorUtils_whenGetNumChangesPerAuthorAndAuthorsEmpty_thenReturnSameMap() {
		Map<String, Integer> authorsMap = new HashMap<String, Integer>();
		authorsMap.put("Some Author Name", 1);

		Map<String, Integer> resultMap = authorUtils.getNumChangesPerAuthor(authorsMap, new ArrayList<Author>());

		assertEquals(authorsMap, resultMap);
	}

	@Test
	void givenAuthorUtils_whenGetNumChangesPerAuthorAndAuthorsMissing_thenAddAuthorAndReturnMap() {
		Author author1 = new Author("Some Author 1", 2);

		Map<String, Integer> resultMap = authorUtils.getNumChangesPerAuthor(new HashMap<String, Integer>(), Arrays.asList(author1));

		assertEquals(resultMap.get("Some Author 1"), 2);
	}

	@Test
	void givenAuthorUtils_whenGetNumChangesPerAuthorAndDiffrentAuthors_thenAddAuthorsAndReturnMap() {
		Author author1 = new Author("Some Author 1", 2);
		Author author2 = new Author("Some Author 2", 5);

		Map<String, Integer> resultMap = authorUtils.getNumChangesPerAuthor(new HashMap<String, Integer>(), Arrays.asList(author1, author2));

		assertEquals(2, resultMap.size());
		assertEquals(resultMap.get("Some Author 1"), 2);
		assertEquals(resultMap.get("Some Author 2"), 5);
	}

	@Test
	void givenAuthorUtils_whenGetNumChangesPerAuthorAndAuthorExists_thenAddAppendAuthorAndReturnMap() {
		Map<String, Integer> authorsMap = new HashMap<String, Integer>();
		authorsMap.put("Some Author 1", 1);

		Author author1 = new Author("Some Author 1", 2);

		Map<String, Integer> resultMap = authorUtils.getNumChangesPerAuthor(authorsMap, Arrays.asList(author1));

		assertEquals(resultMap.get("Some Author 1"), 3);
	}
}
