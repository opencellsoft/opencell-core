package org.meveo.client;

import java.lang.reflect.Type;
import java.util.Date;

import com.docapost.portal.model.shared.DateUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MillisDate implements JsonDeserializer<Date>,JsonSerializer<Date> {
	
	
    
	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {		
		return  new Date(new Long(json.getAsString()));
	}

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {		
		return  new JsonPrimitive(DateUtils.formatDateWithPattern(src, "yyyy-MM-dd'T'HH:mm:ss"));
	}
  }