package org.eclipse.digitaltwin.basyx.aasregistry.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GenerateByteUrlEncodedBase64Id {

	  public static void main(String[] args) throws UnsupportedEncodingException {
		  if (args.length != 1) {
			  throw new IllegalArgumentException("One argument expected!");
		  }
		  String id = args[0];
		  byte[] values = Base64.getUrlEncoder().encode(id.getBytes(StandardCharsets.UTF_8));
		  System.out.print('[');
		  
		  for (int i = 0; i < values.length; i++) {
			  System.out.print(values[i]);
			  if (i+1 != values.length) {
				  System.out.print(", ");
			  }
		  }
		  System.out.print(']');
	}
	  
}
