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

package org.meveo.service.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.EnumUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.FilterParameterTypeEnum;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;
import org.meveo.model.filter.Projector;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author Edward P. Legaspi
 * @author anasseh
 **/
@Stateless
public class FilterService extends BusinessService<Filter> {

    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private Validator validator;

    private static final String POSITION_PREFIX = "tab:Custom fields:0;field:";

    private static final List<String> requiresClassName = Arrays.asList(FilterParameterTypeEnum.ENTITY.getPrefix(), FilterParameterTypeEnum.ENUM.getPrefix());

    public Filter parse(String xmlInput) throws XStreamException {
        xmlInput = xmlInput.trim();
        Filter result = new Filter();

        XStream xstream = getXStream();
        result = (Filter) xstream.fromXML(xmlInput);
        if (result.getPrimarySelector() != null && StringUtils.isBlank(result.getPrimarySelector().getAlias())) {
            result.getPrimarySelector().setAlias("entity");
        }

        return result;
    }

    /**
     * Use in the UI when creating a filter hierarchy from xml.
     * 
     * @return
     */
    private XStream getXStream() {
        XStream xStream = new XStream();
        // rename the selector field
        xStream.alias("andCompositeFilterCondition", AndCompositeFilterCondition.class);
        xStream.alias("filter", Filter.class);
        xStream.alias("filterCondition", FilterCondition.class);
        xStream.alias("filterSelector", FilterSelector.class);
        xStream.alias("nativeFilterCondition", NativeFilterCondition.class);
        xStream.alias("orCompositeFilterCondition", OrCompositeFilterCondition.class);
        xStream.alias("orderCondition", OrderCondition.class);
        xStream.alias("primitiveFilterCondition", PrimitiveFilterCondition.class);
        xStream.alias("projector", Projector.class);

        xStream.setMode(XStream.NO_REFERENCES);

        // rename String to field, arrayList must be specify in the fieldName
        // setter
        ClassAliasingMapper orderConditionFieldMapper = new ClassAliasingMapper(xStream.getMapper());
        orderConditionFieldMapper.addClassAlias("field", String.class);
        xStream.registerLocalConverter(OrderCondition.class, "fieldNames", new CollectionConverter(orderConditionFieldMapper));

        // rename projector exportField
        ClassAliasingMapper projectorExportFieldMapper = new ClassAliasingMapper(xStream.getMapper());
        projectorExportFieldMapper.addClassAlias("field", String.class);
        xStream.registerLocalConverter(FilterSelector.class, "exportFields", new CollectionConverter(projectorExportFieldMapper));

        // rename projector displayField
        ClassAliasingMapper projectorDisplayFieldMapper = new ClassAliasingMapper(xStream.getMapper());
        projectorDisplayFieldMapper.addClassAlias("field", String.class);
        xStream.registerLocalConverter(FilterSelector.class, "displayFields", new CollectionConverter(projectorDisplayFieldMapper));

        // rename projector ignore field
        ClassAliasingMapper projectorIgnoreFieldMapper = new ClassAliasingMapper(xStream.getMapper());
        projectorIgnoreFieldMapper.addClassAlias("field", String.class);
        xStream.registerLocalConverter(FilterSelector.class, "ignoreIfNotFoundForeignKeys", new CollectionConverter(projectorIgnoreFieldMapper));

        return xStream;
    }

    /**
     * Remove fields except the ones that were asked to be displayed, from the found entities to be serialized
     * 
     * @param xstream
     * @param filter Filter definition
     */
    private void applyOmittedFields(XStream xstream, Filter filter) {
        List<String> displayOrExportFields = filter.getPrimarySelector().getDisplayFields();

        @SuppressWarnings("rawtypes")
        Class targetClass = ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass();
        List<Field> fields = new ArrayList<Field>();
        ReflectionUtils.getAllFields(fields, targetClass);

        // allFields - display = omit
        if (displayOrExportFields != null) {
            List<Field> displayFields = new ArrayList<>();
            for (Field field : fields) {
                for (String displayField : displayOrExportFields) {
                    if (field.getName().equals(displayField)) {
                        displayFields.add(field);
                        break;
                    }
                }
            }

            fields.removeAll(displayFields);
        }

        // omit fields
        // log.debug("Omitting fields={} from class={}", Arrays.asList(fields), targetClass.getName());
        for (Field field : fields) {
            xstream.omitField(field.getDeclaringClass(), field.getName());
        }
    }

