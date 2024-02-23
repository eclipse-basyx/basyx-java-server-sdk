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

package org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
/**
 * Updates identifiables on server-side based on version and revision 
 *
 * @author Gerhard Sonnenberg DFKI GmbH
 *
 */
public class IdentifiableUploader<T extends Identifiable> {

	private final IdentifiableRepository<T> repo;

	public IdentifiableUploader(IdentifiableRepository<T> repo) {
		this.repo = repo;
	}

	public boolean upload(T toUpload) {
		Optional<T> currentOpt = getDeployedById(toUpload);
		if (currentOpt.isEmpty()) {
			return create(toUpload);
		}
		T current = currentOpt.get();
		if (shouldUpdate(current, toUpload)) {
			return update(toUpload);
		}
		return false;
	}	
	
	private boolean shouldUpdate(T current, T toUpload) {
		if (isVersionOrRevisionPresent(current)) {
			if (isVersionOrRevisionPresent(toUpload)) {
				// both have version and revision
				// => do not update
				return !hasEqualVersionAndRevision(current.getAdministration(), toUpload.getAdministration());
			} else {
				// version or revision was removed
				return true;
			}
		} else {
			// only update if version info was added
			return isVersionOrRevisionPresent(toUpload);
		}
	}

	private Optional<T> getDeployedById(T toUpload) {
		String id = toUpload.getId();
		if (id == null) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(repo.getById(id));
		}catch (ElementDoesNotExistException e) {
			return Optional.empty();
		}
	}
	
	private boolean create(T toUpload) {
		try {
			repo.create(toUpload);
			return true;
		} catch (CollidingIdentifierException ex) {
			return false;
		}
	}
	
	private boolean update(T toUpload) {
		try {
			repo.update(toUpload);
			return true;
		} catch (ElementDoesNotExistException ex) {
			return false;
		}
	}

	private boolean isVersionOrRevisionPresent(Identifiable toUpdate) {
		AdministrativeInformation info = toUpdate.getAdministration();
		if (info == null) {
			return false;
		}
		String version = info.getVersion();
		String revision = info.getRevision();
		return version != null || revision != null;
	}

	private static boolean hasEqualVersionAndRevision(AdministrativeInformation toUpdateInfo, AdministrativeInformation currentInfo) {
		return Objects.equals(toUpdateInfo.getRevision(), currentInfo.getRevision()) && Objects.equals(toUpdateInfo.getVersion(), currentInfo.getVersion());
	}

	public static interface IdentifiableRepository<T extends Identifiable> {

		T getById(String id) throws ElementDoesNotExistException;

		void update(T identifiable) throws ElementDoesNotExistException;

		void create(T identifiable) throws CollidingIdentifierException;

	}

	public static class DelegatingIdentifiableRepository<T extends Identifiable> implements IdentifiableRepository<T> {

		private final Function<String, T> getFunction;
		private final BiConsumer<String, T> updateConsumer;
		private final Consumer<T> createConsumer;

		public DelegatingIdentifiableRepository(Function<String, T> getFunction, BiConsumer<String, T> updateConsumer, Consumer<T> createConsumer) {
			this.getFunction = getFunction;
			this.updateConsumer = updateConsumer;
			this.createConsumer = createConsumer;
		}

		@Override
		public T getById(String id) throws ElementDoesNotExistException {
			return getFunction.apply(id);
		}

		@Override
		public void update(T identifiable) throws ElementDoesNotExistException {
			updateConsumer.accept(identifiable.getId(), identifiable);
		}

		@Override
		public void create(T identifiable) throws CollidingIdentifierException {
			createConsumer.accept(identifiable);
		}

	}
}