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
package org.eclipse.digitaltwin.basyx.aasregistry.paths; 

import java.util.ArrayDeque;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.DataSpecificationContent;
import org.eclipse.digitaltwin.basyx.aasregistry.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Key;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LevelType;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ProtocolInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ProtocolInformationSecurityAttributes;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ReferenceParent;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ValueList;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ValueReferencePair;

public class AasRegistryPathProcessor {

	private final AssetAdministrationShellDescriptor subject;

	public AasRegistryPathProcessor(AssetAdministrationShellDescriptor subject) {
		this.subject = subject;
	}

	public void visitValuesAtPath(String path, AssetAdministrationShellDescriptorVisitor visitor) {
		InternalPathProcessor processor = new InternalPathProcessor(visitor, path);
		processor.visitObject(subject, processor::visitAssetAdministrationShellDescriptor);
	}
	
	
	public static void visitPath(String path, AssetAdministrationShellDescriptorPathVisitor visitor) {
		visitor = new InternalPathRunner.LeafPathChecker(visitor);
		InternalPathRunner runner = new InternalPathRunner(visitor, path);
		visitor.startObjectSegment(path, "", "");
		runner.visitAssetAdministrationShellDescriptorPath("", 0);
		visitor.endObjectSegment(path, "", "");
	}

	public static interface AssetAdministrationShellDescriptorVisitor {

		default void visitResolvedPathValue(String path, Object[] objectPathToValue, String value) {
		}
	}

	public static interface AssetAdministrationShellDescriptorPathVisitor {
		
		default void startObjectListSegment(String targetPath, String currentPath, String currentSegment) {
		}
		
		default void startObjectSegment(String targetPath, String currentPath, String currentSegment) {
		}
		
		default void visitPrimitiveListSegment(String targetPath, String currentPath, String currentSegment, String rangeType) {
		}
		
		default void visitPrimitiveSegment(String targetPath, String currentPath, String currentSegment, String rangeType) {
		}
		
		default void endObjectListSegment(String targetPath, String currentPath, String currentSegment) {
		}
		
		default void endObjectSegment(String targetPath, String currentPath, String currentSegment) {
		}
					
	}

	public static class UnknownLeafPathException extends RuntimeException {
	
		private static final long serialVersionUID = 1L;
	
		public UnknownLeafPathException(String path) {
			super ("'" + path +"' in not a known path to a leaf!");
		}
	}

	private static final class InternalPathRunner {

		private final AssetAdministrationShellDescriptorPathVisitor visitor;
		private final String[] pathAsArray;
		private final String targetPath;

		public InternalPathRunner(AssetAdministrationShellDescriptorPathVisitor visitor, String path) {
			this.visitor = visitor;
			this.targetPath = path;
			pathAsArray = path.split("\\.");
		}
		
