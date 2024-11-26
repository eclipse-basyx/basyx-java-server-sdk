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

public class AasRegistryPaths {

  public static final String SEGMENT_ADMINISTRATION = "administration";
  public static final String SEGMENT_ASSET_KIND = "assetKind";
  public static final String SEGMENT_ASSET_TYPE = "assetType";
  public static final String SEGMENT_CREATOR = "creator";
  public static final String SEGMENT_DATA_SPECIFICATION = "dataSpecification";
  public static final String SEGMENT_DATA_SPECIFICATION_CONTENT = "dataSpecificationContent";
  public static final String SEGMENT_DATA_TYPE = "dataType";
  public static final String SEGMENT_DEFINITION = "definition";
  public static final String SEGMENT_DESCRIPTION = "description";
  public static final String SEGMENT_DISPLAY_NAME = "displayName";
  public static final String SEGMENT_EMBEDDED_DATA_SPECIFICATIONS = "embeddedDataSpecifications";
  public static final String SEGMENT_ENDPOINT_PROTOCOL = "endpointProtocol";
  public static final String SEGMENT_ENDPOINT_PROTOCOL_VERSION = "endpointProtocolVersion";
  public static final String SEGMENT_ENDPOINTS = "endpoints";
  public static final String SEGMENT_EXTENSIONS = "extensions";
  public static final String SEGMENT_EXTERNAL_SUBJECT_ID = "externalSubjectId";
  public static final String SEGMENT_GLOBAL_ASSET_ID = "globalAssetId";
  public static final String SEGMENT_HREF = "href";
  public static final String SEGMENT_ID = "id";
  public static final String SEGMENT_ID_SHORT = "idShort";
  public static final String SEGMENT_INTERFACE = "interface";
  public static final String SEGMENT_KEY = "key";
  public static final String SEGMENT_KEYS = "keys";
  public static final String SEGMENT_LANGUAGE = "language";
  public static final String SEGMENT_LEVEL_TYPE = "levelType";
  public static final String SEGMENT_MAX = "max";
  public static final String SEGMENT_MIN = "min";
  public static final String SEGMENT_MODEL_TYPE = "modelType";
  public static final String SEGMENT_NAME = "name";
  public static final String SEGMENT_NOM = "nom";
  public static final String SEGMENT_PREFERRED_NAME = "preferredName";
  public static final String SEGMENT_PROTOCOL_INFORMATION = "protocolInformation";
  public static final String SEGMENT_REFERRED_SEMANTIC_ID = "referredSemanticId";
  public static final String SEGMENT_REFERS_TO = "refersTo";
  public static final String SEGMENT_REVISION = "revision";
  public static final String SEGMENT_SECURITY_ATTRIBUTES = "securityAttributes";
  public static final String SEGMENT_SEMANTIC_ID = "semanticId";
  public static final String SEGMENT_SHORT_NAME = "shortName";
  public static final String SEGMENT_SOURCE_OF_DEFINITION = "sourceOfDefinition";
  public static final String SEGMENT_SPECIFIC_ASSET_IDS = "specificAssetIds";
  public static final String SEGMENT_SUBMODEL_DESCRIPTORS = "submodelDescriptors";
  public static final String SEGMENT_SUBPROTOCOL = "subprotocol";
  public static final String SEGMENT_SUBPROTOCOL_BODY = "subprotocolBody";
  public static final String SEGMENT_SUBPROTOCOL_BODY_ENCODING = "subprotocolBodyEncoding";
  public static final String SEGMENT_SUPPLEMENTAL_SEMANTIC_ID = "supplementalSemanticId";
  public static final String SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS = "supplementalSemanticIds";
  public static final String SEGMENT_SYMBOL = "symbol";
  public static final String SEGMENT_TEMPLATE_ID = "templateId";
  public static final String SEGMENT_TEXT = "text";
  public static final String SEGMENT_TYP = "typ";
  public static final String SEGMENT_TYPE = "type";
  public static final String SEGMENT_UNIT = "unit";
  public static final String SEGMENT_UNIT_ID = "unitId";
  public static final String SEGMENT_VALUE = "value";
  public static final String SEGMENT_VALUE_FORMAT = "valueFormat";
  public static final String SEGMENT_VALUE_ID = "valueId";
  public static final String SEGMENT_VALUE_LIST = "valueList";
  public static final String SEGMENT_VALUE_REFERENCE_PAIRS = "valueReferencePairs";
  public static final String SEGMENT_VALUE_TYPE = "valueType";
  public static final String SEGMENT_VERSION = "version";

