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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

/**
 * Dummy store for holding test {@link DummyCredential}
 * 
 * @author danish
 */
public class DummyCredentialStore {
	
	public static final DummyCredential ADMIN_CREDENTIAL = new DummyCredential("john.doe", "johndoe");
	public static final DummyCredential USER_CREDENTIAL = new DummyCredential("jane.doe", "janedoe");
	public static final DummyCredential MAINTAINER_CREDENTIAL = new DummyCredential("bob.maintainer", "bobmaintainer");
	public static final DummyCredential VISITOR_CREDENTIAL = new DummyCredential("paul.visitor", "paulvisitor");
	public static final DummyCredential BASYX_READER_CREDENTIAL = new DummyCredential("basyx.reader", "basyxreader");
	public static final DummyCredential BASYX_READER_TWO_CREDENTIAL = new DummyCredential("basyx.reader.2", "basyxreader2");
	public static final DummyCredential BASYX_CREATOR_CREDENTIAL = new DummyCredential("basyx.creator", "basyxcreator");
	public static final DummyCredential BASYX_UPDATER_CREDENTIAL = new DummyCredential("basyx.updater", "basyxupdater");
	public static final DummyCredential BASYX_UPDATER_TWO_CREDENTIAL = new DummyCredential("basyx.updater.2", "basyxupdater2");
	public static final DummyCredential BASYX_ASSET_UPDATER_CREDENTIAL = new DummyCredential("basyx.asset.updater", "basyxassetupdater");
	public static final DummyCredential BASYX_ASSET_UPDATER_TWO_CREDENTIAL = new DummyCredential("basyx.asset.updater.2", "basyxassetupdater2");
	public static final DummyCredential BASYX_DELETER_CREDENTIAL = new DummyCredential("basyx.deleter", "basyxdeleter");
	public static final DummyCredential BASYX_DELETER_TWO_CREDENTIAL = new DummyCredential("basyx.deleter.2", "basyxdeleter2");

}