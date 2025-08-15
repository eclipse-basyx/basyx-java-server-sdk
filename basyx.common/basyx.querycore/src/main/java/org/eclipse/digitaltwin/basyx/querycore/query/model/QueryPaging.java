package org.eclipse.digitaltwin.basyx.querycore.query.model;

public class QueryPaging {
    public String cursor;
    public String resulType;

    public QueryPaging(String cursor, String resulType) {
        this.cursor = cursor;
        this.resulType = resulType;
    }
}
