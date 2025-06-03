package org.eclipse.digitaltwin.basyx.aasrepository.http;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Integrationstest für fehlerhafte AAS API-Nutzung mit echter InMemory-Komponente
 */
public class TestAasRepositoryHTTPError extends AasRepositoryHTTPErrorSuite {

    private static ConfigurableApplicationContext appContext;

    @BeforeClass
    public static void startAasRepo() {
        appContext = new SpringApplicationBuilder(DummyAasRepositoryComponent.class).profiles("httptests").run();
    }

    @AfterClass
    public static void stopAasRepo() {
        appContext.close();
    }

    @Override
    protected String getURL() {
        return "http://localhost:8080/shells";
    }

    @Override
    public void resetRepository() {
        AasRepository repo = appContext.getBean(AasRepository.class);
        repo.getAllAas(PaginationInfo.NO_LIMIT)
                .getResult()
                .stream()
                .map(aas -> aas.getId())
                .forEach(repo::deleteAas);
    }
}
