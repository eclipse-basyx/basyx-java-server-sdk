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
package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;

/**
 * MongoDB implementation of the SubmodelRepository
 * 
 * @author jungjan, kammognie, zhangzai, danish
 *
 */
public class MongoDBSubmodelRepository implements SubmodelRepository {
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private static final String MONGO_ID = "_id";
	private static final String TEMP_DIR_PREFIX = "basyx-temp";
	private static final String GRIDFS_ID_DELIMITER = "#";
	private static final String ID_JSON_PATH = "id";

	private MongoTemplate mongoTemplate;
	private String collectionName;
	private SubmodelServiceFactory submodelServiceFactory;
	private String smRepositoryName;

	private GridFsTemplate gridFsTemplate;

	/**
	 * Creates the MongoDBSubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and uses a
	 * collectionName and a mongoTemplate for operating MongoDB
	 * 
	 * @param mongoTemplate
	 * @param collectionName
	 * @param submodelServiceFactory
	 */
	public MongoDBSubmodelRepository(MongoTemplate mongoTemplate, String collectionName, SubmodelServiceFactory submodelServiceFactory) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
		this.submodelServiceFactory = submodelServiceFactory;
		configureIndexForSubmodelId(mongoTemplate);

		configureDefaultGridFsTemplate(this.mongoTemplate);
	}

	/**
	 * Creates the MongoDBSubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and uses a
	 * collectionName and a mongoTemplate for operating MongoDB
	 * 
	 * @param mongoTemplate
	 * @param collectionName
	 * @param submodelServiceFactory
	 * @param smRepositoryName
	 *            Name of the SubmodelRepository
	 */
	public MongoDBSubmodelRepository(MongoTemplate mongoTemplate, String collectionName, SubmodelServiceFactory submodelServiceFactory, String smRepositoryName, GridFsTemplate gridFsTemplate) {
		this(mongoTemplate, collectionName, submodelServiceFactory);
		this.smRepositoryName = smRepositoryName;
		this.gridFsTemplate = gridFsTemplate;

		if (this.gridFsTemplate == null)
			configureDefaultGridFsTemplate(mongoTemplate);

		configureIndexForSubmodelId(mongoTemplate);
	}

	/**
	 * Creates the MongoDBSubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and uses a
	 * collectionName and a mongoTemplate for operating MongoDB. Additionally
	 * initializes the MongoDB collection with a collection of submodels.
	 * 
	 * @param submodelServiceFactory
	 * @param submodels
	 */
	public MongoDBSubmodelRepository(MongoTemplate mongoTemplate, String collectionName, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels) {
		this(mongoTemplate, collectionName, submodelServiceFactory);
		initializeRemoteCollection(submodels);

		configureDefaultGridFsTemplate(this.mongoTemplate);
	}

	/**
	 * Creates the MongoDBSubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and uses a
	 * collectionName and a mongoTemplate for operating MongoDB. Additionally
	 * initializes the MongoDB collection with a collection of submodels. And
	 * configures the SubmodelRepository name.
	 * 
	 * @param mongoTemplate
	 * @param collectionName
	 * @param submodelServiceFactory
	 * @param submodels
	 * @param smRepositoryName
	 *            Name of the SubmodelRepository
	 */
	public MongoDBSubmodelRepository(MongoTemplate mongoTemplate, String collectionName, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels, String smRepositoryName, GridFsTemplate gridFsTemplate) {
		this(mongoTemplate, collectionName, submodelServiceFactory, submodels);

		this.smRepositoryName = smRepositoryName;
		this.gridFsTemplate = gridFsTemplate;

		if (this.gridFsTemplate == null)
			configureDefaultGridFsTemplate(mongoTemplate);
	}

	private void initializeRemoteCollection(Collection<Submodel> submodels) {
		if (submodels == null || submodels.isEmpty()) {
			return;
		}
		submodels.forEach(this::createSubmodel);
	}

	private void configureIndexForSubmodelId(MongoTemplate mongoTemplate) {
		Index idIndex = new Index().on(ID_JSON_PATH, Direction.ASC);
		mongoTemplate.indexOps(Submodel.class).ensureIndex(idIndex);
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		Query query = new Query();
		applySorting(query, pInfo);
		applyPagination(query, pInfo);
		List<Submodel> foundDescriptors = mongoTemplate.find(query, Submodel.class, collectionName);

		String cursor = resolveCursor(pInfo, foundDescriptors, Submodel::getId);
		return new CursorResult<List<Submodel>>(cursor, foundDescriptors);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = mongoTemplate.findOne(new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodelId)), Submodel.class, collectionName);
		if (submodel == null) {
			throw new ElementDoesNotExistException(submodelId);
		}
		return submodel;
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		Query query = new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodelId));

		throwIfSubmodelDoesNotExist(query, submodelId);
		throwIfMismatchingIds(submodelId, submodel);

		mongoTemplate.remove(query, Submodel.class, collectionName);
		mongoTemplate.save(submodel, collectionName);
	}

	private void throwIfSubmodelDoesNotExist(Query query, String submodelId) {
		if (!mongoTemplate.exists(query, Submodel.class, collectionName))
			throw new ElementDoesNotExistException(submodelId);

	}

	private void throwIfMismatchingIds(String submodelId, Submodel submodel) {
		String newSubmodelId = submodel.getId();

		if (!submodelId.equals(newSubmodelId))
			throw new IdentificationMismatchException();

	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		throwIfCollidesWithRemoteId(submodel);
		mongoTemplate.save(submodel, collectionName);
	}

	private void throwIfCollidesWithRemoteId(Submodel submodel) {
		if (mongoTemplate.exists(new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodel.getId())), Submodel.class, collectionName)) {
			throw new CollidingIdentifierException(submodel.getId());
		}
	}

	private SubmodelService getSubmodelService(String submodelId) {
		return submodelServiceFactory.create(getSubmodel(submodelId));
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElements(pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElement(submodelElementIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElementValue(submodelElementIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String submodelElementIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.setSubmodelElementValue(submodelElementIdShort, value);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		DeleteResult result = mongoTemplate.remove(new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodelId)), Submodel.class, collectionName);

		if (result.getDeletedCount() == 0) {
			throw new ElementDoesNotExistException(submodelId);
		}

	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement submodelElement) {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.createSubmodelElement(submodelElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.createSubmodelElement(idShortPath, submodelElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelService(submodelId);
		submodelService.deleteSubmodelElement(idShortPath);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return new SubmodelValueOnly(getSubmodelElements(submodelId, NO_LIMIT_PAGINATION_INFO).getResult());
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = getSubmodel(submodelId);
		submodel.setSubmodelElements(null);
		return submodel;
	}

	@Override
	public String getName() {
		return smRepositoryName == null ? SubmodelRepository.super.getName() : smRepositoryName;
	}

	private <T> String resolveCursor(PaginationInfo pRequest, List<T> foundDescriptors, Function<T, String> idResolver) {
		if (foundDescriptors.isEmpty() || !pRequest.isPaged()) {
			return null;
		}
		T last = foundDescriptors.get(foundDescriptors.size() - 1);
		return idResolver.apply(last);
	}

	private void applySorting(Query query, PaginationInfo pInfo) {
		query.with(Sort.by(Direction.ASC, MONGO_ID));
	}

	private void applyPagination(Query query, PaginationInfo pInfo) {
		if (pInfo.getCursor() != null) {
			query.addCriteria(Criteria.where(MONGO_ID).gt(pInfo.getCursor()));
		}
		if (pInfo.getLimit() != null) {
			query.limit(pInfo.getLimit());
		}
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		throw new FeatureNotSupportedException("Operation Invocation");
	}

	@Override
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) {
		Query query = new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodelId));

		throwIfSubmodelDoesNotExist(query, submodelId);

		SubmodelElement submodelElement = getSubmodelService(submodelId).getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;

		throwIfFileDoesNotExist(fileSmElement);

		String fileId = getFileId(fileSmElement.getValue());

		GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where(MONGO_ID).is(fileId)));

		InputStream fileIs = getGridFsFileAsInputStream(file);

		return createFileInTempDirectory(idShortPath, fileSmElement, fileIs);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) {
		Query query = new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodelId));

		throwIfSubmodelDoesNotExist(query, submodelId);

		SubmodelElement submodelElement = getSubmodelService(submodelId).getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;

		throwIfFileDoesNotExist(fileSmElement);

		String fileId = getFileId(fileSmElement.getValue());

		gridFsTemplate.delete(new Query(Criteria.where(MONGO_ID).is(fileId)));

		FileBlobValue fileValue = new FileBlobValue(StringUtils.EMPTY, StringUtils.EMPTY);

		setSubmodelElementValue(submodelId, idShortPath, fileValue);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) {
		Query query = new Query().addCriteria(Criteria.where(ID_JSON_PATH).is(submodelId));

		throwIfSubmodelDoesNotExist(query, submodelId);

		SubmodelElement submodelElement = getSubmodelService(submodelId).getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;

		ObjectId id = gridFsTemplate.store(inputStream, fileSmElement.getValue(), fileSmElement.getContentType());

		FileBlobValue fileValue = new FileBlobValue(fileSmElement.getContentType(), appendFsIdToFileValue(fileSmElement, id));

		setSubmodelElementValue(submodelId, idShortPath, fileValue);
	}

	private void configureDefaultGridFsTemplate(MongoTemplate mongoTemplate) {
		this.gridFsTemplate = new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
	}

	private String appendFsIdToFileValue(File fileSmElement, ObjectId id) {
		return id.toString() + GRIDFS_ID_DELIMITER + fileSmElement.getValue();
	}

	private java.io.File createFileInTempDirectory(String idShortPath, File fileSmElement, InputStream fileIs) {

		Path tempDir = createTempDirectory(TEMP_DIR_PREFIX);

		String absolutePath = tempDir.toAbsolutePath().toString();

		String filePath = getFilePath(absolutePath, idShortPath, fileSmElement.getContentType());

		java.io.File targetFile = new java.io.File(filePath);

		try {
			FileUtils.copyInputStreamToFile(fileIs, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return targetFile;
	}

	private Path createTempDirectory(String prefix) {
		Path tempDir = null;
		try {
			tempDir = Files.createTempDirectory(prefix);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempDir;
	}

	private InputStream getGridFsFileAsInputStream(GridFSFile file) {
		InputStream fileIs = null;

		try {
			fileIs = gridFsTemplate.getResource(file).getInputStream();
		} catch (IllegalStateException | IOException e1) {
			e1.printStackTrace();
		}
		return fileIs;
	}

	private String getFileId(String value) {
		return value.substring(0, value.indexOf(GRIDFS_ID_DELIMITER));
	}

	private void throwIfFileDoesNotExist(File fileSmElement) {
		if (fileSmElement.getValue().isBlank() || fileSmElement.getValue().indexOf(GRIDFS_ID_DELIMITER) == -1)
			throw new FileDoesNotExistException(fileSmElement.getIdShort());
	}

	private void throwIfSmElementIsNotAFile(SubmodelElement submodelElement) {

		if (!(submodelElement instanceof File))
			throw new ElementNotAFileException(submodelElement.getIdShort());
	}

	private String getFilePath(String tmpDirectory, String idShortPath, String contentType) {
		String fileName = idShortPath.replace("/", "-");

		String extension = getFileExtension(contentType);

		return tmpDirectory + "/" + fileName + extension;
	}

	private String getFileExtension(String contentType) {
		MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		try {
			MimeType mimeType = allTypes.forName(contentType);
			return mimeType.getExtension();
		} catch (MimeTypeException e) {
			e.printStackTrace();
			return "";
		}
	}

}
