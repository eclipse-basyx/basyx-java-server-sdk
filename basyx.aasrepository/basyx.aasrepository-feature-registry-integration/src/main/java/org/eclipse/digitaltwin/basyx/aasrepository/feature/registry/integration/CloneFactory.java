package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CloneFactory<I, O> {
	
	private ObjectMapper mapper = new ObjectMapper();
	private JsonSerializer serializer = new JsonSerializer();
	
	private JsonDeserializer deserializer = new JsonDeserializer();
	
	private Class<O> elementType;
	
	public CloneFactory(Class<O> elementType) {
		super();
		this.elementType = elementType;
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
			return mapper.readValue(serializedLangString, new TypeReference<O>() {
            });
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
//		return resultList;
	}
	
}
