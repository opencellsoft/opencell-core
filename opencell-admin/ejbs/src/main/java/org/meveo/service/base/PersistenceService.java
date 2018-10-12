/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Updated;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IAuditable;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEnable;
import org.meveo.model.IEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.ObservableEntity;
import org.meveo.model.UniqueEntity;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.filter.Filter;
import org.meveo.model.transformer.AliasToEntityOrderedMapResultTransformer;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.custom.CfValueAccumulator;
import org.meveo.service.index.ElasticClient;

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
    public static String SEARCH_ATTR_TYPE_CLASS = "type_class";
    /**
     * Entity list search parameter value - parameter's value is null
     */
    public static String SEARCH_IS_NULL = "IS_NULL";
    /**
     * Entity list search parameter value - parameter's value is not null
     */
    public static String SEARCH_IS_NOT_NULL = "IS_NOT_NULL";
    /**
     * Entity list search parameter criteria - wildcard Or
     */
    public static String SEARCH_WILDCARD_OR = "wildcardOr";
    /**
     * Entity list search parameter name - parameter's value contains sql statement
     */
    public static String SEARCH_SQL = "SQL";
    /**
     * Entity list search parameter criteria - just like wildcardOr but Ignoring case
     */
    public static String SEARCH_WILDCARD_OR_IGNORE_CAS = "wildcardOrIgnoreCase";
    /**
     * Entity list search parameter name - parameter's value contains filter name
     */
    public static String SEARCH_FILTER = "$FILTER";

    /**
     * Entity list search parameter name - parameter's value contains filter parameters
     */
    public static String SEARCH_FILTER_PARAMETERS = "$FILTER_PARAMETERS";

    static boolean accumulateCF = true;

    @PostConstruct
    private void init() {
        accumulateCF = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("accumulateCF", "true"));
    }

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private ElasticClient elasticClient;

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
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    private CfValueAccumulator cfValueAccumulator;

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
    public E updateNoCheck(E entity) throws BusinessException {
        log.debug("start of update {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());

        if (BusinessEntity.class.isAssignableFrom(entity.getClass())) {
            // validate code
            validateCode((BusinessEntity) entity);
        }

        updateAudit(entity);
        E mergedEntity = getEntityManager().merge(entity);

        return mergedEntity;
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
     * Use by API.
     */
    @Override
    public E findById(Long id, boolean refresh) {
        log.trace("start of find {}/{} by id ..", entityClass.getSimpleName(), id);
        E e = getEntityManager().find(entityClass, id);
        if (e != null) {
            if (refresh) {
                log.debug("refreshing loaded entity");
                getEntityManager().refresh(e);
            }
        }
        log.trace("end of find {}/{} by id. Result found={}.", entityClass.getSimpleName(), id, e != null);
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
        log.debug("start of find {}/{} by id ..", getEntityClass().getSimpleName(), id);
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
                log.debug("refreshing loaded entity");
                getEntityManager().refresh(e);
            }
        }
        log.trace("end of find {}/{} by id. Result found={}.", getEntityClass().getSimpleName(), id, e != null);
        return e;
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
    public void remove(E entity) throws BusinessException {
        log.debug("start of remove {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
        entity = findById((Long) entity.getId());
        if (entity != null) {
            getEntityManager().remove(entity);
            if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
                entityRemovedEventProducer.fire((BaseEntity) entity);
            }
            // getEntityManager().flush();

            // Remove entity from Elastic Search
            if (BusinessEntity.class.isAssignableFrom(entity.getClass())) {
                elasticClient.remove((BusinessEntity) entity);
            }

            // Remove custom field values from cache if applicable
            if (entity instanceof ICustomFieldEntity) {
                customFieldInstanceService.removeCFValues((ICustomFieldEntity) entity);
            }

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
    public E update(E entity) throws BusinessException {
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
        // Be carefull - if called after persistence might loose ability to determine new period as CustomFeldvalue.isNewPeriod is not serialized to json
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

        entity = getEntityManager().merge(entity);

        // Update entity in Elastic Search. ICustomFieldEntity is updated
        // partially, as entity itself does not have Custom field values
        if (entity instanceof BusinessCFEntity) {
            elasticClient.partialUpdate((BusinessEntity) entity);
        } else if (entity instanceof ISearchable) {
            elasticClient.createOrFullUpdate((ISearchable) entity);
        }

        if (accumulateCF && entity instanceof ICustomFieldEntity && dirtyCfValues != null && !dirtyCfValues.isEmpty()) {
            // CustomFieldValues cfValues = ((ICustomFieldEntity) entity).getCfValues();
            cfValueAccumulator.entityUpdated((ICustomFieldEntity) entity, dirtyCfValues, dirtyCfPeriods);
        }

        log.trace("end of update {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());

        return entity;
    }

    private boolean validateCode(ISearchable entity) throws BusinessException {
        // if (!StringUtils.isMatch(entity.getCode(), ParamBeanFactory.getAppScopeInstance().getProperty("meveo.code.pattern", StringUtils.CODE_REGEX))) {
        // throw new BusinessException("Invalid characters found in entity code.");
        // }

        return true;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#create(org.meveo.model.IEntity)
     */
    @Override
    public void create(E entity) throws BusinessException {
        log.debug("start of create {}", entity.getClass().getSimpleName());

        if (entity instanceof ISearchable) {
            validateCode((ISearchable) entity);
        }

        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }
        // Schedule end of period events
        // Be carefull - if called after persistence might loose ability to determine new period as CustomFeldvalue.isNewPeriod is not serialized to json
        if (entity instanceof ICustomFieldEntity) {
            customFieldInstanceService.scheduleEndPeriodEvents((ICustomFieldEntity) entity);
        }

        getEntityManager().persist(entity);

        // Add entity to Elastic Search
        if (ISearchable.class.isAssignableFrom(entity.getClass())) {
            elasticClient.createOrFullUpdate((ISearchable) entity);
        }

        if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
            entityCreatedEventProducer.fire((BaseEntity) entity);
        }

        if (accumulateCF && entity instanceof ICustomFieldEntity) {
            cfValueAccumulator.entityCreated((ICustomFieldEntity) entity);
        }

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
        Query query = queryBuilder.getQuery(getEntityManager());
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
     * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.util.pagination.PaginationConfiguration)
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public List<E> list(PaginationConfiguration config) {
        Map<String, Object> filters = config.getFilters();

        if (filters != null && filters.containsKey("$FILTER")) {
            Filter filter = (Filter) filters.get("$FILTER");
            FilteredQueryBuilder queryBuilder = (FilteredQueryBuilder) getQuery(config);
            queryBuilder.processOrderCondition(filter.getOrderCondition(), filter.getPrimarySelector().getAlias());
            Query query = queryBuilder.getQuery(getEntityManager());
            return query.getResultList();
        } else {
            QueryBuilder queryBuilder = getQuery(config);
            Query query = queryBuilder.getQuery(getEntityManager());
            return query.getResultList();
        }
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#count(PaginationConfiguration config)
     */
    @Override
    public long count(PaginationConfiguration config) {
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
        } else {
            return findById((Long) entity.getId());
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
     * <li>"type_class". Value is a full classname. Used to limit search results to a particular entity type in case of entity subclasses. Can be combined to condition "ne" to
     * exclude those classes.</li>
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
     * <li>fromRange. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value&lt;=fiel.value. Applies to date and number type fields.</li>
     * <li>toRange. Ranged search - field value in between from - to values. Specifies "to" part value: e.g field.value&lt;=value</li>
     * <li>list. Value is in field's list value. Applies to date and number type fields.</li>
     * <li>inList/not-inList. Field value is [not] in value (list). A comma separated string will be parsed into a list if values. A single value will be considered as a list value
     * of one item</li>
     * <li>minmaxRange. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields.</li>
     * <li>minmaxOptionalRange. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must be specified.</li>
     * <li>overlapOptionalRange. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an array of two
     * values.</li>
     * <li>likeCriterias. Multiple fieldnames can be specified. Any of the multiple field values match the value (OR criteria). In case value contains *, a like criteria match will
     * be used. In either case case insensative matching is used. Applies to String type fields.</li>
     * <li>wildcardOr. Similar to likeCriterias. A wildcard match will always used. A * will be appended to start and end of the value automatically if not present. Applies to
     * <li>wildcardOrIgnoreCase. Similar to wildcardOr but ignoring case String type fields.</li>
     * <li>ne. Not equal.
     * </ul>
     * 
     * Following special meaning values are supported:
     * <ul>
     * <li>IS_NULL. Field value is null</li>
     * <li>IS_NOT_NULL. Field value is not null</li>
     * </ul>
     * 
     * 
     * 
     * To filter by a related entity's field you can either filter by related entity's field or by related entity itself specifying code as value. These two example will do the
     * same in case when quering a customer account: customer.code=aaa OR customer=aaa
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
     * <li>Invoice date is between 2017-05-01 and 2017-06-01: Filter key: fromRange invoiceDate. Filter value: 2017-05-01 Filter key: toRange invoiceDate. Filter value:
     * 2017-06-01</li>
     * <li>Date is between creation and update dates: Filter key: minmaxRange audit.created audit.updated. Filter value: 2017-05-25</li>
     * <li>invoice number is any of 158AU, 159KU or 189LL: Filter key: inList invoiceNumber. Filter value: 158AU,159KU,189LL</li>
     * <li>any of param1, param2 or param3 fields contains "energy": Filter key: wildcardOr param1 param2 param3. Filter value: energy</li>
     * <li>any of param1, param2 or param3 fields start with "energy": Filter key: likeCriterias param1 param2 param3. Filter value: *energy</li>
     * <li>any of param1, param2 or param3 fields is "energy": Filter key: likeCriterias param1 param2 param3. Filter value: energy</li>
     * </ul>
     * 
     * 
     * @param config PaginationConfiguration data holding object
     * @return query to filter entities according pagination configuration data.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public QueryBuilder getQuery(PaginationConfiguration config) {

        final Class<? extends E> entityClass = getEntityClass();

        Map<String, Object> filters = config.getFilters();

        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", config.getFetchFields());

        if (filters != null && !filters.isEmpty()) {

            if (filters.containsKey(SEARCH_FILTER)) {
                Filter filter = (Filter) filters.get(SEARCH_FILTER);
                Map<CustomFieldTemplate, Object> parameterMap = (Map<CustomFieldTemplate, Object>) filters.get(SEARCH_FILTER_PARAMETERS);
                queryBuilder = new FilteredQueryBuilder(filter, parameterMap, false, false);
            } else {

                for (String key : filters.keySet()) {

                    Object filterValue = filters.get(key);
                    if (filterValue == null) {
                        continue;
                    }

                    // Key format is: condition field1 field2 or condition-field1-field2-fieldN
                    // example: "ne code", condition=code, fieldName=code, fieldName2=null
                    String[] fieldInfo = key.split(" ");
                    String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                    String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1];
                    String fieldName2 = fieldInfo.length == 3 ? fieldInfo[2] : null;

                    String[] fields = null;
                    if (condition != null) {
                        fields = Arrays.copyOfRange(fieldInfo, 1, fieldInfo.length);
                    }

                    // if ranged search - field value in between from - to values. Specifies "from" value: e.g value<=field.value
                    if ("fromRange".equals(condition)) {
                        if (filterValue instanceof Double) {
                            BigDecimal rationalNumber = new BigDecimal((Double) filterValue);
                            queryBuilder.addCriterion("a." + fieldName, " >= ", rationalNumber, true);
                        } else if (filterValue instanceof Number) {
                            queryBuilder.addCriterion("a." + fieldName, " >= ", filterValue, true);
                        } else if (filterValue instanceof Date) {
                            queryBuilder.addCriterionDateRangeFromTruncatedToDay("a." + fieldName, (Date) filterValue);
                        }

                        // if ranged search - field value in between from - to values. Specifies "to" value: e.g field.value<=value
                    } else if ("toRange".equals(condition)) {
                        if (filterValue instanceof Double) {
                            BigDecimal rationalNumber = new BigDecimal((Double) filterValue);
                            queryBuilder.addCriterion("a." + fieldName, " <= ", rationalNumber, true);
                        } else if (filterValue instanceof Number) {
                            queryBuilder.addCriterion("a." + fieldName, " <= ", filterValue, true);
                        } else if (filterValue instanceof Date) {
                            queryBuilder.addCriterionDateRangeToTruncatedToDay("a." + fieldName, (Date) filterValue);
                        }

                        // Value which is a list should be in field value
                    } else if ("listInList".equals(condition)) {
                        this.addListInListCreterion(queryBuilder, filterValue, fieldName);
                        // Value is in field value (list)
                    } else if ("list".equals(condition)) {
                        String paramName = queryBuilder.convertFieldToParam(fieldName);
                        queryBuilder.addSqlCriterion(":" + paramName + " in elements(a." + fieldName + ")", paramName, filterValue);

                        // Field value is in value (list)
                    } else if ("inList".equals(condition) || "not-inList".equals(condition)) {

                        boolean isNot = "not-inList".equals(condition);

                        Field field = ReflectionUtils.getField(entityClass, fieldName);
                        Class<?> fieldClassType = field.getType();

                        // Searching for a list inside a list field requires to join it first as collection member e.g. "IN (a.sellers) seller"
                        if (Collection.class.isAssignableFrom(fieldClassType)) {

                            String paramName = queryBuilder.convertFieldToParam(fieldName);
                            String collectionItem = queryBuilder.convertFieldToCollectionMemberItem(fieldName);

                            // this worked at first, but now complains about distinct clause, so switched to EXISTS clause instead.
                            // queryBuilder.addCollectionMember(fieldName);
                            // queryBuilder.addSqlCriterion(collectionItem + " IN (:" + paramName + ")", paramName, filterValue);

                            String inListAlias = collectionItem + "Alias";
                            queryBuilder.addSqlCriterion(
                                " exists (select " + inListAlias + " from " + entityClass.getName() + " " + inListAlias + ",IN (" + inListAlias + "." + fieldName + ") as "
                                        + collectionItem + " where " + inListAlias + "=a and " + collectionItem + (isNot ? " NOT " : "") + " IN (:" + paramName + "))",
                                paramName, filterValue);

                        } else {
                            if (filterValue instanceof String) {
                                queryBuilder.addSql("a." + fieldName + (isNot ? " NOT " : "") + " IN (" + filterValue + ")");
                            } else if (filterValue instanceof Collection) {
                                String paramName = queryBuilder.convertFieldToParam(fieldName);
                                queryBuilder.addSqlCriterion("a." + fieldName + (isNot ? " NOT " : "") + " IN (:" + paramName + ")", paramName, filterValue);
                            }
                        }

                        // Search by an entity type
                    } else if (SEARCH_ATTR_TYPE_CLASS.equals(fieldName)) {
                        if (filterValue instanceof Collection && !((Collection) filterValue).isEmpty()) {
                            List classes = new ArrayList<Class>();
                            for (Object classNameOrClass : (Collection) filterValue) {
                                if (classNameOrClass instanceof Class) {
                                    classes.add((Class) classNameOrClass);
                                } else {
                                    try {
                                        classes.add(Class.forName((String) classNameOrClass));
                                    } catch (ClassNotFoundException e) {
                                        log.error("Search by a type will be ignored - unknown class {}", (String) classNameOrClass);
                                    }
                                }
                            }

                            if (condition == null) {
                                queryBuilder.addSqlCriterion("type(a) in (:typeClass)", "typeClass", classes);
                            } else if ("ne".equalsIgnoreCase(condition)) {
                                queryBuilder.addSqlCriterion("type(a) not in (:typeClass)", "typeClass", classes);
                            }

                        } else if (filterValue instanceof Class) {
                            if (condition == null) {
                                queryBuilder.addSqlCriterion("type(a) = :typeClass", "typeClass", filterValue);
                            } else if ("ne".equalsIgnoreCase(condition)) {
                                queryBuilder.addSqlCriterion("type(a) != :typeClass", "typeClass", filterValue);
                            }

                        } else if (filterValue instanceof String) {
                            try {
                                if (condition == null) {
                                    queryBuilder.addSqlCriterion("type(a) = :typeClass", "typeClass", Class.forName((String) filterValue));
                                } else if ("ne".equalsIgnoreCase(condition)) {
                                    queryBuilder.addSqlCriterion("type(a) != :typeClass", "typeClass", Class.forName((String) filterValue));
                                }
                            } catch (ClassNotFoundException e) {
                                log.error("Search by a type will be ignored - unknown class {}", filterValue);
                            }
                        }

                        // The value is in between two field values
                    } else if ("minmaxRange".equals(condition)) {
                        if (filterValue instanceof Double) {
                            BigDecimal rationalNumber = new BigDecimal((Double) filterValue);
                            queryBuilder.addCriterion("a." + fieldName, " <= ", rationalNumber, false);
                            queryBuilder.addCriterion("a." + fieldName2, " >= ", rationalNumber, false);
                        } else if (filterValue instanceof Number) {
                            queryBuilder.addCriterion("a." + fieldName, " <= ", filterValue, false);
                            queryBuilder.addCriterion("a." + fieldName2, " >= ", filterValue, false);
                        }
                        if (filterValue instanceof Date) {
                            Date value = (Date) filterValue;
                            Calendar c = Calendar.getInstance();
                            c.setTime(value);
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH);
                            int date = c.get(Calendar.DATE);
                            c.set(year, month, date, 0, 0, 0);
                            value = c.getTime();
                            queryBuilder.addCriterion("a." + fieldName, "<=", value, false);
                            queryBuilder.addCriterion("a." + fieldName2, ">=", value, false);
                        }

                        // The value is in between two field values with either them being optional
                    } else if ("minmaxOptionalRange".equals(condition)) {

                        String paramName = queryBuilder.convertFieldToParam(fieldName);

                        String sql = "((a." + fieldName + " IS NULL and a." + fieldName2 + " IS NULL) or (a." + fieldName + "<=:" + paramName + " and :" + paramName + "<a."
                                + fieldName2 + ") or (a." + fieldName + "<=:" + paramName + " and a." + fieldName2 + " IS NULL) or (a." + fieldName + " IS NULL and :" + paramName
                                + "<a." + fieldName2 + "))";
                        queryBuilder.addSqlCriterionMultiple(sql, paramName, filterValue);

                        // The value range is overlapping two field values with either them being optional
                    } else if ("overlapOptionalRange".equals(condition)) {

                        String paramNameFrom = queryBuilder.convertFieldToParam(fieldName);
                        String paramNameTo = queryBuilder.convertFieldToParam(fieldName2);

                        String sql = "(( a." + fieldName + " IS NULL and a." + fieldName2 + " IS NULL) or  ( a." + fieldName + " IS NULL and a." + fieldName2 + ">:" + paramNameFrom
                                + ") or (a." + fieldName2 + " IS NULL and a." + fieldName + "<:" + paramNameTo + ") or (a." + fieldName + " IS NOT NULL and a." + fieldName2
                                + " IS NOT NULL and ((a." + fieldName + "<=:" + paramNameFrom + " and :" + paramNameFrom + "<a." + fieldName2 + ") or (:" + paramNameFrom + "<=a."
                                + fieldName + " and a." + fieldName + "<:" + paramNameTo + "))))";

                        if (filterValue.getClass().isArray()) {
                            queryBuilder.addSqlCriterionMultiple(sql, paramNameFrom, ((Object[]) filterValue)[0], paramNameTo, ((Object[]) filterValue)[1]);
                        } else if (filterValue instanceof List) {
                            queryBuilder.addSqlCriterionMultiple(sql, paramNameFrom, ((List) filterValue).get(0), paramNameTo, ((List) filterValue).get(1));
                        }

                        // Any of the multiple field values wildcard or not wildcard match the value (OR criteria)
                    } else if ("likeCriterias".equals(condition)) {

                        queryBuilder.startOrClause();
                        if (filterValue instanceof String) {
                            String filterString = (String) filterValue;
                            for (String field : fields) {
                                queryBuilder.addCriterionWildcard("a." + field, filterString, true);
                            }
                        }
                        queryBuilder.endOrClause();

                        // Any of the multiple field values wildcard match the value (OR criteria) - a diference from "likeCriterias" is that wildcard will be appended to the value
                        // automatically
                    } else if (SEARCH_WILDCARD_OR.equals(condition)) {
                        queryBuilder.startOrClause();
                        for (String field : fields) {
                            queryBuilder.addSql("a." + field + " like '%" + filterValue + "%'");
                        }
                        queryBuilder.endOrClause();

                        // Just like wildcardOr but ignoring case :
                    } else if (SEARCH_WILDCARD_OR_IGNORE_CAS.equals(condition)) {
                        queryBuilder.startOrClause();
                        for (String field : fields) { // since SEARCH_WILDCARD_OR_IGNORE_CAS , then filterValue is necessary a String
                            queryBuilder.addSql("lower(a." + field + ") like '%" + String.valueOf(filterValue).toLowerCase() + "%'");
                        }
                        queryBuilder.endOrClause();

                        // Search by additional Sql clause with specified parameters
                    } else if (SEARCH_SQL.equals(key)) {
                        if (filterValue.getClass().isArray()) {
                            String additionalSql = (String) ((Object[]) filterValue)[0];
                            Object[] additionalParameters = Arrays.copyOfRange(((Object[]) filterValue), 1, ((Object[]) filterValue).length);
                            queryBuilder.addSqlCriterionMultiple(additionalSql, additionalParameters);
                        } else {
                            queryBuilder.addSql((String) filterValue);
                        }

                    } else {
                        if (filterValue instanceof String && SEARCH_IS_NULL.equals(filterValue)) {
                            Field field = ReflectionUtils.getField(entityClass, fieldName);
                            Class<?> fieldClassType = field.getType();

                            if (Collection.class.isAssignableFrom(fieldClassType)) {
                                queryBuilder.addSql("a." + fieldName + " is empty ");
                            } else {
                                queryBuilder.addSql("a." + fieldName + " is null ");
                            }

                        } else if (filterValue instanceof String && SEARCH_IS_NOT_NULL.equals(filterValue)) {
                            Field field = ReflectionUtils.getField(entityClass, fieldName);
                            Class<?> fieldClassType = field.getType();

                            if (Collection.class.isAssignableFrom(fieldClassType)) {

                                queryBuilder.addSql("a." + fieldName + " is not empty ");
                            } else {
                                queryBuilder.addSql("a." + fieldName + " is not null ");
                            }

                        } else if (filterValue instanceof String) {

                            // if contains dot, that means join is needed
                            String filterString = (String) filterValue;
                            boolean wildcard = (filterString.indexOf("*") != -1);
                            if (wildcard) {
                                queryBuilder.addCriterionWildcard("a." + fieldName, filterString, true, "ne".equals(condition));
                            } else {
                                queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " != " : " = ", filterString, true);
                            }

                        } else if (filterValue instanceof Date) {
                            queryBuilder.addCriterionDateTruncatedToDay("a." + fieldName, (Date) filterValue);

                        } else if (filterValue instanceof Number) {
                            queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " != " : " = ", filterValue, true);

                        } else if (filterValue instanceof Boolean) {
                            queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " not is" : " is ", filterValue, true);

                        } else if (filterValue instanceof Enum) {
                            if (filterValue instanceof IdentifiableEnum) {
                                String enumIdKey = new StringBuilder(fieldName).append("Id").toString();
                                queryBuilder.addCriterion("a." + enumIdKey, "ne".equals(condition) ? " != " : " = ", ((IdentifiableEnum) filterValue).getId(), true);
                            } else {
                                queryBuilder.addCriterionEnum("a." + fieldName, (Enum) filterValue, "ne".equals(condition) ? " != " : " = ");
                            }

                        } else if (BaseEntity.class.isAssignableFrom(filterValue.getClass())) {
                            queryBuilder.addCriterionEntity("a." + fieldName, filterValue, "ne".equals(condition) ? " != " : " = ");

                        } else if (filterValue instanceof UniqueEntity || filterValue instanceof IEntity) {
                            queryBuilder.addCriterionEntity("a." + fieldName, filterValue, "ne".equals(condition) ? " != " : " = ");

                        } else if (filterValue instanceof List) {
                            queryBuilder.addSqlCriterion("a." + fieldName + ("ne".equals(condition) ? " not in  " : " in ") + ":" + fieldName, fieldName, filterValue);
                        }
                    }
                }
            }
        }

        if (filters != null && filters.containsKey("$FILTER")) {
            Filter filter = (Filter) filters.get("$FILTER");
            queryBuilder.addPaginationConfiguration(config, filter.getPrimarySelector().getAlias());
        } else {
            queryBuilder.addPaginationConfiguration(config, "a");
        }

        // log.trace("Filters is {}", filters);
        // log.trace("Query is {}", queryBuilder.getSqlString());
        // log.trace("Query params are {}", queryBuilder.getParams());
        return queryBuilder;
    }

    /**
     * add a creterion to check if all filterValue (Array) elements are elements of the fieldName (Array)
     * 
     * @param queryBuilder
     * @param filterValue
     * @param fieldName
     */
    private void addListInListCreterion(QueryBuilder queryBuilder, Object filterValue, String fieldName) {
        String paramName = queryBuilder.convertFieldToParam(fieldName);
        if (filterValue.getClass().isArray()) {
            Object[] values = (Object[]) filterValue;
            IntStream.range(0, values.length)
                .forEach(idx -> queryBuilder.addSqlCriterion(":" + paramName + idx + " in elements(a." + fieldName + ")", paramName + idx, values[idx]));
        }
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
     * Flush data to DB. NOTE: unlike the name suggest, no transaction commit is done
     */
    public void commit() {
        getEntityManager().flush();
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
                        List<Long> childIds = getEntityManager().createQuery(String.format("select c.%s from %s c where c.%s.%s = :pid", childClassIdField.getName(),
                            childClass.getSimpleName(), manyToOneField.getName(), idField.getName())).setParameter("pid", parentId).getResultList();
                        for (Long childId : childIds) {
                            getEntityManager().createQuery(String.format("delete from %s c where c.%s = :id", childClass.getSimpleName(), childClassIdField.getName()))
                                .setParameter("id", childId).executeUpdate();
                        }
                    }
                }
            }
            getEntityManager().createQuery(String.format("delete from %s e where e.%s = :id", parentClass.getSimpleName(), idField.getName())).setParameter("id", parentId)
                .executeUpdate();
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
     * @return A concatenated list of entities (humanized classnames and their codes) E.g. Customer Account: first ca, second ca, third ca; Customer: first customer, second
     *         customer
     */
    @SuppressWarnings("rawtypes")
    public String findReferencedByEntities(Class<E> entityClass, Long id) {

        E referencedEntity = getEntityManager().getReference(entityClass, id);

        int totalMatched = 0;
        String matchedEntityInfo = null;
        Map<Class, List<Field>> classesAndFields = ReflectionUtils.getClassesAndFieldsOfType(entityClass);

        for (Entry<Class, List<Field>> classFieldInfo : classesAndFields.entrySet()) {

            boolean isBusinessEntity = BusinessEntity.class.isAssignableFrom(classFieldInfo.getKey());

            String sql = "select " + (isBusinessEntity ? "code" : "id") + " from " + classFieldInfo.getKey().getName() + " where ";
            boolean fieldAddedToSql = false;
            for (Field field : classFieldInfo.getValue()) {
                // For now lets ignore list type fields
                if (field.getType() == entityClass) {
                    sql = sql + (fieldAddedToSql ? " or " : " ") + field.getName() + "=:id";
                    fieldAddedToSql = true;
                }
            }

            if (fieldAddedToSql) {

                List entitiesMatched = getEntityManager().createQuery(sql).setParameter("id", referencedEntity).setMaxResults(10).getResultList();
                if (!entitiesMatched.isEmpty()) {

                    matchedEntityInfo = (matchedEntityInfo == null ? "" : matchedEntityInfo + "; ") + ReflectionUtils.getHumanClassName(classFieldInfo.getKey().getSimpleName())
                            + ": ";
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
}