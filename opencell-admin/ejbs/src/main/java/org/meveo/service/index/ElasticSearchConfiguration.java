package org.meveo.service.index;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.util.ApplicationProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads Elastic Search configuration
 *
 * @author Andrius Karpavicius
 */
@Singleton
@Lock(LockType.READ)
public class ElasticSearchConfiguration implements Serializable {

    private static final long serialVersionUID = 7200163625956435849L;

    private static String DEFAULT = "default";

    /**
     * Contains a mapping of classnames to Elastic Search index name. Index name does not contain provider code prefix.
     */
    private Map<String, String> indexMap = new HashMap<>();

    /**
     * Contains a mapping of classnames to Elastic Search type
     */
    private Map<String, String> typeMap = new HashMap<>();

    /**
     * Contains a set of classames that should use upsert
     */
    private Set<String> upsertMap = new HashSet<>();

    /**
     * Contains a mapping of Elastic Search field to entity field per classname
     */
    private Map<String, Map<String, String>> fieldMap = new HashMap<>();

    /**
     * Contains index configuration/data model for each index. Index name does not contain provider code prefix.
     */
    private Map<String, String> dataModels = new HashMap<>();

    /**
     * Contains mapping rules for custom fields. Map key is an EL expression.
     */
    private Map<String, String> customFieldTemplates = new HashMap<>();

    /**
     * Contains custom entity template data model
     */
    private String customEntityTemplate = null;

    // @Inject
    // private Logger log;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /**
     * Load configuration from elasticSearchConfiguration.json file.
     * 
     * @throws IOException I/O exception
     * @throws JsonProcessingException Json processing exception.
     */
    public void loadConfiguration() throws JsonProcessingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(this.getClass().getClassLoader().getResourceAsStream("elasticSearchConfiguration.json"));

        // Load entity mapping to index, type and update type
        Iterator<Entry<String, JsonNode>> entityMappings = node.get("entityMapping").fields();

        while (entityMappings.hasNext()) {

            Entry<String, JsonNode> entityMappingInfo = entityMappings.next();

            String[] classnames = StringUtils.stripAll(entityMappingInfo.getKey().split(","));

            for (String classname : classnames) {

                JsonNode entityMapping = entityMappingInfo.getValue();

                indexMap.put(classname, entityMapping.get("index").textValue());
                if (entityMapping.has("type")) {
                    typeMap.put(classname, entityMapping.get("type").textValue());
                } else {
                    typeMap.put(classname, classname);
                }
                if (entityMapping.has("upsert") && entityMapping.get("upsert").asBoolean()) {
                    upsertMap.add(classname);
                }
            }
        }

        // Load entity field mapping to JSON
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

        // Load index data model: settings, mappings and aliases
        Iterator<Entry<String, JsonNode>> dataModelInfos = node.get("dataModel").fields();

        while (dataModelInfos.hasNext()) {
            Entry<String, JsonNode> dataModelInfo = dataModelInfos.next();
            dataModels.put(dataModelInfo.getKey(), dataModelInfo.getValue().toString());
        }

        // Load customField field mapping templates
        Iterator<Entry<String, JsonNode>> fieldTemplateInfos = node.get("customFieldTemplates").fields();

        while (fieldTemplateInfos.hasNext()) {
            Entry<String, JsonNode> fieldTemplateInfo = fieldTemplateInfos.next();
            customFieldTemplates.put(fieldTemplateInfo.getKey(), fieldTemplateInfo.getValue().toString());
        }

