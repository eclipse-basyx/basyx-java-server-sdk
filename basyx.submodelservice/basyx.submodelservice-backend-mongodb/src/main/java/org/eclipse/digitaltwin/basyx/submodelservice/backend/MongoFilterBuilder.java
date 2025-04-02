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

package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelservice.backend.IdShortPathParser.GenericPath;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.IdShortPathParser.IdShortPath;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.IdShortPathParser.IndexPath;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.IfNull;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.lang.NonNull;

/**
 * Builds MongoDB filters and aggregation operations for submodel elements.
 * 
 * @author mateusmolina
 * 
 */
public final class MongoFilterBuilder{
    static final String KEY_VALUE = "value";
    static final String KEY_STATEMENTS = "statements";
    static final String KEY_SUBMODEL_ELEMENTS = "submodelElements";
    static final String KEY_ID_SHORT = "idShort";

    public static MongoFilterResult parse(@NonNull String idShortPath) {
        Deque<GenericPath> paths = IdShortPathParser.parse(idShortPath);

        assert !paths.isEmpty();

        StringBuilder updateKey = new StringBuilder();
        List<CriteriaDefinition> filterArray = new ArrayList<>();
        int filterCounter = 0;

        updateKey.append(KEY_SUBMODEL_ELEMENTS);

        GenericPath rootPath = paths.pop();

        assert rootPath instanceof IdShortPath;
        
        String placeholder = "elem" + filterCounter++;
        updateKey.append(".$[").append(placeholder).append("]");
        filterArray.add(buildCriteria(joinKeys(placeholder, KEY_ID_SHORT), ((IdShortPath) rootPath).idShort()));

        while (!paths.isEmpty()) {
            GenericPath segment = paths.pop();
            updateKey.append(".").append(KEY_VALUE);
            if (segment instanceof IdShortPath idPath) {
                placeholder = "elem" + filterCounter++;
                updateKey.append(".$[").append(placeholder).append("]");
                filterArray.add(buildCriteria(joinKeys(placeholder, KEY_ID_SHORT), idPath.idShort()));
            } else if (segment instanceof IndexPath ixPath) {
                updateKey.append(".").append(ixPath.index());
            }
        }
        return new MongoFilterResult(updateKey.toString(), filterArray);
    }

    public static List<AggregationOperation> buildAggregationOperations(@NonNull String submodelId, @NonNull String idShortPath) {
        
        Deque<GenericPath> paths = IdShortPathParser.parse(idShortPath);
        List<AggregationOperation> ops = new ArrayList<>();

        ops.add(Aggregation.match(Criteria.where("_id").is(submodelId)));
        ops.add(Aggregation.unwind(KEY_SUBMODEL_ELEMENTS));

        GenericPath currentPath = paths.pop();
        assert currentPath instanceof IdShortPath;

        ops.add(Aggregation.match(Criteria.where(joinKeys(KEY_SUBMODEL_ELEMENTS, KEY_ID_SHORT)).is(((IdShortPath) currentPath).idShort())));
        ops.add(Aggregation.replaceRoot("$"+KEY_SUBMODEL_ELEMENTS));
        
        while (!paths.isEmpty()) {
            currentPath = paths.pop();
            ops.add(new UnwindOperation(Fields.field("$"+KEY_VALUE), true));
            ops.add(new UnwindOperation(Fields.field("$"+KEY_STATEMENTS), true));
            if (currentPath instanceof IdShortPath idPath) {
                Criteria inValue = Criteria.where(joinKeys(KEY_VALUE, KEY_ID_SHORT)).is(idPath.idShort());
                Criteria inStatements = Criteria.where(joinKeys(KEY_STATEMENTS, KEY_ID_SHORT)).is(idPath.idShort());
                ops.add(Aggregation.match(new Criteria().orOperator(inValue, inStatements)));
            } else if (currentPath instanceof IndexPath ixPath) {
                ops.add(Aggregation.skip(ixPath.index()));
                ops.add(Aggregation.limit(1));
            }
            ops.add(Aggregation.replaceRoot(IfNull.ifNull("$"+KEY_VALUE).then("$"+KEY_STATEMENTS)));

        }

        return ops;
    }

    static CriteriaDefinition buildCriteria(String key, String value) {
        return Criteria.where(key).is(value);
    }

    static String joinKeys(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append('.');
        }
        return sb.substring(0, sb.length() - 1);
    }

    public record MongoFilterResult(String key, List<CriteriaDefinition> filters) {
    }
}
