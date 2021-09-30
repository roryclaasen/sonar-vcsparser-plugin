// Copyright (c) Rory Claasen. All rights reserved.
// Licensed under the MIT License.

package dev.roryclaasen.vcsparser.authors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.sonar.api.utils.log.Logger;

import dev.roryclaasen.vcsparser.LoggerCreator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class TestJsonAuthorParser {
    @Mock
    private LoggerCreator loggerCreator;

    @Mock
    private Logger logger;

    private DateTimeFormatter formatter;

    private JsonAuthorParser jsonParser;

    @BeforeEach
    void SetUp() {

        when(loggerCreator.get(AuthorListConverter.class)).thenReturn(logger);

        formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        jsonParser = new JsonAuthorParser(loggerCreator);
    }

    private JSONObject createJsonAuthorData(String date, JSONObject... authors) {
        JSONObject authorData = new JSONObject();
        authorData.put("date", date);
        authorData.put("authors", new JSONArray(authors));
        return authorData;
    }

    private JSONObject createJsonAuthor(String name, int numChanges) {
        JSONObject author = new JSONObject();
        author.put("Author", name);
        author.put("NumberOfChanges", numChanges);
        return author;
    }

    @Test
    void givenJsonAuthorParser_whenJsonStringArrayToAuthorDataList_thenReturnListAuthorData() {
        JSONArray jsonAuthorDataArray = new JSONArray();
        jsonAuthorDataArray.put(createJsonAuthorData("2019/06/19 00:00:00", createJsonAuthor("Some Author Name", 1)));

        List<AuthorData> result = jsonParser.jsonStringArrayToAuthorDataList(jsonAuthorDataArray.toString());

        assertEquals(1, result.size());
    }

    @Test
    void givenJsonAuthorParser_whenJsonArrayToAuthorDataList_thenReturnListAuthorData() {
        JSONArray jsonAuthorDataArray = new JSONArray();
        jsonAuthorDataArray.put(createJsonAuthorData("2019/06/19 00:00:00", createJsonAuthor("Some Author Name", 1)));

        List<AuthorData> result = jsonParser.jsonArrayToAuthorDataList(jsonAuthorDataArray);

        assertEquals(1, result.size());
    }

    @Test
    void givenJsonAuthorParser_whenJsonArrayToAuthorDataListThrow_thenReturnNull() {
        JSONObject someBadJsonObject = createJsonAuthorData("2019-06-19 00:00:00", createJsonAuthor("Some Author Name", 1));

        JSONArray jsonAuthorDataArray = new JSONArray();
        jsonAuthorDataArray.put(someBadJsonObject);

        List<AuthorData> result = jsonParser.jsonArrayToAuthorDataList(jsonAuthorDataArray);

        assertNull(result);
    }

    @Test
    void givenJsonAuthorParser_whenJsonObjectToAuthorData_thenReturnAuthorData() throws JSONException, DateTimeParseException {
        JSONObject jsonAuthorData = createJsonAuthorData("2019/06/19 00:00:00", createJsonAuthor("Some Author Name", 1));

        AuthorData authorData = jsonParser.jsonObjectToAuthorData(jsonAuthorData);

        assertEquals("2019/06/19 00:00:00", authorData.date.format(formatter));
        assertEquals(1, authorData.authors.size());
        Author author = authorData.authors.get(0);
        assertEquals("Some Author Name", author.name);
        assertEquals(1, author.numberOfChanges);
    }

    @Test
    void givenJsonAuthorParser_whenJsonObjectToAuthorDataAndDateWrongFormat_thenThrowParseException() {
        JSONObject jsonAuthorData = createJsonAuthorData("2019-06-19 00:00:00", createJsonAuthor("Some Author Name", 1));

        assertThrows(DateTimeParseException.class, () -> jsonParser.jsonObjectToAuthorData(jsonAuthorData));
    }

    @Test
    void givenJsonAuthorParser_whenJsonObjectToAuthorDataAndMissingJsonEntries_thenThrowJSONException() {
        JSONObject someBadJsonObject = new JSONObject();

        assertThrows(JSONException.class, () -> jsonParser.jsonObjectToAuthorData(someBadJsonObject));
    }
}