        // Load customEntity field mapping template - only the first one is used.
        Iterator<Entry<String, JsonNode>> cetTemplateInfos = node.get("cetTemplates").fields();
        if (cetTemplateInfos.hasNext()) {
            customEntityTemplate = cetTemplateInfos.next().getValue().toString();
        }
    }

    /**
     * Determine index value for Elastic Search for a given entity. Index name is prefixed by provider code (removed spaces and lowercase).
     *
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return Index property name
     */
    public String getIndex(ISearchable entity) {
        return getIndex(entity.getClass());
    }

    /**
     * Determine index value for Elastic Search for a given entity class and provider. Index name is prefixed by provider code (removed spaces and lowercase).
     *
     * @param clazzToConvert Entity class
     * @return Index property name
     */
    @SuppressWarnings("rawtypes")
    public String getIndex(Class<? extends ISearchable> clazzToConvert) {

        String indexPrefix = ElasticClient.cleanUpAndLowercaseCode(appProvider.getCode());

        Class clazz = clazzToConvert;
        while (clazz != null && !ISearchable.class.equals(clazz)) {
            if (indexMap.containsKey(clazz.getSimpleName())) {
                return indexPrefix + "_" + indexMap.get(clazz.getSimpleName());
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    /**
     * Get a unique list of indexes for given entity classes. Index names are prefixed by provider code (removed spaces and lowercase).
     * 
     * @param classesInfo A list of entity class information
     * @return A set of index property names
     */
    public Set<String> getIndexes(List<ElasticSearchClassInfo> classesInfo) {

        Set<String> indexes = new HashSet<>();

        for (ElasticSearchClassInfo classInfo : classesInfo) {
            indexes.add(getIndex(classInfo.getClazz()));
        }

        return indexes;
    }

    /**
     * Get a unique list of indexes. Index names are prefixed by provider code (removed spaces and lowercase).
     * 
     * @return A set of index property names
     */
    public Set<String> getIndexes() {

        String indexPrefix = ElasticClient.cleanUpAndLowercaseCode(appProvider.getCode());

        Set<String> indexNames = new HashSet<>();

        for (String indexName : indexMap.values()) {
            indexNames.add(indexPrefix + "_" + indexName);
        }

        return indexNames;
    }

    /**
     * Determine Type value for Elastic Search for a given entity. If nothing found in configuration, a default value - classname will be used
     * 
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return Type property name
     */
    public String getType(ISearchable entity) {
        String cetCode = null;
        if (entity instanceof CustomEntityInstance) {
            cetCode = ((CustomEntityInstance) entity).getCetCode();
        }
        return getType(entity.getClass(), cetCode);
    }

    /**
     * Determine Type value for Elastic Search for a given class. If nothing found in configuration, a default value - classname will be used
     * 
     * @param clazzToConvert Entity class
     * @param cetCode cet code
     * @return Type property name
     */
    @SuppressWarnings("rawtypes")
    public String getType(Class<? extends ISearchable> clazzToConvert, String cetCode) {

        Class clazz = clazzToConvert;
        while (!ISearchable.class.equals(clazz)) {
            if (typeMap.containsKey(clazz.getSimpleName())) {
                String type = typeMap.get(clazz.getSimpleName());

                if (type.startsWith("#")) {
                    return ValueExpressionWrapper.evaluateToStringIgnoreErrors(type, "cetCode", ElasticClient.cleanUpCode(cetCode));

                } else {
                    return type;
                }
            }
            clazz = clazz.getSuperclass();
        }

        return ReflectionUtils.getCleanClassName(clazzToConvert.getSimpleName());
    }

    /**
     * Get a unique list of types for given entity classes
     * 
     * @param classesInfo A list of entity class information
     * @return A set of Type property names
     */
    public Set<String> getTypes(List<ElasticSearchClassInfo> classesInfo) {

        Set<String> types = new HashSet<>();

        for (ElasticSearchClassInfo classInfo : classesInfo) {
            types.add(getType(classInfo.getClazz(), classInfo.getCetCode()));
        }

        return types;
    }

    /**
     * Determine if upsert (update if exist or create is not exist) should be done instead of just update in Elastic Search for a given entity. Assume False if nothing found in
     * configuration.
     * 
     * @param entity Business entity to be stored/indexed in Elastic Search
     * @return True if upsert should be used
     */
    @SuppressWarnings("rawtypes")
    public boolean isDoUpsert(ISearchable entity) {

        Class clazz = entity.getClass();

        while (clazz != null && !ISearchable.class.equals(clazz)) {
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
    public Map<String, String> getFields(ISearchable entity) {

        Class clazz = entity.getClass();

        Map<String, String> fields = new HashMap<>();

        if (fieldMap.containsKey(DEFAULT)) {
            fields.putAll(fieldMap.get(DEFAULT));
        }

        while (!BaseEntity.class.equals(clazz)) {
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

    public Map<String, String> getDataModel() {
        return dataModels;
    }

    /**
     * Get a field mapping configuration for a given custom field template
     * 
     * @param cft Custom field template
     * @return Field mapping JSON string
     */
    public String getCustomFieldMapping(CustomFieldTemplate cft) {

        for (Entry<String, String> fieldTemplate : customFieldTemplates.entrySet()) {
            if (ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(fieldTemplate.getKey(), "cft", cft)) {

                // Change index property to "no" from "analyzed" or "not_analyzed"
                if (cft.getIndexType().isStoreOnly()) {
                    return fieldTemplate.getValue().replace("not_analyzed", "no").replace("analyzed", "no").replace("<fieldName>", cft.getCode());
                } else {
                    return fieldTemplate.getValue().replace("<fieldName>", cft.getCode());
                }
            }
        }
        return null;
    }

    /**
     * Get a field mapping configuration for a given custom entity template
     * 
     * @param cet Custom entity template
     * @return Field mapping JSON string
     */
    public String getCetMapping(CustomEntityTemplate cet) {
        return customEntityTemplate;
    }
}