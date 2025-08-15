/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

import co.elastic.clients.json.JsonpMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.querycore.query.converter.AASQueryToElasticSearchConverter;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zielstor, fried
 */
public class AASQueryConverterTest {

    Map<String, String> queries = new HashMap<>() {{
        put("and_eq_eq_lt_field_strval.json", "expected_and_eq_eq_lt_field_strval.json");
        put("contains_field_strval.json", "expected_contains_field_strval.json");
        put("eq_field_field.json", "expected_eq_field_field.json");
        put("eq_field_strval.json", "expected_eq_field_strval.json");
        put("gt_field_numcast_field_numcast.json", "expected_gt_field_numcast_field_numcast.json");
        put("le_field_field.json", "expected_le_field_field.json");
        put("le_field_numVal.json", "expected_le_field_numVal.json");
        put("match_eq_eq_field_strval.json", "expected_match_eq_eq_field_strval.json");
        put("match_specific_asset_ids.json", "expected_match_specific_asset_ids.json");
        put("regex_field_strval.json", "expected_regex_field_strval.json");
        put("starts-with_field_strval.json", "expected_starts-with_field_strval.json");
        put("ends-with_field_strval.json", "expected_ends-with_field_strval.json");
    }};

    @Test
    public void testQueryConverter(){
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            String inputFileName = entry.getKey();
            String expectedFileName = entry.getValue();

            try {
                String inputQueryString = loadInputFileAsString(inputFileName);
                AASQuery query = loadStringAsQuery(inputQueryString);
                AASQueryToElasticSearchConverter converter = new AASQueryToElasticSearchConverter();
                co.elastic.clients.elasticsearch._types.query_dsl.Query convertedQuery = converter.convert(query);

                String actual = convertedQuery.toString().substring(7); // Remove "Query{" and "}"
                String expected = loadExpectedFileAsString(expectedFileName);

                BaSyxHttpTestUtils.assertSameJSONContent(expected,actual);
            } catch (IOException e) {
                throw new RuntimeException("Error processing files: " + inputFileName + " or " + expectedFileName, e);
            } catch (AssertionError ae) {
                System.err.println("Assertion failed for input file: " + inputFileName + " with expected file: " + expectedFileName);
                throw ae;
            }
            System.out.println("✓ Test passed for input file: " + inputFileName + " with expected file: " + expectedFileName);
        }
    }

    private AASQuery loadStringAsQuery(String queryString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(queryString, AASQuery.class);
    }

    private String loadInputFileAsString(String fileName) throws IOException {
        return BaSyxHttpTestUtils.readJSONStringFromClasspath("input/" + fileName);
    }

    private String loadExpectedFileAsString(String fileName) throws IOException {
        return BaSyxHttpTestUtils.readJSONStringFromClasspath("output/" + fileName);
    }
}
