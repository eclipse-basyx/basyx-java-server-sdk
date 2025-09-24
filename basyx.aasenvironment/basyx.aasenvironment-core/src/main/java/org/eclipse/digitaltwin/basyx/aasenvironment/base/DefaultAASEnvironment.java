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
package org.eclipse.digitaltwin.basyx.aasenvironment.base;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.InMemoryFile;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.ConceptDescriptionIdCollector;
import org.eclipse.digitaltwin.basyx.aasenvironment.FileElementPathCollector;
import org.eclipse.digitaltwin.basyx.aasenvironment.IdShortPathBuilder;
import org.eclipse.digitaltwin.basyx.aasenvironment.MetamodelCloneCreator;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.IdentifiableUploader;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.IdentifiableAssertion;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.IdentifiableUploader.DelegatingIdentifiableRepository;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.IdentifiableUploader.IdentifiableRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

/**
 * Default implementation of {@link AasEnvironment}
 *
 * @author zhangzai, danish, fried
 */
public class DefaultAASEnvironment implements AasEnvironment {

	private static Logger logger = LoggerFactory.getLogger(DefaultAASEnvironment.class);

	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private ConceptDescriptionRepository conceptDescriptionRepository;
	private JsonSerializer jsonSerializer = new JsonSerializer();
	private XmlSerializer xmlSerializer = new XmlSerializer();
	private AASXSerializer aasxSerializer = new AASXSerializer();
	private MetamodelCloneCreator cloneCreator = new MetamodelCloneCreator();
	private IdentifiableAssertion checker;
	private static String aasxFilePathPrefix = "/aasx/files/";

	public DefaultAASEnvironment(AasRepository aasRepository, SubmodelRepository submodelRepository, ConceptDescriptionRepository conceptDescriptionRepository) {
		this.aasRepository = aasRepository;
		this.submodelRepository = submodelRepository;
		this.conceptDescriptionRepository = conceptDescriptionRepository;
		this.checker = new IdentifiableAssertion(aasRepository, submodelRepository);
	}

