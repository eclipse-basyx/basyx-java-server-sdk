package org.eclipse.digitaltwin.basyx.aasenvironment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory.AasInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.InMemoryConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestAASEnvironmentSerialization {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, "");
	public static final String AAS_TECHNICAL_DATA_ID = "shell001";
	public static final String AAS_OPERATIONAL_DATA_ID = "shell002";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	public static final String SUBMODEL_OPERATIONAL_DATA_ID = "AC69B1CB44F07935";
	public static final String CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV = "IdNotToBeIncludedInSerializedEnv";

	private AasEnvironmentSerialization aasEnvironment;
	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private ConceptDescriptionRepository conceptDescriptionRepository;

	@Before
	public void setup() {
		submodelRepository = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
		aasRepository = new SimpleAasRepositoryFactory(new AasInMemoryBackendProvider(), new InMemoryAasServiceFactory()).create();
		conceptDescriptionRepository = new InMemoryConceptDescriptionRepository(createDummyConceptDescriptions());

		for (Submodel submodel : createDummySubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}

		for (AssetAdministrationShell shell : createDummyShells()) {
			aasRepository.createAas(shell);
		}

		aasEnvironment = new DefaultAASEnvironmentSerialization(aasRepository, submodelRepository, conceptDescriptionRepository);
	}

	private static Collection<Submodel> createDummySubmodels() {
		Collection<Submodel> submodels = new ArrayList<>();
		submodels.add(DummySubmodelFactory.createOperationalDataSubmodel());
		submodels.add(DummySubmodelFactory.createTechnicalDataSubmodel());
		return submodels;
	}

	private static Collection<AssetAdministrationShell> createDummyShells() {
		AssetAdministrationShell shell1 = new DefaultAssetAdministrationShell.Builder().id(AAS_TECHNICAL_DATA_ID).idShort(AAS_TECHNICAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(SUBMODEL_TECHNICAL_DATA_ID).build()).build();

		AssetAdministrationShell shell2 = new DefaultAssetAdministrationShell.Builder().id(AAS_OPERATIONAL_DATA_ID).idShort(AAS_OPERATIONAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(SUBMODEL_TECHNICAL_DATA_ID).build()).build();
		Collection<AssetAdministrationShell> shells = new ArrayList<>();
		shells.add(shell1);
		shells.add(shell2);
		return shells;
	}

	@Test
	public void testAASEnviromentSerializationWithJSON() throws SerializationException, IOException, DeserializationException {
		boolean includeConceptDescription = true;

		String jsonSerialization = aasEnvironment.createJSONAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		validateJSON(jsonSerialization, includeConceptDescription);

		validateRepositoriesState();
	}

	@Test
	public void testAASEnviromentSerializationWithXML() throws SerializationException, IOException, SAXException, DeserializationException {
		boolean includeConceptDescription = true;

		String xmlSerialization = aasEnvironment.createXMLAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		validateXml(xmlSerialization, includeConceptDescription);

		validateRepositoriesState();
	}

	@Test
	public void testAASEnviromentSerializationWithAASX() throws SerializationException, IOException, InvalidOperationException, InvalidFormatException, DeserializationException {
		boolean includeConceptDescription = true;

		byte[] serialization = aasEnvironment.createAASXAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		checkAASX(new ByteArrayInputStream(serialization), includeConceptDescription);

		validateRepositoriesState();
	}

	@Test
	public void testAASEnviromentSerializationWithJSONExcludeCD() throws SerializationException, IOException, DeserializationException {
		boolean includeConceptDescription = false;

		String jsonSerialization = aasEnvironment.createJSONAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		validateJSON(jsonSerialization, includeConceptDescription);

		validateRepositoriesState();
	}

	public static void validateJSON(String actual, boolean includeConceptDescription) throws DeserializationException {
		JsonDeserializer jsonDeserializer = new JsonDeserializer();
		Environment aasEnvironment = jsonDeserializer.read(actual);
		checkAASEnvironment(aasEnvironment, includeConceptDescription);
	}

	public static void validateXml(String actual, boolean includeConceptDescription) throws DeserializationException {
		XmlDeserializer xmlDeserializer = new XmlDeserializer();
		Environment aasEnvironment = xmlDeserializer.read(actual);

		checkAASEnvironment(aasEnvironment, includeConceptDescription);
	}

	public static void checkAASX(InputStream inputStream, boolean includeConceptDescription) throws IOException, InvalidFormatException, DeserializationException {
		AASXDeserializer aasxDeserializer = new AASXDeserializer(inputStream);
		Environment environment = aasxDeserializer.read();

		checkAASEnvironment(environment, includeConceptDescription);
		inputStream.close();
	}

	public static Collection<ConceptDescription> createDummyConceptDescriptions() {
		Collection<ConceptDescription> conceptDescriptions = new ArrayList<>();

		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY).build());
		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY).build());
		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV).build());

		return conceptDescriptions;
	}

	private static void checkAASEnvironment(Environment aasEnvironment, boolean areConceptDescriptionsIncluded) {
		assertAasIds(aasEnvironment);

		assertSubmodelIds(aasEnvironment);

		if (areConceptDescriptionsIncluded) {
			assertConceptDescriptionIds(aasEnvironment);
		}
	}

	private static void assertConceptDescriptionIds(Environment aasEnvironment) {
		List<String> conceptDescriptionIds = retrieveConceptDescriptionIds(aasEnvironment);
		assertTrue(conceptDescriptionIds.contains(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY));
		assertTrue(conceptDescriptionIds.contains(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY));
		assertFalse(conceptDescriptionIds.contains(CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV));
	}

	private static void assertSubmodelIds(Environment aasEnvironment) {
		List<String> submodelIds = retrieveSubmodelIds(aasEnvironment);
		assertTrue(submodelIds.contains(SUBMODEL_OPERATIONAL_DATA_ID));
		assertTrue(submodelIds.contains(SUBMODEL_TECHNICAL_DATA_ID));
	}

	private static void assertAasIds(Environment aasEnvironment) {
		List<String> aasIds = retrieveShellIds(aasEnvironment);
		assertTrue(aasIds.contains(AAS_TECHNICAL_DATA_ID));
		assertTrue(aasIds.contains(AAS_OPERATIONAL_DATA_ID));
	}

	private List<String> getSubmodelIds(Collection<Submodel> submodels) {
		return submodels.stream().map(sm -> ((DefaultSubmodel) sm).getId()).collect(Collectors.toList());
	}

	private List<String> getShellIds(Collection<AssetAdministrationShell> shells) {
		return shells.stream().map(shell -> ((DefaultAssetAdministrationShell) shell).getId()).collect(Collectors.toList());
	}

	private static List<String> retrieveSubmodelIds(Environment aasEnvironment) {
		List<String> submodelIds = new ArrayList<>();
		aasEnvironment.getSubmodels().forEach(s -> {
			submodelIds.add(((DefaultSubmodel) s).getId());
		});
		return submodelIds;
	}

	private static List<String> retrieveShellIds(Environment aasEnvironment) {
		List<String> aasIds = new ArrayList<>();

		aasEnvironment.getAssetAdministrationShells().forEach(a -> {
			aasIds.add(((DefaultAssetAdministrationShell) a).getId());
		});
		return aasIds;
	}

	private static List<String> retrieveConceptDescriptionIds(Environment aasEnvironment) {
		return aasEnvironment.getConceptDescriptions().stream().map(cd -> cd.getId()).collect(Collectors.toList());
	}

	private void validateRepositoriesState() {
		assertTrue(aasRepository.getAllAas(NO_LIMIT_PAGINATION_INFO).getResult().containsAll(createDummyShells()));
		assertTrue(submodelRepository.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().containsAll(createDummySubmodels()));
		assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO).getResult().containsAll(createDummyConceptDescriptions()));
	}

}