		public void visitAdministrativeInformationPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_EMBEDDED_DATA_SPECIFICATIONS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitEmbeddedDataSpecificationPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_CREATOR:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_VERSION:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_REVISION:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_TEMPLATE_ID:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitAssetAdministrationShellDescriptorPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_DESCRIPTION:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringTextTypePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_DISPLAY_NAME:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringNameTypePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_EXTENSIONS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitExtensionPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_ADMINISTRATION:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitAdministrativeInformationPath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_ENDPOINTS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitEndpointPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SPECIFIC_ASSET_IDS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitSpecificAssetIdPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitSubmodelDescriptorPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_ASSET_KIND:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "AssetKind");
				break;
			case AasRegistryPaths.SEGMENT_ASSET_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_GLOBAL_ASSET_ID:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_ID_SHORT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_ID:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitDataSpecificationContentPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			default:				
				 visitDataSpecificationIec61360Path(prefix, pos);
			}
		}
		
		public void visitDataSpecificationIec61360Path(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_PREFERRED_NAME:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringPreferredNameTypeIec61360Path(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SHORT_NAME:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringShortNameTypeIec61360Path(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_UNIT_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_DEFINITION:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringDefinitionTypeIec61360Path(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_VALUE_LIST:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitValueListPath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_LEVEL_TYPE:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitLevelTypePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_UNIT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_SOURCE_OF_DEFINITION:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_SYMBOL:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_DATA_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "DataTypeIec61360");
				break;
			case AasRegistryPaths.SEGMENT_VALUE_FORMAT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_MODEL_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitEmbeddedDataSpecificationPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_DATA_SPECIFICATION:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_DATA_SPECIFICATION_CONTENT:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitDataSpecificationContentPath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			default:				
			}
		}
		
		public void visitEndpointPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_PROTOCOL_INFORMATION:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitProtocolInformationPath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_INTERFACE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitExtensionPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_SEMANTIC_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_REFERS_TO:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_NAME:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_VALUE_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "DataTypeDefXsd");
				break;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitKeyPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "KeyTypes");
				break;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitLangStringDefinitionTypeIec61360Path(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitLangStringNameTypePath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitLangStringPreferredNameTypeIec61360Path(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitLangStringShortNameTypeIec61360Path(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitLangStringTextTypePath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitLevelTypePath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_MIN:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "Boolean");
				break;
			case AasRegistryPaths.SEGMENT_NOM:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "Boolean");
				break;
			case AasRegistryPaths.SEGMENT_TYP:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "Boolean");
				break;
			case AasRegistryPaths.SEGMENT_MAX:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "Boolean");
				break;
			default:				
			}
		}
		
		public void visitProtocolInformationPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_SECURITY_ATTRIBUTES:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitProtocolInformationSecurityAttributesPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_HREF:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_ENDPOINT_PROTOCOL:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_ENDPOINT_PROTOCOL_VERSION:
				visitor.visitPrimitiveListSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_SUBPROTOCOL:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_SUBPROTOCOL_BODY:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_SUBPROTOCOL_BODY_ENCODING:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitProtocolInformationSecurityAttributesPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "TypeEnum");
				break;
			case AasRegistryPaths.SEGMENT_KEY:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitReferencePath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_KEYS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitKeyPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_REFERRED_SEMANTIC_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferenceParentPath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "ReferenceTypes");
				break;
			default:				
			}
		}
		
		public void visitReferenceParentPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_KEYS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitKeyPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_TYPE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "ReferenceTypes");
				break;
			default:				
			}
		}
		
		public void visitSpecificAssetIdPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_SEMANTIC_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_EXTERNAL_SUBJECT_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_NAME:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitSubmodelDescriptorPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_DESCRIPTION:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringTextTypePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_DISPLAY_NAME:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitLangStringNameTypePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_EXTENSIONS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitExtensionPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_ADMINISTRATION:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitAdministrativeInformationPath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SEMANTIC_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_SUPPLEMENTAL_SEMANTIC_ID:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_ENDPOINTS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitEndpointPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_ID_SHORT:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			case AasRegistryPaths.SEGMENT_ID:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		public void visitValueListPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_VALUE_REFERENCE_PAIRS:
				visitor.startObjectListSegment(targetPath, newPrefix, current);
				visitValueReferencePairPath(newPrefix, pos + 1); 
				visitor.endObjectListSegment(targetPath, newPrefix, current);
				break;
			default:				
			}
		}
		
		public void visitValueReferencePairPath(String prefix, int pos) {
			if (pos >= pathAsArray.length) {
				throw new UnknownLeafPathException(prefix);
			}
			String current = pathAsArray[pos];	
			String newPrefix = prefix.isEmpty() ? current : prefix + "." + current;
			switch (current) {
			case AasRegistryPaths.SEGMENT_VALUE_ID:
				visitor.startObjectSegment(targetPath, newPrefix, current);
				visitReferencePath(newPrefix, pos + 1);
				visitor.endObjectSegment(targetPath, newPrefix, current);
				break;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitor.visitPrimitiveSegment(targetPath, newPrefix, current, "String");
				break;
			default:				
			}
		}
		
		
		private static class LeafPathChecker implements AssetAdministrationShellDescriptorPathVisitor {
	
			private boolean leafReached;	
			private final AssetAdministrationShellDescriptorPathVisitor decorated;
			
			private LeafPathChecker(AssetAdministrationShellDescriptorPathVisitor decorated)  {
				this.decorated = decorated;
			}
			
			@Override
			public void startObjectListSegment(String targetPath, String currentPath, String currentSegment) {
				if (decorated != null) {
					decorated.startObjectListSegment(targetPath, currentPath, currentSegment);
				}
			}
			
			@Override
			public void startObjectSegment(String targetPath, String currentPath, String currentSegment) {
				if (currentPath.isEmpty()) { 
					leafReached = false;
				}
				if (decorated != null) {
					decorated.startObjectSegment(targetPath, currentPath, currentSegment);
				}
			}
		
			@Override
			public void visitPrimitiveListSegment(String targetPath, String currentPath, String currentSegment, String rangeType) {
				leafReached = true;
				if (decorated != null) {
					decorated.visitPrimitiveListSegment(targetPath, currentPath, currentSegment, rangeType);
				}
			}
			
			@Override
			public void visitPrimitiveSegment(String targetPath, String currentPath, String currentSegment, String rangeType) {
				leafReached = true;
				if (decorated != null) {
					decorated.visitPrimitiveSegment(targetPath, currentPath, currentSegment, rangeType);
				}
			}
			
			@Override
			public void endObjectListSegment(String targetPath, String currentPath, String currentSegment) {
				if (decorated != null) {
					decorated.endObjectListSegment(targetPath, currentPath, currentSegment);
				}
			}
			
			@Override
			public void endObjectSegment(String targetPath, String currentPath, String currentSegment) {
				if (currentPath.isEmpty() && !leafReached) {
					throw new UnknownLeafPathException(targetPath);
				}
				if (decorated != null) {
					decorated.endObjectSegment(targetPath, currentPath, currentSegment);
				}
			}
		}
	}

	private static final class InternalPathProcessor {

		private final AssetAdministrationShellDescriptorVisitor visitor;
		private final ArrayDeque<Object> currentPathElements = new ArrayDeque<>();
		private final String path;
		private final ListIterator<String> pathIterator;

		public InternalPathProcessor(AssetAdministrationShellDescriptorVisitor visitor, String path) {
			this.visitor = visitor;
			this.path = path;
			String[] pathAsArray = path.split("\\.");
			pathIterator = List.of(pathAsArray).listIterator();
		}

		public void visitAdministrativeInformation(AdministrativeInformation toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_EMBEDDED_DATA_SPECIFICATIONS:
				visitObjectList(toVisit.getEmbeddedDataSpecifications(), this::visitEmbeddedDataSpecification);
				return;
			case AasRegistryPaths.SEGMENT_CREATOR:
				visitObject(toVisit.getCreator(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_VERSION:
				visitPrimitiveValue(toVisit.getVersion());
				return;
			case AasRegistryPaths.SEGMENT_REVISION:
				visitPrimitiveValue(toVisit.getRevision());
				return;
			case AasRegistryPaths.SEGMENT_TEMPLATE_ID:
				visitPrimitiveValue(toVisit.getTemplateId());
				return;
			default:
			}
		}
		
		public void visitAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_DESCRIPTION:
				visitObjectList(toVisit.getDescription(), this::visitLangStringTextType);
				return;
			case AasRegistryPaths.SEGMENT_DISPLAY_NAME:
				visitObjectList(toVisit.getDisplayName(), this::visitLangStringNameType);
				return;
			case AasRegistryPaths.SEGMENT_EXTENSIONS:
				visitObjectList(toVisit.getExtensions(), this::visitExtension);
				return;
			case AasRegistryPaths.SEGMENT_ADMINISTRATION:
				visitObject(toVisit.getAdministration(), this::visitAdministrativeInformation);
				return;
			case AasRegistryPaths.SEGMENT_ENDPOINTS:
				visitObjectList(toVisit.getEndpoints(), this::visitEndpoint);
				return;
			case AasRegistryPaths.SEGMENT_SPECIFIC_ASSET_IDS:
				visitObjectList(toVisit.getSpecificAssetIds(), this::visitSpecificAssetId);
				return;
			case AasRegistryPaths.SEGMENT_SUBMODEL_DESCRIPTORS:
				visitObjectList(toVisit.getSubmodelDescriptors(), this::visitSubmodelDescriptor);
				return;
			case AasRegistryPaths.SEGMENT_ASSET_KIND:
				visitPrimitiveValue(toVisit.getAssetKind());
				return;
			case AasRegistryPaths.SEGMENT_ASSET_TYPE:
				visitPrimitiveValue(toVisit.getAssetType());
				return;
			case AasRegistryPaths.SEGMENT_GLOBAL_ASSET_ID:
				visitPrimitiveValue(toVisit.getGlobalAssetId());
				return;
			case AasRegistryPaths.SEGMENT_ID_SHORT:
				visitPrimitiveValue(toVisit.getIdShort());
				return;
			case AasRegistryPaths.SEGMENT_ID:
				visitPrimitiveValue(toVisit.getId());
				return;
			default:
			}
		}
		
		public void visitDataSpecificationContent(DataSpecificationContent toVisit, String segment) {
			switch (segment) {
			default:
				if (toVisit instanceof DataSpecificationIec61360) {
					visitDataSpecificationIec61360((DataSpecificationIec61360) toVisit, segment);
					return;
				}
			}
		}
		
		public void visitDataSpecificationIec61360(DataSpecificationIec61360 toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_PREFERRED_NAME:
				visitObjectList(toVisit.getPreferredName(), this::visitLangStringPreferredNameTypeIec61360);
				return;
			case AasRegistryPaths.SEGMENT_SHORT_NAME:
				visitObjectList(toVisit.getShortName(), this::visitLangStringShortNameTypeIec61360);
				return;
			case AasRegistryPaths.SEGMENT_UNIT_ID:
				visitObject(toVisit.getUnitId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_DEFINITION:
				visitObjectList(toVisit.getDefinition(), this::visitLangStringDefinitionTypeIec61360);
				return;
			case AasRegistryPaths.SEGMENT_VALUE_LIST:
				visitObject(toVisit.getValueList(), this::visitValueList);
				return;
			case AasRegistryPaths.SEGMENT_LEVEL_TYPE:
				visitObject(toVisit.getLevelType(), this::visitLevelType);
				return;
			case AasRegistryPaths.SEGMENT_UNIT:
				visitPrimitiveValue(toVisit.getUnit());
				return;
			case AasRegistryPaths.SEGMENT_SOURCE_OF_DEFINITION:
				visitPrimitiveValue(toVisit.getSourceOfDefinition());
				return;
			case AasRegistryPaths.SEGMENT_SYMBOL:
				visitPrimitiveValue(toVisit.getSymbol());
				return;
			case AasRegistryPaths.SEGMENT_DATA_TYPE:
				visitPrimitiveValue(toVisit.getDataType());
				return;
			case AasRegistryPaths.SEGMENT_VALUE_FORMAT:
				visitPrimitiveValue(toVisit.getValueFormat());
				return;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitPrimitiveValue(toVisit.getValue());
				return;
			case AasRegistryPaths.SEGMENT_MODEL_TYPE:
				visitPrimitiveValue(toVisit.getModelType());
				return;
			default:
			}
		}
		
		public void visitEmbeddedDataSpecification(EmbeddedDataSpecification toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_DATA_SPECIFICATION:
				visitObject(toVisit.getDataSpecification(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_DATA_SPECIFICATION_CONTENT:
				visitObject(toVisit.getDataSpecificationContent(), this::visitDataSpecificationContent);
				return;
			default:
			}
		}
		
		public void visitEndpoint(Endpoint toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_PROTOCOL_INFORMATION:
				visitObject(toVisit.getProtocolInformation(), this::visitProtocolInformation);
				return;
			case AasRegistryPaths.SEGMENT_INTERFACE:
				visitPrimitiveValue(toVisit.getInterface());
				return;
			default:
			}
		}
		
		public void visitExtension(Extension toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_SEMANTIC_ID:
				visitObject(toVisit.getSemanticId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS:
				visitObjectList(toVisit.getSupplementalSemanticIds(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_REFERS_TO:
				visitObjectList(toVisit.getRefersTo(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_NAME:
				visitPrimitiveValue(toVisit.getName());
				return;
			case AasRegistryPaths.SEGMENT_VALUE_TYPE:
				visitPrimitiveValue(toVisit.getValueType());
				return;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitPrimitiveValue(toVisit.getValue());
				return;
			default:
			}
		}
		
		public void visitKey(Key toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_TYPE:
				visitPrimitiveValue(toVisit.getType());
				return;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitPrimitiveValue(toVisit.getValue());
				return;
			default:
			}
		}
		
		public void visitLangStringDefinitionTypeIec61360(LangStringDefinitionTypeIec61360 toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitPrimitiveValue(toVisit.getLanguage());
				return;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitPrimitiveValue(toVisit.getText());
				return;
			default:
			}
		}
		
		public void visitLangStringNameType(LangStringNameType toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitPrimitiveValue(toVisit.getLanguage());
				return;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitPrimitiveValue(toVisit.getText());
				return;
			default:
			}
		}
		
		public void visitLangStringPreferredNameTypeIec61360(LangStringPreferredNameTypeIec61360 toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitPrimitiveValue(toVisit.getLanguage());
				return;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitPrimitiveValue(toVisit.getText());
				return;
			default:
			}
		}
		
		public void visitLangStringShortNameTypeIec61360(LangStringShortNameTypeIec61360 toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitPrimitiveValue(toVisit.getLanguage());
				return;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitPrimitiveValue(toVisit.getText());
				return;
			default:
			}
		}
		
		public void visitLangStringTextType(LangStringTextType toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_LANGUAGE:
				visitPrimitiveValue(toVisit.getLanguage());
				return;
			case AasRegistryPaths.SEGMENT_TEXT:
				visitPrimitiveValue(toVisit.getText());
				return;
			default:
			}
		}
		
		public void visitLevelType(LevelType toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_MIN:
				visitPrimitiveValue(toVisit.isMin());
				return;
			case AasRegistryPaths.SEGMENT_NOM:
				visitPrimitiveValue(toVisit.isNom());
				return;
			case AasRegistryPaths.SEGMENT_TYP:
				visitPrimitiveValue(toVisit.isTyp());
				return;
			case AasRegistryPaths.SEGMENT_MAX:
				visitPrimitiveValue(toVisit.isMax());
				return;
			default:
			}
		}
		
		public void visitProtocolInformation(ProtocolInformation toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_SECURITY_ATTRIBUTES:
				visitObjectList(toVisit.getSecurityAttributes(), this::visitProtocolInformationSecurityAttributes);
				return;
			case AasRegistryPaths.SEGMENT_HREF:
				visitPrimitiveValue(toVisit.getHref());
				return;
			case AasRegistryPaths.SEGMENT_ENDPOINT_PROTOCOL:
				visitPrimitiveValue(toVisit.getEndpointProtocol());
				return;
			case AasRegistryPaths.SEGMENT_ENDPOINT_PROTOCOL_VERSION:
				visitPrimitiveValueList(toVisit.getEndpointProtocolVersion());
				return;
			case AasRegistryPaths.SEGMENT_SUBPROTOCOL:
				visitPrimitiveValue(toVisit.getSubprotocol());
				return;
			case AasRegistryPaths.SEGMENT_SUBPROTOCOL_BODY:
				visitPrimitiveValue(toVisit.getSubprotocolBody());
				return;
			case AasRegistryPaths.SEGMENT_SUBPROTOCOL_BODY_ENCODING:
				visitPrimitiveValue(toVisit.getSubprotocolBodyEncoding());
				return;
			default:
			}
		}
		
		public void visitProtocolInformationSecurityAttributes(ProtocolInformationSecurityAttributes toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_TYPE:
				visitPrimitiveValue(toVisit.getType());
				return;
			case AasRegistryPaths.SEGMENT_KEY:
				visitPrimitiveValue(toVisit.getKey());
				return;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitPrimitiveValue(toVisit.getValue());
				return;
			default:
			}
		}
		
		public void visitReference(Reference toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_KEYS:
				visitObjectList(toVisit.getKeys(), this::visitKey);
				return;
			case AasRegistryPaths.SEGMENT_REFERRED_SEMANTIC_ID:
				visitObject(toVisit.getReferredSemanticId(), this::visitReferenceParent);
				return;
			case AasRegistryPaths.SEGMENT_TYPE:
				visitPrimitiveValue(toVisit.getType());
				return;
			default:
			}
		}
		
		public void visitReferenceParent(ReferenceParent toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_KEYS:
				visitObjectList(toVisit.getKeys(), this::visitKey);
				return;
			case AasRegistryPaths.SEGMENT_TYPE:
				visitPrimitiveValue(toVisit.getType());
				return;
			default:
			}
		}
		
		public void visitSpecificAssetId(SpecificAssetId toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_SEMANTIC_ID:
				visitObject(toVisit.getSemanticId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS:
				visitObjectList(toVisit.getSupplementalSemanticIds(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_EXTERNAL_SUBJECT_ID:
				visitObject(toVisit.getExternalSubjectId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_NAME:
				visitPrimitiveValue(toVisit.getName());
				return;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitPrimitiveValue(toVisit.getValue());
				return;
			default:
			}
		}
		
		public void visitSubmodelDescriptor(SubmodelDescriptor toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_DESCRIPTION:
				visitObjectList(toVisit.getDescription(), this::visitLangStringTextType);
				return;
			case AasRegistryPaths.SEGMENT_DISPLAY_NAME:
				visitObjectList(toVisit.getDisplayName(), this::visitLangStringNameType);
				return;
			case AasRegistryPaths.SEGMENT_EXTENSIONS:
				visitObjectList(toVisit.getExtensions(), this::visitExtension);
				return;
			case AasRegistryPaths.SEGMENT_ADMINISTRATION:
				visitObject(toVisit.getAdministration(), this::visitAdministrativeInformation);
				return;
			case AasRegistryPaths.SEGMENT_SEMANTIC_ID:
				visitObject(toVisit.getSemanticId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_SUPPLEMENTAL_SEMANTIC_ID:
				visitObjectList(toVisit.getSupplementalSemanticId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_ENDPOINTS:
				visitObjectList(toVisit.getEndpoints(), this::visitEndpoint);
				return;
			case AasRegistryPaths.SEGMENT_ID_SHORT:
				visitPrimitiveValue(toVisit.getIdShort());
				return;
			case AasRegistryPaths.SEGMENT_ID:
				visitPrimitiveValue(toVisit.getId());
				return;
			default:
			}
		}
		
		public void visitValueList(ValueList toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_VALUE_REFERENCE_PAIRS:
				visitObjectList(toVisit.getValueReferencePairs(), this::visitValueReferencePair);
				return;
			default:
			}
		}
		
		public void visitValueReferencePair(ValueReferencePair toVisit, String segment) {
			switch (segment) {
			case AasRegistryPaths.SEGMENT_VALUE_ID:
				visitObject(toVisit.getValueId(), this::visitReference);
				return;
			case AasRegistryPaths.SEGMENT_VALUE:
				visitPrimitiveValue(toVisit.getValue());
				return;
			default:
			}
		}
		
		private <T> void visitObjectList(List<T> list, BiConsumer<T, String> processor) {
			if (list != null) {
				for (T eachValue : list) {
					visitObject(eachValue, processor);
				}
			}
		}
		
		private <T> void visitObject(T object, BiConsumer<T, String> processor) {
			if (object != null && pathIterator.hasNext()) {
				String token = pathIterator.next();
				currentPathElements.addLast(object);
				processor.accept(object, token);
				currentPathElements.removeLast();
				pathIterator.previous();
			}
		}

		private void visitPrimitiveValue(Object value) {
			if (value != null) {
				Object[] objectPath = currentPathElements.toArray();
				visitor.visitResolvedPathValue(path, objectPath, value.toString());
			}
		}

		private void visitPrimitiveValueList(List<?> values) {
			if (values != null) {
				Object[] objectPath = currentPathElements.toArray();
				for (Object eachValue : values) {
					visitor.visitResolvedPathValue(path, objectPath, eachValue.toString());
				}
			}
		}
	}
}