  private AasRegistryPaths() {
  }
  public static String assetKind() {
    return SEGMENT_ASSET_KIND;
  }

  public static String assetType() {
    return SEGMENT_ASSET_TYPE;
  }

  public static String globalAssetId() {
    return SEGMENT_GLOBAL_ASSET_ID;
  }

  public static String idShort() {
    return SEGMENT_ID_SHORT;
  }

  public static String id() {
    return SEGMENT_ID;
  }

  public static LangStringTextTypePath description() {
    return new LangStringTextTypePath(SEGMENT_DESCRIPTION);
  }

  public static LangStringNameTypePath displayName() {
    return new LangStringNameTypePath(SEGMENT_DISPLAY_NAME);
  }

  public static ExtensionPath extensions() {
    return new ExtensionPath(SEGMENT_EXTENSIONS);
  }

  public static AdministrativeInformationPath administration() {
    return new AdministrativeInformationPath(SEGMENT_ADMINISTRATION);
  }

  public static EndpointPath endpoints() {
    return new EndpointPath(SEGMENT_ENDPOINTS);
  }

  public static SpecificAssetIdPath specificAssetIds() {
    return new SpecificAssetIdPath(SEGMENT_SPECIFIC_ASSET_IDS);
  }

  public static SubmodelDescriptorPath submodelDescriptors() {
    return new SubmodelDescriptorPath(SEGMENT_SUBMODEL_DESCRIPTORS);
  }

  public abstract static class SimplePath {

    protected final String path;

    protected SimplePath(String path) {
      this.path = path;
    }

    protected SimplePath(String path, String segment) {
      this(path + "." + segment);
    }

    protected String append(String segment) {
		return path + "." + segment;
    }
    
    @Override
    public String toString() {
      return path;
    }
  }

  public static final class AdministrativeInformationPath extends SimplePath {

    private AdministrativeInformationPath(String path) {
      super(path);
    }

    private AdministrativeInformationPath(String path, String segment) {
	  super(path, segment);
	}

    public String version() {
    	return append(SEGMENT_VERSION);
    }

    public String revision() {
    	return append(SEGMENT_REVISION);
    }

    public String templateId() {
    	return append(SEGMENT_TEMPLATE_ID);
    }

    public EmbeddedDataSpecificationPath embeddedDataSpecifications() {
      return new EmbeddedDataSpecificationPath(path, SEGMENT_EMBEDDED_DATA_SPECIFICATIONS);
    }

    public ReferencePath creator() {
      return new ReferencePath(path, SEGMENT_CREATOR);
    }


  }
  public static final class DataSpecificationContentPath extends SimplePath {

    private DataSpecificationContentPath(String path, String segment) {
	  super(path, segment);
	}

    public DataSpecificationIec61360Path asDataSpecificationIec61360() {
      return new DataSpecificationIec61360Path(path);
    }


  }
  public static final class DataSpecificationIec61360Path extends SimplePath {

    private DataSpecificationIec61360Path(String path) {
      super(path);
    }

    public String unit() {
    	return append(SEGMENT_UNIT);
    }

    public String sourceOfDefinition() {
    	return append(SEGMENT_SOURCE_OF_DEFINITION);
    }

    public String symbol() {
    	return append(SEGMENT_SYMBOL);
    }

    public String dataType() {
    	return append(SEGMENT_DATA_TYPE);
    }

    public String valueFormat() {
    	return append(SEGMENT_VALUE_FORMAT);
    }

    public String value() {
    	return append(SEGMENT_VALUE);
    }

    public String modelType() {
    	return append(SEGMENT_MODEL_TYPE);
    }

    public LangStringPreferredNameTypeIec61360Path preferredName() {
      return new LangStringPreferredNameTypeIec61360Path(path, SEGMENT_PREFERRED_NAME);
    }

    public LangStringShortNameTypeIec61360Path shortName() {
      return new LangStringShortNameTypeIec61360Path(path, SEGMENT_SHORT_NAME);
    }

    public ReferencePath unitId() {
      return new ReferencePath(path, SEGMENT_UNIT_ID);
    }

    public LangStringDefinitionTypeIec61360Path definition() {
      return new LangStringDefinitionTypeIec61360Path(path, SEGMENT_DEFINITION);
    }

    public ValueListPath valueList() {
      return new ValueListPath(path, SEGMENT_VALUE_LIST);
    }

    public LevelTypePath levelType() {
      return new LevelTypePath(path, SEGMENT_LEVEL_TYPE);
    }


  }
  public static final class EmbeddedDataSpecificationPath extends SimplePath {

