package org.meveo.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
	
	public static String toJson(Object object) {
	    if (object == null) {
	        return "";
	    }
	    final GsonBuilder builder = new GsonBuilder();
	    builder.setVersion(1.0);
	    final Gson gson = builder.setPrettyPrinting().create();
	    return gson.toJson(object);

	}

}