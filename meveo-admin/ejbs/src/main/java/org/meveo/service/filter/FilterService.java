package org.meveo.service.filter;

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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.commons.lang3.EnumUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
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

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilterService extends BusinessService<Filter> {

    @Inject
    private FilterSelectorService filterSelectorService;

    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    public Filter parse(String xmlInput) throws XStreamException {
        xmlInput = xmlInput.trim();
        Filter result = new Filter();

        XStream xstream = getXStream();
        result = (Filter) xstream.fromXML(xmlInput);

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

    public void applyOmittedFields(XStream xstream, Filter filter) {
        applyOmittedFields(xstream, filter, true);
    }

    public void applyOmittedFields(XStream xstream, Filter filter, boolean display) {
        List<String> displayOrExportFields = filter.getPrimarySelector().getDisplayFields();
        if (!display) {
            displayOrExportFields = filter.getPrimarySelector().getExportFields();
        }

        @SuppressWarnings("rawtypes")
        Class targetClass = ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass();
        List<Field> fields = new ArrayList<Field>();
        ReflectionUtils.getAllFields(fields, targetClass);

        // allFields - display = omit
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

        // omit fields
        log.debug("Omitting fields={} from class={}", Arrays.asList(fields), targetClass.getName());
        for (Field field : fields) {
            xstream.omitField(field.getDeclaringClass(), field.getName());
        }
    }

    public boolean isMatch(NativeFilterCondition filter, Map<Object, Object> params) {
        try {
            return ((Boolean) ValueExpressionWrapper.evaluateExpression(filter.getEl(), params, Boolean.class)).booleanValue();
        } catch (BusinessException e) {
            return false;
        }
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
    public String filteredList(Filter filter, Provider provider) throws BusinessException {
        FilteredQueryBuilder fqb = new FilteredQueryBuilder(filter);

        try {
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
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<? extends IEntity> filteredListAsObjects(Filter filter, Provider provider) throws BusinessException {
        FilteredQueryBuilder fqb = new FilteredQueryBuilder(filter);

        try {
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

            return objects;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public String filteredList(String filterName, Integer firstRow, Integer numberOfRows, Provider provider) throws BusinessException {
        Filter filter = (Filter) findByCode(filterName, provider);
        return filteredList(filter, firstRow, numberOfRows);
    }

    @SuppressWarnings("unchecked")
    public String filteredList(Filter filter, Integer firstRow, Integer numberOfRows) throws BusinessException {
        FilteredQueryBuilder fqb = new FilteredQueryBuilder(filter);

        try {
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
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Filter> findByPrimaryTargetClass(String className) {
        QueryBuilder qb = new QueryBuilder(Filter.class, "f", null, getCurrentProvider());
        qb.addCriterion("primarySelector.targetEntity", "=", className, true);
        qb.startOrClause();
        qb.addBooleanCriterion("shared", true);
        qb.addCriterionEntity("f.auditable.creator", getCurrentUser());
        qb.endOrClause();

        try {
            return (List<Filter>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateFilterDetails(Filter sourceFilter, Filter targetFilter, User currentUser) throws BusinessException {

        Provider provider = currentUser.getProvider();

        targetFilter.setPrimarySelector(sourceFilter.getPrimarySelector());
        if(targetFilter.getPrimarySelector() != null){
            targetFilter.getPrimarySelector().setProvider(provider);
        }

//        if(targetFilter.getSecondarySelectors() != null){
//            targetFilter.getSecondarySelectors().clear();
//        } else {
//            targetFilter.setSecondarySelectors(new ArrayList<FilterSelector>());
//        }
//        for (FilterSelector filterSelector : sourceFilter.getSecondarySelectors()) {
//            filterSelector.setProvider(provider);
//            targetFilter.getSecondarySelectors().add(filterSelector);
//        }

        targetFilter.setFilterCondition(sourceFilter.getFilterCondition());
        setProviderToFilterCondition(targetFilter.getFilterCondition(), provider);

        targetFilter.setOrderCondition(sourceFilter.getOrderCondition());
        if(targetFilter.getOrderCondition() != null){
            targetFilter.getOrderCondition().setProvider(provider);
        }
    }

    public FilterCondition setProviderToFilterCondition(FilterCondition filterCondition, Provider provider) {
        if(filterCondition != null){
            filterCondition.setProvider(provider);

            if (filterCondition.getFilterConditionType().equals(AndCompositeFilterCondition.class.getAnnotation(DiscriminatorValue.class).value())) {
                AndCompositeFilterCondition andCompositeFilterCondition = (AndCompositeFilterCondition) filterCondition;
                for (FilterCondition filterConditionLoop : andCompositeFilterCondition.getFilterConditions()) {
                    setProviderToFilterCondition(filterConditionLoop, provider);
                }
            }

            if (filterCondition.getFilterConditionType().equals(OrCompositeFilterCondition.class.getAnnotation(DiscriminatorValue.class).value())) {
                OrCompositeFilterCondition orCompositeFilterCondition = (OrCompositeFilterCondition) filterCondition;
                for (FilterCondition filterConditionLoop : orCompositeFilterCondition.getFilterConditions()) {
                    setProviderToFilterCondition(filterConditionLoop, provider);
                }
            }
        }
        return filterCondition;
    }

    @Override
    public void create(Filter filter, User user) throws BusinessException {
        persistCustomFieldTemplates(filter, user);
        super.create(filter, user);
    }

    @Override
    public Filter update(Filter filter, User user) throws BusinessException {
        persistCustomFieldTemplates(filter, user);
        return super.update(filter, user);
    }

    public void persistCustomFieldTemplates(Filter filter, User user) throws BusinessException {
        try {
            List<CustomFieldTemplate> customFieldTemplates = new ArrayList<>();
            extractCustomFields(filter, filter.getFilterCondition(), user, customFieldTemplates);
            customFieldTemplateService.createMissingTemplates(filter, customFieldTemplates, user, true, true);
        } catch (CustomFieldException e) {
            throw new BusinessException(e);
        }
    }

    public void extractCustomFields(ICustomFieldEntity entity, FilterCondition filterCondition, User user, List<CustomFieldTemplate> customFieldTemplates) throws CustomFieldException {
        if(filterCondition != null){
            if (filterCondition instanceof OrCompositeFilterCondition) {
                OrCompositeFilterCondition orCondition = (OrCompositeFilterCondition) filterCondition;
                for (FilterCondition subCondition : orCondition.getFilterConditions()) {
                    extractCustomFields(entity, subCondition, user, customFieldTemplates);
                }
            } else if (filterCondition instanceof AndCompositeFilterCondition) {
                AndCompositeFilterCondition andCondition = (AndCompositeFilterCondition) filterCondition;
                for (FilterCondition subCondition : andCondition.getFilterConditions()) {
                    extractCustomFields(entity, subCondition, user, customFieldTemplates);
                }
            } else if (filterCondition instanceof PrimitiveFilterCondition) {
                String appliesTo = customFieldTemplateService.calculateAppliesToValue(entity);
                PrimitiveFilterCondition condition = (PrimitiveFilterCondition) filterCondition;
                extractCustomField(user, customFieldTemplates, appliesTo, condition);
            }
        }
    }

    private void extractCustomField(User user, List<CustomFieldTemplate> customFields, String appliesTo, PrimitiveFilterCondition primitiveFilterCondition) throws CustomFieldException {
        String operand = primitiveFilterCondition.getOperand();
        String[] typeAndCode = null;
        String typePrefix = null;
        String code = null;
        String defaultValue = null;
        String className = null;
        String label = null;

        CustomFieldTemplate customField = null;
        for(FilterParameterTypeEnum type : FilterParameterTypeEnum.values()){
            if(type.matchesPrefixOf(operand)){
                typeAndCode = operand.split(":");
                code = typeAndCode[1];
                defaultValue = primitiveFilterCondition.getDefaultValue();
                className = primitiveFilterCondition.getClassName();
                label = primitiveFilterCondition.getLabel();
                if(StringUtils.isBlank(label)){
                    label = code;
                }
                customField = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo, user.getProvider());
                if(customField == null) {
                    customField = new CustomFieldTemplate();
                    customField.setAppliesTo(appliesTo);
                    customField.setCode(code);
                }
                customField.setDescription(label);
                customField.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
                customField.setAllowEdit(true);
                customField.setDefaultValue(defaultValue);
                customField.setFieldType(type.getFieldType());
                switch (type){
                    case ENTITY:
                        customField.setEntityClazz(className);
                        break;
                    case ENUM:
                        try {
                            Map<String, String> items = new HashMap<>();
                            String name = null;
                            for (Object enumItem : EnumUtils.getEnumList((Class<? extends Enum>) Class.forName(className))) {
                                name = ((Enum) enumItem).name();
                                items.put(name, name);
                            }
                            customField.setListValues(items);
                        } catch (ClassNotFoundException e) {
                            String message = "Failed to create enum values list.";
                            log.error(message, e);
                            throw new CustomFieldException(message);
                        }
                        break;
                }
                customFields.add(customField);
                break;
            }
        }
    }

    public void validateUnmarshalledFilter(Filter filter) throws BusinessException {
        validateFilterCondition(filter.getFilterCondition());
    }

    private void validateFilterCondition(FilterCondition filterCondition) throws BusinessException{
        boolean isValid = true;
        String fieldName = null;
        List<String> requiresClassName = Arrays.asList(FilterParameterTypeEnum.ENTITY.getPrefix(), FilterParameterTypeEnum.ENUM.getPrefix());
        if(filterCondition != null){
            if(filterCondition instanceof AndCompositeFilterCondition){
                for (FilterCondition condition : ((AndCompositeFilterCondition) filterCondition).getFilterConditions()) {
                    validateFilterCondition(condition);
                }
            } else if (filterCondition instanceof OrCompositeFilterCondition){
                for (FilterCondition condition : ((OrCompositeFilterCondition) filterCondition).getFilterConditions()) {
                    validateFilterCondition(condition);
                }
            } else if (filterCondition instanceof PrimitiveFilterCondition){
                PrimitiveFilterCondition condition = (PrimitiveFilterCondition) filterCondition;
                String operand = condition.getOperand();
                if(operand != null){
                    String[] typeAndCode = operand.split(":");
                    String type = typeAndCode[0];
                    if(requiresClassName.contains(type)){
                        fieldName = condition.getFieldName();
                        if(condition.getClassName() != null){
                            try {
                                Class<?> conditionClass = Class.forName(condition.getClassName());
                                isValid = conditionClass != null;
                            } catch (ClassNotFoundException e) {
                                isValid = false;
                            }
                        } else {
                            isValid = false;
                        }
                    }
                }
            }
        }
        if(!isValid){
            throw new BusinessException("A valid class name must be entered for " + fieldName + " field.");
        }
    }
}
