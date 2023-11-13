package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CloneFactory<I, O> {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private I input;
	
	public CloneFactory(I input) {
		super();
		this.input = input;
	}
	
	public O create() {
		String serializedLangString = "";
		try {
			serializedLangString = mapper.writeValueAsString(input);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
//		if (input instanceof List) {
//			List<O> langStringList = new ArrayList<O>();
//			
//			try {
//				langStringList = mapper.readValue(serializedLangString, new TypeReference<List<O>>() {
//				});
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
//
//			return (O) langStringList;
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
		
		O administrativeInformation = null;
		
		try {
			administrativeInformation = mapper.readValue(serializedLangString,  new TypeReference<O>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return administrativeInformation;
	}
	
}
