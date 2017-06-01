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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.EnableEntity;
import org.meveo.model.IAuditable;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.UniqueEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.index.ElasticClient;
import org.meveo.util.MeveoJpa;
import org.meveo.util.MeveoJpaForJobs;

/**
 * Generic implementation that provides the default implementation for persistence methods declared in the {@link IPersistenceService} interface.
 */
public abstract class PersistenceService<E extends IEntity> extends BaseService implements IPersistenceService<E> {
    protected Class<E> entityClass;

    public static String SEARCH_ATTR_TYPE_CLASS = "type_class";
    public static String SEARCH_IS_NULL = "IS_NULL";
    public static String SEARCH_IS_NOT_NULL = "IS_NOT_NULL";
    public static String SEARCH_FIELD1_OR_FIELD2 = "FIELD1_OR_FIELD2";

    @Inject
    @MeveoJpa
    private EntityManager em;

    @Inject
    @MeveoJpaForJobs
    private EntityManager emfForJobs;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    private Conversation conversation;
//
//    @Resource
//    protected TransactionSynchronizationRegistry txReg;

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

    @EJB
    private CatMessagesService catMessagesService;

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

    public E updateNoCheck(E entity) {
        log.debug("start of update {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());

        updateAudit(entity);
        E mergedEntity = getEntityManager().merge(entity);

        return mergedEntity;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long)
     */
    @Override
    public E findById(Long id) {
        return findById(id, false);
    }

    /**
     * Use by API.
     */
    @Override
    public E findById(Long id, boolean refresh) {
        log.debug("start of find {} by id (id={}) ..", getEntityClass().getSimpleName(), id);
        final Class<? extends E> productClass = getEntityClass();
        E e = getEntityManager().find(productClass, id);
        if (e != null) {
            if (refresh) {
                log.debug("refreshing loaded entity");
                getEntityManager().refresh(e);
            }
        }
        log.trace("end of find {} by id (id={}). Result found={}.", getEntityClass().getSimpleName(), id, e != null);
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
        log.debug("start of find {} by id (id={}) ..", getEntityClass().getSimpleName(), id);
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
        log.trace("end of find {} by id (id={}). Result found={}.", getEntityClass().getSimpleName(), id, e != null);
        return e;
    }


    /**
     * @see org.meveo.service.base.local.IPersistenceService#disable(java.lang.Long, org.meveo.model.admin.User)
     */
    @Override
    public void disable(Long id) throws BusinessException {
        E e = findById(id);
        if (e != null) {
            disable(e);
        }
    }

    @Override
    public E disable(E entity) throws BusinessException {
        if (entity instanceof EnableEntity && ((EnableEntity) entity).isActive()) {
            log.debug("start of disable {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
            ((EnableEntity) entity).setDisabled(true);
            if (entity instanceof IAuditable) {
                ((IAuditable) entity).updateAudit(currentUser);
            }
            entity = getEntityManager().merge(entity);
            if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
                entityDisabledEventProducer.fire((BaseEntity)entity);
            }
            log.trace("end of disable {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());
        }
        return entity;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#enable(java.lang.Long, org.meveo.model.admin.User)
     */
    @Override
    public void enable(Long id) throws BusinessException {
        E e = findById(id);
        if (e != null) {
            enable(e);
        }
    }

    @Override
    public E enable(E entity) throws BusinessException {
        if (entity instanceof EnableEntity && ((EnableEntity) entity).isDisabled()) {
            log.debug("start of enable {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
            ((EnableEntity) entity).setDisabled(false);
            if (entity instanceof IAuditable) {
                ((IAuditable) entity).updateAudit(currentUser);
            }
            entity = getEntityManager().merge(entity);
            if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
                entityEnabledEventProducer.fire((BaseEntity)entity);
            }
            log.trace("end of enable {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());
        }
        return entity;
    }

    @Override
    public void remove(E entity) throws BusinessException {
        log.debug("start of remove {} entity (id={}) ..", getEntityClass().getSimpleName(), entity.getId());
        getEntityManager().remove(entity);
        if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
            entityRemovedEventProducer.fire((BaseEntity)entity);
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

        // Remove description translations
        if (entity instanceof BusinessEntity && entity.getClass().isAnnotationPresent(MultilanguageEntity.class)) {
            catMessagesService.batchRemove((BusinessEntity) entity);
        }

        log.trace("end of remove {} entity (id={}).", getEntityClass().getSimpleName(), entity.getId());
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
     * @see org.meveo.service.base.local.IPersistenceService#update(org.manaty.model.BaseEntity)
     */
    @Override
    public E update(E entity) throws BusinessException {
        log.debug("start of update {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());

        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }

        entity = getEntityManager().merge(entity);

        log.trace("updated class {}, is BusinessEntity {}", entity.getClass(), BusinessEntity.class.isAssignableFrom(entity.getClass()));

        // Update entity in Elastic Search. ICustomFieldEntity is updated
        // partially, as entity itself does not have Custom field values
        if (entity instanceof BusinessCFEntity) {
            elasticClient.partialUpdate((BusinessEntity) entity);

        } else if (entity instanceof BusinessEntity) {
            elasticClient.createOrFullUpdate((BusinessEntity) entity);
        }

        if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
            entityUpdatedEventProducer.fire((BaseEntity)entity);
        }

        log.trace("end of update {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());

        return entity;
    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#create(org.manaty.model.BaseEntity)
     */
    @Override
    public void create(E entity) throws BusinessException {
        log.debug("start of create {} entity={}", entity.getClass().getSimpleName(), entity);

        if (entity instanceof IAuditable) {
            ((IAuditable) entity).updateAudit(currentUser);
        }

        getEntityManager().persist(entity);

        // Add entity to Elastic Search
        if (BusinessEntity.class.isAssignableFrom(entity.getClass())) {
            elasticClient.createOrFullUpdate((BusinessEntity) entity);
        }

        if (entity instanceof BaseEntity && entity.getClass().isAnnotationPresent(ObservableEntity.class)) {
            entityCreatedEventProducer.fire((BaseEntity)entity);
        }

        log.trace("end of create {}. entity id={}.", entity.getClass().getSimpleName(), entity.getId());

    }

    /**
     * @see org.meveo.service.base.local.IPersistenceService#list()
     */
    @Override
    public List<E> list() {
        return list((Boolean) null);
    }

    @Override
    public List<E> listActive() {
        return list(true);
    }

    @SuppressWarnings("unchecked")
    public List<E> list(Boolean active) {
        final Class<? extends E> entityClass = getEntityClass();
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        if (active != null && EnableEntity.class.isAssignableFrom(entityClass)) {
            queryBuilder.addBooleanCriterion("disabled", !active);
        }
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

    /**
     * Find entities by code - wild match
     * 
     * @param code Code to match
     * @return A list of entities matching code
     */
    @SuppressWarnings("unchecked")
    public List<E> findByCodeLike(String wildcode) {
        final Class<? extends E> entityClass = getEntityClass();
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        if (EnableEntity.class.isAssignableFrom(entityClass)) {
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
     * @see org.meveo.service.base.local.IPersistenceService#refresh(org.meveo.model.BaseEntity)
     */
    @Override
    public void refresh(E entity) {
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
     * @see org.meveo.service.base.local.IPersistenceService#refreshOrRetrieve(org.meveo.model.BaseEntity)
     */
    @Override
    public E refreshOrRetrieve(E entity) {

        if (getEntityManager().contains(entity)) {
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
     * Creates query to filter entities according data provided in pagination configuration.
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

            if (filters.containsKey("$FILTER")) {
                Filter filter = (Filter) filters.get("$FILTER");
                Map<CustomFieldTemplate, Object> parameterMap = (Map<CustomFieldTemplate, Object>) filters.get("$FILTER_PARAMETERS");
                queryBuilder = new FilteredQueryBuilder(filter, parameterMap, false, false);
            } else {

                for (String key : filters.keySet()) {
                    
                    // condition field1 field2
                    // example1 ne code, condition=code, fieldName=code,
                    // fieldName2=null
                    String[] fieldInfo = key.split(" ");
                    String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                    String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1];
                    String fieldName2 = fieldInfo.length == 3 ? fieldInfo[2] : null;

                    Object filter = filters.get(key);
                    if (filter != null) {
                        // if ranged search (from - to fields)
                        if (key.contains("fromRange-")) {
                            String parsedKey = key.substring(10);
                            if (filter instanceof Double) {
                                BigDecimal rationalNumber = new BigDecimal((Double) filter);
                                queryBuilder.addCriterion("a." + parsedKey, " >= ", rationalNumber, true);
                            } else if (filter instanceof Number) {
                                queryBuilder.addCriterion("a." + parsedKey, " >= ", filter, true);
                            } else if (filter instanceof Date) {
                                queryBuilder.addCriterionDateRangeFromTruncatedToDay("a." + parsedKey, (Date) filter);
                            }
                        } else if (key.contains("toRange-")) {
                            String parsedKey = key.substring(8);
                            if (filter instanceof Double) {
                                BigDecimal rationalNumber = new BigDecimal((Double) filter);
                                queryBuilder.addCriterion("a." + parsedKey, " <= ", rationalNumber, true);
                            } else if (filter instanceof Number) {
                                queryBuilder.addCriterion("a." + parsedKey, " <= ", filter, true);
                            } else if (filter instanceof Date) {
                                queryBuilder.addCriterionDateRangeToTruncatedToDay("a." + parsedKey, (Date) filter);
                            }
                        } else if (key.contains("list-")) {
                            // if searching elements from list
                            String parsedKey = key.substring(5);
                            queryBuilder.addSqlCriterion(":" + parsedKey + " in elements(a." + parsedKey + ")", parsedKey, filter);
                        } else if (key.contains("inList-")) {
                            // if searching elements from list
                            String parsedKey = key.substring(7);
                            queryBuilder.addSql("a." + parsedKey + " in (" + filter + ")");

                            // Search by an entity type
                        } else if (SEARCH_ATTR_TYPE_CLASS.equals(fieldName)) {
                            if (filter instanceof Collection) {
                                List classes = new ArrayList<Class>();
                                for (String className : (Collection<String>) filter) {
                                    try {
                                        classes.add(Class.forName(className));
                                    } catch (ClassNotFoundException e) {
                                        log.error("Search by a type will be ignored - unknown class {}", className);
                                    }
                                }

                                if (condition == null) {
                                    queryBuilder.addSqlCriterion("type(a) in (:typeClass)", "typeClass", classes);
                                } else if ("ne".equalsIgnoreCase(condition)) {
                                    queryBuilder.addSqlCriterion("type(a) not in (:typeClass)", "typeClass", classes);
                                }

                            } else if (filter instanceof Class) {
                                if (condition == null) {
                                    queryBuilder.addSqlCriterion("type(a) = :typeClass", "typeClass", filter);
                                } else if ("ne".equalsIgnoreCase(condition)) {
                                    queryBuilder.addSqlCriterion("type(a) != :typeClass", "typeClass", filter);
                                }

                            } else if (filter instanceof String) {
                                try {
                                    if (condition == null) {
                                        queryBuilder.addSqlCriterion("type(a) = :typeClass", "typeClass", Class.forName((String) filter));
                                    } else if ("ne".equalsIgnoreCase(condition)) {
                                        queryBuilder.addSqlCriterion("type(a) != :typeClass", "typeClass", Class.forName((String) filter));
                                    }
                                } catch (ClassNotFoundException e) {
                                    log.error("Search by a type will be ignored - unknown class {}", filter);
                                }
                            }
                        } else if (key.contains("minmaxRange-")) {
                            // if searching elements from list
                            String parsedKey = key.substring(12);
                            String[] kss = parsedKey.split("-");
                            if (kss.length == 2) {
                                if (filter instanceof Double) {
                                    BigDecimal rationalNumber = new BigDecimal((Double) filter);
                                    queryBuilder.addCriterion("a." + kss[0], " <= ", rationalNumber, false);
                                    queryBuilder.addCriterion("a." + kss[1], " >= ", rationalNumber, false);
                                } else if (filter instanceof Number) {
                                    queryBuilder.addCriterion("a." + kss[0], " <= ", filter, false);
                                    queryBuilder.addCriterion("a." + kss[1], " >= ", filter, false);
                                }
                                if (filter instanceof Date) {
                                    Date value = (Date) filter;
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(value);
                                    int year = c.get(Calendar.YEAR);
                                    int month = c.get(Calendar.MONTH);
                                    int date = c.get(Calendar.DATE);
                                    c.set(year, month, date, 0, 0, 0);
                                    value = c.getTime();
                                    queryBuilder.addCriterion("a." + kss[0], "<=", value, false);
                                    queryBuilder.addCriterion("a." + kss[1], ">=", value, false);
                                }
                            }
                        } else if (key.contains("likeCriterias-")) {
                            // if searching elements from list
                            String parsedKey = key.substring(14);
                            String[] fields = parsedKey.split("-");
                            queryBuilder.startOrClause();
                            for (String field : fields) {
                                if (filter instanceof String) {
                                    String filterString = (String) filter;
                                    queryBuilder.addCriterionWildcard("a." + field, filterString, true);
                                }
                            }
                            queryBuilder.endOrClause();

                            // if not ranged search
                        } else if (key.contains(SEARCH_FIELD1_OR_FIELD2)) {
                            queryBuilder.startOrClause();
                            queryBuilder.addSql("a." + fieldName + " like '%" + filter + "%'");
                            queryBuilder.addSql("a." + fieldName2 + " like '%" + filter + "%'");
                            queryBuilder.endOrClause();
                        } else {
                            if (filter instanceof String && SEARCH_IS_NULL.equals(filter)) {
                                queryBuilder.addSql("a." + fieldName + " is null ");

                            } else if (filter instanceof String && SEARCH_IS_NOT_NULL.equals(filter)) {
                                queryBuilder.addSql("a." + fieldName + " is not null ");

                            } else if (filter instanceof String) {

                                // if contains dot, that means join is needed
                                String filterString = (String) filter;
                                boolean wildcard = (filterString.indexOf("*") != -1);
                                if (wildcard) {
                                    queryBuilder.addCriterionWildcard("a." + fieldName, filterString, true, "ne".equals(condition));
                                } else {
                                    queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " != " : " = ", filterString, true);
                                }

                            } else if (filter instanceof Date) {
                                queryBuilder.addCriterionDateTruncatedToDay("a." + fieldName, (Date) filter);

                            } else if (filter instanceof Number) {
                                queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " != " : " = ", filter, true);

                            } else if (filter instanceof Boolean) {
                                queryBuilder.addCriterion("a." + fieldName, "ne".equals(condition) ? " not is" : " is ", filter, true);

                            } else if (filter instanceof Enum) {
                                if (filter instanceof IdentifiableEnum) {
                                    String enumIdKey = new StringBuilder(fieldName).append("Id").toString();
                                    queryBuilder.addCriterion("a." + enumIdKey, "ne".equals(condition) ? " != " : " = ", ((IdentifiableEnum) filter).getId(), true);
                                } else {
                                    queryBuilder.addCriterionEnum("a." + fieldName, (Enum) filter, "ne".equals(condition) ? " != " : " = ");
                                }

                            } else if (BaseEntity.class.isAssignableFrom(filter.getClass())) {
                                queryBuilder.addCriterionEntity("a." + fieldName, filter, "ne".equals(condition) ? " != " : " = ");

                            } else if (filter instanceof UniqueEntity || filter instanceof IEntity) {
                                queryBuilder.addCriterionEntity("a." + fieldName, filter, "ne".equals(condition) ? " != " : " = ");

                            } else if (filter instanceof List) {
                                queryBuilder.addSqlCriterion("a." + fieldName + ("ne".equals(condition) ? " not in  " : " in ") + ":" + fieldName, fieldName, filter);
                            }
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
        return queryBuilder;
    }

    public E attach(E entity) {
        return (E) getEntityManager().merge(entity);
    }

    protected boolean isConversationScoped() {
        if (conversation != null) {
            try {
                conversation.isTransient();
                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    public EntityManager getEntityManager() {
        EntityManager result = emfForJobs;
        if (conversation != null) {
            try {
                conversation.isTransient();
                result = em;
            } catch (Exception e) {
            }
        }

        // log.debug("em.txKey={}, em.hashCode={}", txReg.getTransactionKey(),
        // em.hashCode());
        return result;
    }

    public void updateAudit(E e) {

        if (e instanceof IAuditable) {
            ((IAuditable) e).updateAudit(currentUser);
        }
    }

    public void commit() {
        getEntityManager().flush();
    }
}