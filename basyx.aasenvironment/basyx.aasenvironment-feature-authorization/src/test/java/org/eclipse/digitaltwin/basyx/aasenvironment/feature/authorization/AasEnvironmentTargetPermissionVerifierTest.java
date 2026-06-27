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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.rbac.AasEnvironmentTargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AasEnvironmentTargetPermissionVerifier}
 */
public class AasEnvironmentTargetPermissionVerifierTest {

	private static final List<String> AAS_ID = Collections.singletonList("aasId");
	private static final List<String> SUBMODEL_ID = Collections.singletonList("submodelId");
	private static final List<String> CONCEPT_DESCRIPTION_ID = Collections.singletonList("conceptDescriptionId");
	private static final List<String> OTHER_CONCEPT_DESCRIPTION_ID = Collections.singletonList("otherConceptDescriptionId");

	private AasEnvironmentTargetPermissionVerifier verifier;

	@Before
	public void setUp() {
		verifier = new AasEnvironmentTargetPermissionVerifier();
	}

	@Test
	public void allowsMatchingConceptDescriptionIds() {
		RbacRule rule = createRule(new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID, CONCEPT_DESCRIPTION_ID));
		AasEnvironmentTargetInformation targetInformation = new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID, CONCEPT_DESCRIPTION_ID);

		assertTrue(verifier.isVerified(rule, targetInformation));
	}

	@Test
	public void rejectsNonMatchingConceptDescriptionIds() {
		RbacRule rule = createRule(new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID, CONCEPT_DESCRIPTION_ID));
		AasEnvironmentTargetInformation targetInformation = new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID, OTHER_CONCEPT_DESCRIPTION_ID);

		assertFalse(verifier.isVerified(rule, targetInformation));
	}

	@Test
	public void treatsLegacyRuleWithoutConceptDescriptionIdsAsWildcard() {
		RbacRule rule = createRule(new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID));
		AasEnvironmentTargetInformation targetInformation = new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID, CONCEPT_DESCRIPTION_ID);

		assertTrue(verifier.isVerified(rule, targetInformation));
	}

	@Test
	public void treatsMissingTargetConceptDescriptionIdsAsEmpty() {
		RbacRule rule = createRule(new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID, CONCEPT_DESCRIPTION_ID));
		AasEnvironmentTargetInformation targetInformation = new AasEnvironmentTargetInformation(AAS_ID, SUBMODEL_ID);

		assertTrue(verifier.isVerified(rule, targetInformation));
	}

	private RbacRule createRule(AasEnvironmentTargetInformation targetInformation) {
		return new RbacRule("role", Collections.singletonList(Action.READ), targetInformation);
	}

}
