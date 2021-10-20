/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.aasx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.eclipse.basyx.components.configuration.BaSyxConfiguration;
import org.eclipse.basyx.components.xml.XMLAASBundleFactory;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The AASX package converter converts a aasx package into a list of aas, a list
 * of submodels a list of assets, a list of Concept descriptions
 * 
 * The aas provides the references to the submodels and assets
 * 
 * @author zhangzai, conradi
 *
 */
public class AASXPackageManager {


	private static final String XML_TYPE = "http://www.admin-shell.io/aasx/relationships/aas-spec";
	private static final String AASX_ORIGIN = "/aasx/aasx-origin";
	
	
	/**
	 * Path to the AASX package
	 */
	private String aasxPath;

	/**
	 * AAS bundle factory
	 */
	private XMLAASBundleFactory bundleFactory;
	
	/**
	 * Cache for generated Bundles
	 */
	private Set<AASBundle> bundles;

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(AASXPackageManager.class);

	/**
	 * Constructor
	 */
	public AASXPackageManager(String path) {
		aasxPath = path;
	}

	public Set<AASBundle> retrieveAASBundles() throws IOException, ParserConfigurationException, SAXException, InvalidFormatException {
		
		// If the XML was already parsed return cached Bundles
		if(bundles != null) {
			return bundles;
		}
		
		OPCPackage aasxRoot = OPCPackage.open(getInputStream(aasxPath));
		
		bundleFactory = new XMLAASBundleFactory(getXMLResourceString(aasxRoot));
		
		bundles = bundleFactory.create();
		
		return bundles;
	}

	/**
	 * Return the Content of the xml file in the aasx-package as String
	 * 
	 * @param aasxPackage - the root package of the AASX
	 * @return Content of XML as String
	 * @throws InvalidFormatException 
	 * @throws IOException
	 */
	private String getXMLResourceString(OPCPackage aasxPackage) throws InvalidFormatException, IOException {

		// Get the "/aasx/aasx-origin" Part. It is Relationship source for the XML-Document
		PackagePart originPart = aasxPackage.getPart(PackagingURIHelper.createPartName(AASX_ORIGIN));
		
		// Get the Relation to the XML Document
		PackageRelationshipCollection originRelationships = originPart.getRelationshipsByType(XML_TYPE);
		
		
		// If there is more than one or no XML-Document that is an error
		if(originRelationships.size() > 1) {
			throw new RuntimeException("More than one 'aasx-spec' document found in .aasx");
		} else if(originRelationships.size() == 0) {
			throw new RuntimeException("No 'aasx-spec' document found in .aasx");
		}
		
		// Get the PackagePart of the XML-Document
		PackagePart xmlPart = originPart.getRelatedPart(originRelationships.getRelationship(0));
		
		// Read the content from the PackagePart
		InputStream stream = xmlPart.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
		return writer.toString();
	}

	/**
	 * Load the referenced filepaths in the submodels such as PDF, PNG files from
	 * the package
	 * 
	 * @return a map of the folder name and folder path, the folder holds the files
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws InvalidFormatException 
	 * 
	 */
	private List<String> parseReferencedFilePathsFromAASX()
			throws IOException, ParserConfigurationException, SAXException, InvalidFormatException {
		
		Set<AASBundle> bundles = retrieveAASBundles();
		
		List<ISubmodel> submodels = new ArrayList<>();
		
		// Get the Submodels from all AASBundles
		for(AASBundle bundle: bundles) {
			submodels.addAll(bundle.getSubmodels());
		}
		
		List<String> paths = new ArrayList<String>();

		for(ISubmodel sm: submodels) {
			paths.addAll(parseElements(sm.getSubmodelElements().values()));
		}
		return paths;
	}
	
	/**
	 * Gets the paths from a collection of ISubmodelElement
	 * 
	 * @param elements
	 * @return the Paths from the File elements
	 */
	private List<String> parseElements(Collection<ISubmodelElement> elements) {
		List<String> paths = new ArrayList<String>();
		
		for(ISubmodelElement element: elements) {
			if(element instanceof IFile) {
				IFile file = (IFile) element;
				// If the path contains a "://", we can assume, that the Path is a link to an other server
				// e.g. http://localhost:8080/aasx/...
				if(!file.getValue().contains("://")) {
					paths.add(file.getValue());
				}
			}
			else if(element instanceof ISubmodelElementCollection) {
				ISubmodelElementCollection collection = (ISubmodelElementCollection) element;
				paths.addAll(parseElements(collection.getSubmodelElements().values()));
			}
		}
		return paths;
	}

	/**
	 * Unzips all files referenced by the aasx file according to its relationships
	 * 
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws InvalidFormatException 
	 */
	public void unzipRelatedFiles()
			throws IOException, ParserConfigurationException, SAXException, URISyntaxException, InvalidFormatException {
		// load folder which stores the files
		List<String> files = parseReferencedFilePathsFromAASX();
		OPCPackage aasxRoot = OPCPackage.open(getInputStream(aasxPath));
		for (String filePath : files) {
			// name of the folder
			unzipFile(filePath, aasxRoot);
		}
	}

	/**
	 * Create a folder to hold the unpackaged files The folder has the path
	 * \target\classes\docs
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private Path getRootFolder() throws IOException, URISyntaxException {
		URI uri = AASXPackageManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		URI parent = new File(uri).getParentFile().toURI();
		return Paths.get(parent);
	}

	/**
	 * unzip the file folders
	 * 
	 * @param filePath - path of the file in the aasx to unzip
	 * @param aasxPath    - aasx path
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InvalidFormatException 
	 */
	private void unzipFile(String filePath, OPCPackage aasxRoot) throws IOException, URISyntaxException, InvalidFormatException {
		// Create destination directory
		if (filePath.startsWith("/")) {
			filePath = filePath.substring(1);
		}
		if(filePath.isEmpty()) {
			logger.warn("A file with empty path can not be unzipped.");
			return;
		}
		logger.info("Unzipping " + filePath + " to root folder:");
		String relativePath = "files/" + VABPathTools.getParentPath(filePath);
		Path rootPath = getRootFolder();
		Path destDir = rootPath.resolve(relativePath);
		logger.info("Unzipping to " + destDir);
		Files.createDirectories(destDir);
		
		PackagePart part = aasxRoot.getPart(PackagingURIHelper.createPartName("/" + filePath));
		
		if(part == null) {
			logger.warn("File '" + filePath + "' could not be unzipped. It does not exist in .aasx.");
			return;
		}
		
		String targetPath = destDir.toString() + "/" + VABPathTools.getLastElement(filePath);
		InputStream stream = part.getInputStream();
		FileUtils.copyInputStreamToFile(stream, new File(targetPath));
	}
	
	private InputStream getInputStream(String aasxFilePath) throws IOException {
		InputStream stream = BaSyxConfiguration.getResourceStream(aasxFilePath);
		if(stream != null) {
			return stream;
		} else {
			// Alternativ, if resource has not been found: load from a file
			try {
				return new FileInputStream(aasxFilePath);
			} catch (FileNotFoundException e) {
				logger.error("File '" + aasxFilePath + "' to be loaded was not found.");
				throw e;
			}
		}
	}
}
