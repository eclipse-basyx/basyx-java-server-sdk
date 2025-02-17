package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.abac.backend.mongodb;

import java.util.List;

import org.eclipse.digitaltwin.basyx.authorization.abac.AbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.abac.AccessPermissionRule;
import org.eclipse.digitaltwin.basyx.authorization.abac.Acl;
import org.eclipse.digitaltwin.basyx.authorization.abac.ObjectItem;
import org.eclipse.digitaltwin.basyx.authorization.abac.RightsEnum;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
@ConditionalOnProperty("basyx.feature.authorization.enabled")
@ConditionalOnExpression(value = "'${basyx.feature.authorization.type}' == 'abac' && ('${basyx.feature.authorization.rules.backend}' == 'MongoDB')")
public class AbacRuleRepository implements AbacStorage {

	private static final String ID = "_id";
	private static final String COLLECTION_NAME = "access-rules";

	@Autowired
	private MongoTemplate mongoTemplate;

	@PostConstruct
	public void createIndexes() {
		mongoTemplate.getCollection(COLLECTION_NAME).createIndex(new org.bson.Document("acl.rights", 1));
		mongoTemplate.getCollection(COLLECTION_NAME).createIndex(new org.bson.Document("acl.access", 1));
		mongoTemplate.getCollection(COLLECTION_NAME).createIndex(new org.bson.Document("objects.identifiable", 1));
	}

	@Override
	public List<AccessPermissionRule> getAbacRules() {
		return mongoTemplate.findAll(AccessPermissionRule.class, COLLECTION_NAME);
	}

	@Override
	public List<AccessPermissionRule> getFilteredAbacRules(RightsEnum right, Acl.Access access, ObjectItem objectItem) {

		Criteria aclCriteria = new Criteria().andOperator(Criteria.where("acl.rights").in(right), Criteria.where("acl.access").is(access));

		Criteria objectCriteria = new Criteria().orOperator(Criteria.where("objects.identifiable").is("(AAS)*"), Criteria.where("objects.identifiable").is(objectItem.getIdentifiable()));

		Query query = new Query(new Criteria().andOperator(aclCriteria, objectCriteria));

		return mongoTemplate.find(query, AccessPermissionRule.class, COLLECTION_NAME);
	}

	@Override
	public void addRule(AccessPermissionRule abacRule) {
		try {
			mongoTemplate.insert(abacRule, COLLECTION_NAME);
		} catch (org.springframework.dao.DuplicateKeyException ex) {
			throw new CollidingIdentifierException("The ABAC rule already exists!");
		}

	}

	@Override
	public void removeRule(String ruleId) {
		Query query = Query.query(Criteria.where(ID).is(ruleId));
		if (mongoTemplate.remove(query, AccessPermissionRule.class, COLLECTION_NAME).getDeletedCount() == 0) {
			throw new ElementDoesNotExistException("ABAC Rule with id: " + ruleId + " doesn't exist.");
		}
	}

	@Override
	public boolean exists(String ruleId) {
		Query query = new Query(Criteria.where(ID).is(ruleId));

		return mongoTemplate.exists(query, AccessPermissionRule.class, COLLECTION_NAME);
	}

	@Override
	public AccessPermissionRule getRule(String id) {
		return mongoTemplate.findById(id, AccessPermissionRule.class, COLLECTION_NAME);

	}

	@Override
	public void update(String ruleId, AccessPermissionRule abacRule) {
		Query query = Query.query(Criteria.where(ID).is(ruleId));
		AccessPermissionRule replaced = mongoTemplate.findAndReplace(query, abacRule, COLLECTION_NAME);
		if (replaced == null) {
			throw new ElementDoesNotExistException("ABAC Rule with id: " + ruleId + " doesn't exist.");
		}
	}

}
