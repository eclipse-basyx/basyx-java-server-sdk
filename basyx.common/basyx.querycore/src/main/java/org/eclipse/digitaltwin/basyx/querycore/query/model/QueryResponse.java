package org.eclipse.digitaltwin.basyx.querycore.query.model;

public class QueryResponse {

        public QueryPaging paging_metadata;
        public QueryResult result;

        public QueryResponse(QueryPaging paging_metadata, QueryResult result) {
            this.paging_metadata = paging_metadata;
            this.result = result;
        }
}
