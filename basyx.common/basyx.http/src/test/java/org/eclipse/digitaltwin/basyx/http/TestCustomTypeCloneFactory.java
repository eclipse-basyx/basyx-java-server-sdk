/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.http;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.basyx.http.CustomTypeCloneFactory;
import org.eclipse.digitaltwin.basyx.http.testmodels.CloneFactoryFirstTestModel;
import org.eclipse.digitaltwin.basyx.http.testmodels.CloneFactorySecondTestModel;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link CustomTypeCloneFactory}
 * 
 * @author danish
 */
public class TestCustomTypeCloneFactory {

	@Test
	public void createCloneOfListType() {
		List<CloneFactorySecondTestModel> expectedDescriptions = Arrays.asList(new CloneFactorySecondTestModel("Java", "Programming Language"), new CloneFactorySecondTestModel("Deutsch", "Language"));

		CustomTypeCloneFactory<CloneFactoryFirstTestModel, CloneFactorySecondTestModel> cloneFactory = new CustomTypeCloneFactory<>(CloneFactorySecondTestModel.class, new ObjectMapper());

		List<CloneFactorySecondTestModel> actualDescriptions = cloneFactory.create(Arrays.asList(new CloneFactoryFirstTestModel("Java", "Programming Language"), new CloneFactoryFirstTestModel("Deutsch", "Language")));

		assertEquals(expectedDescriptions.size(), actualDescriptions.size());
		assertEquals(expectedDescriptions, actualDescriptions);
	}

	@Test
	public void createCloneOfNonListType() {
		CloneFactoryFirstTestModel expectedAssetKind = new CloneFactoryFirstTestModel("Deutsch", "Language");

		CustomTypeCloneFactory<CloneFactorySecondTestModel, CloneFactoryFirstTestModel> cloneFactory = new CustomTypeCloneFactory<>(CloneFactoryFirstTestModel.class, new ObjectMapper());

		CloneFactoryFirstTestModel actualAssetKind = cloneFactory.create(new CloneFactorySecondTestModel("Deutsch", "Language"));

		assertEquals(expectedAssetKind, actualAssetKind);
	}

}
