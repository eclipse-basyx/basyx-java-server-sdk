/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.support.bundle;

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class can be used to check if all required resources are present on a server<br>
 * (e.g. after a restart) and upload them if necessary.
 * 
 * @author conradi
 *
 */
public class AASBundleHelper {
	
	private static Logger logger = LoggerFactory.getLogger(AASBundleHelper.class);

	/**
	 * Checks (by ID) if all AASs/SMs contained<br>
	 * in the given AASBundles exist in the AASAggregator.<br>
	 * Adds missing ones to the Aggregator.<br>
	 * If a given object already exists in the Aggregator it will NOT be replaced.
	 * 
	 * @param aggregator the Aggregator to be populated
	 * @param bundles the AASBundles
	 * @return true if an AAS/SM was uploaded; false otherwise
	 */
	public static boolean integrate(IAASAggregator aggregator, Collection<AASBundle> bundles) {
		
		if(aggregator == null || bundles == null) {
			throw new RuntimeException("'aggregator' and 'bundles' must not be null.");
		}
		
		boolean objectUploaded = false;
		
		for(AASBundle bundle: bundles) {
			IAssetAdministrationShell aas = bundle.getAAS();
			
			try {				
				aggregator.getAAS(aas.getIdentification());
				// If no ResourceNotFoundException occurs, AAS exists on server
				// -> no further action required
			} catch(ResourceNotFoundException e) {
				// AAS does not exist and needs to be pushed to the server
				// Cast Interface to concrete class
				if(aas instanceof AssetAdministrationShell) {
					aggregator.createAAS((AssetAdministrationShell) aas);
					objectUploaded = true;
				} else {
					throw new RuntimeException("aas Objects in bundles need to be instance of 'AssetAdministrationShell'");
				}
			}
			
			IModelProvider provider = aggregator.getAASProvider(aas.getIdentification());
			for (ISubmodel sm : bundle.getSubmodels()) {
				try {
					provider.getValue("/aas/submodels/" + sm.getIdShort() + "/" + SubmodelProvider.SUBMODEL);
					// If no ResourceNotFoundException occurs, SM exists on server
					// -> no further action required
				} catch (ResourceNotFoundException e) {
					// AAS does not exist and needs to be pushed to the server
					// Check if ISubmodel is a concrete Submodel
					if (sm instanceof Submodel) {
						provider.setValue("/aas/submodels/" + sm.getIdShort(), sm);
						objectUploaded = true;
					} else {
						throw new RuntimeException("sm Objects in bundles need to be instance of 'Submodel'");
					}
				}
			}
		}
		return objectUploaded;
	}
	
	/**
	 * Registers a given set of bundles with the registry
	 * 
	 * @param registry
	 *            the registry to register with
	 * @param bundles
	 *            the bundles to register
	 * @param aasAggregatorPath
	 *            the aggregator path, e.g. <i>http://localhost:4000/shells</i>
	 */
	public static void register(IAASRegistry registry, Collection<AASBundle> bundles, String aasAggregatorPath) {
		bundles.stream().map(b -> AASBundleDescriptorFactory.createAASDescriptor(b, aasAggregatorPath)).forEach(registry::register);
	}
	
	/**
	 * Deregisters a given set of bundles from a given registry
	 * 
	 * @param registry the registry to deregister from
	 * @param bundles the AASBundles to be deregistred
	 */
	public static void deregister(IAASRegistry registry, Collection<AASBundle> bundles) {
		if(registry != null && bundles != null) {
			for(AASBundle bundle: bundles) {
				IAssetAdministrationShell aas = bundle.getAAS();
				
				try {
					registry.delete(aas.getIdentification());
				} catch (ProviderException e) {
					logger.info("The AAS '" + aas.getIdShort() + "' can't be deregistered. It was not found in registry.");
					// Just continue if deregistration failed
				}
				
				for(ISubmodel sm: bundle.getSubmodels()) {
					try {
						registry.delete(sm.getIdentification());
					} catch (ProviderException e) {
						logger.info("The SM '" + sm.getIdShort() + "' can't be deregistered. It was not found in registry.");
						// Just continue if deregistration failed
					}
				}
			}
		}
	}
}
