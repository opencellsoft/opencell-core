package org.meveo.commons.utils;

import java.util.Date;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
	
	static  Gson gson = null;
	static{
	     GsonBuilder builder = new GsonBuilder();
	    builder.setVersion(1.0);
	    gson = builder.setPrettyPrinting().registerTypeAdapter(java.util.Date.class, new MillisDate()).create();
	}
	
	
	public static String toJson(Object object) {
	    if (object == null) {
	        return "";
	    }	    
	    return gson.toJson(object);
	}
	
	public static <T> T toObject(String jsonString,Class<T> clazz) {
	    if (jsonString == null) {
	        return null;
	    }	    
	    return gson.fromJson(jsonString, clazz);
	}

}