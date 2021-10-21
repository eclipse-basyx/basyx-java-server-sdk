/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelFacadeCustomSemantics;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelFacadeIRDISemantics;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for providers that receiver their configuration through a configuration properties object
 * 
 * @author kuhn
 *
 */

@Deprecated
public class BaseConfiguredProvider extends SubmodelProvider {
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseConfiguredProvider.class);

	/**
	 * This is a sub model
	 */
	protected Submodel submodelData = null;
	
	public static final String SUBMODELSEMANTICS = "submodelSemantics";
	public static final String TYPE = "type";
	public static final String SEMANTICSINTERNAL = "semanticsInternal";
	
	public static final String SUBMODELID = "submodelID";
	

	/**
	 * Constructor
	 */
	public BaseConfiguredProvider(Map<Object, Object> cfgValues) {
		// Invoke base constructor
		super();

		// Create sub model
		submodelData = createSubmodel(cfgValues);

		setSubmodel(submodelData);
	}

	protected void setSubmodel(Submodel sm) {
		setAPI(new VABSubmodelAPI(new VABMapProvider(sm)));
	}

	/**
	 * Split a comma delimited string
	 * 
	 * @param input
	 *            String input
	 */
	protected Collection<String> splitString(String input) {
		// Return value
		HashSet<String> result = new HashSet<>();

		// Split string into segments
		for (String inputStr : input.split(","))
			result.add(inputStr.trim());

		// Return result
		return result;
	}

	/**
	 * Get list of configured properties
	 * 
	 * @param cfgValues
	 *            Provider configuration
	 */
	protected Collection<String> getConfiguredProperties(Map<Object, Object> cfgValues) {
		// Split property string
		return splitString((String) cfgValues.get(MultiSubmodelElementProvider.ELEMENTS));
	}

	/**
	 * Output a hash map
	 */
	@SuppressWarnings({ "rawtypes" })
	protected void printHashMap(Map map, int indent) {
		// Process map
		for (Object key : map.keySet()) {
			// Process map element
			if (map.get(key) instanceof Map) {
				// Output key
				for (int i = 0; i < indent; i++)
					logger.debug(" ");
				logger.debug("  " + key);
				// Output hash map
				printHashMap((Map) map.get(key), indent + 2);
			} else {
				// Output element
				for (int i = 0; i < indent; i++)
					logger.debug(" ");
				logger.debug("  " + key + " = " + map.get(key));
			}
		}
	}

	/**
	 * Split a key based on path '/' separators
	 */
	protected String[] splitPath(String path) {
		return path.split("/");
	}

	/**
	 * Create BaSys sub model based on configuration data
	 * 
	 * @param cfgValues
	 *            Provider configuration
	 */
	protected Submodel createSubmodel(Map<Object, Object> cfgValues) {
		// Create sub model
		Submodel submodel = null;

		// Try to load and convert configuration values. Keep value null if any error occurs
		String basyx_submodelSemantics = null;
		try {
			basyx_submodelSemantics = cfgValues.get(buildBasyxCfgName(SUBMODELSEMANTICS)).toString().toLowerCase();
		} catch (Exception e) {
		}
		String basyx_idType = null;
		try {
			basyx_idType = cfgValues.get(buildBasyxCfgName(Identifier.IDTYPE)).toString().toLowerCase();
		} catch (Exception e) {
		}
		String basyx_id = null;
		try {
			basyx_id = cfgValues.get(buildBasyxCfgName(Identifier.ID)).toString();
		} catch (Exception e) {
		}
		String basyx_idShort = null;
		try {
			basyx_idShort = cfgValues.get(buildBasyxCfgName(Referable.IDSHORT)).toString();
		} catch (Exception e) {
		}
		String basyx_category = null;
		try {
			basyx_category = cfgValues.get(buildBasyxCfgName(Referable.CATEGORY)).toString();
		} catch (Exception e) {
		}
		String basyx_description = null;
		try {
			basyx_description = cfgValues.get(buildBasyxCfgName(Referable.DESCRIPTION)).toString();
		} catch (Exception e) {
		}
		String basyx_qualifier = null;
		try {
			basyx_qualifier = cfgValues.get(buildBasyxCfgName(Qualifier.QUALIFIER)).toString();
		} catch (Exception e) {
		}
		String basyx_qualifierType = null;
		try {
			basyx_qualifierType = cfgValues.get(buildBasyxCfgName(Qualifier.QUALIFIER, Qualifier.TYPE)).toString();
		} catch (Exception e) {
		}
		String basyx_version = null;
		try {
			basyx_version = cfgValues.get(buildBasyxCfgName(AdministrativeInformation.VERSION)).toString();
		} catch (Exception e) {
		}
		String basyx_revision = null;
		try {
			basyx_revision = cfgValues.get(buildBasyxCfgName(AdministrativeInformation.REVISION)).toString();
		} catch (Exception e) {
		}

		// Process ID Type - default value is internal
		IdentifierType idType = IdentifierType.CUSTOM;
		// - Compare to known values
		if (basyx_idType == null)
			basyx_idType = "IdentifierType.Custom";
		if (basyx_idType.equals("IdentifierType.IRDI"))
			idType = IdentifierType.IRDI;
		if (basyx_idType.equals("IdentifierType.URI"))
			idType = IdentifierType.IRI;
		if (basyx_idType.equals("IdentifierType.Custom"))
			idType = IdentifierType.CUSTOM;

		// Try to load properties
		// Check type of sub model template to use
		if (basyx_submodelSemantics == null)
			basyx_submodelSemantics = IdentifierType.CUSTOM.toString().toLowerCase();
		if (basyx_submodelSemantics.equals(IdentifierType.IRDI.toString().toLowerCase())) {
			// Create sub model from template
			submodel = new SubmodelFacadeIRDISemantics(basyx_submodelSemantics, idType, basyx_id,
					basyx_idShort, basyx_category, new LangStrings("", basyx_description),
					new Qualifier(basyx_qualifierType, basyx_qualifier, "", null), null, ModelingKind.INSTANCE, basyx_version,
					basyx_revision);
		}
		if (basyx_submodelSemantics.equals(IdentifierType.CUSTOM.toString().toLowerCase())) {
			// Create sub model from template
			submodel = new SubmodelFacadeCustomSemantics(basyx_submodelSemantics, idType, basyx_id,
					basyx_idShort, basyx_category, new LangStrings("", basyx_description),
					new Qualifier(basyx_qualifierType, basyx_qualifier, "", null), new HasDataSpecification(),
					ModelingKind.INSTANCE, basyx_version, basyx_revision);
		}

		// If no sub model was created, create an empty one
		if (submodel == null)
			submodel = new Submodel();

		// Return sub model data
		return submodel;
	}

	/**
	 * Create a property with given name
	 * 
	 * @param propertyName
	 *            Property name
	 * @param propertyValue
	 *            Property value
	 * @param cfgValues
	 *            Provider configuration
	 */
	protected Property createSubmodelElement(String propertyName, Object propertyValue, Map<Object, Object> cfgValues) {

		// Get property type
		String propertyType = cfgValues.get(buildCfgName(propertyName, TYPE)).toString();

		// Dispatch to requested create function
		if (propertyType.equals("Property"))
			return createProperty(propertyName, propertyValue, cfgValues);

		// Do not return anything
		return null;
	}

	/**
	 * Create a single valued property with given name
	 * 
	 * @param propertyName
	 *            Property name
	 * @param propertyValue
	 *            Property value
	 * @param cfgValues
	 *            Provider configuration
	 */
	protected Property createProperty(String propertyName, Object propertyValue, Map<Object, Object> cfgValues) {

		// Try to get property meta data
		String property_semanticsInternal = null;
		try {
			property_semanticsInternal = cfgValues.get(buildCfgName(propertyName, SEMANTICSINTERNAL)).toString();
		} catch (Exception e) {
		}
		String property_qualifier = null;
		try {
			property_qualifier = cfgValues.get(buildCfgName(propertyName, Qualifier.QUALIFIER)).toString();
		} catch (Exception e) {
		} // might need to rename to constraints
		String property_qualifierType = null;
		try {
			property_qualifierType = cfgValues.get(buildCfgName(propertyName, Qualifier.QUALIFIER, Qualifier.TYPE)).toString();
		} catch (Exception e) {
		}
		String property_description = null;
		try {
			property_description = cfgValues.get(buildCfgName(propertyName, Referable.DESCRIPTION)).toString();
		} catch (Exception e) {
		}

		// Create and return single valued property
		Property prop = new Property(
				propertyValue, 
				new Referable(propertyName, "", new LangStrings("", property_description)),
				new Reference(new Key(KeyElements.PROPERTY, true, property_semanticsInternal, IdentifierType.CUSTOM)), 
				new Qualifiable(new Qualifier(property_qualifierType, property_qualifier, "", null)));
		return prop;
	}
	
	public static String buildBasyxCfgName(String... valueName) {
		return buildCfgName("basyx", valueName);
	}
	
	public static String buildCfgName(String propertyName, String... valueName) {
		String result = propertyName;
		for(String s: valueName)
			result += "." + s;
		return result;
	}
	
}
