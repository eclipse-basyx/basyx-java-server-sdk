/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.junit.Test;

public class AuthorizedAasEnvironmentTest {

	private static final String CONCEPT_DESCRIPTION_ID = "conceptDescriptionId";
	private static final String ALL_ALLOWED_WILDCARD = "*";

	@Test
	public void loadEnvironmentWithNullSectionsChecksEmptyTargetIds() {
		RecordingAasEnvironment decorated = new RecordingAasEnvironment();
		CapturingPermissionResolver permissionResolver = new CapturingPermissionResolver();
		AuthorizedAasEnvironment authorizedAasEnvironment = new AuthorizedAasEnvironment(decorated, permissionResolver);

		authorizedAasEnvironment.loadEnvironment(new CompleteEnvironment(new DefaultEnvironment(), null));

		assertEquals(Action.CREATE, permissionResolver.action);
		assertEquals(Collections.emptyList(), permissionResolver.targetInformation.getAasIds());
		assertEquals(Collections.emptyList(), permissionResolver.targetInformation.getSubmodelIds());
		assertEquals(Collections.emptyList(), permissionResolver.targetInformation.getConceptDescriptionIds());
		assertTrue(decorated.loadEnvironmentCalled);
	}

	@Test
	public void loadEnvironmentChecksConceptDescriptionTargetIds() {
		RecordingAasEnvironment decorated = new RecordingAasEnvironment();
		CapturingPermissionResolver permissionResolver = new CapturingPermissionResolver();
		AuthorizedAasEnvironment authorizedAasEnvironment = new AuthorizedAasEnvironment(decorated, permissionResolver);
		Environment environment = new DefaultEnvironment.Builder()
				.conceptDescriptions(List.of(new DefaultConceptDescription.Builder().id(CONCEPT_DESCRIPTION_ID).build()))
				.build();

		authorizedAasEnvironment.loadEnvironment(new CompleteEnvironment(environment, null));

		assertEquals(List.of(CONCEPT_DESCRIPTION_ID), permissionResolver.targetInformation.getConceptDescriptionIds());
		assertTrue(decorated.loadEnvironmentCalled);
	}

	@Test
	public void serializationWithConceptDescriptionsChecksWildcardConceptDescriptionTarget() throws SerializationException {
		RecordingAasEnvironment decorated = new RecordingAasEnvironment();
		CapturingPermissionResolver permissionResolver = new CapturingPermissionResolver();
		AuthorizedAasEnvironment authorizedAasEnvironment = new AuthorizedAasEnvironment(decorated, permissionResolver);

		authorizedAasEnvironment.createJSONAASEnvironmentSerialization(List.of("aasId"), List.of("submodelId"), true);

		assertEquals(Action.READ, permissionResolver.action);
		assertEquals(List.of(ALL_ALLOWED_WILDCARD), permissionResolver.targetInformation.getConceptDescriptionIds());
	}

	private static class CapturingPermissionResolver implements RbacPermissionResolver<AasEnvironmentTargetInformation> {
		private Action action;
		private AasEnvironmentTargetInformation targetInformation;

		@Override
		public boolean hasPermission(Action action, AasEnvironmentTargetInformation targetInformation) {
			this.action = action;
			this.targetInformation = targetInformation;

			return true;
		}

		@Override
		public List<TargetInformation> getMatchingTargetInformationInRules(Action action, AasEnvironmentTargetInformation targetInformation) {
			return Collections.emptyList();
		}
	}

	private static class RecordingAasEnvironment implements AasEnvironment {
		private boolean loadEnvironmentCalled;

		@Override
		public String createJSONAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) {
			return "{}";
		}

		@Override
		public String createXMLAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) {
			return "";
		}

		@Override
		public byte[] createAASXAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws IOException {
			return new byte[0];
		}

		@Override
		public void loadEnvironment(CompleteEnvironment completeEnvironment, boolean ignoreDuplicates) {
			loadEnvironmentCalled = true;
		}

		@Override
		public void loadEnvironment(CompleteEnvironment completeEnvironment) {
			loadEnvironmentCalled = true;
		}
	}

}
