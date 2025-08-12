package org.eclipse.digitaltwin.basyx.querycore.query.model;

import java.util.List;

public class QueryResponse {

        public QueryPaging paging_metadata;
        public List<Object> result;

        public QueryResponse(QueryPaging paging_metadata, List<Object> result) {
            this.paging_metadata = paging_metadata;
            this.result = result;
        }
}