    public boolean isMatch(NativeFilterCondition filter, Map<Object, Object> params) {

        return ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(filter.getEl(), params);
    }

    public String serializeEntities(XStream xstream, Filter filter, List<? extends IEntity> entities) {
        if (entities.isEmpty()) {
            log.info("No entities to serialize");
            return "";
        }

        Class<? extends Object> primaryTargetClass = ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass();
        xstream.alias(primaryTargetClass.getSimpleName().toLowerCase(), primaryTargetClass);

        // Add custom converters
        xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernateProxyConverter());
        xstream.setMode(XStream.NO_REFERENCES);

        return xstream.toXML(entities);
    }

    @SuppressWarnings("unchecked")
    public String filteredList(Filter filter) throws BusinessException {
        FilteredQueryBuilder fqb = getFilteredQueryBuilder(filter);

        Query query = fqb.getQuery(getEntityManager());
        log.debug("query={}", fqb.getSqlString());
        List<? extends IEntity> objects = (List<? extends IEntity>) query.getResultList();
        XStream xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new HibernateMapper(next);
            }
        };

        applyOmittedFields(xstream, filter);

        // String result = xstream.toXML(countries);
        return serializeEntities(xstream, filter, objects);

    }

    @SuppressWarnings("unchecked")
    /**
     * Execute a filter query and return found data
     * @param filter Filter to run
     * @param params Parameters to pass to a filter query
     * @return A list of fu=ound 
     * @throws BusinessException
     */
    public List<? extends IEntity> filteredListAsObjects(Filter filter, Map<String, Object> params) throws BusinessException {

        if (!StringUtils.isBlank(filter.getPollingQuery())) {
            return (List<? extends IEntity>) executeSelectQuery(filter.getPollingQuery(), params);
        }
        FilteredQueryBuilder fqb = getFilteredQueryBuilder(filter);

        Query query = fqb.getQuery(getEntityManager());
        log.debug("query={}", fqb.getSqlString());
        List<? extends IEntity> objects = (List<? extends IEntity>) query.getResultList();
        return objects;

    }

    public String filteredList(String filterName, Integer firstRow, Integer numberOfRows) throws BusinessException {
        Filter filter = (Filter) findByCode(filterName);
        return filteredList(filter, firstRow, numberOfRows);
    }

    @SuppressWarnings("unchecked")
    public String filteredList(Filter filter, Integer firstRow, Integer numberOfRows) throws BusinessException {

        FilteredQueryBuilder fqb = getFilteredQueryBuilder(filter);

        Query query = fqb.getQuery(getEntityManager());
        log.debug("query={}", fqb.getSqlString());
        fqb.applyPagination(query, firstRow, numberOfRows);
        List<? extends IEntity> objects = (List<? extends IEntity>) query.getResultList();
        XStream xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new HibernateMapper(next);
            }
        };

        applyOmittedFields(xstream, filter);

        // String result = xstream.toXML(countries);
        return serializeEntities(xstream, filter, objects);

    }

    @SuppressWarnings("unchecked")
    public List<Filter> findByPrimaryTargetClass(String className) {
        QueryBuilder qb = new QueryBuilder(Filter.class, "f", null);
        qb.addCriterion("primarySelector.targetEntity", "=", className, true);
        qb.startOrClause();
        qb.addBooleanCriterion("shared", true);
        qb.addCriterionEntity("f.auditable.creator", currentUser.getUserName());
        qb.endOrClause();

        try {
            return (List<Filter>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    private void updateFilterDetails(Filter sourceFilter, Filter targetFilter) throws BusinessException {

        targetFilter.setPrimarySelector(sourceFilter.getPrimarySelector());

        if (targetFilter.getSecondarySelectors() != null) {
            targetFilter.getSecondarySelectors().clear();
        } else {
            targetFilter.setSecondarySelectors(new ArrayList<FilterSelector>());
        }
        
        if (sourceFilter.getSecondarySelectors() != null) {
            for (FilterSelector filterSelector : sourceFilter.getSecondarySelectors()) {
                targetFilter.getSecondarySelectors().add(filterSelector);
            }
        }

        targetFilter.setFilterCondition(sourceFilter.getFilterCondition());

        targetFilter.setOrderCondition(sourceFilter.getOrderCondition());

    }

    @Override
    public void create(Filter filter) throws BusinessException {
        parseInputXML(filter.getInputXml(), filter);
        super.create(filter);
    }

    @Override
    public Filter update(Filter filter) throws BusinessException {
        parseInputXML(filter.getInputXml(), filter);
        return super.update(filter);
    }

    @Override
    public void remove(Filter filter) throws BusinessException {
        try {
            customFieldTemplateService.createMissingTemplates(filter, new ArrayList<CustomFieldTemplate>(), true, true);
        } catch (BusinessException e) {
            log.error("Failed to remove custom fields.", e);
        }
        super.remove(filter);
    }

    private void persistCustomFieldTemplates(Filter filter) throws BusinessException {
        try {
            List<CustomFieldTemplate> customFieldTemplates = new ArrayList<>();
            extractCustomFields(filter, filter.getFilterCondition(), customFieldTemplates);
            customFieldTemplateService.createMissingTemplates(filter, customFieldTemplates, true, true);
        } catch (CustomFieldException e) {
            throw new BusinessException(e);
        }
    }

    private void extractCustomFields(ICustomFieldEntity entity, FilterCondition filterCondition, List<CustomFieldTemplate> customFieldTemplates) throws CustomFieldException {
        if (filterCondition != null) {
            if (filterCondition instanceof OrCompositeFilterCondition) {
                OrCompositeFilterCondition orCondition = (OrCompositeFilterCondition) filterCondition;
                for (FilterCondition subCondition : orCondition.getFilterConditions()) {
                    extractCustomFields(entity, subCondition, customFieldTemplates);
                }
            } else if (filterCondition instanceof AndCompositeFilterCondition) {
                AndCompositeFilterCondition andCondition = (AndCompositeFilterCondition) filterCondition;
                for (FilterCondition subCondition : andCondition.getFilterConditions()) {
                    extractCustomFields(entity, subCondition, customFieldTemplates);
                }
            } else if (filterCondition instanceof PrimitiveFilterCondition) {
                String appliesTo = CustomFieldTemplateService.calculateAppliesToValue(entity);
                PrimitiveFilterCondition condition = (PrimitiveFilterCondition) filterCondition;
                extractCustomField(customFieldTemplates, appliesTo, condition);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void extractCustomField(List<CustomFieldTemplate> customFields, String appliesTo, PrimitiveFilterCondition primitiveFilterCondition) throws CustomFieldException {
        String operand = primitiveFilterCondition.getOperand();
        String[] typeAndCode = null;
        String code = null;
        String defaultValue = null;
        String className = null;
        String label = null;

        CustomFieldTemplate customField = null;
        for (FilterParameterTypeEnum type : FilterParameterTypeEnum.values()) {
            if (type.matchesPrefixOf(operand)) {
                typeAndCode = operand.split(":");
                code = typeAndCode[1];
                defaultValue = primitiveFilterCondition.getDefaultValue();
                className = primitiveFilterCondition.getClassName();
                label = primitiveFilterCondition.getLabel();
                if (StringUtils.isBlank(label)) {
                    label = code;
                }
                customField = customFieldTemplateService.findByCodeAndAppliesToNoCache(code, appliesTo);
                if (customField == null) {
                    customField = new CustomFieldTemplate();
                    customField.setAppliesTo(appliesTo);
                    customField.setCode(code);
                }
                customField.setDescription(label);
                customField.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
                customField.setAllowEdit(true);
                customField.setDefaultValue(defaultValue);
                customField.setFieldType(type.getFieldType());
                customField.setGuiPosition(POSITION_PREFIX + primitiveFilterCondition.getIndex());
                switch (type) {
                case ENTITY:
                    customField.setEntityClazz(className);
                    break;
                case ENUM:
                    try {
                        Map<String, String> items = new HashMap<>();
                        for (Object enumItem : EnumUtils.getEnumList((Class<? extends Enum>) Class.forName(className))) {
                            Enum item = (Enum) enumItem;
                            items.put(item.getClass().getName() + "." + item.name(), item.name());
                        }
                        customField.setListValues(items);
                    } catch (ClassNotFoundException e) {
                        String message = "Failed to create enum values list.";
                        log.error(message, e);
                        throw new CustomFieldException(message);
                    }
                    break;
                default:
                    break;
                }
                customFields.add(customField);
                break;
            }
        }
    }

    public Filter parseInputXML(String inputXml, Filter targetFilter) throws BusinessException {
        if (inputXml != null) {
            Filter parsedFilter = parse(inputXml);
            updateFilterDetails(parsedFilter, targetFilter);
        }
        validate(targetFilter);
        persistCustomFieldTemplates(targetFilter);
        return targetFilter;
    }

    private void validate(Filter filter) throws ConstraintViolationException, BusinessException {
        if (filter == null) {
            return;
        }

//        if (filter != null) {
//            Set<ConstraintViolation<Filter>> violations = validator.validate(filter);
//            if (!violations.isEmpty()) {
//                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
//            }
//        }
        if (filter.getOrderCondition() != null) {
            Set<ConstraintViolation<OrderCondition>> violations = validator.validate(filter.getOrderCondition());
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
            }
        }
        if (filter.getPrimarySelector() != null) {
            Set<ConstraintViolation<FilterSelector>> violations = validator.validate(filter.getPrimarySelector());
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
            }
        }
        if (filter.getSecondarySelectors() != null) {
            for (FilterSelector fs : filter.getSecondarySelectors()) {
                Set<ConstraintViolation<FilterSelector>> violations = validator.validate(fs);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
                }
            }
        }
        // filterCondition
        if (filter.getFilterCondition() != null) {
            validateFilterCondition(filter.getFilterCondition());
        }
    }

    private void validateFilterCondition(FilterCondition filterCondition) throws ConstraintViolationException, BusinessException {
        if (filterCondition instanceof OrCompositeFilterCondition) {
            OrCompositeFilterCondition tempFilter = (OrCompositeFilterCondition) filterCondition;

            if (tempFilter.getFilterConditions() != null) {
                for (FilterCondition fc : tempFilter.getFilterConditions()) {
                    validateFilterCondition(fc);
                }
            }
        } else if (filterCondition instanceof AndCompositeFilterCondition) {
            AndCompositeFilterCondition tempFilter = (AndCompositeFilterCondition) filterCondition;

            if (tempFilter.getFilterConditions() != null) {
                for (FilterCondition fc : tempFilter.getFilterConditions()) {
                    validateFilterCondition(fc);
                }
            }
        } else if (filterCondition instanceof PrimitiveFilterCondition) {
            PrimitiveFilterCondition tempFilter = (PrimitiveFilterCondition) filterCondition;

            Set<ConstraintViolation<PrimitiveFilterCondition>> violations = validator.validate(tempFilter);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
            }

            PrimitiveFilterCondition condition = (PrimitiveFilterCondition) filterCondition;
            String operand = condition.getOperand();
            if (operand != null) {
                String[] typeAndCode = operand.split(":");
                String type = typeAndCode[0];
                if (requiresClassName.contains(type)) {
                    boolean isValid = true;
                    String fieldName = condition.getFieldName();
                    if (condition.getClassName() != null) {
                        try {
                            Class<?> conditionClass = Class.forName(condition.getClassName());
                            isValid = conditionClass != null;
                        } catch (ClassNotFoundException e) {
                            isValid = false;
                        }
                    } else {
                        isValid = false;
                    }
                    if (!isValid) {
                        throw new BusinessException("A valid class name must be entered for " + fieldName + " field.");
                    }
                }
            }

        } else if (filterCondition instanceof NativeFilterCondition) {
            NativeFilterCondition tempFilter = (NativeFilterCondition) filterCondition;

            Set<ConstraintViolation<NativeFilterCondition>> violations = validator.validate(tempFilter);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
            }
        }
    }

    private FilteredQueryBuilder getFilteredQueryBuilder(Filter filter) throws BusinessException {
        if (filter == null) {
            throw new BusinessException("filter is null");
        }
        String clazzName = filter.getPrimarySelector().getTargetEntity();

        Object obj = ReflectionUtils.createObject(clazzName);
        FilteredQueryBuilder filteredQueryBuilder = null;
        if (obj == null) {
            throw new BusinessException("Target entity " + clazzName + " is invalid");
        }

        filteredQueryBuilder = new FilteredQueryBuilder(retrieveIfNotManaged(filter));

        return filteredQueryBuilder;
    }
}
