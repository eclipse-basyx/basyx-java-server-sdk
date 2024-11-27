package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestConnectedAasManagerMultithreading {
    static final String AAS_REPOSITORY_BASE_PATH = "http://localhost:8081";
    static final String SM_REPOSITORY_BASE_PATH = "http://localhost:8081";
    static final String AAS_REGISTRY_BASE_PATH = "http://localhost:8050";
    static final String SM_REGISTRY_BASE_PATH = "http://localhost:8060";
    static final int N_THREADS = 20;

    static ConfigurableApplicationContext appContext;
    static AasRepository aasRepository;
    static SubmodelRepository smRepository;

    static ConnectedAasRepository connectedAasRepository;
    static ConnectedSubmodelRepository connectedSmRepository;
    static RegistryAndDiscoveryInterfaceApi aasRegistryApi;
    static SubmodelRegistryApi smRegistryApi;

    static ConnectedAasManager aasManager;

    @BeforeClass
    public static void setupRepositories() {
        appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});

        connectedAasRepository = new ConnectedAasRepository(AAS_REPOSITORY_BASE_PATH);
        connectedSmRepository = new ConnectedSubmodelRepository(SM_REPOSITORY_BASE_PATH);
        aasRegistryApi = new RegistryAndDiscoveryInterfaceApi(AAS_REGISTRY_BASE_PATH);
        smRegistryApi = new SubmodelRegistryApi(SM_REGISTRY_BASE_PATH);
        aasManager = new ConnectedAasManager(AAS_REGISTRY_BASE_PATH, AAS_REPOSITORY_BASE_PATH, SM_REGISTRY_BASE_PATH, SM_REPOSITORY_BASE_PATH);

        cleanUpRegistries();
    }

    @After
    public void cleanUpComponents() {
        cleanUpRegistries();
    }

    @AfterClass
    public static void stopContext() {
        appContext.close();
    }

    @Test
    public void testParallelSubmodelCreation() throws ExecutionException, InterruptedException {
        AssetAdministrationShell shell = createShell();

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        ConcurrentLinkedDeque<String> createdSubmodelIds = new ConcurrentLinkedDeque<>();

        List<Future<Boolean>> futures = IntStream.range(0, N_THREADS).mapToObj(i -> executorService.submit(() -> createdSubmodelIds.add(createSubmodel(shell.getId(), i)))).toList();

        try {
            for (int i = 0; i < N_THREADS; i++) {
                futures.get(i).get();
            }
        } finally {
            executorService.shutdown();
        }

        createdSubmodelIds.forEach(TestConnectedAasManagerMultithreading::assertSubmodelExists);
    }

    static void assertSubmodelExists(String submodelId) {
        assertEquals(submodelId, aasManager.getSubmodelService(submodelId).getSubmodel().getId());
    }

    private static void cleanUpRegistries() {
        try {
            aasRegistryApi.deleteAllShellDescriptors();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            smRegistryApi.deleteAllSubmodelDescriptors();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static AssetAdministrationShell createShell() {
        String id = UUID.randomUUID().toString();
        DefaultAssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder().id(id).build();
        aasManager.createAas(shell);
        return aasManager.getAasService(id).getAAS();
    }

    private static String createSubmodel(String aasId, int threadId) {
        try {
            String id = aasId + "-thread" + threadId;
            DefaultSubmodel submodel = new DefaultSubmodel.Builder().id(id).build();
            aasManager.createSubmodelInAas(aasId, submodel);
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Failed at thread " + threadId, e);
        }
    }

}