	@Override
	public String createJSONAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds, @Valid boolean includeConceptDescriptions) throws SerializationException {
		Environment aasEnvironment = createEnvironment(aasIds, submodelIds, includeConceptDescriptions);
		return jsonSerializer.write(aasEnvironment);
	}

	@Override
	public String createXMLAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds, @Valid boolean includeConceptDescriptions) throws SerializationException {
		Environment aasEnvironment = createEnvironment(aasIds, submodelIds, includeConceptDescriptions);

		return xmlSerializer.write(aasEnvironment);
	}

	@Override
	public byte[] createAASXAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds, @Valid boolean includeConceptDescriptions) throws SerializationException, IOException {
		Environment aasEnvironment = createEnvironment(aasIds, submodelIds, includeConceptDescriptions);

		List<InMemoryFile> relatedFiles = new ArrayList<>();
		HashMap<String, List<File>> fileSubmodelElements = new HashMap<>();

		aasEnvironment.getSubmodels().forEach(sm-> fileSubmodelElements.put(sm.getId(),getAllFileSubmodelElements(sm)));

		addFilesToRelatedFiles(fileSubmodelElements, relatedFiles);
		aasEnvironment.getAssetAdministrationShells().forEach(aas->addThumbnailToRelatedFiles(aas.getId(),aas.getAssetInformation().getDefaultThumbnail(),relatedFiles));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		aasxSerializer.write(aasEnvironment, relatedFiles, outputStream);
		return outputStream.toByteArray();
	}

	public List<File> getAllFileSubmodelElements(Submodel submodel) {
		List<File> files = new ArrayList<>();
		Stack<SubmodelElement> stack = new Stack<>();

		stack.addAll(submodel.getSubmodelElements());

		while (!stack.isEmpty()) {
			SubmodelElement submodelElement = stack.pop();

			if (submodelElement instanceof File file) {
				if(!isURL(file.getValue()))
					files.add(file);
			} else if (submodelElement instanceof SubmodelElementCollection collection) {
				stack.addAll(collection.getValue());
			} else if (submodelElement instanceof SubmodelElementList list) {
				stack.addAll(list.getValue());
			}
		}

		return files;
	}

	public void loadEnvironment(CompleteEnvironment completeEnvironment, boolean ignoreDuplicates) {
		Environment environment = completeEnvironment.getEnvironment();
		List<InMemoryFile> relatedFiles = completeEnvironment.getRelatedFiles();

		if (environment == null)
			return;

		if(!ignoreDuplicates){
			checker.assertNoDuplicateIds(environment);
		}

		createShellsOnRepositoryFromEnvironment(environment, relatedFiles);
		createSubmodelsOnRepositoryFromEnvironment(environment, completeEnvironment.getRelatedFiles());
		createConceptDescriptionsOnRepositoryFromEnvironment(environment);
	}

	@Override
	public void loadEnvironment(CompleteEnvironment completeEnvironment) {
		loadEnvironment(completeEnvironment, false);
	}

	private void createConceptDescriptionsOnRepositoryFromEnvironment(Environment environment) {
		IdentifiableRepository<ConceptDescription> repo = new DelegatingIdentifiableRepository<ConceptDescription>(conceptDescriptionRepository::getConceptDescription, conceptDescriptionRepository::updateConceptDescription,
				conceptDescriptionRepository::createConceptDescription);
		IdentifiableUploader<ConceptDescription> uploader = new IdentifiableUploader<ConceptDescription>(repo);
		for (ConceptDescription conceptDescription : environment.getConceptDescriptions()) {
			boolean success = uploader.upload(conceptDescription);
			logSuccessConceptDescription(conceptDescription.getId(), success);
		}
	}

	private void createSubmodelsOnRepositoryFromEnvironment(Environment environment, List<InMemoryFile> relatedFiles) {
		List<Submodel> submodels = environment.getSubmodels();

		createSubmodelsOnRepository(submodels);

		if (relatedFiles == null || relatedFiles.isEmpty())
			return;

		for (Submodel submodel : submodels) {
			List<List<SubmodelElement>> idShortElementPathsOfAllFileSMEs = new FileElementPathCollector(submodel).collect();

			idShortElementPathsOfAllFileSMEs.stream().forEach(fileSMEIdShortPath -> setFileToFileElement(submodel.getId(), fileSMEIdShortPath, relatedFiles));
		}
	}

	private void setFileToFileElement(String submodelId, List<SubmodelElement> fileSMEIdShortPathElements, List<InMemoryFile> relatedFiles) {
		String fileSMEIdShortPath = new IdShortPathBuilder(new ArrayList<>(fileSMEIdShortPathElements)).build();

		org.eclipse.digitaltwin.aas4j.v3.model.File fileSME = (org.eclipse.digitaltwin.aas4j.v3.model.File) submodelRepository.getSubmodelElement(submodelId, fileSMEIdShortPath);

		InMemoryFile inMemoryFile = getAssociatedInMemoryFile(relatedFiles, fileSME.getValue());

		if (inMemoryFile == null) {
			logger.info("Unable to set file to the SubmodelElement File with IdShortPath '{}' because it does not exist in the AASX file.", fileSMEIdShortPath);

			return;
		}

		String contentType = (fileSME.getContentType() != null && !fileSME.getContentType().trim().isEmpty()) ? fileSME.getContentType() : null;
		submodelRepository.setFileValue(submodelId, fileSMEIdShortPath, getFileName(inMemoryFile.getPath()), contentType, new ByteArrayInputStream(inMemoryFile.getFileContent()));
	}

	private String getFileName(String path) {
		return FilenameUtils.getName(path);
	}

	private InMemoryFile getAssociatedInMemoryFile(List<InMemoryFile> relatedFiles, String value) {

		Optional<InMemoryFile> inMemoryFile = relatedFiles.stream().filter(file -> file.getPath().equals(value)).findAny();

		if (inMemoryFile.isEmpty())
			return null;

		return inMemoryFile.get();
	}

	private void createShellsOnRepositoryFromEnvironment(Environment environment, List<InMemoryFile> relatedFiles) {
		IdentifiableRepository<AssetAdministrationShell> repo = new DelegatingIdentifiableRepository<AssetAdministrationShell>(aasRepository::getAas, aasRepository::updateAas, aasRepository::createAas);
		IdentifiableUploader<AssetAdministrationShell> uploader = new IdentifiableUploader<>(repo);
		for (AssetAdministrationShell shell : environment.getAssetAdministrationShells()) {
			boolean success = uploader.upload(shell);
			setThumbnail(shell.getId(), relatedFiles);
			logSuccess("shell", shell.getId(), success);
		}
	}

	private void setThumbnail(String shellId, List<InMemoryFile> relatedFiles) {
		if (relatedFiles == null || relatedFiles.isEmpty())
			return;

		Resource thumbnailResource = aasRepository.getAssetInformation(shellId).getDefaultThumbnail();

		if (thumbnailResource == null || !isValidPath(thumbnailResource.getPath()) || !isValidContentType(thumbnailResource.getContentType())) {
			logger.info("Could not find thumbnail resource for aas {}", shellId);
			return;
		}

		String thumbnailPath = thumbnailResource.getPath();
		String thumbnailContentType = thumbnailResource.getContentType();

		Optional<InMemoryFile> optionalInMemoryFile = relatedFiles.stream().filter(file -> file.getPath().equals(thumbnailPath)).findAny();

		if (optionalInMemoryFile.isEmpty()) {
			logger.info("Thumbnail file specified at path {} for aas {} could not be found.", thumbnailPath, shellId);
			return;
		}

		byte[] thumbnailContent = optionalInMemoryFile.get().getFileContent();

		if (thumbnailContent.length == 0) {
			logger.info("Thumbnail content for aas {} is empty.", thumbnailPath);
			return;
		}

		aasRepository.setThumbnail(shellId, getFileName(thumbnailPath), thumbnailContentType, new ByteArrayInputStream(thumbnailContent));
	}

	private boolean isValidContentType(String contentType) {
		return contentType != null && !contentType.isBlank();
	}

	private boolean isValidPath(String path) {
		return path != null && !path.isBlank();
	}

	private void createSubmodelsOnRepository(List<Submodel> submodels) {
		IdentifiableRepository<Submodel> repo = new DelegatingIdentifiableRepository<Submodel>(submodelRepository::getSubmodel, submodelRepository::updateSubmodel, submodelRepository::createSubmodel);
		IdentifiableUploader<Submodel> uploader = new IdentifiableUploader<>(repo);
		for (Submodel submodel : submodels) {
			boolean success = uploader.upload(submodel);
			logSuccess("submodel", submodel.getId(), success);
		}
	}

	private void logSuccess(String resourceName, String id, boolean success) {
		if (success) {
			logger.info("Uploading " + resourceName + " " + id + " was successful!");
		} else {
			logger.warn("Uploading " + resourceName + " " + id + " was not successful!");
		}
	}

	private void logSuccessConceptDescription(String conceptDescriptionId, boolean success) {
		if (!success) {
			logger.warn("Colliding Ids detected for ConceptDescription: " + conceptDescriptionId + ". If they are not identical, this is an error. Please note that the already existing ConceptDescription was not updated.");
		} else {
			logSuccess("conceptDescription", conceptDescriptionId, success);
		}
	}

	private Environment createEnvironment(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) {
		List<AssetAdministrationShell> shells = aasIds.stream().map(aasRepository::getAas).collect(Collectors.toList());
		List<Submodel> submodels = submodelIds.stream().map(submodelRepository::getSubmodel).collect(Collectors.toList());

		Environment aasEnvironment = new DefaultEnvironment();
		aasEnvironment.setAssetAdministrationShells(cloneCreator.cloneAssetAdministrationShells(shells));
		aasEnvironment.setSubmodels(cloneCreator.cloneSubmodels(submodels));

		if (includeConceptDescriptions) {
			includeConceptDescriptions(aasEnvironment);
		}

		return aasEnvironment;
	}

	private void includeConceptDescriptions(Environment aasEnvironment) {
		List<ConceptDescription> conceptDescriptions = cloneCreator.cloneConceptDescriptions(getConceptDescriptions(aasEnvironment));
		aasEnvironment.setConceptDescriptions(conceptDescriptions);
	}

	private List<ConceptDescription> getConceptDescriptions(Environment env) {
		if (conceptDescriptionRepository == null) {
			throw new NullPointerException("The parameter includeConceptDescriptions is set to true but ConceptDescriptionRepository is null");
		}

		Set<String> cdIds = new ConceptDescriptionIdCollector(env).collect();

		return cdIds.stream().map(this::fetchConceptDescriptionFromRepo).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private ConceptDescription fetchConceptDescriptionFromRepo(String conceptDescriptionId) {
		try {
			return conceptDescriptionRepository.getConceptDescription(conceptDescriptionId);
		} catch (ElementDoesNotExistException e) {
			logger.error("Concept description with id {} could not be found in the repository", conceptDescriptionId);
			return null;
		}
	}

	private void addFilesToRelatedFiles(HashMap<String, List<File>> submodelFileSMEMap, List<InMemoryFile> relatedFiles) {
		submodelFileSMEMap.forEach((submodelId, fileSubmodelElementList) -> fileSubmodelElementList.forEach(file -> {
			try {
				if (isFileAlreadyAdded(file.getValue())) {
					return;
				}
				processFile(submodelId, file, relatedFiles);
			} catch (IOException | NullPointerException | FileDoesNotExistException e) {
				handleFileError(file);
			}
		}));
	}

	private void processFile(String key, File file, List<InMemoryFile> relatedFiles) throws IOException {
		byte[] fileContent = getFileContent(key, file);
		String path = getFilePathInAASX(file.getValue());
		relatedFiles.add(new InMemoryFile(fileContent, path));
		file.setValue(path);
	}

	private void handleFileError(File file) {
		logger.error("File {} does not exist in the repository", file.getValue());
	}


	private byte[] getFileContent(String submodelId, File file) throws IOException {
		return submodelRepository.getFileByFilePath(submodelId, file.getValue()).readAllBytes();
	}

	private boolean isFileAlreadyAdded(String filePath){
		return filePath.startsWith(aasxFilePathPrefix);
	}

	private void addThumbnailToRelatedFiles(String aasId, Resource thumbnail, List<InMemoryFile> relatedFiles) {
		if(!isThumbnailSet(thumbnail)) {
			logger.info("No thumbnail specified for aas {}", aasId);
		}else {
			try {
				String newPath = getThumbnailPathInAASX(thumbnail.getPath());
				relatedFiles.add(
						new InMemoryFile(
								Files.readAllBytes(aasRepository.getThumbnail(aasId).toPath()),
								newPath
						)
				);
				thumbnail.setPath(newPath);
			} catch (IOException | FileDoesNotExistException e) {
				logger.error("Thumbnail file {} does not exist in the repository", thumbnail.getPath());
			}
		}
	}

	private static boolean isThumbnailSet(Resource thumbnail) {
		return thumbnail != null && thumbnail.getPath() != null;
	}

	private static String getThumbnailPathInAASX(String path) {
		try {
			return "/" + getHashedFilePath(path) + "." + getFileExtensionFromPath(path);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not hash thumbnail path {}", path);
			throw new RuntimeException(e);
		}
	}

	private static String getFilePathInAASX(String path) {
		try {
			return aasxFilePathPrefix + getHashedFilePath(path) + "." + getFileExtensionFromPath(path);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not hash file path {}", path);
			throw new RuntimeException(e);
		}
	}

	private static String getFileExtensionFromPath(String file) {
		String[] split = file.split("\\.");
		return split[split.length - 1];
	}

	private static String getHashedFilePath(String filePath) throws NoSuchAlgorithmException {
		int hashCode = filePath.hashCode();
		return Integer.toHexString(hashCode);
	}

	private static boolean isURL(String path) {
		if(path == null || path.isEmpty()) {
			return false;
		}
		return path.startsWith("http");
	}
}
