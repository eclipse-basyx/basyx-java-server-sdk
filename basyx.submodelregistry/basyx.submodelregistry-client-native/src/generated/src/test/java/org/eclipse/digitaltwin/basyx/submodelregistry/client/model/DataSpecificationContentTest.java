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

package org.eclipse.digitaltwin.basyx.submodelregistry.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LevelType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ValueList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Model tests for DataSpecificationContent
 */
public class DataSpecificationContentTest {
    private final DataSpecificationContent model = new DataSpecificationContent();

    /**
     * Model tests for DataSpecificationContent
     */
    @Test
    public void testDataSpecificationContent() {
        // TODO: test DataSpecificationContent
    }

    /**
     * Test the property 'preferredName'
     */
    @Test
    public void preferredNameTest() {
        // TODO: test preferredName
    }

    /**
     * Test the property 'shortName'
     */
    @Test
    public void shortNameTest() {
        // TODO: test shortName
    }

    /**
     * Test the property 'unit'
     */
    @Test
    public void unitTest() {
        // TODO: test unit
    }

    /**
     * Test the property 'unitId'
     */
    @Test
    public void unitIdTest() {
        // TODO: test unitId
    }

    /**
     * Test the property 'sourceOfDefinition'
     */
    @Test
    public void sourceOfDefinitionTest() {
        // TODO: test sourceOfDefinition
    }

    /**
     * Test the property 'symbol'
     */
    @Test
    public void symbolTest() {
        // TODO: test symbol
    }

    /**
     * Test the property 'dataType'
     */
    @Test
    public void dataTypeTest() {
        // TODO: test dataType
    }

    /**
     * Test the property 'definition'
     */
    @Test
    public void definitionTest() {
        // TODO: test definition
    }

    /**
     * Test the property 'valueFormat'
     */
    @Test
    public void valueFormatTest() {
        // TODO: test valueFormat
    }

    /**
     * Test the property 'valueList'
     */
    @Test
    public void valueListTest() {
        // TODO: test valueList
    }

    /**
     * Test the property 'value'
     */
    @Test
    public void valueTest() {
        // TODO: test value
    }

    /**
     * Test the property 'levelType'
     */
    @Test
    public void levelTypeTest() {
        // TODO: test levelType
    }

    /**
     * Test the property 'modelType'
     */
    @Test
    public void modelTypeTest() {
        // TODO: test modelType
    }

}
