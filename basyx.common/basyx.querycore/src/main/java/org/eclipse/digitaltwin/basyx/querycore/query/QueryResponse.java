package org.eclipse.digitaltwin.basyx.querycore.query;

public class QueryResponse<T> {

        public QueryPaging paging_metadata;
        public QueryResult<T> result;

        public QueryResponse(QueryPaging paging_metadata, QueryResult<T> result) {
            this.paging_metadata = paging_metadata;
            this.result = result;
        }
}