    private EmbeddedDataSpecificationPath(String path, String segment) {
	  super(path, segment);
	}

    public ReferencePath dataSpecification() {
      return new ReferencePath(path, SEGMENT_DATA_SPECIFICATION);
    }

    public DataSpecificationContentPath dataSpecificationContent() {
      return new DataSpecificationContentPath(path, SEGMENT_DATA_SPECIFICATION_CONTENT);
    }


  }
  public static final class EndpointPath extends SimplePath {

    private EndpointPath(String path) {
      super(path);
    }

    private EndpointPath(String path, String segment) {
	  super(path, segment);
	}

    public String _interface() {
    	return append(SEGMENT_INTERFACE);
    }

    public ProtocolInformationPath protocolInformation() {
      return new ProtocolInformationPath(path, SEGMENT_PROTOCOL_INFORMATION);
    }


  }
  public static final class ExtensionPath extends SimplePath {

    private ExtensionPath(String path) {
      super(path);
    }

    private ExtensionPath(String path, String segment) {
	  super(path, segment);
	}

    public String name() {
    	return append(SEGMENT_NAME);
    }

    public String valueType() {
    	return append(SEGMENT_VALUE_TYPE);
    }

    public String value() {
    	return append(SEGMENT_VALUE);
    }

    public ReferencePath semanticId() {
      return new ReferencePath(path, SEGMENT_SEMANTIC_ID);
    }

    public ReferencePath supplementalSemanticIds() {
      return new ReferencePath(path, SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS);
    }

    public ReferencePath refersTo() {
      return new ReferencePath(path, SEGMENT_REFERS_TO);
    }


  }
  public static final class KeyPath extends SimplePath {

    private KeyPath(String path, String segment) {
	  super(path, segment);
	}

    public String type() {
    	return append(SEGMENT_TYPE);
    }

    public String value() {
    	return append(SEGMENT_VALUE);
    }


  }
  public static final class LangStringDefinitionTypeIec61360Path extends SimplePath {

    private LangStringDefinitionTypeIec61360Path(String path, String segment) {
	  super(path, segment);
	}

    public String language() {
    	return append(SEGMENT_LANGUAGE);
    }

    public String text() {
    	return append(SEGMENT_TEXT);
    }


  }
  public static final class LangStringNameTypePath extends SimplePath {

    private LangStringNameTypePath(String path) {
      super(path);
    }

    private LangStringNameTypePath(String path, String segment) {
	  super(path, segment);
	}

    public String language() {
    	return append(SEGMENT_LANGUAGE);
    }

    public String text() {
    	return append(SEGMENT_TEXT);
    }


  }
  public static final class LangStringPreferredNameTypeIec61360Path extends SimplePath {

    private LangStringPreferredNameTypeIec61360Path(String path, String segment) {
	  super(path, segment);
	}

    public String language() {
    	return append(SEGMENT_LANGUAGE);
    }

    public String text() {
    	return append(SEGMENT_TEXT);
    }


  }
  public static final class LangStringShortNameTypeIec61360Path extends SimplePath {

    private LangStringShortNameTypeIec61360Path(String path, String segment) {
	  super(path, segment);
	}

    public String language() {
    	return append(SEGMENT_LANGUAGE);
    }

    public String text() {
    	return append(SEGMENT_TEXT);
    }


  }
  public static final class LangStringTextTypePath extends SimplePath {

    private LangStringTextTypePath(String path) {
      super(path);
    }

    private LangStringTextTypePath(String path, String segment) {
	  super(path, segment);
	}

    public String language() {
    	return append(SEGMENT_LANGUAGE);
    }

    public String text() {
    	return append(SEGMENT_TEXT);
    }


  }
  public static final class LevelTypePath extends SimplePath {

    private LevelTypePath(String path, String segment) {
	  super(path, segment);
	}

    public String min() {
    	return append(SEGMENT_MIN);
    }

    public String nom() {
    	return append(SEGMENT_NOM);
    }

    public String typ() {
    	return append(SEGMENT_TYP);
    }

    public String max() {
    	return append(SEGMENT_MAX);
    }


  }
  public static final class ProtocolInformationPath extends SimplePath {

    private ProtocolInformationPath(String path, String segment) {
	  super(path, segment);
	}

    public String href() {
    	return append(SEGMENT_HREF);
    }

    public String endpointProtocol() {
    	return append(SEGMENT_ENDPOINT_PROTOCOL);
    }

