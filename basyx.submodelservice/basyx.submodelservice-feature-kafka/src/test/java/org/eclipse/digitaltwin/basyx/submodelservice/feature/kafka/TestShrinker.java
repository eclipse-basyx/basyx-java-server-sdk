/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.BlobRemovingSubmodelShrinker;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.DataPreservationLevel;
import org.junit.Assert;
import org.junit.Test;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class TestShrinker {

	@Test
	public void testRemoveBlobValue() throws SerializationException {
		runTest(DataPreservationLevel.REMOVE_BLOB_VALUE);
	}

	private void runTest(DataPreservationLevel level) throws SerializationException {
		SubmodelElement testValue = testInput();
		SubmodelElement expected = expected(level);

		BlobRemovingSubmodelShrinker shrinker = new BlobRemovingSubmodelShrinker();
		SubmodelElement elem = shrinker.shrinkSubmodelElement(testValue);
		Assert.assertEquals(expected, elem);
	}

	private SubmodelElement testInput() {
		return data(true);
	}

	private SubmodelElement expected(DataPreservationLevel level) {
		return data(level == DataPreservationLevel.RETAIN_FULL);
	}

	private SubmodelElement data(boolean withBlobValue) {
		return new DefaultSubmodelElementList.Builder()
				.value(new DefaultProperty.Builder().idShort("a").value("5").build())
				.value(new DefaultEntity.Builder().idShort("b").statements(List.of()).build())
				.value(new DefaultEntity.Builder().idShort("c")
						.statements(new DefaultProperty.Builder().idShort("d").value("7").build()).build())
				.value(new DefaultSubmodelElementCollection.Builder().idShort("e").build())
				.value(new DefaultSubmodelElementCollection.Builder().idShort("f")
						.value(new DefaultBlob.Builder().idShort("g")
								.value(withBlobValue ? new byte[] { 0, 1, 2 } : null).build())
						.value(new DefaultOperation.Builder().idShort("h")
								.inputVariables(new DefaultOperationVariable.Builder()
										.value(new DefaultProperty.Builder().idShort("i").value("77").build()).build())
								.build())
						.value(new DefaultAnnotatedRelationshipElement.Builder().idShort("j")
								.annotations(withBlobValue ? 
										new DefaultBlob.Builder().idShort("k").value( new byte[] { 3, 4, 5 }).build()
										: new DefaultBlob.Builder().idShort("k").build()) 
								.build())
						.value(new DefaultAnnotatedRelationshipElement.Builder().idShort("l").build())
						.value(new DefaultOperation.Builder().idShort("m").build())
						.value(new DefaultOperation.Builder().idShort("n")
								.outputVariables(
										new DefaultOperationVariable.Builder().value(
												withBlobValue ?
												new DefaultBlob.Builder().idShort("o").value(new byte[] { 3, 4, 5 }).build()
												: new DefaultBlob.Builder().idShort("o").build())
										.build()
								).build())
						.value(new DefaultOperation.Builder().idShort("p")
								.inoutputVariables(
										new DefaultOperationVariable.Builder().value(
												withBlobValue ?
												new DefaultBlob.Builder().idShort("q").value(new byte[] { 3, 4, 5 }).build()
												: new DefaultBlob.Builder().idShort("q").build())
										.build()
								).build())
						.build())
					.value(new DefaultSubmodelElementList.Builder().idShort("r").value(List.of()).build())
				.build();
	}

}
