//package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.confguration;
//
//import org.eclipse.digitaltwin.basyx.aasregistry.service.api.LocationBuilder;
//import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;
//import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
//import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.RegistrationEventSendingAasRegistryStorage;
//import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.MongoDbAasRegistryStorage;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//@Configuration
//@Profile("MongoDB")
//public class ProfileConfigurations {
//
//    @Bean
//    public AasRegistryStorage baseMongoStorage(MongoTemplate mongoTemplate) {
//        return new MongoDbAasRegistryStorage(mongoTemplate);
//    }
//
//    @Bean
//    @Primary
//    public AasRegistryStorage aasRegistryStorage(
//            @Lazy AasRegistryStorage baseMongoStorage,  // Use @Lazy to break circular reference
//            RegistryEventSink eventSink,
//            LocationBuilder locationBuilder) {
//        return new RegistrationEventSendingAasRegistryStorage(baseMongoStorage, eventSink);
//    }
//}