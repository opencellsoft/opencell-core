package org.meveo.service.index;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Singleton;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class ElasticSearchConfiguration implements Serializable {

    private static final long serialVersionUID = 7200163625956435849L;

    private static String DEFAULT = "default";

    private Map<String, String> indexMap = new HashMap<>();

    private Map<String, String> typeMap = new HashMap<>();

    private Set<String> upsertMap = new HashSet<>();

    private Map<String, Map<String, String>> fieldMap = new HashMap<>();

    private Map<Long, String> providerCodes = new HashMap<>();

    /**
     * Load configuration from elasticSearchConfiguration.json file
     * 
     * @return Configuration instance
     * @throws IOException
     * @throws JsonProcessingException
     */
    public void loadConfiguration() throws JsonProcessingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(this.getClass().getClassLoader().getResourceAsStream("elasticSearchConfiguration.json"));

        Iterator<Entry<String, JsonNode>> entityMappings = node.get("entityMapping").fields();

        while (entityMappings.hasNext()) {

            Entry<String, JsonNode> entityMappingInfo = entityMappings.next();

            JsonNode entityMapping = entityMappingInfo.getValue();

            indexMap.put(entityMappingInfo.getKey(), entityMapping.get("index").textValue());
            if (entityMapping.has("type")) {
                typeMap.put(entityMappingInfo.getKey(), entityMapping.get("type").textValue());
            }
            if (entityMapping.has("upsert") && entityMapping.get("upsert").asBoolean()) {
                upsertMap.add(entityMappingInfo.getKey());
            }
        }

        Iterator<Entry<String, JsonNode>> entityFieldMappings = node.get("entityFieldMapping").fields();

        while (entityFieldMappings.hasNext()) {

            Entry<String, JsonNode> entityFieldMappingInfo = entityFieldMappings.next();

            JsonNode entityFieldMapping = entityFieldMappingInfo.getValue();

            Map<String, String> fieldMaps = new HashMap<>();
            fieldMap.put(entityFieldMappingInfo.getKey(), fieldMaps);

            Iterator<Entry<String, JsonNode>> fieldMappings = entityFieldMapping.fields();

            while (fieldMappings.hasNext()) {
                Entry<String, JsonNode> fieldMappingInfo = fieldMappings.next();
                fieldMaps.put(fieldMappingInfo.getKey(), fieldMappingInfo.getValue().textValue());
            }
        }
    }

    /**
     * Determine index value for Elastic Search for a given entity. Index name is prefixed by provider code (removed spaces and lowercase).
     * 
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return Index property name
     */
    public String getIndex(BusinessEntity entity) {
        return getIndex(entity.getClass(), entity.getProvider());
    }

    /**
     * Determine index value for Elastic Search for a given entity class and provider. Index name is prefixed by provider code (removed spaces and lowercase).
     * 
     * @param clazzToConvert Entity class
     * @param provider Provider
     * @return Index property name
     */
    @SuppressWarnings("rawtypes")
    public String getIndex(Class<? extends BusinessEntity> clazzToConvert, Provider provider) {

        Class clazz = clazzToConvert;
        while (!BusinessEntity.class.equals(clazz)) {
            if (indexMap.containsKey(clazz.getSimpleName())) {

                if (!providerCodes.containsKey(provider.getId())) {
                    providerCodes.put(provider.getId(), provider.getCode().replace(' ', '_').toLowerCase());
                }
                return providerCodes.get(provider.getId()) + "_" + indexMap.get(clazz.getSimpleName());
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    /**
     * Get a unique list of indexes for given entity classes
     * 
     * @param provider Provider
     * @param clazzes A list of entity classes
     * @return A set of index property names
     */
    @SuppressWarnings("unchecked")
    public Set<String> getIndexes(Provider provider, Class<? extends BusinessEntity>... clazzes) {

        Set<String> indexes = new HashSet<>();

        for (Class<? extends BusinessEntity> clazz : clazzes) {
            indexes.add(getIndex(clazz, provider));
        }

        return indexes;
    }

    /**
     * Determine Type value for Elastic Search for a given entity. If nothing found in configuration, a default value - classname will be used
     * 
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return Type property name
     */
    public String getType(BusinessEntity entity) {
        return getType(entity.getClass());
    }

    /**
     * Determine Type value for Elastic Search for a given class. If nothing found in configuration, a default value - classname will be used
     * 
     * @param clazzToConvert Entity class
     * @return Type property name
     */
    @SuppressWarnings("rawtypes")
    public String getType(Class<? extends BusinessEntity> clazzToConvert) {

        Class clazz = clazzToConvert;
        while (!BusinessEntity.class.equals(clazz)) {
            if (typeMap.containsKey(clazz.getSimpleName())) {
                return typeMap.get(clazz.getSimpleName());
            }
            clazz = clazz.getSuperclass();
        }

        return ReflectionUtils.getCleanClassName(clazzToConvert.getSimpleName());
    }

    /**
     * Get a unique list of types for given entity classes
     * 
     * @param clazzes A list of entity classes
     * @return A set of Type property names
     */
    @SuppressWarnings("unchecked")
    public Set<String> getTypes(Class<? extends BusinessEntity>... clazzes) {

        Set<String> types = new HashSet<>();

        for (Class<? extends BusinessEntity> clazz : clazzes) {
            types.add(getType(clazz));
        }

        return types;
    }

    /**
     * Determine if upsert (update if exist or create is not exist) should be done instead of just update in Elastic Search for a given entity. Assume False if nothing found in
     * configuration.
     * 
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return True if upsrt should be used
     */
    @SuppressWarnings("rawtypes")
    public boolean isDoUpsert(BusinessEntity entity) {

        Class clazz = entity.getClass();

        while (!BusinessEntity.class.equals(clazz)) {
            if (upsertMap.contains(clazz.getSimpleName())) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }

        return false;
    }

    /**
     * Get a list of fields to be stored in Elastic search for a given entity
     * 
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return A map of fields with key being fieldname in Json and value being a fieldname in entity. Fieldnames can be simple e.g. "company" or nested e.g.
     *         "company.address.street"
     */
    @SuppressWarnings("rawtypes")
    public Map<String, String> getFields(BusinessEntity entity) {

        Class clazz = entity.getClass();

        Map<String, String> fields = new HashMap<>();

        if (fieldMap.containsKey(DEFAULT)) {
            fields.putAll(fieldMap.get(DEFAULT));
        }

        while (!BusinessEntity.class.equals(clazz)) {
            if (fieldMap.containsKey(clazz.getSimpleName())) {
                fields.putAll(fieldMap.get(clazz.getSimpleName()));
            }
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    /**
     * Get a list of entity classes that is managed by Elastic Search
     * 
     * @return A list of entity simple classnames
     */
    public Set<String> getEntityClassesManaged() {
        return indexMap.keySet();
    }
}