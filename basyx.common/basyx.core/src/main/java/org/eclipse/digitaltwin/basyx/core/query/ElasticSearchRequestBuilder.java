package org.eclipse.digitaltwin.basyx.core.query;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;

import java.util.List;
import java.util.Arrays;

/**
 * Utility class for building ElasticSearch SearchRequest objects from custom Query objects
 */
public class ElasticSearchRequestBuilder {
    
    private final QueryToElasticSearchConverter queryConverter;
    
    public ElasticSearchRequestBuilder() {
        this.queryConverter = new QueryToElasticSearchConverter();
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
    
    /**
     * Builds ElasticSearch SearchRequest with custom source includes
     * 
     * @param customQuery The custom query to convert
     * @param indexName The ElasticSearch index name to search
     * @param sourceIncludes List of fields to include in the response
     * @return ElasticSearch SearchRequest
     */
    public SearchRequest buildSearchRequestWithSources(AASQuery customQuery, String indexName, List<String> sourceIncludes) {
        co.elastic.clients.elasticsearch._types.query_dsl.Query esQuery = queryConverter.convert(customQuery);
        
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
            .index(indexName)
            .query(esQuery);
        
        if (sourceIncludes != null && !sourceIncludes.isEmpty()) {
            searchBuilder.source(SourceConfig.of(s -> s
                .filter(SourceFilter.of(f -> f
                    .includes(sourceIncludes)
                ))
            ));
        }
        
        return searchBuilder.build();
    }
    
    /**
     * Builds ElasticSearch SearchRequest with custom source includes and excludes
     * 
     * @param customQuery The custom query to convert
     * @param indexName The ElasticSearch index name to search
     * @param sourceIncludes List of fields to include in the response
     * @param sourceExcludes List of fields to exclude from the response
     * @return ElasticSearch SearchRequest
     */
    public SearchRequest buildSearchRequestWithSourceFilters(AASQuery customQuery, String indexName,
                                                             List<String> sourceIncludes, List<String> sourceExcludes) {
        co.elastic.clients.elasticsearch._types.query_dsl.Query esQuery = queryConverter.convert(customQuery);
        
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
            .index(indexName)
            .query(esQuery);
        
        if ((sourceIncludes != null && !sourceIncludes.isEmpty()) || 
            (sourceExcludes != null && !sourceExcludes.isEmpty())) {
            
            SourceFilter.Builder filterBuilder = new SourceFilter.Builder();
            
            if (sourceIncludes != null && !sourceIncludes.isEmpty()) {
                filterBuilder.includes(sourceIncludes);
            }
            
            if (sourceExcludes != null && !sourceExcludes.isEmpty()) {
                filterBuilder.excludes(sourceExcludes);
            }
            
            searchBuilder.source(SourceConfig.of(s -> s
                .filter(filterBuilder.build())
            ));
        }
        
        return searchBuilder.build();
    }
}