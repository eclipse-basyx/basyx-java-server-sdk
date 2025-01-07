package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory.AasInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Before;

public class TestAASEnvironmentSerialization extends AASEnvironmentSerializationTestSuite {

	private AasEnvironment aasEnvironment;
	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private ConceptDescriptionRepository conceptDescriptionRepository;
	
	@Before
	public void setup() {
		submodelRepository = new SimpleSubmodelRepositoryFactory(new SubmodelInMemoryBackendProvider(), new InMemorySubmodelServiceFactory(new InMemoryFileRepository())).create();
		aasRepository = new SimpleAasRepositoryFactory(new AasInMemoryBackendProvider(), new InMemoryAasServiceFactory(new InMemoryFileRepository())).create();
		conceptDescriptionRepository = new SimpleConceptDescriptionRepositoryFactory(new ConceptDescriptionInMemoryBackendProvider(), createDummyConceptDescriptions(), "cdRepo").create();

		for (Submodel submodel : createDummySubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}

		for (AssetAdministrationShell shell : createDummyShells()) {
			aasRepository.createAas(shell);
		}

		aasEnvironment = new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
	}
	
	@Override
	public AasEnvironment getAasEnvironment() {
		return this.aasEnvironment;
	}

	@Override
	public AasRepository getAasRepository() {
		return this.aasRepository;
	}

	@Override
	public SubmodelRepository getSubmodelRepository() {
		return this.submodelRepository;
	}

	@Override
	public ConceptDescriptionRepository getConceptDescriptionRepository() {
		return this.conceptDescriptionRepository;
	}
}