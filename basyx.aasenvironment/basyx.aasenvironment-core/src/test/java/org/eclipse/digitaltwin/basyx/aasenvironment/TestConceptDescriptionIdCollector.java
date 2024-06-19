package org.eclipse.digitaltwin.basyx.aasenvironment;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * Tests the behavior of {@link ConceptDescriptionIdCollector}
 * 
 * @author danish
 *
 */
public class TestConceptDescriptionIdCollector {
	
	private Environment environment;
	
	@Test
	public void getAllConceptDescriptionIdsWithDefaultSetup() {
		defaultSetup();
		
		Set<String> expectedCDIDs = Sets.newHashSet("0173-1#02-BAA120#008", "http://customer.com/cd/1/1/18EBD56F6B43D895","0173-1#01-AFZ615#016");
		
		ConceptDescriptionIdCollector cdIdCollector = new ConceptDescriptionIdCollector(environment);
		
		assertEquals(expectedCDIDs, cdIdCollector.collect());
	}
	
	@Test
	public void getAllConceptDescriptionIdsWithCustomtSetup() {
		customSetup();
		
		Set<String> expectedCDIDs = Sets.newHashSet("dummyProperty1CDId", "dummySMC1CDId", "dummyProperty3CDId","dummyProperty2CapabilityId","dummyNoCDSemanticPropertyBlobId");
		
		ConceptDescriptionIdCollector cdIdCollector = new ConceptDescriptionIdCollector(environment);
		
		assertEquals(expectedCDIDs, cdIdCollector.collect());
	}
	
	private void defaultSetup() {
		Submodel dummySM1 = DummySubmodelFactory.createTechnicalDataSubmodel();
		
		Submodel dummySM2 = DummySubmodelFactory.createOperationalDataSubmodel();

		AssetAdministrationShell dummyAAS1 = new DefaultAssetAdministrationShell.Builder().id("dummyAAS1Id")
				.idShort("dummyAAS1IdShort")
				.submodels(new DefaultReference.Builder()
						.keys(Arrays.asList(createDefaultSubmodelKey(dummySM1.getId()), createDefaultSubmodelKey(dummySM2.getId()))).build())
				.build();
		
		environment = new DefaultEnvironment.Builder().assetAdministrationShells(dummyAAS1).submodels(Arrays.asList(dummySM1, dummySM2)).build();
	}
	
	private void customSetup() {
		Submodel dummySM1 = new DefaultSubmodel.Builder().submodelElements(createDummySMEs()).idShort("dummySM1IdShort")
				.id("dummySM1Id").build();
		
		Submodel dummySM2 = new DefaultSubmodel.Builder().submodelElements(createDummySMEWithoutCDSemantic()).idShort("dummySM2IdShort")
				.id("dummySM2Id").build();

		AssetAdministrationShell dummyAAS1 = new DefaultAssetAdministrationShell.Builder().id("dummyAAS1Id")
				.idShort("dummyAAS1IdShort")
				.submodels(new DefaultReference.Builder()
						.keys(Arrays.asList(createDefaultSubmodelKey("dummySM1Id"), createDefaultSubmodelKey("dummySM2Id"))).build())
				.build();
		
		environment = new DefaultEnvironment.Builder().assetAdministrationShells(dummyAAS1).submodels(Arrays.asList(dummySM1, dummySM2)).build();
	}
	
	private DefaultKey createDefaultSubmodelKey(String submodelId) {
		return new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(submodelId).build();
	}
	
	private List<SubmodelElement> createDummySMEs() {
		Property dummyProperty1 = new DefaultProperty.Builder().idShort("dummyProperty1IdShort")
				.value("dummyProperty1Value")
				.semanticId(new DefaultReference.Builder().keys(
						new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION).value("dummyProperty1CDId").build())
						.build())
				.build();
		Property dummyProperty2 = new DefaultProperty.Builder().idShort("dummyProperty2IdShort")
				.value("dummyProperty2Value")
				.semanticId(new DefaultReference.Builder().keys(
						new DefaultKey.Builder().type(KeyTypes.CAPABILITY).value("dummyProperty2CapabilityId").build())
						.build())
				.build();
		Property dummyProperty3 = new DefaultProperty.Builder().idShort("dummyProperty3IdShort")
				.value("dummyProperty3Value")
				.semanticId(new DefaultReference.Builder().keys(
						new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION).value("dummyProperty3CDId").build())
						.build())
				.build();
		SubmodelElementCollection dummySMC1 = new DefaultSubmodelElementCollection.Builder().idShort("dummySMC1IdShort")
				.value(dummyProperty3)
				.semanticId(new DefaultReference.Builder().keys(
						new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION).value("dummySMC1CDId").build())
						.build())
				.build();

		return Arrays.asList(dummyProperty1, dummyProperty2, dummySMC1);
	}
	
	private SubmodelElement createDummySMEWithoutCDSemantic() {
		return new DefaultProperty.Builder().idShort("dummyNoCDSemanticPropertyIdShort")
				.value("dummyNoCDSemanticPropertyValue")
				.semanticId(new DefaultReference.Builder().keys(
						new DefaultKey.Builder().type(KeyTypes.BLOB).value("dummyNoCDSemanticPropertyBlobId").build())
						.build())
				.build();
	}
	
}
