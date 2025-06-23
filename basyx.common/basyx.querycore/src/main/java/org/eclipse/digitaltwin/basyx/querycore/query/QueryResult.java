package org.eclipse.digitaltwin.basyx.querycore.query;

import java.util.List;

public class QueryResult<T> {
    public List<T> result;

    public QueryResult(List<T> result) {
        this.result = result;
    }

}