    public String endpointProtocolVersion() {
    	return append(SEGMENT_ENDPOINT_PROTOCOL_VERSION);
    }

    public String subprotocol() {
    	return append(SEGMENT_SUBPROTOCOL);
    }

    public String subprotocolBody() {
    	return append(SEGMENT_SUBPROTOCOL_BODY);
    }

    public String subprotocolBodyEncoding() {
    	return append(SEGMENT_SUBPROTOCOL_BODY_ENCODING);
    }

    public ProtocolInformationSecurityAttributesPath securityAttributes() {
      return new ProtocolInformationSecurityAttributesPath(path, SEGMENT_SECURITY_ATTRIBUTES);
    }


  }
  public static final class ProtocolInformationSecurityAttributesPath extends SimplePath {

    private ProtocolInformationSecurityAttributesPath(String path, String segment) {
	  super(path, segment);
	}

    public String type() {
    	return append(SEGMENT_TYPE);
    }

    public String key() {
    	return append(SEGMENT_KEY);
    }

    public String value() {
    	return append(SEGMENT_VALUE);
    }


  }
  public static final class ReferencePath extends SimplePath {

    private ReferencePath(String path, String segment) {
	  super(path, segment);
	}

    public String type() {
    	return append(SEGMENT_TYPE);
    }

    public KeyPath keys() {
      return new KeyPath(path, SEGMENT_KEYS);
    }

    public ReferenceParentPath referredSemanticId() {
      return new ReferenceParentPath(path, SEGMENT_REFERRED_SEMANTIC_ID);
    }


  }
  public static final class ReferenceParentPath extends SimplePath {

    private ReferenceParentPath(String path, String segment) {
	  super(path, segment);
	}

    public String type() {
    	return append(SEGMENT_TYPE);
    }

    public KeyPath keys() {
      return new KeyPath(path, SEGMENT_KEYS);
    }


  }
  public static final class SpecificAssetIdPath extends SimplePath {

    private SpecificAssetIdPath(String path) {
      super(path);
    }

    public String name() {
    	return append(SEGMENT_NAME);
    }

    public String value() {
    	return append(SEGMENT_VALUE);
    }

    public ReferencePath semanticId() {
      return new ReferencePath(path, SEGMENT_SEMANTIC_ID);
    }

    public ReferencePath supplementalSemanticIds() {
      return new ReferencePath(path, SEGMENT_SUPPLEMENTAL_SEMANTIC_IDS);
    }

    public ReferencePath externalSubjectId() {
      return new ReferencePath(path, SEGMENT_EXTERNAL_SUBJECT_ID);
    }


  }
  public static final class SubmodelDescriptorPath extends SimplePath {

    private SubmodelDescriptorPath(String path) {
      super(path);
    }

    public String idShort() {
    	return append(SEGMENT_ID_SHORT);
    }

    public String id() {
    	return append(SEGMENT_ID);
    }

    public LangStringTextTypePath description() {
      return new LangStringTextTypePath(path, SEGMENT_DESCRIPTION);
    }

    public LangStringNameTypePath displayName() {
      return new LangStringNameTypePath(path, SEGMENT_DISPLAY_NAME);
    }

    public ExtensionPath extensions() {
      return new ExtensionPath(path, SEGMENT_EXTENSIONS);
    }

    public AdministrativeInformationPath administration() {
      return new AdministrativeInformationPath(path, SEGMENT_ADMINISTRATION);
    }

    public ReferencePath semanticId() {
      return new ReferencePath(path, SEGMENT_SEMANTIC_ID);
    }

    public ReferencePath supplementalSemanticId() {
      return new ReferencePath(path, SEGMENT_SUPPLEMENTAL_SEMANTIC_ID);
    }

    public EndpointPath endpoints() {
      return new EndpointPath(path, SEGMENT_ENDPOINTS);
    }


  }
  public static final class ValueListPath extends SimplePath {

    private ValueListPath(String path, String segment) {
	  super(path, segment);
	}

    public ValueReferencePairPath valueReferencePairs() {
      return new ValueReferencePairPath(path, SEGMENT_VALUE_REFERENCE_PAIRS);
    }


  }
  public static final class ValueReferencePairPath extends SimplePath {

    private ValueReferencePairPath(String path, String segment) {
	  super(path, segment);
	}

    public String value() {
    	return append(SEGMENT_VALUE);
    }

    public ReferencePath valueId() {
      return new ReferencePath(path, SEGMENT_VALUE_ID);
    }


  }
}
