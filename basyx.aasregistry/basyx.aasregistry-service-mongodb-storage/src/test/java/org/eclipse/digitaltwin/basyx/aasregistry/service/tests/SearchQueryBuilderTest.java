/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.junit.Assert.assertThrows;

import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor.AssetAdministrationShellDescriptorPathVisitor;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPathProcessor.UnknownLeafPathException;
import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.junit.Test;
import org.mockito.Mockito;

public class SearchQueryBuilderTest {

	private static final String EMPTY_STRING = "";
	private static final String STRING = "String";

	@Test
	public void testUnknownLeafPaths() {
		assertThrows(UnknownLeafPathException.class, ()-> AasRegistryPathProcessor.visitPath("unknown", null));
		assertThrows(UnknownLeafPathException.class, ()-> AasRegistryPathProcessor.visitPath(EMPTY_STRING, null));
		assertThrows(UnknownLeafPathException.class, ()-> AasRegistryPathProcessor.visitPath(".", null));
		assertThrows(UnknownLeafPathException.class, ()-> AasRegistryPathProcessor.visitPath("...", null));
		assertThrows(UnknownLeafPathException.class, ()-> AasRegistryPathProcessor.visitPath(".unknown..", null));
	}
	
	@Test
	public void testIdPath() {
		AssetAdministrationShellDescriptorPathVisitor mvisitor = Mockito.mock(AssetAdministrationShellDescriptorPathVisitor.class);
		String targetPath = AasRegistryPaths.id();
		AasRegistryPathProcessor.visitPath(targetPath, mvisitor);
		Mockito.verify(mvisitor, Mockito.times(1)).startObjectSegment(targetPath, EMPTY_STRING, EMPTY_STRING);
		Mockito.verify(mvisitor, Mockito.times(1)).visitPrimitiveSegment(targetPath, targetPath , AasRegistryPaths.SEGMENT_ID, STRING);		
		Mockito.verify(mvisitor, Mockito.times(1)).endObjectSegment(targetPath, EMPTY_STRING, EMPTY_STRING);
		Mockito.verifyNoMoreInteractions(mvisitor);
	}
	
	@Test
	public void testAdministrationRevision() {
		AssetAdministrationShellDescriptorPathVisitor mvisitor = Mockito.mock(AssetAdministrationShellDescriptorPathVisitor.class);
		String targetPath = AasRegistryPaths.administration().revision();
		String administrationPath = AasRegistryPaths.administration().toString();
		AasRegistryPathProcessor.visitPath(targetPath, mvisitor);
		Mockito.verify(mvisitor, Mockito.times(1)).startObjectSegment(targetPath, EMPTY_STRING, EMPTY_STRING);
		Mockito.verify(mvisitor, Mockito.times(1)).startObjectSegment(targetPath, administrationPath, AasRegistryPaths.SEGMENT_ADMINISTRATION);
		Mockito.verify(mvisitor, Mockito.times(1)).visitPrimitiveSegment(targetPath, targetPath, AasRegistryPaths.SEGMENT_REVISION, STRING);
		Mockito.verify(mvisitor, Mockito.times(1)).endObjectSegment(targetPath, administrationPath, AasRegistryPaths.SEGMENT_ADMINISTRATION);		
		Mockito.verify(mvisitor, Mockito.times(1)).endObjectSegment(targetPath, EMPTY_STRING, EMPTY_STRING);
		Mockito.verifyNoMoreInteractions(mvisitor);	
	}
	
	@Test
	public void testSubmodelId() {
		AssetAdministrationShellDescriptorPathVisitor mvisitor = Mockito.mock(AssetAdministrationShellDescriptorPathVisitor.class);
		String targetPath = AasRegistryPaths.submodelDescriptors().id();
		String submodelPath = AasRegistryPaths.submodelDescriptors().toString();
		AasRegistryPathProcessor.visitPath(targetPath, mvisitor);
		Mockito.verify(mvisitor, Mockito.times(1)).startObjectSegment(targetPath, EMPTY_STRING, EMPTY_STRING);
		Mockito.verify(mvisitor, Mockito.times(1)).startObjectListSegment(targetPath, submodelPath, AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS);
		Mockito.verify(mvisitor, Mockito.times(1)).visitPrimitiveSegment(targetPath, targetPath, AasRegistryPaths.SEGMENT_ID, STRING);
		Mockito.verify(mvisitor, Mockito.times(1)).endObjectListSegment(targetPath, submodelPath, AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS);		
		Mockito.verify(mvisitor, Mockito.times(1)).endObjectSegment(targetPath, EMPTY_STRING, EMPTY_STRING);
		Mockito.verifyNoMoreInteractions(mvisitor);	
	}
	
	
}
