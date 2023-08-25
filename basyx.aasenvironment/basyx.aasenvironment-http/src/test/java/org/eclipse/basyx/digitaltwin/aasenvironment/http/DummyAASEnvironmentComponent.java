package org.eclipse.basyx.digitaltwin.aasenvironment.http;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasenvironment.TestAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.InMemoryAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.InMemoryConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx")
public class DummyAASEnvironmentComponent {
	public static final String AAS_TECHNICAL_DATA_ID = "shell001";
	public static final String AAS_OPERATIONAL_DATA_ID = "shell002";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	public static final String SUBMODEL_OPERATIONAL_DATA_ID = "AC69B1CB44F07935";
	public static final String CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV = "IdNotToBeIncludedInSerializedEnv";
	@Bean
	public AasEnvironmentSerialization createAasEnvironment() {
		SubmodelRepository submodelRepository = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
		AasRepository aasRepository = new InMemoryAasRepository(new InMemoryAasServiceFactory());
		ConceptDescriptionRepository conceptDescriptionRepository = new InMemoryConceptDescriptionRepository(TestAASEnvironmentSerialization.createDummyConceptDescriptions());
		
		for (Submodel submodel : createDummySubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}

		for (AssetAdministrationShell shell : createDummyShells()) {
			aasRepository.createAas(shell);
		}

		return new DefaultAASEnvironmentSerialization(aasRepository, submodelRepository, conceptDescriptionRepository);
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
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetID(AAS_OPERATIONAL_DATA_ID).build()).build();
		Collection<AssetAdministrationShell> shells = new ArrayList<>();
		shells.add(shell1);
		shells.add(shell2);
		return shells;
	}

}
