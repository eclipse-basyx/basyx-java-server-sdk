/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;

/**
 * Decorator for authorized {@link AasEnvironment}
 *
 * @author danish
 *
 */
public class AuthorizedAasEnvironment implements AasEnvironment {

	private AasEnvironment decorated;
	private RbacPermissionResolver<AasEnvironmentTargetInformation> permissionResolver;
	
	public AuthorizedAasEnvironment(AasEnvironment decorated, RbacPermissionResolver<AasEnvironmentTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public String createJSONAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws SerializationException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new AasEnvironmentTargetInformation(aasIds, submodelIds));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.createJSONAASEnvironmentSerialization(aasIds, submodelIds, includeConceptDescriptions);
	}

	@Override
	public String createXMLAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws SerializationException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new AasEnvironmentTargetInformation(aasIds, submodelIds));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.createXMLAASEnvironmentSerialization(aasIds, submodelIds, includeConceptDescriptions);
	}

	@Override
	public byte[] createAASXAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws SerializationException, IOException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new AasEnvironmentTargetInformation(aasIds, submodelIds));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.createAASXAASEnvironmentSerialization(aasIds, submodelIds, includeConceptDescriptions);
	}

	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}

	@Override
	public void loadEnvironment(CompleteEnvironment completeEnvironment, boolean ignoreDuplicates) {
		Environment environment = completeEnvironment.getEnvironment();
		
		boolean isAuthorized = permissionResolver.hasPermission(Action.CREATE, new AasEnvironmentTargetInformation(getAasIds(environment.getAssetAdministrationShells()), getSubmodelIds(environment.getSubmodels())));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.loadEnvironment(completeEnvironment, ignoreDuplicates);
	}

	@Override
	public void loadEnvironment(CompleteEnvironment completeEnvironment) {
		loadEnvironment(completeEnvironment, false);
	}

	private List<String> getSubmodelIds(List<Submodel> submodels) {
		
		return submodels.stream().map(Identifiable::getId).collect(Collectors.toList());
	}

	private List<String> getAasIds(List<AssetAdministrationShell> assetAdministrationShells) {
		
		return assetAdministrationShells.stream().map(Identifiable::getId).collect(Collectors.toList());
	}

}
