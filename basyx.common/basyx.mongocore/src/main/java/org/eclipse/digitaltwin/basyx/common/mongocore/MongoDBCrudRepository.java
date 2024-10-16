package org.eclipse.digitaltwin.basyx.common.mongocore;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public class MongoDBCrudRepository<T> extends SimpleMongoRepository<T, String> {
	
	private List<AggregationOperation> allAggregations;
	private MongoTemplate template;
	private Class<T> clazz;

	public MongoDBCrudRepository(MongoEntityInformation<T, String> metadata, MongoTemplate mongoTemplate, List<AggregationOperation> allAggregations, Class<T> clazz) {
		super(metadata, mongoTemplate);
		
		this.template = mongoTemplate;
		this.allAggregations = allAggregations;
		this.clazz = clazz;
	}
	
	@Override
	public List<T> findAll() {
		
		AggregationResults<T> results = template.aggregate(Aggregation.newAggregation(allAggregations), clazz, clazz);
		
		return results.getMappedResults();
	}
 

}
