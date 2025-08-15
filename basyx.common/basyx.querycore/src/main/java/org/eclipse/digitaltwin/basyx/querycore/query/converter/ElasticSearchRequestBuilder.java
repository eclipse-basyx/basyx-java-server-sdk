package org.eclipse.digitaltwin.basyx.querycore.query.converter;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;

import java.util.List;
import java.util.Arrays;

/**
 * Utility class for building ElasticSearch SearchRequest objects from custom Query objects
 */
public class ElasticSearchRequestBuilder {
    
    private final AASQueryToElasticSearchConverter queryConverter;
    
    public ElasticSearchRequestBuilder() {
        this.queryConverter = new AASQueryToElasticSearchConverter();
    }
    
    /**
     * Builds a complete ElasticSearch SearchRequest from a custom Query
     * 
     * @param customQuery The custom query to convert
     * @param indexName The ElasticSearch index name to search
     * @return ElasticSearch SearchRequest
     */
    public SearchRequest buildSearchRequest(AASQuery customQuery, String indexName) {
        return buildSearchRequest(customQuery, indexName, null, null);
    }
    
    /**
     * Builds a complete ElasticSearch SearchRequest from a custom Query with pagination
     * 
     * @param customQuery The custom query to convert
     * @param indexName The ElasticSearch index name to search
     * @param from The starting point for pagination (offset)
     * @param size The number of results to return
     * @return ElasticSearch SearchRequest
     */
    public SearchRequest buildSearchRequest(AASQuery customQuery, String indexName, Integer from, Integer size) {
        co.elastic.clients.elasticsearch._types.query_dsl.Query esQuery = queryConverter.convert(customQuery);
        
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
            .index(indexName)
            .query(esQuery);
        
        // Handle source filtering based on $select field
        if (customQuery != null && customQuery.get$select() != null) {
            String selectField = customQuery.get$select();
            if ("id".equals(selectField)) {
                // Only return the id field
                searchBuilder.source(SourceConfig.of(s -> s
                    .filter(SourceFilter.of(f -> f
                        .includes(Arrays.asList("id", "_id"))
                    ))
                ));
            }
        }
        
        // Handle pagination
        if (from != null) {
            searchBuilder.from(from);
        }
        if (size != null) {
            searchBuilder.size(size);
        }
        
        return searchBuilder.build();
    }

}
