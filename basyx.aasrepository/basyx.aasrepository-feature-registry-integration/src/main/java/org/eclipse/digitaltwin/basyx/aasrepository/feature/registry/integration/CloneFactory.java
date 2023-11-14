package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin.AdministrativeInformationMixin;

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
