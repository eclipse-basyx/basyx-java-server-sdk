package org.eclipse.digitaltwin.basyx.querycore.query.executor;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryPaging;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResult;
import org.eclipse.digitaltwin.basyx.querycore.query.converter.ElasticSearchRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ESQueryExecutor {

    private final String indexName;
    private final String modelName;
    public static final int DEFAULT_PAGE_SIZE = 100;
    private final ElasticsearchClient client;
    private static final Logger logger = LoggerFactory.getLogger(ESQueryExecutor.class);

    public ESQueryExecutor(ElasticsearchClient client, String indexName, String modelName){
        this.indexName = indexName;
        this.modelName = modelName;
        this.client = client;
    }

    public QueryResponse executeQueryAndGetResponse(AASQuery query, Integer limit, Base64UrlEncodedCursor cursor) throws IOException {
        ElasticSearchRequestBuilder builder = new ElasticSearchRequestBuilder();
        SearchRequest baseSearchRequest = builder.buildSearchRequest(query, indexName);

        int pageSize = getPageSize(limit);

        SearchRequest.Builder searchRequestBuilder = buildSearchRequestWithPagination(baseSearchRequest, pageSize);

        applyCursor(cursor, searchRequestBuilder);

        SearchRequest paginatedSearchRequest = searchRequestBuilder.build();

        SearchResponse<Object> response = client.search(paginatedSearchRequest, Object.class);

        List<Hit<Object>> topHits = response.hits().hits();

        boolean hasMore = topHits.size() > pageSize;
        List<Hit<Object>> pageHits = hasMore ? topHits.subList(0, pageSize) : topHits;

        ObjectMapper mapper = new ObjectMapper();

        List<Object> objectHits = pageHits.stream()
                .map(Hit::source)
                .map(this::filterEmptyArrays)
                .map(mapper::valueToTree)
                .toList();



        String nextCursor = getNextCursor(hasMore, pageHits);

        for(Object hit : objectHits){
            if (hit instanceof ObjectNode) {
                ObjectNode node = (ObjectNode) hit;
                rewriteRecursively(node);
            } else {
                logger.warn("Hit is not an ObjectNode: {}", hit.getClass().getName());
            }
        }

        QueryResponse queryResponse = getQueryResponse(query, objectHits, nextCursor);
        return queryResponse;
    }

    /**
     * Recursively filters out empty arrays from the given object.
     * @param obj The object to filter
     * @return The filtered object with empty arrays removed
     */
    @SuppressWarnings("unchecked")
    private Object filterEmptyArrays(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, Object> filteredMap = new LinkedHashMap<>();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = filterEmptyArrays(entry.getValue());
                // Only add the entry if the value is not an empty array
                if (!(value instanceof List && ((List<?>) value).isEmpty())) {
                    filteredMap.put(entry.getKey(), value);
                }
            }
            return filteredMap;
        }

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (list.isEmpty()) {
                return list;
            }

            return list.stream()
                    .map(this::filterEmptyArrays)
                    .collect(Collectors.toList());
        }

        return obj;
    }

    private QueryResponse getQueryResponse(AASQuery query, List<Object> objectHits, String nextCursor) {
        QueryResult queryResult = new QueryResult(objectHits);
        QueryPaging queryPaging = new QueryPaging(nextCursor, getResultType(query));
        QueryResponse queryResponse = new QueryResponse(queryPaging, queryResult);
        return queryResponse;
    }

    private String getNextCursor(boolean hasMore, List<Hit<Object>> pageHits) {
        if (hasMore && !pageHits.isEmpty()) {
            Hit<Object> lastHit = pageHits.get(pageHits.size() - 1);
            String lastId = lastHit.id();
            return Base64.getUrlEncoder().encodeToString(lastId.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    private String getResultType(AASQuery query) {
        return (query.get$select() == null || query.get$select().isEmpty()) ? modelName : "Identifier";
    }

    private void applyCursor(Base64UrlEncodedCursor cursor, SearchRequest.Builder searchRequestBuilder) {
        if (cursor != null && cursor.getDecodedCursor() != null) {
            try {
                String decodedCursor = cursor.getDecodedCursor();
                searchRequestBuilder.searchAfter(decodedCursor);
            } catch (IllegalArgumentException e) {
                // Invalid cursor, ignore and start from beginning
            }
        }
    }

    private SearchRequest.Builder buildSearchRequestWithPagination(SearchRequest baseSearchRequest, int pageSize) {
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                .index(indexName)
                .query(baseSearchRequest.query())
                .size(pageSize + 1)
                .sort(SortOptions.of(s -> s.field(f -> f.field("id.keyword").order(SortOrder.Asc))));
        
        if (baseSearchRequest.source() != null) {
            searchRequestBuilder.source(baseSearchRequest.source());
        }
        
        return searchRequestBuilder;
    }

    private static int getPageSize(Integer limit) {
        return (limit != null && limit > 0) ? limit : DEFAULT_PAGE_SIZE;
    }

    private static void move(ObjectNode node, String from, String to) {
        JsonNode v = node.remove(from);
        if (v != null) node.set(to, v);
    }

    public void rewriteRecursively(JsonNode node){
        if (node == null || !node.isObject()) return;
        
        Stack<JsonNode> nodeStack = new Stack<>();
        nodeStack.push(node);
        
        while (!nodeStack.isEmpty()) {
            JsonNode currentNode = nodeStack.pop();
            
            if (!currentNode.isObject()) continue;
            ObjectNode obj = (ObjectNode) currentNode;

            move(obj, "smcChildren", "value");
            move(obj, "smlChildren", "value");
            move(obj, "referenceChildren", "value");
            move(obj, "langContent", "value");

            List<String> names = new ArrayList<>();
            obj.fieldNames().forEachRemaining(names::add);

            for (String name : names) {
                JsonNode child = obj.get(name);

                if (child.isArray()) {
                    for (int i = 0; i < child.size(); i++) {
                        JsonNode arrayChild = child.get(i);
                        if (arrayChild.isObject()) {
                            nodeStack.push(arrayChild);
                        }
                    }
                } else if (child.isObject()) {
                    nodeStack.push(child);
                }
            }
        }
    }

}
