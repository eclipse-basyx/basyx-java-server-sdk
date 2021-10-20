/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.basyx.aas.factory.json.JSONToMetamodelConverter;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates multiple {@link AASBundle} from a JSON containing several AAS and
 * Submodels <br />
 * TODO: ConceptDescriptions
 * 
 * @author espen
 *
 */
public class JSONAASBundleFactory {
	private static Logger logger = LoggerFactory.getLogger(JSONAASBundleFactory.class);

	private String content;

	/**
	 * 
	 * @param jsonContent
	 *                    the content of the JSON
	 */
	public JSONAASBundleFactory(String jsonContent) {
		this.content = jsonContent;
	}

	public JSONAASBundleFactory(Path jsonFile) throws IOException {
		content = new String(Files.readAllBytes(jsonFile));
	}

	/**
	 * Creates the set of {@link AASBundle} contained in the JSON string.
	 * 
	 * @return
	 */
	public Set<AASBundle> create() {
		JSONToMetamodelConverter converter = new JSONToMetamodelConverter(content);

		List<AssetAdministrationShell> shells = converter.parseAAS();
		List<Submodel> submodels = converter.parseSubmodels();

		List<Asset> assets = converter.parseAssets();

		Set<AASBundle> bundles = new HashSet<>();

		for (AssetAdministrationShell shell : shells) {
			// Retrieve asset
			try {
				IReference assetRef = shell.getAssetReference();
				Asset asset = getByReference(assetRef, assets);
				shell.setAsset(asset);
			} catch (ResourceNotFoundException e) {
				logger.warn("Can't find asset with id " + shell.getAssetReference().getKeys().get(0).getValue() + " for AAS " + shell.getIdShort() + "; If the asset is not provided in another way, this is an error!");
			}

			// Retrieve submodels
			Set<ISubmodel> currentSM = retrieveSubmodelsForAAS(submodels, shell);
			bundles.add(new AASBundle(shell, currentSM));
		}

		return bundles;
	}

	/**
	 * Retrieves the Submodels belonging to an AAS
	 * 
	 * @param submodels
	 * @param shell
	 * @return
	 */
	private Set<ISubmodel> retrieveSubmodelsForAAS(List<Submodel> submodels, AssetAdministrationShell shell) {
		Set<ISubmodel> currentSM = new HashSet<>();

		for (IReference submodelRef : shell.getSubmodelReferences()) {
			try {
				ISubmodel sm = getByReference(submodelRef, submodels);
				currentSM.add(sm);
				logger.debug("Found Submodel " + sm.getIdShort() + " for AAS " + shell.getIdShort());
			} catch (ResourceNotFoundException e) {
				// If there's no match, the submodel is assumed to be provided by different
				// means, e.g. it is already being hosted
				logger.warn("Could not find Submodel " + submodelRef.getKeys().get(0).getValue() + " for AAS " + shell.getIdShort() + "; If it is not hosted elsewhere this is an error!");
			}
		}
		return currentSM;
	}

	/**
	 * Retrieves an identifiable from a list of identifiable by its reference
	 * 
	 * @param submodelRef
	 * @param identifiable
	 * @return
	 * @throws ResourceNotFoundException
	 */
	private <T extends IIdentifiable> T getByReference(IReference ref, List<T> identifiable) throws ResourceNotFoundException {
		IKey lastKey = null;
		// It may be that only one key fits to the Submodel contained in the XML
		for (IKey key : ref.getKeys()) {
			lastKey = key;
			// There will only be a single submodel matching the identification at max
			Optional<T> match = identifiable.stream().filter(s -> s.getIdentification().getId().equals(key.getValue())).findFirst();
			if (match.isPresent()) {
				return match.get();
			}
		}
		if (lastKey == null) {
			throw new ResourceNotFoundException("Could not resolve reference without keys");
		} else {
			throw new ResourceNotFoundException(
					"Could not resolve reference with last key '" + lastKey.getValue() + "'");
		}

		// If no identifiable is found, indicate it by throwing an exception

	}
}
