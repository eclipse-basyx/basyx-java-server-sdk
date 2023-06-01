package org.eclipse.digitaltwin.basyx.aasenvironment;

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
import org.eclipse.digitaltwin.aas4j.v3.dataformat.Deserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.InMemoryAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestAASEnvironmentSerialization {
	public static final String AAS_TECHNICAL_DATA_ID = "shell001";
	public static final String AAS_OPERATIONAL_DATA_ID = "shell002";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	public static final String SUBMODEL_OPERATIONAL_DATA_ID = "AC69B1CB44F07935";

	private AasEnvironmentSerialization aasEnvironment;

	@Before
	public void setup() {
		SubmodelRepository submodelRepository = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
		AasRepository aasRepository = new InMemoryAasRepository(new InMemoryAasServiceFactory());

		for (Submodel submodel : createDummySubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}

		for (AssetAdministrationShell shell : createDummyShells()) {
			aasRepository.createAas(shell);
		}
		aasEnvironment = new DefaultAASEnvironmentSerialization(aasRepository, submodelRepository);
	}

	private Collection<Submodel> createDummySubmodels() {
		Collection<Submodel> submodels = new ArrayList<>();
		submodels.add(DummySubmodelFactory.createOperationalDataSubmodel());
		submodels.add(DummySubmodelFactory.createTechnicalDataSubmodel());
		return submodels;
	}

	private Collection<AssetAdministrationShell> createDummyShells() {
		AssetAdministrationShell shell1 = new DefaultAssetAdministrationShell.Builder().id(AAS_TECHNICAL_DATA_ID).idShort(AAS_TECHNICAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetID(SUBMODEL_TECHNICAL_DATA_ID).build()).build();

		AssetAdministrationShell shell2 = new DefaultAssetAdministrationShell.Builder().id(AAS_OPERATIONAL_DATA_ID).idShort(AAS_OPERATIONAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetID(SUBMODEL_TECHNICAL_DATA_ID).build()).build();
		Collection<AssetAdministrationShell> shells = new ArrayList<>();
		shells.add(shell1);
		shells.add(shell2);
		return shells;
	}

	@Test
	public void testAASEnviromentSerializationWithJSON() throws SerializationException, IOException, DeserializationException {
		String jsonSerialization = aasEnvironment.createJSONAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()));
		validateJSON(jsonSerialization);
	}

	@Test
	public void testAASEnviromentSerializationWithXML() throws SerializationException, IOException, SAXException, DeserializationException {
		String xmlSerialization = aasEnvironment.createXMLAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()));
		validateXml(xmlSerialization);
	}

	@Test
	public void testAASEnviromentSerializationWithAASX() throws SerializationException, IOException, InvalidOperationException, InvalidFormatException, DeserializationException {
		byte[] serialization = aasEnvironment.createAASXAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()));
		checkAASX(new ByteArrayInputStream(serialization));
	}

	public static void validateJSON(String actual) throws DeserializationException {
		Deserializer jsonDeserializer = new JsonDeserializer();
		Environment aasEnvironment = jsonDeserializer.read(actual);
		checkAASEnvironment(aasEnvironment);
	}

	private static void checkAASEnvironment(Environment aasEnvironment) {
		List<String> aasIds = retrieveShellIds(aasEnvironment);
		assertTrue(aasIds.contains(AAS_TECHNICAL_DATA_ID));
		assertTrue(aasIds.contains(AAS_OPERATIONAL_DATA_ID));

		List<String> submodelIds = retrieveSubmodelIds(aasEnvironment);
		assertTrue(submodelIds.contains(SUBMODEL_OPERATIONAL_DATA_ID));
		assertTrue(submodelIds.contains(SUBMODEL_TECHNICAL_DATA_ID));
	}

	public static void validateXml(String actual) throws DeserializationException {
		Deserializer xmlDeserializer = new XmlDeserializer();
		Environment aasEnvironment = xmlDeserializer.read(actual);

		checkAASEnvironment(aasEnvironment);
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

	public static void checkAASX(InputStream inputStream) throws IOException, InvalidFormatException, DeserializationException {
		AASXDeserializer aasxDeserializer = new AASXDeserializer(inputStream);
		Environment environment = aasxDeserializer.read();

		checkAASEnvironment(environment);
		inputStream.close();
	}

	private List<String> getSubmodelIds(Collection<Submodel> submodels) {
		return submodels.stream().map(sm -> ((DefaultSubmodel) sm).getId()).collect(Collectors.toList());
	}

	private List<String> getShellIds(Collection<AssetAdministrationShell> shells) {
		return shells.stream().map(shell -> ((DefaultAssetAdministrationShell) shell).getId()).collect(Collectors.toList());
	}

}
