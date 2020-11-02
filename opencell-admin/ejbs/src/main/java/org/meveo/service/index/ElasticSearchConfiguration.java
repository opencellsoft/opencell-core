/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.ValueExpressionWrapper;
import org.reflections.Reflections;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads Elastic Search configuration
 *
 * @author Andrius Karpavicius
 * @lastModifiedVersion 5.0
 */
@Singleton
@Lock(LockType.READ)
@Deprecated
public class ElasticSearchConfiguration implements Serializable {

    private static final long serialVersionUID = 7200163625956435849L;

    private static String MAPPING_DEFAULT = "default";

    public static final String MAPPING_FIELD_TYPE = "entityType";

    private static String MAPPING_CFT_INDEX_VALUE_PLACEHOLDER = "<indexValue>";

    private static String MAPPING_CFT_STRING_TYPE_PLACEHOLDER = "<keywordOrText>";

    protected static final String MAPPING_DOC_TYPE = "_doc";

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
    private Map<String, String> customFieldTemplates = new LinkedHashMap<>();

    /**
     * Contains index definition template for custom entity instances and custom tables
     */
    private Map<String, String> customEntityTemplates = new HashMap<>();

    @Inject
    private Logger log;

    /**
     * Load configuration from elasticSearchConfiguration.json file.
     * 
     * @throws IOException I/O exception
     * @throws JsonProcessingException Json processing exception.
     */
    @SuppressWarnings("rawtypes")
    public void loadConfiguration() throws JsonProcessingException, IOException {

        JsonNode node = null;
        upsertMap = new HashSet<>();

        ObjectMapper mapper = new ObjectMapper();

        Reflections reflections = ReflectionUtils.getReflectionsInstance("org.meveo");

        ParamBean paramBean = ParamBean.getInstance();
        String configFileParam = paramBean.getProperty("elasticsearch.config.file.path", "");

        if (!StringUtils.isEmpty(configFileParam)) {
            String rootDir = paramBean.getProperty("providers.rootDir", "");
            String providerDir = paramBean.getProperty("provider.rootDir", "");
            Path configFilePath = Paths.get(rootDir + File.separator + providerDir + File.separator + configFileParam);

            if (!Files.exists(configFilePath, LinkOption.NOFOLLOW_LINKS)) {
                throw new FileNotFoundException("The ES config file [" + configFileParam + "] does not exist in the provider root dir.");
            }
            node = mapper.readTree(configFilePath.toFile());

        } else {
            node = mapper.readTree(this.getClass().getClassLoader().getResourceAsStream("elasticSearchConfiguration.json"));
        }

        // Load entity mapping to index, type and update type. In case configuration is provided for a parent class, configuration will be repeated for all subclasses as well.
        Iterator<Entry<String, JsonNode>> entityMappings = node.get("entityMapping").fields();
        while (entityMappings.hasNext()) {

            Entry<String, JsonNode> entityMappingInfo = entityMappings.next();

            String[] classnames = StringUtils.stripAll(entityMappingInfo.getKey().split(","));

            for (String classname : classnames) {
                try {
                    Class clazz = Class.forName(classname);

                    Set<Class<?>> clazzes = ReflectionUtils.getSubclasses(clazz, reflections);
                    if (clazzes == null) {
                        clazzes = new HashSet<>();
                    }
                    clazzes.add(clazz);

                    for (Class<?> classToIndex : clazzes) {

                        if (!ISearchable.class.isAssignableFrom(classToIndex) || Modifier.isAbstract(classToIndex.getModifiers()) || classToIndex.getEnclosingClass() != null) { // Ignoring
                                                                                                                                                                                 // inner
                                                                                                                                                                                 // classes
                            continue;
                        }

                        String classnameToIndex = classToIndex.getName();
                        JsonNode entityMapping = entityMappingInfo.getValue();

                        indexMap.put(classnameToIndex, entityMapping.get("index").textValue());
                        if (entityMapping.has("type")) {
                            typeMap.put(classnameToIndex, entityMapping.get("type").textValue());

                            // It is important the order of class mapping definition is ES config file. Specific class should be defined later.
                        } else if (entityMapping.has("useType")) {
                            if (entityMapping.get("useType").booleanValue()) {
                                typeMap.put(classnameToIndex, classToIndex.getSimpleName());
                            } else {
                                typeMap.remove(classnameToIndex);
                            }
                        }
                        if (entityMapping.has("upsert") && entityMapping.get("upsert").asBoolean()) {
                            upsertMap.add(classnameToIndex);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Can not find class {} defined in ES configuration", classname);
                }
            }
        }

        if (upsertMap.isEmpty()) {
            upsertMap = null;
        }

        // Load entity field mapping to JSON
        Iterator<Entry<String, JsonNode>> entityFieldMappings = node.get("entityFieldMapping").fields();

        while (entityFieldMappings.hasNext()) {

            Entry<String, JsonNode> entityFieldMappingInfo = entityFieldMappings.next();

            List<String> classNamesOrDefault = new ArrayList<String>();
            classNamesOrDefault.add(entityFieldMappingInfo.getKey());

            if (!entityFieldMappingInfo.getKey().equals(MAPPING_DEFAULT)) {
                try {
                    Class clazz = Class.forName(entityFieldMappingInfo.getKey());

                    Set<Class<?>> clazzes = ReflectionUtils.getSubclasses(clazz, reflections);
                    if (clazzes != null) {
                        for (Class<?> subclass : clazzes) {
                            classNamesOrDefault.add(subclass.getName());
                        }
                    }

                } catch (ClassNotFoundException e) {
                    log.error("Can not find class {} defined in ES configuration", entityFieldMappingInfo.getKey());
                }
            }

            JsonNode entityFieldMapping = entityFieldMappingInfo.getValue();
            Iterator<Entry<String, JsonNode>> fieldMappings = entityFieldMapping.fields();

            Map<String, String> fieldMaps = new HashMap<>();
            while (fieldMappings.hasNext()) {
                Entry<String, JsonNode> fieldMappingInfo = fieldMappings.next();
                fieldMaps.put(fieldMappingInfo.getKey(), fieldMappingInfo.getValue().textValue());
            }

            for (String entityKey : classNamesOrDefault) {
                fieldMap.put(entityKey, fieldMaps);
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

        // Load customEntity mapping template
        Iterator<Entry<String, JsonNode>> cetTemplateInfos = node.get("cetTemplates").fields();
        while (cetTemplateInfos.hasNext()) {
            Entry<String, JsonNode> cetTemplateInfo = cetTemplateInfos.next();
            customEntityTemplates.put(cetTemplateInfo.getKey(), cetTemplateInfo.getValue().toString());
        }
    }

    /**
     * Get index name value for a given class name
     * 
     * @param classname Class name
     * @return Index name without provider prefix or EL expression to determine index name without the provider
     */
    public String getIndexName(String classname) {
        return indexMap.get(classname);
    }

    /**
     * Get type name value for a given class name
     * 
     * @param classname Class name
     * @return Type name without provider prefix or EL expression to determine index name without the provider
     */
    public String getType(String classname) {
        return typeMap.get(classname);
    }

    /**
     * Determine if upsert (update if exist or create is not exist) should be done instead of just update in Elastic Search for a given entity. Assume False if nothing found in
     * configuration.
     * 
     * @param entity ISearchable entity to be stored/indexed in Elastic Search
     * @return True if upsert should be used
     */
    public boolean isDoUpsert(ISearchable entity) {

        if (upsertMap == null) {
            return false;
        }

        return isDoUpsert(entity.getClass());
    }

    /**
     * Determine if upsert (update if exist or create is not exist) should be done instead of just update in Elastic Search for a given entity. Assume False if nothing found in
     * configuration.
     * 
     * @param entityClass ISearchable entity to be stored/indexed in Elastic Search
     * @return True if upsert should be used
     */
    @SuppressWarnings("rawtypes")
    public boolean isDoUpsert(Class<? extends ISearchable> entityClass) {

        if (upsertMap == null) {
            return false;
        }

        Class clazz = entityClass;

        while (clazz != null && !ISearchable.class.equals(clazz)) {
            if (upsertMap.contains(clazz.getName())) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }

        return false;
    }

    /**
     * Get a list of fields to be stored in Elastic search for a given entity
     * 
     * @param entity ISearchable entity to be stored/indexed in Elastic Search
     * @return A map of fields with key being fieldname in Json and value being a fieldname in entity. Fieldnames can be simple e.g. "company" or nested e.g.
     *         "company.address.street"
     */
    @SuppressWarnings("rawtypes")
    public Map<String, String> getFields(ISearchable entity) {

        Class clazz = entity.getClass();

        Map<String, String> fields = new HashMap<>();

        if (fieldMap.containsKey(MAPPING_DEFAULT)) {
            fields.putAll(fieldMap.get(MAPPING_DEFAULT));
        }

        if (fieldMap.containsKey(clazz.getName())) {
            fields.putAll(fieldMap.get(clazz.getName()));
        }

        return fields;
    }

    /**
     * Get a list of entity classes that is managed by Elastic Search
     * 
     * @return A list of entity full classnames
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
     * @param cleanupFieldname Should field name (customFieldTemplate.code) be cleanedup - lowercased and spaces replaced by '_'
     * @param isOwnerACustomTable Is entity, that CFT is for, is a custom table
     * @return Field mapping JSON string
     */
    public String getCustomFieldMapping(CustomFieldTemplate cft, boolean cleanupFieldname, boolean isOwnerACustomTable) {

        for (Entry<String, String> fieldTemplate : customFieldTemplates.entrySet()) {
            if (ValueExpressionWrapper.evaluateToBooleanMultiVariable(fieldTemplate.getKey(), "cft", cft, "isOwnerACustomTable", isOwnerACustomTable)) {

                String fieldname = cft.getCode();
                if (cleanupFieldname) {
                    fieldname = BaseEntity.cleanUpAndLowercaseCodeOrId(fieldname);
                }

                String mapping = fieldTemplate.getValue().replace("<fieldName>", fieldname);
                mapping = mapping.replace(MAPPING_CFT_INDEX_VALUE_PLACEHOLDER, cft.getIndexType() == CustomFieldIndexTypeEnum.STORE_ONLY ? "false" : "true");
                mapping = mapping.replace(MAPPING_CFT_STRING_TYPE_PLACEHOLDER, cft.getIndexType() == CustomFieldIndexTypeEnum.INDEX ? "text" : "keyword");

                return mapping;
            }
        }
        return null;
    }

    /**
     * Get a index definition template/configuration for a given custom entity template
     * 
     * @param cet Custom entity template
     * @return Index configuration JSON string
     */
    public String getCetIndexConfiguration(CustomEntityTemplate cet) {

        if (cet.isStoreAsTable()) {
            return customEntityTemplates.get("customTable");
        } else {
            return customEntityTemplates.get("cei");
        }
    }
}