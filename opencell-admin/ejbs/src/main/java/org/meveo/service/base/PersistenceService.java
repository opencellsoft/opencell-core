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
package org.meveo.service.base;

import static java.math.BigInteger.ONE;
import static org.meveo.jpa.EntityManagerProvider.isDBOracle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Attribute;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.CacheMode;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.encryption.IEncryptable;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.InstantiateWF;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Updated;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IAuditable;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEnable;
import org.meveo.model.IEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.persistence.CustomFieldJsonTypeDescriptor;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.transformer.AliasToEntityOrderedMapResultTransformer;
import org.meveo.service.base.expressions.ExpressionFactory;
import org.meveo.service.base.expressions.ExpressionParser;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CfValueAccumulator;
import org.meveo.service.notification.GenericNotificationService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Generic implementation that provides the default implementation for persistence methods declared in the {@link IPersistenceService} interface.
 *
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 *
 */
public abstract class PersistenceService<E extends IEntity> extends BaseService implements IPersistenceService<E> {
    protected Class<E> entityClass;

    /**
     * Entity list search parameter name - parameter's value contains entity class
     */
    public static final String SEARCH_ATTR_TYPE_CLASS = "type_class";
    /**
     * Entity list search parameter value - parameter's value is null
     */
    public static final String SEARCH_IS_NULL = "IS_NULL";
    /**
     * Entity list search parameter value - parameter's value is not null
     */
    public static final String SEARCH_IS_NOT_NULL = "IS_NOT_NULL";
    /**
     * Entity list search parameter criteria - wildcard Or
     */
    public static final String SEARCH_WILDCARD_OR = "wildcardOr";

    /**
     * Entity list search parameter criteria - list
     */
    public static final String SEARCH_LIST = "list";

    /**
     * Entity list search parameter name - parameter's value contains sql statement. Word "SQL" can be succeeded by a anything else (e.g. number) allowing to support more than one SQL clause.
     */
    public static final String SEARCH_SQL = "SQL";
    /**
     * Entity list search parameter name - parameter's value contains a set of elements that are joined by OR clause between them
     */
    public static final String SEARCH_OR = "OR";
    /**
     * Entity list search parameter name - a prefix allowing to have multiple criteria for the same field. Word will be removed and any other condition evaluated as usual. Word "AND" can be succeeded by a anything else
     * (e.g. number) allowing to support more than one clause for the same field.
     */
    public static final String SEARCH_AND = "AND";
    /**
     * Entity list search parameter criteria - just like wildcardOr but Ignoring case
     */
    public static final String SEARCH_WILDCARD_OR_IGNORE_CAS = "wildcardOrIgnoreCase";
    /**
     * Entity list search parameter name - parameter's value contains filter name
     */
    public static final String SEARCH_FILTER = "$FILTER";

    /**
     * Entity list search parameter name - parameter's value contains filter parameters
     */
    public static final String SEARCH_FILTER_PARAMETERS = "$FILTER_PARAMETERS";
    
    /**
     * Entity list search if any of input parameters match the given values (example: "anyMatch customer.id billingAccount.customerAccount.customer.id customerAccount.customer.id": "19 19 19")
     */
    public static final String ANY_MATCH = "anyMatch";

    public static final String FROM_JSON_FUNCTION = "FromJson(a.";
    public static final String CF_VALUES_FIELD = "cfValues";

    public static final int SHORT_MAX_VALUE = 32767;
    /**
     * Is custom field accumulation being used
     */
    protected static boolean accumulateCF = true;
    
    /**
     * Is generic workflow being used
     */
    protected static boolean applyGenericWorkflow = true;

    protected static Map<Class, String> jsonTypes = new HashMap<Class, String>();

    protected static boolean encryptCFSetting = false;

    private static Long MAX_DEPTH = 5L;

