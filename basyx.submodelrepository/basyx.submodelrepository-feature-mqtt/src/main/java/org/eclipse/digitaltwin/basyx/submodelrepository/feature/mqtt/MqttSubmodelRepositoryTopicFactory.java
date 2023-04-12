package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import java.util.StringJoiner;

import org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt.encoding.Encoder;

public class MqttSubmodelRepositoryTopicFactory extends AbstractMqttTopicFactory {
	private static final String SUBMODELREPOSITORY = "sm-repository";
	private static final String SUBMODELS = "submodels";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	private static final String SUBMODELELEMENTS = "submodelElements";

	/**
	 * @param encoder
	 *            Used for encoding the aasId/submodelId
	 */
	public MqttSubmodelRepositoryTopicFactory(Encoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for the create event of submodels
	 * 
	 * @param repoId
	 */
	public String createCreateSubmodelTopic(String repoId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(CREATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the update event of submodels
	 * 
	 * @param repoId
	 */
	public String createUpdateSubmodelTopic(String repoId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(UPDATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the delete event of submodels
	 * 
	 * @param repoId
	 */
	public String createDeleteSubmodelTopic(String repoId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(DELETED).toString();
	}

	/**
	 * Creates the hierarchical topic for the create event of submodelElements
	 * 
	 * @param repoId
	 */
	public String createCreateSubmodelElementTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(submodelId).add(SUBMODELELEMENTS).add(submodelElementId).add(CREATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the update event of submodelElements
	 * 
	 * @param repoId
	 */
	public String createUpdateSubmodelElementTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(submodelId).add(SUBMODELELEMENTS).add(submodelElementId).add(UPDATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the delete event of submodelElements
	 * 
	 * @param repoId
	 */
	public String createDeleteSubmodelElementTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(submodelId).add(SUBMODELELEMENTS).add(submodelElementId).add(DELETED).toString();
	}
}
