package org.meveo.api.mapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A custom deserializer for list of filters
 */
public class FilterListDeserializer extends JsonDeserializer<Map<String, String>> {

	@Override
	public Map<String, String> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.readValueAsTree();
		Map<String, String> map = new HashMap<>();

		if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                ObjectNode objectNode = (ObjectNode) arrayNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> fieldsIterator = objectNode.fields();
                while (fieldsIterator.hasNext()) {
                    Map.Entry<String, JsonNode> field = fieldsIterator.next();
                    map.put(field.getKey(), field.getValue().asText());
                }
            }
		}
		
		return map;
	}
}