    @PostConstruct
    private void init() {
        accumulateCF = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("customFields.accumulateCF", "false"));
        applyGenericWorkflow = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("workflow.enabled", "true"));
        encryptCFSetting = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty(IEncryptable.ENCRYPT_CUSTOM_FIELDS_PROPERTY, IEncryptable.FALSE_STR));
    }

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    @InstantiateWF
    protected Event<BaseEntity> entityInstantiateWFEventProducer;

    @Inject
    @Created
    protected Event<BaseEntity> entityCreatedEventProducer;

    @Inject
    @Updated
    protected Event<BaseEntity> entityUpdatedEventProducer;

    @Inject
    @Disabled
    protected Event<BaseEntity> entityDisabledEventProducer;

    @Inject
    @Enabled
    protected Event<BaseEntity> entityEnabledEventProducer;

    @Inject
    @Removed
    protected Event<BaseEntity> entityRemovedEventProducer;

    @EJB
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private DeletionService deletionService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    protected CfValueAccumulator cfValueAccumulator;

    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private GenericNotificationService genericNotificationService;
    
    /**
     * Constructor.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PersistenceService() {
        Class clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

        if (o instanceof TypeVariable) {
            this.entityClass = (Class<E>) ((TypeVariable) o).getBounds()[0];
        } else {
            this.entityClass = (Class<E>) o;
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#getEntityClass()
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

    /**
     * Update entity in DB without firing any notification events nor publishing data to Elastic Search
     *
     * @param entity Entity to update in DB
     * @return Updated entity
     * @throws BusinessException General business exception
     */
    public E updateNoCheck(E entity) throws ValidationException {
        log.debug("start of update {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());

        if (BusinessEntity.class.isAssignableFrom(entity.getClass())) {
            // validate code
            validateCode((BusinessEntity) entity);
        }

        updateAudit(entity);

        EntityManager em = getEntityManager();
        if (!em.contains(entity)) { // https://vladmihalcea.com/jpa-persist-and-merge/
            entity = em.merge(entity); // here could also use session.update(); see https://vladmihalcea.com/how-to-optimize-the-merge-operation-using-update-while-batching-with-jpa-and-hibernate/
        }

        return entity;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long)
     */
    @Override
    public E findById(Long id) {

        log.trace("Find {}/{} by id", entityClass.getSimpleName(), id);
        return getEntityManager().find(entityClass, id);

    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#findByIds(java.util.List)
     */
    @Override
    public List<E> findByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<E>();
        }
        return getEntityManager().createQuery("select e from " + entityClass.getSimpleName() + " e where e.id in (:ids)", entityClass).setParameter("ids", ids).getResultList();
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#findById(java.util.List, java.util.List)
     */
    @SuppressWarnings("unchecked")
    public List<E> findByIds(List<Long> ids, List<String> fetchFields) {
        final Class<? extends E> productClass = getEntityClass();
        StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
        if (fetchFields != null && !fetchFields.isEmpty()) {
            for (String fetchField : fetchFields) {
                queryString.append(" left join fetch a." + fetchField);
            }
        }
        queryString.append(" where a.id in :ids");
        Query query = getEntityManager().createQuery(queryString.toString(), productClass);
        query.setParameter("ids", ids);

        List<E> results = query.getResultList();
        return results;
    }

    /**
     * Use by API.
     */
    @Override
    public E findById(Long id, boolean refresh) {
        log.trace("Find {}/{} by id with refresh {}", entityClass.getSimpleName(), id, refresh);
        E e = getEntityManager().find(entityClass, id);
        if (e != null) {
            if (refresh) {
                getEntityManager().refresh(e);
            }
        }
        return e;

    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long, java.util.List)
     */
    @Override
    public E findById(Long id, List<String> fetchFields) {
        return findById(id, fetchFields, false);
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long, java.util.List, boolean)
     */
    @SuppressWarnings("unchecked")
    public E findById(Long id, List<String> fetchFields, boolean refresh) {
        log.trace("Find {}/{} by id with refresh {}", entityClass.getSimpleName(), id, refresh);
        final Class<? extends E> productClass = getEntityClass();
        StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");
        if (fetchFields != null && !fetchFields.isEmpty()) {
            for (String fetchField : fetchFields) {
                queryString.append(" left join fetch a." + fetchField);
            }
        }
        queryString.append(" where a.id = :id");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("id", id);

        List<E> results = query.getResultList();
        E e = null;
        if (!results.isEmpty()) {
            e = (E) results.get(0);
            if (refresh) {
                getEntityManager().refresh(e);
            }
        }
        return e;
    }

    public E findByIdIgnoringCache(Long id, List<String> fetchFields) {
        log.trace("Find {}/{} by id without cache hint {}", entityClass.getSimpleName(), id);
        final Class<? extends E> productClass = getEntityClass();
        if (fetchFields != null && !fetchFields.isEmpty()) {
            StringBuilder queryString = new StringBuilder("from " + productClass.getName() + " a");

            for (String fetchField : fetchFields) {
                queryString.append(" left join fetch a." + fetchField);
            }

            queryString.append(" where a.id = :id");
            Query query = getEntityManager().createQuery(queryString.toString()).setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);
            query.setParameter("id", id);

            List<E> results = query.getResultList();
            E e = null;
            if (!results.isEmpty()) {
                e = (E) results.get(0);
            }
            return e;
        } else {
	        
	        Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
            return getEntityManager().find(entityClass, id, hints);
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#disable(java.lang.Long)
     */
    @Override
    public E disable(Long id) throws BusinessException {
        E e = findById(id);
        if (e != null) {
            e = disable(e);
        }
        return e;
    }

    @Override
    public E disable(E entity) throws BusinessException {
        if (entity instanceof IEnable && ((IEnable) entity).isActive()) {
            log.debug("start of disable {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
            ((IEnable) entity).setDisabled(true);
            if (entity instanceof IAuditable) {
                ((IAuditable) entity).updateAudit(currentUser);
            }
            entity = getEntityManager().merge(entity);
            if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
                entityDisabledEventProducer.fire((BaseEntity) entity);
            }
            log.trace("end of disable {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());
        }
        return entity;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#enable(java.lang.Long)
     */
    @Override
    public E enable(Long id) throws BusinessException {
        E e = findById(id);
        if (e != null) {
            e = enable(e);
        }
        return e;
    }

    @Override
    public E enable(E entity) throws BusinessException {
        if (entity instanceof IEnable && ((IEnable) entity).isDisabled()) {
            log.debug("start of enable {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
            ((IEnable) entity).setDisabled(false);
            if (entity instanceof IAuditable) {
                ((IAuditable) entity).updateAudit(currentUser);
            }
            entity = getEntityManager().merge(entity);
            if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
                entityEnabledEventProducer.fire((BaseEntity) entity);
            }
            log.trace("end of enable {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());
        }
        return entity;
    }

    @Override
    public void remove(E entity) {
        log.debug("start of remove {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
        deletionService.checkEntityIsNotreferenced(entity);
        entity = retrieveIfNotManaged(entity);
        if (entity != null) {
            getEntityManager().remove(entity);
            if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
                entityRemovedEventProducer.fire((BaseEntity) entity);
            }
            // getEntityManager().flush();

            if (entity instanceof IImageUpload) {
                try {
                    ImageUploadEventHandler<E> imageUploadEventHandler = new ImageUploadEventHandler<E>(currentUser.getProviderCode());
                    imageUploadEventHandler.deleteImage(entity);
                } catch (IOException e) {
                    log.error("Failed deleting image file");
                }
            }
        }

        if (entity != null) {
            log.trace("end of remove {} entity (id={}).", getEntityClass().getSimpleName(), entity.getId());
        }
    }

    private Predicate<List<EntityReferenceWrapper>> matchesCustomEntity() {
        return e -> e.stream().anyMatch(c -> {
            try {
                return BusinessEntity.class.isAssignableFrom(Class.forName(c.getClassname()));
            } catch (ClassNotFoundException ex) {
                return false;
            }
        });
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#remove(java.lang.Long)
     */
    @Override
    public void remove(Long id) throws BusinessException {
        E e = findById(id);
        if (e != null) {
            remove(e);
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#remove(java.util.Set)
     */
    @Override
    public void remove(Set<Long> ids) throws BusinessException {
        Query query = getEntityManager().createQuery("delete from " + getEntityClass().getName() + " where id in (:ids)");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#update(org.meveo.model.IEntity)
     */
    @Override
    public E update(E entity)  {
        log.debug("start of update {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());

        if (entity instanceof ISearchable) {
            validateCode((ISearchable) entity);
        }

        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }

        if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
            entityUpdatedEventProducer.fire((BaseEntity) entity);
        }

        // Schedule end of period events
        // Be careful - if called after persistence might loose ability to determine new period as CustomFeldvalue.isNewPeriod is not serialized to json
        if (entity instanceof ICustomFieldEntity) {
            customFieldInstanceService.scheduleEndPeriodEvents((ICustomFieldEntity) entity);
        }

        Set<String> dirtyCfValues = null;
        Set<String> dirtyCfPeriods = null;
        if (accumulateCF && entity instanceof ICustomFieldEntity) {
            CustomFieldValues cfValues = ((ICustomFieldEntity) entity).getCfValues();
            if (cfValues != null) {
                dirtyCfValues = cfValues.getDirtyCfValues();
                dirtyCfPeriods = cfValues.getDirtyCfPeriods();
            }
        }

        EntityManager em = getEntityManager();
        if (!em.contains(entity)) { // https://vladmihalcea.com/jpa-persist-and-merge/
            entity = em.merge(entity); // here could also use session.update(); see https://vladmihalcea.com/how-to-optimize-the-merge-operation-using-update-while-batching-with-jpa-and-hibernate/
        }
    
        // Andrius K. Commented out for now as solution is not currently used. Please don't remove it.
        // if (dirtyCfValues != null && !dirtyCfValues.isEmpty()) {
        // // CustomFieldValues cfValues = ((ICustomFieldEntity) entity).getCfValues();
        // cfValueAccumulator.entityUpdated((ICustomFieldEntity) entity, dirtyCfValues, dirtyCfPeriods);
        // }

        log.trace("end of update {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());

        return entity;
    }

    private boolean validateCode(ISearchable entity) throws ValidationException {
        // if (!StringUtils.isMatch(entity.getCode(), ParamBeanFactory.getAppScopeInstance().getProperty("meveo.code.pattern", StringUtils.CODE_REGEX))) {
        // throw new BusinessException("Invalid characters found in entity code.");
        // }

        return true;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#create(org.meveo.model.IEntity)
     */
    @Override
    public void create(E entity)  {
        log.debug("start of create {}", entity.getClass().getSimpleName());

        if (entity instanceof ISearchable) {
            validateCode((ISearchable) entity);
        }

        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }
        // Schedule end of period events
        // Be careful - if called after persistence might loose ability to determine new period as CustomFeldvalue.isNewPeriod is not serialized to json
        if (entity instanceof ICustomFieldEntity) {
            customFieldInstanceService.scheduleEndPeriodEvents((ICustomFieldEntity) entity);
        }

        getEntityManager().persist(entity);

        if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
            entityCreatedEventProducer.fire((BaseEntity) entity);
        }

        if (applyGenericWorkflow && entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(WorkflowedEntity.class)) {
            entityInstantiateWFEventProducer.fire((BaseEntity) entity);
        }

        // Andrius K. Commented out for now as solution is not currently used. Please don't remove it.
        // if (accumulateCF && entity instanceof ICustomFieldEntity) {
        // cfValueAccumulator.entityCreated((ICustomFieldEntity) entity);
        // }

        log.trace("end of create {}. entity id={}.", entity.getClass().getSimpleName(), entity.getId());
    }


    /**
     * @see org.meveo.service.base.local.IPersistenceService#create(org.meveo.model.IEntity)
     */
    public void createWithoutNotif(E entity)  {
        log.debug("start of create {}", entity.getClass().getSimpleName());

        if (entity instanceof ISearchable) {
            validateCode((ISearchable) entity);
        }

        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }
        // Schedule end of period events
        // Be careful - if called after persistence might loose ability to determine new period as CustomFeldvalue.isNewPeriod is not serialized to json
        if (entity instanceof ICustomFieldEntity) {
            customFieldInstanceService.scheduleEndPeriodEvents((ICustomFieldEntity) entity);
        }

        getEntityManager().persist(entity);

        // Andrius K. Commented out for now as solution is not currently used. Please don't remove it.
        // if (accumulateCF && entity instanceof ICustomFieldEntity) {
        // cfValueAccumulator.entityCreated((ICustomFieldEntity) entity);
        // }

        log.trace("end of create {}. entity id={}.", entity.getClass().getSimpleName(), entity.getId());
    }

    @Override
    public List<E> list() {
        return list((Boolean) null);
    }

    @Override
    public List<E> listActive() {
        return list(true);
    }

    /**
     * List entities, optionally filtering by its enable/disable status
     *
     * @param active True to retrieve enabled entities only, False to retrieve disabled entities only. Do not provide any value to retrieve all entities.
     * @return A list of entities
     */
    @SuppressWarnings("unchecked")
    public List<E> list(Boolean active) {
        final Class<? extends E> entityClass = getEntityClass();
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        if (active != null && IEnable.class.isAssignableFrom(entityClass)) {
            queryBuilder.addBooleanCriterion("disabled", !active);
        }
        if (BusinessEntity.class.isAssignableFrom(entityClass)) {
            queryBuilder.addOrderCriterionAsIs("code", true);
        } else {
            queryBuilder.addOrderCriterionAsIs("id", true);
        }
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

    /**
     * List entities limiting by max result, optionally filtering by its enable/disable status
     *
     * @param active True to retrieve enabled entities only, False to retrieve disabled entities only. Do not provide any value to retrieve all entities.
     * @param maxResult Maximum result to retrieve
     * @return A list of entities
     */
    @SuppressWarnings("unchecked")
    public List<E> list(Boolean active, Integer maxResult) {
        final Class<? extends E> entityClass = getEntityClass();
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        if (active != null && IEnable.class.isAssignableFrom(entityClass)) {
            queryBuilder.addBooleanCriterion("disabled", !active);
        }
        if (BusinessEntity.class.isAssignableFrom(entityClass)) {
            queryBuilder.addOrderCriterionAsIs("code", true);
        } else {
            queryBuilder.addOrderCriterionAsIs("id", true);
        }
        Query query = queryBuilder.getQuery(getEntityManager()).setMaxResults(maxResult);
        return query.getResultList();
    }

    /**
     * Find entities by code - wild match.
     *
     * @param wildcode code to match
     * @return A list of entities matching code
     */
    @SuppressWarnings("unchecked")
    public List<E> findByCodeLike(String wildcode) {
        final Class<? extends E> entityClass = getEntityClass();
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        if (IEnable.class.isAssignableFrom(entityClass)) {
            queryBuilder.addBooleanCriterion("disabled", false);
        }
        queryBuilder.addCriterion("code", "like", "%" + wildcode + "%", true);
        return queryBuilder.getQuery(getEntityManager()).getResultList();
    }
    
    /**
     * Find entities by code
     *
     * @param code to match
     * @return entity matching code
     */
    public BusinessEntity findBusinessEntityByCode(String code) {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "entity", null);
        queryBuilder.addCriterion("entity.code", "=", code, true);
        try {
            return (BusinessEntity) queryBuilder.getQuery(getEntityManager()).getSingleResult();
        } catch (Exception exception) {
            return null;
        }
    }
    /**
     * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.util.pagination.PaginationConfiguration)
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public List<E> list(PaginationConfiguration config) {
        Map<String, Object> filters = config.getFilters();
        if (isAnEmptyListInFilter(filters)) {
            return new ArrayList<E>();
        }

        config.setDoFetch(false);
        Query query = listQueryBuilder(config).getQuery(getEntityManager());
        if (config.isCacheable()) {
            query.setHint("org.hibernate.cacheable", true);
        }
        if (config.getLimit() != null) {
            query = query.setMaxResults(config.getLimit());
        }
        return query.getResultList();
    }

    public QueryBuilder listQueryBuilder(PaginationConfiguration config) {
    	Map<String, Object> filters = config.getFilters();

        if (filters != null && filters.containsKey("$FILTER")) {
            Filter filter = (Filter) filters.get("$FILTER");
            FilteredQueryBuilder queryBuilder = (FilteredQueryBuilder) getQuery(config);
            queryBuilder.processOrderCondition(filter.getOrderCondition(), filter.getPrimarySelector().getAlias());
            return queryBuilder;
        } else {
            QueryBuilder queryBuilder = getQuery(config);
            return queryBuilder;
        }
    }

    /**
     * @param filters
     * @return
     */
    private boolean isAnEmptyListInFilter(Map<String, Object> filters) {
        return filters == null ? false : filters.values().stream().filter(v -> v != null && v instanceof Collection && ((Collection) v).isEmpty()).findAny().isPresent();
    }

    /**
     * Used to retrieve related fields of an entity
     */
    @SuppressWarnings({ "unchecked" })
    public Map<String, Object> mapRelatedFields(String filter, long maxDepth, long currentDepth, Class<?> parentEntity) {
        final Class<? extends E> productClass = getEntityClass();
        StringBuilder queryString = new StringBuilder("from CustomFieldTemplate");
        Query query = getEntityManager().createQuery(queryString.toString());
        List<CustomFieldTemplate> resultsCFTmpl = query.getResultList();
        Map<String, Object> mapAttributeAndType = new LinkedHashMap<>();
        Set<Attribute<? super E, ?>> setAttributes = ((Session) getEntityManager().getDelegate()).getSessionFactory().getMetamodel().managedType(getEntityClass()).getAttributes();
        List<Attribute<? super E, ?>> sortedAttributes = new ArrayList<>(setAttributes);
        sortedAttributes.sort((a, b) -> a.getName().compareTo(b.getName()));
        for (Attribute<? super E, ?> att : sortedAttributes) {
            if (att.getJavaType() != CustomFieldValues.class) {
                Map<String, Object> mapStringAndType = new HashMap();
                mapStringAndType.put("fullQualifiedTypeName", att.getJavaType().toString());
                mapStringAndType.put("shortTypeName", att.getJavaType().getSimpleName());
                Boolean isEntity = BaseEntity.class.isAssignableFrom(att.getJavaType()) || ServiceTemplate.class.isAssignableFrom(att.getJavaType());
                if(StringUtils.isNotBlank(filter) && (!isEntity || maxDepth == (currentDepth+1) ) && !att.getName().toLowerCase().contains(filter.toLowerCase())) {
                	continue;
                }
				mapStringAndType.put("isEntity",  Boolean.toString(isEntity));
				if(isEntity && !att.getJavaType().equals(parentEntity) && (maxDepth == 0 || currentDepth < maxDepth) && currentDepth <= MAX_DEPTH) {
					PersistenceService<?> persistenceService = (PersistenceService<?>) EjbUtils.getServiceInterface(att.getJavaType().getSimpleName() + "Service");
					if(persistenceService == null){
						persistenceService = (PersistenceService) EjbUtils.getServiceInterface("BaseEntityService");
			            ((BaseEntityService) persistenceService).setEntityClass((Class<IEntity>) att.getJavaType());
			        }
					Map<String, Object> relatedFields = persistenceService.mapRelatedFields(filter, maxDepth, currentDepth + 1, entityClass);
					if(relatedFields.isEmpty()) {
						continue;
					}
					mapStringAndType.put("entityDetails", relatedFields);
				}
                mapAttributeAndType.put(att.getName(), mapStringAndType);
            } else {
                if (!resultsCFTmpl.isEmpty()) {
                	if(StringUtils.isNotBlank(filter) && !att.getName().toLowerCase().contains(filter.toLowerCase())) {
                    	continue;
                    }
                    Map<String, Map<String, String>> mapCFValues = new HashMap();
                    for (CustomFieldTemplate aCFTmpl : resultsCFTmpl) {
                        if (aCFTmpl.getAppliesTo().equals(productClass.getSimpleName())) {
                            Map<String, String> mapStringAndType = new HashMap();
                            mapStringAndType.put("fullQualifiedTypeName", aCFTmpl.getFieldType().getDataClass().toString());
                            mapStringAndType.put("shortTypeName", aCFTmpl.getFieldType().getDataClass().getSimpleName());
                            mapStringAndType.put("isEntity",  Boolean.toString(BaseEntity.class.isAssignableFrom(aCFTmpl.getFieldType().getDataClass()) || ServiceTemplate.class.isAssignableFrom(aCFTmpl.getFieldType().getDataClass())));
                            mapCFValues.put(aCFTmpl.getCode(), mapStringAndType);
                        }
                    }
                    if (!mapCFValues.isEmpty())
                        mapAttributeAndType.put(att.getName(), mapCFValues);
                }
            }
        }
        return mapAttributeAndType;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#count(PaginationConfiguration config)
     */
    @Override
    public long count(PaginationConfiguration config) {
        Map<String, Object> filters = config.getFilters();
        if (isAnEmptyListInFilter(filters)) {
            return 0;
        }
        List<String> fetchFields = config.getFetchFields();
        config.setFetchFields(null);
        QueryBuilder queryBuilder = getQuery(config);
        config.setFetchFields(fetchFields);
        return queryBuilder.count(getEntityManager());
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#count()
     */
    @Override
    public long count() {
        final Class<? extends E> entityClass = getEntityClass();
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        return queryBuilder.count(getEntityManager());
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#detach
     */
    @Override
    public void detach(E entity) {
        // TODO: Hibernate. org.hibernate.Session session = (Session)
        // getEntityManager().getDelegate();
        // session.evict(entity);
        getEntityManager().detach(entity);
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#refresh(org.meveo.model.IEntity)
     */
    @Override
    public void refresh(IEntity entity) {
        // entity manager throws exception if trying to refresh not managed
        // entity (ejb spec requires this).
        /*
         * TODO: Hibernate. org.hibernate.Session session = (Session) getEntityManager().getDelegate(); session.refresh(entity);
         */
        // getEntityManager().getEntityManagerFactory().getCache().evict(entity.getClass(),
        // entity.getId());
        if (getEntityManager().contains(entity)) {
            getEntityManager().refresh(entity);
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#refreshOrRetrieve(org.meveo.model.IEntity)
     */
    @Override
    public E refreshOrRetrieve(E entity) {

        if (entity == null) {
            return null;
        }

        if (getEntityManager().contains(entity)) {
            log.trace("Entity {}/{} will be refreshed) ..", getEntityClass().getSimpleName(), entity.getId());
            getEntityManager().refresh(entity);
            return entity;
        } else if (entity.getId() != null) {
            return findById((Long) entity.getId());
        } else {
            return entity;
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#refreshOrRetrieve(java.util.List)
     */
    @Override
    public List<E> refreshOrRetrieve(List<E> entities) {

        if (entities == null) {
            return null;
        }

        List<E> refreshedEntities = new ArrayList<E>();
        for (E entity : entities) {
            refreshedEntities.add(refreshOrRetrieve(entity));
        }

        return refreshedEntities;
    }

    @Override
    public Set<E> refreshOrRetrieve(Set<E> entities) {

        if (entities == null) {
            return null;
        }

        Set<E> refreshedEntities = new HashSet<E>();
        for (E entity : entities) {
            refreshedEntities.add(refreshOrRetrieve(entity));
        }

        return refreshedEntities;
    }

    /**
     * Retrieve an entity if it is not managed by EM
     *
     * @param entity Entity to retrieve
     * @return New instance of an entity
     */
    @Override
    public E retrieveIfNotManaged(E entity) {

        if (entity.getId() == null) {
            return entity;
        }

        // Entity is managed already
        if (getEntityManager().contains(entity)) {
            return entity;

        } else {
            return findById((Long) entity.getId());
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#retrieveIfNotManaged(java.util.List)
     */
    @Override
    public List<E> retrieveIfNotManaged(List<E> entities) {

        if (entities == null) {
            return null;
        }

        List<E> refreshedEntities = new ArrayList<E>();
        for (E entity : entities) {
            refreshedEntities.add(retrieveIfNotManaged(entity));
        }

        return refreshedEntities;
    }

    @Override
    public Set<E> retrieveIfNotManaged(Set<E> entities) {

        if (entities == null) {
            return null;
        }

        Set<E> refreshedEntities = new HashSet<E>();
        for (E entity : entities) {
            refreshedEntities.add(retrieveIfNotManaged(entity));
        }

        return refreshedEntities;
    }

    /**
     * Creates query to filter entities according data provided in pagination configuration.
     *
     * Search filters (key = Filter key, value = search pattern or value).
     *
     * Filter key can be:
     * <ul>
     * <li>"$FILTER". Value is a filter name</li>
     * <li>"type_class". Value is a full classname. Used to limit search results to a particular entity type in case of entity subclasses. Can be combined to condition "ne" to exclude those classes.</li>
     * <li>SQL. Additional sql to apply. Value is either a sql query or an array consisting of sql query and one or more parameters to apply</li>
     * <li>&lt;condition&gt; &lt;fieldname1&gt; &lt;fieldname2&gt; ... &lt;fieldnameN&gt;. Value is a value to apply in condition</li>
     * </ul>
     *
     * A union between different filter items is AND.
     *
     *
     * Condition is optional. Number of fieldnames depend on condition used. If no condition is specified an "equals ignoring case" operation is considered.
     *
     *
     * Following conditions are supported:
     * <ul>
     * <li><b>fromRange</b>. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value&lt;=fieldValue. Applies to date and number type fields. Date value is truncated to start of the
     * day</li>
     * <li><b>toRange</b>. Ranged search - field value in between from - to values. Specifies "to" part value: e.g fieldValue&lt;value. Value is exclusive. Applies to date and number type fields. Date value is truncated
     * to the start of the day</li>
     * <li><b>toRangeInclusive</b>. Ranged search - field value in between from - to values. Specifies "to" part value: e.g fieldValue&lt;=value. Value is inclusive. Applies to date and number type fields. Date value is
     * truncated to the end of the day</li>
     * <li><b>fromOptionalRange</b>. Ranged search - field value in between from - to values. Field value is optional. Specifies "from" part value: e.g value&lt;=field.value. Applies to date and number type fields. Date
     * value is truncated to start of the day</li>
     * <li><b>toOptionalRange</b>. Ranged search - field value in between from - to values. Field value is optional. Specifies "to" part value: e.g fieldValue&lt;value. Value is inclusive. Applies to date and number type
     * fields. Date value is truncated to the start of the day</li>
     * <li><b>toOptionalRangeInclusive</b>. Ranged search - field value in between from - to values. Field value is optional. Specifies "to" part value: e.g fieldValue&lt;=value. Value is inclusive. Applies to date and
     * number type fields. Date value is truncated to the end of the day</li>
     * <li><b>list</b>. Value is in field's list value. Applies to string, date and number type fields.</li>
     * <li><b>listInList</b>. Value, which is a list, should be in field value (list)
     * <li><b>inList</b>/<b>not-inList</b>. Field value is [not] in value (list). A comma separated string will be parsed into a list if values. A single value will be considered as a list value of one item</li>
     * <li><b>minmaxRange</b>. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields. The TO field value is exclusive. Date value is truncated to the start of
     * the day. E.f. field1Value&lt;value&ltfield2Value</li>
     * <li><b>minmaxRangeInclusive</b>. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields. The TO field value is inclusive. Date value is truncated to the
     * start of the day. E.g. field1Value&lt;=value&ltfield2Value</li>
     * <li><b>minmaxOptionalRange</b>. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must be specified. The TO field value is exclusive. Date value is
     * truncated to the start of the day.</li>
     * <li><b>minmaxOptionalRangeInclusive</b>. Similar to minmaxRangeOptional. The value is in between two field values with either them being optional. TWO fieldnames must be specified. The TO field value is inclusive.
     * Date value is truncated to the start of the day.</li>
     * <li><b>overlapOptionalRange</b>. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an array or a list of two values. End fields and to
     * values are exclusive.</li>
     * <li><b>overlapOptionalRangeInclusive</b>. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an array or a list of two values. End
     * fields and to values are inclusive.</li>
     * <li><b>likeCriterias</b>. Multiple fieldnames can be specified. Any of the multiple field values match the value (OR criteria). In case value contains *, a like criteria match will be used. In either case case
     * insensative matching is used. Applies to String type fields.</li>
     * <li><b>wildcardOr</b>. Similar to likeCriterias. A wildcard match will always used. A * will be appended to start and end of the value automatically if not present. Applies to
     * <li><b>wildcardOrIgnoreCase</b>. Similar to wildcardOr but ignoring case String type fields.</li>
     * <li><b>eq</b>. Equals. Supports wildcards in case of string value. NOTE: This is a default behavior when condition is not specified
     * <li><b>eqOptional</b>. Equals. Supports wildcards in case of string value. Field value is optional.
     * <li><b>ne</b>. Not equal.
     * <li><b>neOptional</b>. Not equal. Field value is optional.
     * </ul>
     *
     *
     * "eq" is a default condition when no condition is not specified
     *
     * Following special meaning values are supported:
     * <ul>
     * <li><b>IS_NULL</b>. Field value is null</li>
     * <li><b>IS_NOT_NULL</b>. Field value is not null</li>
     * </ul>
     *
     *
     *
     * To filter by a related entity's field you can either filter by related entity's field or by related entity itself specifying code as value. These two example will do the same in case when quering a customer
     * account: customer.code=aaa OR customer=aaa
     *
     * To filter a list of related entities by a list of entity codes use "inList" on related entity field. e.g. for quering offer template by sellers: inList sellers=code1,code2
     *
     *
     * <b>Note:</b> Quering by related entity field directly will result in exception when entity with a specified code does not exists
     *
     *
     * Examples:
     * <ul>
     * <li>invoice number equals "1578AU": Filter key: invoiceNumber. Filter value: 1578AU</li>
     * <li>invoice number is not "1578AU": Filter key: ne invoiceNumber. Filter value: 1578AU</li>
     * <li>invoice number is null: Filter key: invoiceNumber. Filter value: IS_NULL</li>
     * <li>invoice number is not empty: Filter key: invoiceNumber. Filter value: IS_NOT_NULL</li>
     * <li>Invoice date is between 2017-05-01 and 2017-06-01: Filter key: fromRange invoiceDate. Filter value: 2017-05-01 Filter key: toRange invoiceDate. Filter value: 2017-06-01</li>
     * <li>Date is between creation and update dates: Filter key: minmaxRange audit.created audit.updated. Filter value: 2017-05-25</li>
     * <li>invoice number is any of 158AU, 159KU or 189LL: Filter key: inList invoiceNumber. Filter value: 158AU,159KU,189LL</li>
     * <li>any of param1, param2 or param3 fields contains "energy": Filter key: wildcardOr param1 param2 param3. Filter value: energy</li>
     * <li>any of param1, param2 or param3 fields start with "energy": Filter key: likeCriterias param1 param2 param3. Filter value: *energy</li>
     * <li>any of param1, param2 or param3 fields is "energy": Filter key: likeCriterias param1 param2 param3. Filter value: energy</li>
     * </ul>
     *
     *
     * @param config Data filtering, sorting and pagination criteria
     * @return query to filter entities according pagination configuration data.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public QueryBuilder getQuery(PaginationConfiguration config) {
    	return getQuery(config,"a", config.isDistinctQuery());
    }
    	
    public QueryBuilder getQuery(PaginationConfiguration config, String alias, boolean distinct) {
        Map<String, Object> filters = config.getFilters();

        adaptOrdering(config, filters);
        
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, alias, config.getSelectFields(), config.isDoFetch(), config.getFetchFields(), config.getJoinType(), config.getFilterOperator(), distinct, !config.isQueryReportQuery());
        if (filters != null && !filters.isEmpty()) {
            if (filters.containsKey(SEARCH_FILTER)) {
                Filter filter = (Filter) filters.get(SEARCH_FILTER);
                Map<CustomFieldTemplate, Object> parameterMap = (Map<CustomFieldTemplate, Object>) filters.get(SEARCH_FILTER_PARAMETERS);
                queryBuilder = new FilteredQueryBuilder(filter, parameterMap, false, false);
            } else {

                Map<String, Object> cfFilters = extractCustomFieldsFilters(filters);
                if(MapUtils.isNotEmpty(cfFilters)) {
                    filters.putAll(cfFilters);
                }

                ExpressionFactory expressionFactory = new ExpressionFactory(queryBuilder, alias);
                filters.keySet().stream().sorted((k1, k2) -> org.apache.commons.lang3.StringUtils.countMatches(k2, ".") - org.apache.commons.lang3.StringUtils.countMatches(k1, "."))
                						 .filter(key -> filters.get(key) != null && !"$OPERATOR".equalsIgnoreCase(key))
                						 .forEach(key -> expressionFactory.addFilters(key, filters.get(key)));
                for (String cft : cfFilters.keySet()) {
                    filters.remove(cft);
                }
            }
        }

        if (filters != null && filters.containsKey("$FILTER")) {
            Filter filter = (Filter) filters.get("$FILTER");
            queryBuilder.addPaginationConfiguration(config, filter.getPrimarySelector().getAlias());
        } else {
            queryBuilder.addPaginationConfiguration(config, alias);
        }

        // log.trace("Filters is {}", filters);
        // log.trace("Query is {}", queryBuilder.getSqlString());
        // log.trace("Query params are {}", queryBuilder.getParams());
        return queryBuilder;
    }

	private void adaptOrdering(PaginationConfiguration config, Map<String, Object> filters) {
		if(config==null || config.getOrderings()==null) {
			return;
		}
		List<String> orderings = new ArrayList<String>();
		for (Object x : config.getOrderings()) {
			String orderElement = x.toString();
			if ((orderElement.startsWith(CF_VALUES_FIELD) || orderElement.contains("." + CF_VALUES_FIELD))
					&& !orderElement.contains(FROM_JSON_FUNCTION)) {
				String fieldName = ((String) x).substring(orderElement.lastIndexOf(".") + 1);
				Class cetClass = extractCETClass(orderElement);
				CustomFieldTemplate cft = extractCFT(filters, fieldName, cetClass);
				String type = getJsonType(cft.getFieldType().getDataClass());
				String nested = orderElement.startsWith(CF_VALUES_FIELD) ? ""
						: orderElement.substring(0, orderElement.indexOf(CF_VALUES_FIELD));
				orderElement = extractCustomFieldSyntax(type, cft.getFieldType().getDataClass(), "", fieldName, nested);
			}
			orderings.add(orderElement);
		}
		config.setOrderings(orderings.toArray());
	}

	private Class extractCETClass(String orderElement) {
		Class currentEntity = entityClass;
		final String[] elements = orderElement.split("\\.");
		for (String element : elements) {
			if (CF_VALUES_FIELD.equals(element)) {
				break;
			}
			try {
				currentEntity = currentEntity.getDeclaredField(element).getType();
			} catch (NoSuchFieldException | SecurityException e) {
				throw new BusinessException("error when tryin to get field " + element + " from entity "
						+ currentEntity.getSimpleName() + " : " + e.getStackTrace());
			}
		}
		return currentEntity;
	}

	private CustomFieldTemplate extractCFT(Map<String, Object> filters, String fieldName, Class currentEntity) {
		String appliesTo = currentEntity.getSimpleName();
		if (currentEntity == CustomEntityInstance.class) {
			String cetCode = (String) filters.get("cetCode");
			if (cetCode == null) {
				throw new ValidationException("cetCode is mandatory on filter to order by custom fields values");
			}
			appliesTo = CustomEntityTemplate.CFT_PREFIX + "_" + cetCode;
		}
		CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(fieldName, appliesTo);
		if (cft == null) {
			throw new ValidationException("no CustomFieldTemplate found for fieldName=" + fieldName + " and appliesTo= " + appliesTo);
		}
		return cft;
	}

    /**
	 * @param dataClass
	 * @return
	 */
	public static String getJsonType(Class dataClass) {
		if (jsonTypes.isEmpty()) {
			final Field[] declaredFields = CustomFieldValue.class.getDeclaredFields();
			for (final Field field : declaredFields) {
				if (field.isAnnotationPresent(JsonProperty.class) && field.getName().endsWith("Value")) {
					jsonTypes.put(field.getType(), field.getAnnotation(JsonProperty.class).value());
				}
			}
		}
		return jsonTypes.get(dataClass);
	}

	/**
     * @param fieldName
     * @return
     */
    private boolean isFieldCollection(String fieldName) {
        if (fieldName.contains(FROM_JSON_FUNCTION)) {
            return false;
        }
        final Class<? extends E> entityClass = getEntityClass();
        Field field = ReflectionUtils.getField(entityClass, fieldName);
        Class<?> fieldClassType = field.getType();
        return Collection.class.isAssignableFrom(fieldClassType);
    }

    private String extractFieldWithAlias(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        return fieldName.contains(FROM_JSON_FUNCTION) ? fieldName : "a." + fieldName;
    }

    private Map<String, Object> extractCustomFieldsFilters(Map<String, Object> filters) {
        Map<String, Object> cftFilters = new TreeMap<String, Object>();
        for (Entry<String, Object> entry : filters.entrySet()) {
            Object filterValue = entry.getValue();
            if (filterValue instanceof CustomFieldValues) {
                CustomFieldValues customFieldValues = (CustomFieldValues) filterValue;
                customFieldValues = checkCFValuesShouldBeEncrypted(customFieldValues);

                Map<String, List<CustomFieldValue>> valuesByCode = customFieldValues.getValuesByCode();
                for (String customFiterName : valuesByCode.keySet()) {
                    // get the filter value
                    CustomFieldValue cfv = valuesByCode.get(customFiterName).get(0);
                    Map<String, Object> map = cfv.getkeyValueMap();
                    String type = (String) map.keySet().toArray()[0];
                    Object value = map.values().toArray()[0];

                    ExpressionParser fieldInfo = new ExpressionParser(customFiterName.split(" "));
                    String transformedFilter = fieldInfo.getCondition() != null ? fieldInfo.getCondition() + " " : "";

                    // In case or search inside a LIST storage type CF field, use a SQL search with the following function:
                    // listVarcharFromJson(<entity>.cfValues,<custom field name>,<value to search for>)=true
                    if (SEARCH_LIST.equals(fieldInfo.getCondition())) {
                        if ("string".equals(type)) {
                           value = "'" + value + "'";
                        }
                        type = "list" + StringUtils.capitalizeFirstLetter(type);
                        String searchFunction = "list";
                        transformedFilter = searchFunction + FROM_JSON_FUNCTION + "cfValues," + fieldInfo.getFieldName() + "," + type + ",0," + value + ")=true ";
                        cftFilters.put(PersistenceService.SEARCH_SQL + "_" + fieldInfo.getFieldName(), transformedFilter);

                    } else {
                        if (type.startsWith("list")) {
                            type = type.substring(4).toLowerCase(); // A fix so inList search by a list of values would be compared against a regular field instead of a list type field e.g. listString - See BaseApi.castFilterValue logic 
                        }

                        String fieldNamePrefix = entry.getKey().replace("cfValues", "");
                        for (String fieldName : fieldInfo.getAllFields()) {
                            transformedFilter = extractCustomFieldSyntax(type, value.getClass(), transformedFilter, fieldName, fieldNamePrefix);
                        }

                        if (value instanceof EntityReferenceWrapper) {
                            cftFilters.put(transformedFilter, ((EntityReferenceWrapper) value).getCode());
                        } else {
                            cftFilters.put(transformedFilter, value);
                        }
                    }
                }
            }
        }
        return cftFilters;
    }

    //check if search by the CF value should use or not the encrypted value
    private CustomFieldValues checkCFValuesShouldBeEncrypted(CustomFieldValues customFieldValues) {
        if (!encryptCFSetting) {
            return customFieldValues;
        }
        String encryptedCFJson = CustomFieldJsonTypeDescriptor.INSTANCE.toString(customFieldValues);
        Map<String, List<CustomFieldValue>> cfValues = JacksonUtil.fromString(encryptedCFJson, new TypeReference<Map<String, List<CustomFieldValue>>>() {});
        return new CustomFieldValues(cfValues);
    }

    private String extractCustomFieldSyntax(String type, Class clazz, String transformedFilter, String fieldName, String nestedFields) {
		nestedFields=nestedFields==null?"":nestedFields;
		String searchFunction = getCustomFieldSearchFunctionPrefix(clazz);
		transformedFilter = transformedFilter + searchFunction + FROM_JSON_FUNCTION+nestedFields+"cfValues," + fieldName + "," + type + ") ";
		return transformedFilter;
	}


    /**
     * Update last modified information (created/updated date and username)
     *
     * @param entity Entity to update
     */
    public void updateAudit(E entity) {
        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }
    }

    /**
     *
     * @param query query to execute
     * @param params map of parameter
     * @return query result.
     */
    public Object executeSelectQuery(String query, Map<String, Object> params) {
        Query q = getEntityManager().createQuery(query);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return q.getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    /**
     * Execute a native select query
     *
     * @param query Sql query to execute
     * @param params Parameters to pass
     * @return A map of values retrieved
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> executeNativeSelectQuery(String query, Map<String, Object> params) {
        Session session = getEntityManager().unwrap(Session.class);
        SQLQuery q = session.createSQLQuery(query);

        q.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);

        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
        List<Map<String, Object>> aliasToValueMapList = q.list();

        return aliasToValueMapList;
    }

    /**
     * Get scrollable result from a native select query
     *
     * @param query Sql query to execute
     * @param params Parameters to pass
     * @return Scrollable results
     */
    @SuppressWarnings("rawtypes")
    public ScrollableResults getScrollableResultNativeQuery(String query, Map<String, Object> params) {
        Session session = getEntityManager().unwrap(Session.class);
        NativeQuery q = session.createNativeQuery(query);
        
        q.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);
        q.setFetchSize(10000);
        q.setReadOnly(true);
        q.setLockMode("a", LockMode.NONE);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return q.scroll(ScrollMode.FORWARD_ONLY);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void remove(Class parentClass, Object parentId) {
        Field idField = getIdField(parentClass);
        if (idField != null) {
            List<Field> oneToManyFields = getOneToManyFields(parentClass);
            for (Field field : oneToManyFields) {
                Class childClass = getFirstActualTypeArgument(field);
                if (childClass != null) {
                    Field manyToOneField = getManyToOneField(childClass, parentClass);
                    Field childClassIdField = getIdField(childClass);
                    if (manyToOneField != null && childClassIdField != null) {
                        List<Long> childIds = getEntityManager()
                            .createQuery(String.format("select c.%s from %s c where c.%s.%s = :pid", childClassIdField.getName(), childClass.getSimpleName(), manyToOneField.getName(), idField.getName()))
                            .setParameter("pid", parentId).getResultList();
                        for (Long childId : childIds) {
                            getEntityManager().createQuery(String.format("delete from %s c where c.%s = :id", childClass.getSimpleName(), childClassIdField.getName())).setParameter("id", childId).executeUpdate();
                        }
                    }
                }
            }
            getEntityManager().createQuery(String.format("delete from %s e where e.%s = :id", parentClass.getSimpleName(), idField.getName())).setParameter("id", parentId).executeUpdate();
        }
    }

    @SuppressWarnings("rawtypes")
    private Class getFirstActualTypeArgument(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                return (Class<?>) typeArguments[0];
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private Field getManyToOneField(Class clazz, Class parentClass) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(ManyToOne.class)) {
                Class<?> type = field.getType();
                if (parentClass.equals(type)) {
                    return field;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private Field getIdField(Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private List<Field> getOneToManyFields(Class clazz) {
        List<Field> fields = new LinkedList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Find entities that reference a given class and ID. Used when deleting entities to determine what FK constraints are preventing to remove a given entity
     *
     * @param entityClass Entity class to reference
     * @param id Entity ID
     * @return A concatenated list of entities (humanized classnames and their codes) E.g. Customer Account: first ca, second ca, third ca; Customer: first customer, second customer
     */
    @SuppressWarnings("rawtypes")
    public String findReferencedByEntities(Class<E> entityClass, Long id) {

        E referencedEntity = getEntityManager().getReference(entityClass, id);

        int totalMatched = 0;
        String matchedEntityInfo = null;
        Map<Class, List<Field>> classesAndFields = ReflectionUtils.getClassesAndFieldsOfType(entityClass);

        for (Entry<Class, List<Field>> classFieldInfo : classesAndFields.entrySet()) {

            boolean isBusinessEntity = BusinessEntity.class.isAssignableFrom(classFieldInfo.getKey());

            StringBuilder sql = new StringBuilder("select ").append(isBusinessEntity ? "code" : "id").append(" from ").append(classFieldInfo.getKey().getName()).append(" where ");
            boolean fieldAddedToSql = false;
            for (Field field : classFieldInfo.getValue()) {
                // For now lets ignore list type fields
                if (field.getType() == entityClass) {
                    sql.append(fieldAddedToSql ? " or " : " ").append(field.getName()).append("=:id");
                    fieldAddedToSql = true;
                }
            }

            if (fieldAddedToSql) {

                List entitiesMatched = getEntityManager().createQuery(sql.toString()).setParameter("id", referencedEntity).setMaxResults(10).getResultList();
                if (!entitiesMatched.isEmpty()) {

                    matchedEntityInfo = (matchedEntityInfo == null ? "" : matchedEntityInfo + "; ") + ReflectionUtils.getHumanClassName(classFieldInfo.getKey().getSimpleName()) + ": ";
                    boolean first = true;
                    for (Object entityIdOrCode : entitiesMatched) {
                        matchedEntityInfo += (first ? "" : ", ") + entityIdOrCode;
                        first = false;
                    }

                    totalMatched += entitiesMatched.size();
                }
            }

            if (totalMatched > 10) {
                break;
            }
        }

        return matchedEntityInfo;
    }

    /**
     * Get a list of entities from a named query
     *
     * @param queryName Named query name
     * @param parameters A list of parameters in a form or parameter name, value, parameter name, value,..
     * @return A list of entities
     */
    public List<E> listByNamedQuery(String queryName, Object... parameters) {

        TypedQuery<E> query = getEntityManager().createNamedQuery(queryName, entityClass);

        for (int i = 0; i < parameters.length; i = i + 2) {
            query.setParameter((String) parameters[i], parameters[i + 1]);
        }

        return query.getResultList();
    }

    /**
     * Get a xxxFromJson function prefix based on a data type
     * 
     * @param clazz Data type
     * @return xxxFromJson function prefix - xxx part
     */
    private String getCustomFieldSearchFunctionPrefix(Class<?> clazz) {
        if (clazz == Date.class) {
            return "timestamp";
        } else if (clazz == EntityReferenceWrapper.class) {
            return "entity";
        } else if (clazz == Double.class) {
            return "numeric";
        } else if (clazz == Long.class) {
            return "bigInt";
        } else if (clazz == Boolean.class) {
            return "boolean";
        } else {
            return "varchar";
        }
    }
    
    public Object deepCopyObject(Class<E> old) throws Exception {
   	 	ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
           ByteArrayOutputStream bos = new ByteArrayOutputStream();
           oos = new ObjectOutputStream(bos);
           oos.writeObject(old);
           oos.flush();               
           ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); 
           ois = new ObjectInputStream(bin);                  
           return ois.readObject(); 
        }
        catch(Exception e) {
        	log.error("Exception in ObjectCloner : ",e);
        	throw(e);
        }
        finally {
           oos.close();
           ois.close();
        }
   }
    
    /**
	 * @param entity
	 * @param id
	 * @return
	 */
	public IEntity tryToFindByEntityClassAndId(Class<? extends IEntity> entity, Long id) {
    	return tryToFindByEntityClassAndId(entity, id, null);
	}
	
	/**
	 * @param entity
	 * @param id
	 * @param fetchFields
	 * @return
	 */
	public IEntity tryToFindByEntityClassAndId(Class<? extends IEntity> entity, Long id, List<String> fetchFields) {
    	if(id==null) {
    		return null;
    	}
        QueryBuilder qb = new QueryBuilder(entity, "entity", null);
        qb.addCriterion("entity.id", "=", id, true);
        try {
			return (IEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("No entity of type "+entity.getSimpleName()+" with id '"+id+"' found");
        } catch (NonUniqueResultException e) {
        	throw new ForbiddenException("More than one entity of type "+entity.getSimpleName()+" with id '"+id+"' found");
        }
	}

	public BusinessEntity tryToFindByEntityClassAndCode(Class<? extends BusinessEntity> entity, String code) {
    	return tryToFindByEntityClassAndCode(entity, code, null);
    }
	
	public BusinessEntity tryToFindByEntityClassAndCode(Class<? extends BusinessEntity> entity, String code, List<String> fetchFields) {
    	if(code==null) {
    		return null;
    	}
        QueryBuilder qb = new QueryBuilder(entity, "entity", null);
        qb.addCriterion("entity.code", "=", code, true);
        try {
			return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("No entity of type "+entity.getSimpleName()+" with code '"+code+"' found");
        } catch (NonUniqueResultException e) {
        	throw new ForbiddenException("'code' for entity "+entity.getSimpleName()+" is not an unique identifier. Please use id instead");
        }
    }
	
	
	
	/**
	 * Try to find IEntity by code, if code is null, try with id.
	 * 
	 * @param entity
	 * @param code
	 * @param id
	 * @return
	 */
	public BusinessEntity tryToFindByEntityClassAndCodeOrId(Class<? extends BusinessEntity> entity, String code, Long id) {
		return tryToFindByEntityClassAndCodeOrId(entity, code, id, null);
	}
	
	/**
	 * Try to find IEntity by code, if code is null, try with id.
	 * 
	 * @param entity
	 * @param code
	 * @param id
	 * @return
	 */
	public BusinessEntity tryToFindByEntityClassAndCodeOrId(Class<? extends BusinessEntity> entity, String code, Long id, List<String> fetchFields) {
		if (code != null && !code.isBlank()) {
			return tryToFindByEntityClassAndCode(entity, code);
		} else if (id != null) {
			return (BusinessEntity) tryToFindByEntityClassAndId(entity, id);
		}
		return null;
	}
	

	/**
	 * try to find entity in database by code or id
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public <B extends BusinessEntity> B tryToFindByCodeOrId(B instance) {
		return (B) tryToFindByCodeOrId(instance, null);
	}

	/**
	 * try to find entity in database by code or id
	 * @param instance
	 * @return
	 */
	public BusinessEntity tryToFindByCodeOrId(BusinessEntity instance, List<String> fetchFields) {
		if (instance == null) {
			return null;
		}
		final String entityName = instance.getClass().getName();
		final BusinessEntity entity = tryToFindByEntityClassAndCodeOrId(instance.getClass(), instance.getCode(),
				instance.getId());
		if (entity == null) {
			throw new BadRequestException("No "+entityName+" found with Id: "+instance.getId()+" or Code :"+instance.getCode());
		}
		return entity;
	}
	
	public BusinessEntity tryToFindByEntityClassAndMap(Class<? extends BusinessEntity> entity, Map<String, Object> criterions) {
    	if(criterions==null) {
    		return null;
    	}
    	String where = "";
        QueryBuilder qb = new QueryBuilder(entity, "entity", null);
        for(Entry<String, Object> e : criterions.entrySet()) {
        	qb.addCriterion("entity."+e.getKey(), "=", e.getValue(), true);
        	where=where +" entity."+e.getKey()+"="+ e.getValue()+",";
        }
        
        try {
			return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("No entity of type "+entity.getSimpleName()+"with "+where+" found");
        } catch (NonUniqueResultException e) {
        	throw new BusinessException("More than one entity of type "+entity.getSimpleName()+" with "+where+" found");
        }
    }

    /**
     * Check if event of a given type is enabled for a given entity
     * 
     * @param eventType Event type
     * @return True if events of such event type exist for an entity class that persistence service corresponds to
     */
    public boolean areEventsEnabled(NotificationEventTypeEnum eventType) {
        List<Notification> notifications = genericNotificationService.getApplicableNotifications(NotificationEventTypeEnum.CREATED, ReflectionUtils.createObject(entityClass.getName()));
        return notifications != null && !notifications.isEmpty();
    }

    /**
     * Execute a HQL select query
     *
     * @param hqlQuery HQL query to execute
     * @param params Parameters to pass
     * @param pageSize 
     * @param pageIndex 
     * @return A map of values retrieved
     */
    public List<Map<String, Object>> getSelectQueryAsMap(String hqlQuery, Map<String, Object> params){
    	return getSelectQueryAsMap(hqlQuery, params, null, null);
    }

    
    /**
     * Execute a HQL select query
     *
     * @param hqlQuery HQL query to execute
     * @param params Parameters to pass
     * @param pageSize 
     * @param pageIndex 
     * @return A map of values retrieved
     */
    public List<Map<String, Object>> getSelectQueryAsMap(String hqlQuery, Map<String, Object> params, Integer pageSize, Integer pageIndex) {
        TypedQuery<Tuple> query = getEntityManager().createQuery(hqlQuery, Tuple.class);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if(pageIndex!=null && pageSize!=null) {
        	query.setMaxResults(pageSize).setFirstResult(pageIndex * pageSize);
        }
        return query.getResultStream().map(mapTuplesAsMap()).collect(Collectors.toList());
    }

    private Function<Tuple, Map<String, Object>> mapTuplesAsMap() {
        return data -> {
            Map<String, Object> map = new HashMap<>();
            for (TupleElement<?> tuple : data.getElements()) {

                Object value = data.get(tuple.getAlias());
                if (value instanceof Clob) {
                    try {
                        value = IOUtils.toString(((Clob) value).getCharacterStream());

                    } catch (IOException | SQLException e) {
                        throw new RuntimeException("Failed to read clob value", e);
                    }
                }

                map.put(tuple.getAlias(), value);
            }
            return map;
        };
    }
    
	/**
     * Create Query builder from a map of filters
     * 
     * @param filters Map of filters
     * @param selectFields Fields to return. If null, a complete entity will be returned
     * @param fetchFields Fields to fetch (join to related tables)
     * @param distinct Is this a distinct query
     * @return QueryBuilder
	 */
    public QueryBuilder getQueryFromFilters(Map<String, Object> filters, String selectFields, List<String> fetchFields, boolean distinct) {
		QueryBuilder queryBuilder;
		String filterValue = QueryBuilder.getFilterByKey(filters, "SQL");
		if (!StringUtils.isBlank(filterValue)) {
            queryBuilder = new QueryBuilder(filterValue, "a", distinct);
		} else {
            FilterConverter converter = new FilterConverter(getEntityClass());
			PaginationConfiguration configuration = new PaginationConfiguration(converter.convertFilters(filters));
			if (!CollectionUtils.isEmpty(fetchFields)) {
				configuration.setFetchFields(fetchFields);
			}
            configuration.setSelectFields(selectFields);
            queryBuilder = getQuery(configuration, "a", distinct);
		}
		return queryBuilder;
	}

    public Long findNextSequenceId(String sequenceName) {
        BigInteger nextSequenceValue = (BigInteger) getEntityManager().createNativeQuery(isDBOracle()
                        ? "SELECT " + sequenceName +".nextval FROM dual"
                        : "select nextval('"+ sequenceName + "')" )
                .getSingleResult();
        return (nextSequenceValue.add(ONE)).longValue();
    }

    /**
     * Get the underlying SQL generated by the provided JPA query. Parameters will be substituted with values if provided.
     *
     * @param jpaQuery JPA query
     * @param params Query parameter values. If provided, parameters will be substituted with values.
     * @return The underlying SQL generated by the provided JPA query
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    public static String getNativeQueryFromJPA(Query jpaQuery, Map<String, Object> params) {

        AbstractProducedQuery abstractProducedQuery = jpaQuery.unwrap(AbstractProducedQuery.class);
        String[] sqls = abstractProducedQuery.getProducer().getFactory().getQueryPlanCache().getHQLQueryPlan(abstractProducedQuery.getQueryString(), false, Collections.emptyMap()).getSqlStrings();

        if (sqls.length == 0) {
            return null;
        }

        String nativeSql = sqls[0];

        String jpaQueryString = abstractProducedQuery.getQueryString();

        Pattern pattern = Pattern.compile("(:\\w*)[ |)]?");
        Matcher matcher = pattern.matcher(jpaQueryString);

        while (matcher.find()) {
            nativeSql = nativeSql.replaceFirst("\\?", matcher.group(1));
        }

        // Parameters will be substituted with values if provided.
        if (params != null) {
            for (Entry<String, Object> param : params.entrySet()) {

                String paramValue = null;
                if (param.getValue() instanceof Number) {
                    paramValue = param.getValue().toString();
                } else if (param.getValue() instanceof Date) {
                    paramValue = "TO_DATE('" + DateUtils.formatAsDate((Date) param.getValue()) + "','YYYY-MM-DD')";
                } else {
                    paramValue = "'" + param.getValue() + "'";
                }

                nativeSql = nativeSql.replaceAll(":" + param.getKey(), paramValue);
            }
        }

        return nativeSql;
    }
}