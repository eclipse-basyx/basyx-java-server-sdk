import org.eclipse.digitaltwin.basyx.aasregistry.service.OpenApiGeneratorApplication;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.AasRegistryBulkApiController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class MongoDbAasTransactionsIT {

    static ConfigurableApplicationContext appContext;

    @BeforeClass
    public static void startAasRegistryEnv() throws Exception {
        appContext = new SpringApplication(OpenApiGeneratorApplication.class).run(new String[] {});
    }

    @Test
    public void beans() {
        AasRegistryBulkApiController service = appContext.getBean(AasRegistryBulkApiController.class);
    }
}
