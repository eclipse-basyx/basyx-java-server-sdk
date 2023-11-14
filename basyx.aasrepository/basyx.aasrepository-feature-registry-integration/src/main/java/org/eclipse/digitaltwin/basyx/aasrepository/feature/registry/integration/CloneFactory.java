package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer.KeyTypeDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer.ReferenceTypeDeserializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin.AdministrativeInformationMixin;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin.ReferenceMixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CloneFactory<I, O> {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private Class<O> elementType;
	
	public CloneFactory(Class<O> elementType) {
		super();
		this.elementType = elementType;
		
		configureMixins();
	}
	
	private void configureMixins() {
		SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(AdministrativeInformation.class, AdministrativeInformationMixin.class);
        mapper.registerModule(module);
        
        SimpleModule module2 = new SimpleModule();
        module2.setMixInAnnotation(Reference.class, ReferenceMixin.class);
        mapper.registerModule(module2);
        
        SimpleModule moduleDeser = new SimpleModule();
        moduleDeser.addDeserializer(KeyTypes.class, new KeyTypeDeserializer());
        mapper.registerModule(moduleDeser);
        
        SimpleModule moduleDeser2 = new SimpleModule();
        moduleDeser2.addDeserializer(ReferenceTypes.class, new ReferenceTypeDeserializer());
        mapper.registerModule(moduleDeser2);
	}

//	public O create() {
//		String serializedLangString = "";
//		try {
//			serializedLangString = mapper.writeValueAsString(input);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		
//		O administrativeInformation = null;
//		
//		try {
//			administrativeInformation = mapper.readValue(serializedLangString,  new TypeReference<O>() {});
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		
//		return administrativeInformation;
//	}
	
	public List<O> create(List<I> input) {
	    String serializedLangString = "";
	    try {
	        serializedLangString = mapper.writeValueAsString(input);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }

	    List<O> resultList = null;

	    try {
	        resultList = mapper.readValue(serializedLangString, mapper.getTypeFactory().constructCollectionType(List.class, elementType));
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }

	    return resultList;
	}
	
	public O create(I input) {
		String serializedLangString = "";
		try {
			serializedLangString = mapper.writeValueAsString(input);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
//		try {
//			resultList = mapper.readValue(serializedLangString, mapper.getTypeFactory().constructCollectionType(List.class, elementType));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		
		try {
			return mapper.readValue(serializedLangString, elementType);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
//		return resultList;
	}
	
}
