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
package org.meveo.admin.action;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.Identity;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.action.admin.CurrentUser;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IAuditable;
import org.meveo.model.IEntity;
import org.meveo.model.IProvider;
import org.meveo.model.ModuleItem;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.filter.Filter;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.index.ElasticClient;
import org.meveo.util.view.ESBasedDataModel;
import org.meveo.util.view.PagePermission;
import org.meveo.util.view.ServiceBasedLazyDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lapis.jsfexporter.csv.CSVExportOptions;

/**
 * Base bean class. Other seam backing beans extends this class if they need functionality it provides.
 */
public abstract class BaseBean<T extends IEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Logger. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    protected Messages messages;

    @Inject
    protected Identity identity;

    @Inject
    @CurrentProvider
    protected Provider currentProvider;

    @Inject
    @CurrentUser
    protected User currentUser;

    @Inject
    protected Conversation conversation;

    @Inject
    protected PermissionService permissionService;

    @Inject
    private CatMessagesService catMessagesService;

    @Inject
    private FilterService filterService;

    @Inject
    private ProviderService providerService;

    @Inject
    private FilterCustomFieldSearchBean filterCustomFieldSearchBean;

    @Inject
    private ElasticClient elasticClient;

    /** Search filters. */
    protected Map<String, Object> filters = new HashMap<String, Object>();

    /** Entity to edit/view. */
    protected T entity;

    /** Class of backing bean. */
    private Class<T> clazz;

    /**
     * Request parameter. Should form be displayed in create/edit or view mode
     */
    @Inject
    @RequestParam()
    private Instance<String> edit;

    // private boolean editSaved;

    protected int dataTableFirstAttribute;

    @Inject
    private MeveoModuleService meveoModuleService;
    private String partOfModules;

    /**
     * Request parameter. A custom back view page instead of a regular list page
     */
    @Inject
    @RequestParam()
    private Instance<String> backView;

    private String backViewSave;

    /**
     * Object identifier to load
     */
    private Long objectId;

    /** Helper field to enter language related field values. */
    protected Map<String, String> languageMessagesMap = new HashMap<String, String>();

    /**
     * Datamodel for lazy dataloading in datatable.
     */
    protected LazyDataModel<T> dataModel;

    /**
     * Bind datatable for search results.
     */
    private DataTable dataTable;

    /**
     * Selected Entities in multiselect datatable.
     */
    private List<T> selectedEntities;

    private Filter listFilter;

    protected boolean listFiltered = false;

    /**
     * Tracks active tabs in GUI
     */
    private int activeTab;

    private int activeMainTab = 0;

    private Map<String, Boolean> writeAccessMap;

    /**
     * Constructor
     */
    public BaseBean() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param clazz Class.
     */
    public BaseBean(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    /**
     * Returns entity class
     * 
     * @return Class
     */
    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    protected void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public void preRenderView() {
        beginConversation();
    }

    /**
     * Initiates entity from request parameter id.
     * 
     * @param objectClass Class of the object.
     * @return Entity from database.
     */
    public T initEntity() {
        log.debug("instantiating {} with id {}", this.getClass(), getObjectId());
        if (getObjectId() != null) {

            List<String> formFieldsToFetch = getFormFieldsToFetch();

            if (formFieldsToFetch == null) {
                entity = (T) getPersistenceService().findById(getObjectId());
            } else {
                entity = (T) getPersistenceService().findById(getObjectId(), formFieldsToFetch);
            }

            loadMultiLanguageFields();
            loadPartOfModules();

            // getPersistenceService().detach(entity);
        } else {
            try {
                entity = getInstance();
                if (entity instanceof IProvider) {
                    ((IProvider) entity).setProvider(providerService.refreshOrRetrieve(currentProvider));
                }
                // FIXME: If entity is Auditable, set here the creator and
                // creation time
            } catch (InstantiationException e) {
                log.error("Unexpected error!", e);
                throw new IllegalStateException("could not instantiate a class, abstract class");
            } catch (IllegalAccessException e) {
                log.error("Unexpected error!", e);
                throw new IllegalStateException("could not instantiate a class, constructor not accessible");
            }
        }

        return entity;
    }

    public T initEntity(Long id) {
        entity = null;
        setObjectId(id);
        return initEntity();
    }

    /**
     * Clear object parameters and instantiate a new entity
     * 
     * @return Entity instantiated
     */
    public T newEntity() {
        log.debug("instantiating {} with id {}", this.getClass(), getObjectId());
        entity = null;
        setObjectId(null);
        return initEntity();
    }

    /**
     * Load multi-language fields if applicable for a class (class contains annotation MultilanguageEntity)
     * 
     * @param entity Entity lo load fields for
     */
    private void loadMultiLanguageFields() {

        if (!isMultilanguageEntity() || !(entity instanceof BusinessEntity)) {
            return;
        }

        languageMessagesMap.clear();
        BusinessEntity businessEntity = (BusinessEntity) entity;

        for (CatMessages msg : catMessagesService.getCatMessagesList(catMessagesService.getEntityClass(clazz), businessEntity.getCode(), currentProvider)) {
            languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
        }
    }

    private boolean isPartOfModules() {
        return clazz.isAnnotationPresent(ModuleItem.class);
    }

    private void loadPartOfModules() {
        if ((entity instanceof BusinessEntity) && isPartOfModules()) {
            BusinessEntity businessEntity = (BusinessEntity) entity;
            String appliesTo = null;
            if (ReflectionUtils.hasField(entity, "appliesTo")) {
                try {
                    appliesTo = (String) FieldUtils.readField(entity, "appliesTo", true);
                } catch (IllegalAccessException e) {
                    log.error("Failed to access 'appliesTo' field value", e);
                }
            }

            partOfModules = meveoModuleService.getRelatedModulesAsString(businessEntity.getCode(), clazz.getName(), appliesTo, businessEntity.getProvider());
        }
    }

    /**
     * When opened to view or edit entity - this getter method returns it. In case entity is not loaded it will initialize it.
     * 
     * @return Entity in current view state.
     */
    public T getEntity() {
        return entity != null ? entity : initEntity();
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    // /**
    // * Refresh entities data model and removes search filters.
    // */
    // public void clean() {
    // if (entities == null) {
    // entities = new PaginationDataModel<T>(getPersistenceService());
    // }
    // filters.clear();
    // filters.put("provider", getCurrentProvider());
    // entities.addFilters(filters);
    // entities.addFetchFields(getListFieldsToFetch());
    // entities.forceRefresh();
    // }
    @ActionMethod
    public String saveOrUpdate(boolean killConversation, String objectName, Long objectId) throws BusinessException {
        String outcome = saveOrUpdate(killConversation);

        if (killConversation) {
            endConversation();
        }

        // return objectId == null ? outcome : (outcome + "&" + objectName + "="
        // + objectId + "&cid=" + conversation.getId());
        return outcome;
    }

    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        String message = entity.isTransient() ? "save.successful" : "update.successful";

        // Save description translations
        if (!isMultilanguageEntity()) {
            entity = saveOrUpdate(entity);

        } else if (entity instanceof BusinessEntity) {
            if (entity.getId() != null) {

                for (String languageKey : languageMessagesMap.keySet()) {
                    String description = languageMessagesMap.get(languageKey);
                    CatMessages catMsg = catMessagesService.getCatMessages((BusinessEntity) entity, languageKey);
                    if (catMsg != null) {
                        catMsg.setDescription(description);
                        catMessagesService.update(catMsg, getCurrentUser());
                    } else {
                        CatMessages catMessages = new CatMessages((BusinessEntity) entity, languageKey, description);
                        catMessagesService.create(catMessages, getCurrentUser());
                    }
                }

                entity = saveOrUpdate(entity);

            } else {
                entity = saveOrUpdate(entity);

                for (String msgKey : languageMessagesMap.keySet()) {
                    String description = languageMessagesMap.get(msgKey);
                    CatMessages catMessages = new CatMessages((BusinessEntity) entity, msgKey, description);
                    catMessagesService.create(catMessages, getCurrentUser());
                }
            }
        }

        if (killConversation) {
            endConversation();
        }

        messages.info(new BundleKey("messages", message));
        return back();
    }

    @ActionMethod
    public String saveOrUpdateWithMessage(boolean killConversation) throws BusinessException {
        boolean result = true;
        try {
            return this.saveOrUpdate(killConversation);
        } catch (Exception e) {
            result = false;
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("result", result);
        return null;
    }

    /**
     * Save method when used in popup - no return value. Sets validation to failed if saveOrUpdate method called does not return a value.
     * 
     * @return
     * @throws BusinessException
     */
    @ActionMethod
    public void saveOrUpdateForPopup() throws BusinessException {
        String result = saveOrUpdate(false);
        if (result == null) {
            FacesContext.getCurrentInstance().validationFailed();
        }
        return;
    }

    /**
     * Save or update entity depending on if entity is transient.
     * 
     * @param entity Entity to save.
     * @throws BusinessException
     */
    protected T saveOrUpdate(T entity) throws BusinessException {
        if (entity.isTransient()) {
            getPersistenceService().create(entity, getCurrentUser());

        } else {
            entity = getPersistenceService().update(entity, getCurrentUser());
        }

        objectId = (Long) entity.getId();

        return entity;
    }

    /**
     * Lists all entities, sorted by description if bean is related to BusinessEntity type
     */
    public List<T> listAll() {
        if (clazz != null && BusinessEntity.class.isAssignableFrom(clazz)) {
            return getPersistenceService().list(new PaginationConfiguration("description", SortOrder.ASCENDING));
        } else {
            return getPersistenceService().list();
        }
    }

    /**
     * Returns view after save() operation. By default it goes back to list view. Override if need different logic (for example return to one view for save and another for update
     * operations)
     */
    public String getViewAfterSave() {
        return getListViewName();
    }

    /**
     * Method to get Back link. If default view name is different than override the method. Default name: entity's name + s;
     * 
     * @return string for navigation
     */
    public String back() {
        if (backViewSave == null && backView != null && backView.get() != null) {
            // log.debug("backview parameter is " + backView.get());
            backViewSave = backView.get();
        } else if (backViewSave == null) {
            return getListViewName();
        }
        return backViewSave;
    }

    /**
     * Go back and end conversation. BeforeRedirect flag is set to true, so conversation is first ended and then redirect is proceeded, that means that after redirect new
     * conversation will have to be created (temp or long running) so that view will have all most up to date info because it will load everything from db when starting new
     * conversation.
     * 
     * @return string for navigation
     */
    // TODO: @End(beforeRedirect = true, root = false)
    public String backAndEndConversation() {
        String outcome = back();
        endConversation();
        return outcome;
    }

    /**
     * Generating action name to get to entity creation page. Override this method if its view name does not fit.
     */
    public String getNewViewName() {
        return getEditViewName();
    }

    /**
     * Get navigation view link name for a current entity class
     */
    public String getEditViewName() {
        return BaseBean.getEditViewName(clazz);
    }

    /**
     * Convert entity class to a detail view name
     * 
     * @param clazz Entity class
     * @return Navigation view link name
     */
    @SuppressWarnings("rawtypes")
    public static String getEditViewName(Class clazz) {
        String className = ReflectionUtils.getCleanClassName(clazz.getSimpleName());
        StringBuilder sb = new StringBuilder(className);
        sb.append("Detail");
        char[] dst = new char[1];
        sb.getChars(0, 1, dst, 0);
        sb.replace(0, 1, new String(dst).toLowerCase());
        return sb.toString();
    }

    /**
     * Generating back link.
     */
    protected String getListViewName() {
        String className = clazz.getSimpleName();
        StringBuilder sb = new StringBuilder(className);
        char[] dst = new char[1];
        sb.getChars(0, 1, dst, 0);
        sb.replace(0, 1, new String(dst).toLowerCase());
        sb.append("s");
        return sb.toString();
    }

    public String getIdParameterName() {
        String className = clazz.getSimpleName();
        StringBuilder sb = new StringBuilder(className);
        sb.append("Id");
        char[] dst = new char[1];
        sb.getChars(0, 1, dst, 0);
        sb.replace(0, 1, new String(dst).toLowerCase());
        return sb.toString();
    }

    /**
     * Delete Entity using it's ID. Add error message to {@link statusMessages} if unsuccessful.
     * 
     * @param id Entity id to delete
     */
    @ActionMethod
    public void delete(Long id) {
        try {
            log.info("Deleting entity {} with id = {}", clazz.getName(), id);
            getPersistenceService().remove(id, getCurrentUser());
            messages.info(new BundleKey("messages", "delete.successful"));
        } catch (Throwable t) {
            if (t.getCause() instanceof EntityExistsException) {
                log.info("delete was unsuccessful because entity is used in the system", t);
                messages.error(new BundleKey("messages", "error.delete.entityUsed"));

            } else {
                log.info("unexpected exception when deleting!", t);
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }
        }

        // initEntity();
    }

    @ActionMethod
    public void delete() {
        try {
            log.info("Deleting entity {} with id = {}", clazz.getName(), getEntity().getId());
            getPersistenceService().remove((Long) getEntity().getId(), getCurrentUser());
            messages.info(new BundleKey("messages", "delete.successful"));
        } catch (Throwable t) {
            if (t.getCause() instanceof EntityExistsException) {
                log.info("delete was unsuccessful because entity is used in the system", t);
                messages.error(new BundleKey("messages", "error.delete.entityUsed"));

            } else {
                log.info("unexpected exception when deleting!", t);
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }
        }

        // initEntity();
    }

    /**
     * Delete checked entities. Add error message to {@link statusMessages} if unsuccessful.
     */
    public void deleteMany() {
        try {
            if (selectedEntities != null && selectedEntities.size() > 0) {
                Set<Long> idsToDelete = new HashSet<Long>();
                StringBuilder idsString = new StringBuilder();
                for (IEntity entity : selectedEntities) {
                    idsToDelete.add((Long) entity.getId());
                    idsString.append(entity.getId()).append(" ");
                }
                log.info("Deleting multiple entities {} with ids = {}", clazz.getName(), idsString.toString());

                getPersistenceService().remove(idsToDelete, getCurrentUser());
                getPersistenceService().commit();
                messages.info(new BundleKey("messages", "delete.entitities.successful"));
            } else {
                messages.info(new BundleKey("messages", "delete.entitities.noSelection"));
            }
        } catch (Throwable t) {
            if (t.getCause() instanceof EntityExistsException) {
                log.info("delete was unsuccessful because entity is used in the system", t);
                messages.error(new BundleKey("messages", "error.delete.entityUsed"));

            } else {
                log.info("unexpected exception when deleting!", t);
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }
        }

        // initEntity();
    }

    /**
     * Gets search filters map.
     * 
     * @return Filters map.
     */
    public Map<String, Object> getFilters() {
        if (filters == null) {
            filters = new HashMap<String, Object>();
        }
        return filters;
    }

    /**
     * Clean search fields in datatable.
     */
    public void clean() {
        dataModel = null;
        filters = new HashMap<String, Object>();
        listFilter = null;
    }

    /**
     * Reset values to the last state.
     */
    public void resetFormEntity() {
        entity = null;
        entity = getEntity();
    }

    /**
     * Get new instance for backing bean class.
     * 
     * @return New instance.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public T getInstance() throws InstantiationException, IllegalAccessException {

        T newInstance = clazz.newInstance();

        // A workaround for #1300 fix. Set auditable property if applicable, so current user would be available for EL expressions.
        if (newInstance instanceof IAuditable) {
            ((IAuditable) newInstance).updateAudit(getCurrentUser());
        }
        return newInstance;
    }

    /**
     * Method that returns concrete PersistenceService. That service is then used for operations on concrete entities (eg. save, delete etc).
     * 
     * @return Persistence service
     */
    protected abstract IPersistenceService<T> getPersistenceService();

    /**
     * Override this method if you need to fetch any fields when selecting list of entities in data table. Return list of field names that has to be fetched.
     */
    protected List<String> getListFieldsToFetch() {
        return null;
    }

    /**
     * Override this method if you need to fetch any fields when selecting one entity to show it a form. Return list of field names that has to be fetched.
     */
    protected List<String> getFormFieldsToFetch() {
        return null;
    }

    /**
     * Override this method when pop up with additional entity information is needed.
     */
    protected String getPopupInfo() {
        return "No popup information. Override BaseBean.getPopupInfo() method.";
    }

    /**
     * Disable current entity. Add error message to {@link statusMessages} if unsuccessful.
     * 
     * @param id Entity id to disable
     */
    @ActionMethod
    public void disable() {
        try {
            log.info("Disabling entity {} with id = {}", clazz.getName(), entity.getId());
            entity = getPersistenceService().disable(entity, getCurrentUser());
            messages.info(new BundleKey("messages", "disabled.successful"));

        } catch (Exception t) {
            log.info("unexpected exception when disabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Disable Entity using it's ID. Add error message to {@link statusMessages} if unsuccessful.
     * 
     * @param id Entity id to disable
     */
    @ActionMethod
    public void disable(Long id) {
        try {
            log.info("Disabling entity {} with id = {}", clazz.getName(), id);
            getPersistenceService().disable(id, getCurrentUser());
            messages.info(new BundleKey("messages", "disabled.successful"));

        } catch (Throwable t) {
            log.info("unexpected exception when disabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Enable current entity. Add error message to {@link statusMessages} if unsuccessful.
     * 
     * @param id Entity id to enable
     */
    @ActionMethod
    public void enable() {
        try {
            log.info("Enabling entity {} with id = {}", clazz.getName(), entity.getId());
            entity = getPersistenceService().enable(entity, getCurrentUser());
            messages.info(new BundleKey("messages", "enabled.successful"));

        } catch (Exception t) {
            log.info("unexpected exception when enabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Enable Entity using it's ID. Add error message to {@link statusMessages} if unsuccessful.
     * 
     * @param id Entity id to enable
     */
    @ActionMethod
    public void enable(Long id) {
        try {
            log.info("Enabling entity {} with id = {}", clazz.getName(), id);
            getPersistenceService().enable(id, getCurrentUser());
            messages.info(new BundleKey("messages", "enabled.successful"));

        } catch (Throwable t) {
            log.info("unexpected exception when enabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * DataModel for primefaces lazy loading datatable component.
     * 
     * @return LazyDataModel implementation.
     */
    public LazyDataModel<T> getLazyDataModel() {
        return getLazyDataModel(filters, listFiltered);
    }

    public LazyDataModel<T> getLazyDataModel(Map<String, Object> inputFilters, boolean forceReload) {
        if (dataModel == null || forceReload) {

            final Map<String, Object> filters = inputFilters;

            dataModel = new ServiceBasedLazyDataModel<T>() {

                private static final long serialVersionUID = 1736191234466041033L;

                @Override
                protected IPersistenceService<T> getPersistenceServiceImpl() {
                    return getPersistenceService();
                }

                @Override
                protected Map<String, Object> getSearchCriteria() {

                    // Omit empty or null values
                    Map<String, Object> cleanFilters = new HashMap<String, Object>();

                    for (Map.Entry<String, Object> filterEntry : filters.entrySet()) {
                        if (filterEntry.getValue() == null) {
                            continue;
                        }
                        if (filterEntry.getValue() instanceof String) {
                            if (StringUtils.isBlank((String) filterEntry.getValue())) {
                                continue;
                            }
                        }
                        cleanFilters.put(filterEntry.getKey(), filterEntry.getValue());
                    }

                    // cleanFilters.put(PersistenceService.SEARCH_CURRENT_USER, getCurrentUser());
                    cleanFilters.put(PersistenceService.SEARCH_CURRENT_PROVIDER, getCurrentProvider());
                    return BaseBean.this.supplementSearchCriteria(cleanFilters);
                }

                @Override
                protected String getDefaultSortImpl() {
                    return getDefaultSort();
                }

                @Override
                protected SortOrder getDefaultSortOrderImpl() {
                    return getDefaultSortOrder();
                }

                @Override
                protected List<String> getListFieldsToFetchImpl() {
                    return getListFieldsToFetch();
                }

                @Override
                protected ElasticClient getElasticClientImpl() {
                    return elasticClient;
                }

                @Override
                public User getCurrentUser() {
                    return BaseBean.this.getCurrentUser();
                }

                @Override
                protected String getFullTextSearchValue(Map<String, Object> loadingFilters) {
                    String fullTextValue = super.getFullTextSearchValue(loadingFilters);
                    if (fullTextValue == null) {
                        return (String) filters.get(ESBasedDataModel.FILTER_FULL_TEXT);
                    }
                    return fullTextValue;
                }
            };
        }

        listFiltered = false;

        return dataModel;
    }

    /**
     * Allows to overwrite, or add additional search criteria for filtering a list. Search criteria is a map with filter criteria name as a key and value as a value. <br/>
     * Criteria name consist of [<condition> ]<field name> (e.g. "like firstName") where <condition> is a condition to apply to field value comparison and <field name> is an entit
     * attribute name.
     * 
     * @param searchCriteria Search criteria - should be same as filters attribute
     * @return HashMap with filter criteria name as a key and value as a value
     */
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {
        return searchCriteria;
    }

    public DataTable search() {
        filterCustomFieldSearchBean.buildFilterParameters(filters);
        dataTable.reset();
        return dataTable;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public List<T> getSelectedEntities() {
        return selectedEntities;
    }

    public void setSelectedEntities(List<T> selectedEntities) {
        this.selectedEntities = selectedEntities;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    /**
     * true in edit mode
     * 
     * @return
     */
    public boolean isEdit() {
        if (edit == null || org.meveo.commons.utils.StringUtils.isBlank(edit.get())) {
            return true;
        }

        return Boolean.valueOf(edit.get());
    }

    protected void clearObjectId() {
        objectId = null;
    }

    protected User getCurrentUser() {
        return currentUser;

        // return ((MeveoUser) identity.getUser()).getUser();
    }

    public List<TradingLanguage> getProviderLanguages() {
        return getCurrentProvider().getTradingLanguages();
    }

    public String getProviderLanguageCode() {
        if (getCurrentProvider() != null && getCurrentProvider().getLanguage() != null) {
            return getCurrentProvider().getLanguage().getLanguageCode();
        }
        return "";
    }

    public Map<String, String> getLanguageMessagesMap() {
        return languageMessagesMap;
    }

    public void setLanguageMessagesMap(Map<String, String> languageMessagesMap) {
        this.languageMessagesMap = languageMessagesMap;
    }

    protected Provider getCurrentProvider() {
        return currentProvider;
    }

    protected String getDefaultSort() {
        if (listFilter != null && listFilter.getOrderCondition() != null) {
            StringBuffer sb = new StringBuffer();
            for (String field : listFilter.getOrderCondition().getFieldNames()) {
                if (field.indexOf(".") == -1) {
                    sb.append(listFilter.getPrimarySelector().getAlias() + "." + field + ",");
                } else {
                    sb.append(field + ",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);

            return StringUtils.join(listFilter.getOrderCondition().getFieldNames(), ",");
        }

        return "id";
    }

    protected SortOrder getDefaultSortOrder() {
        if (listFilter != null && listFilter.getOrderCondition() != null) {
            if (listFilter.getOrderCondition().isAscending()) {
                return SortOrder.ASCENDING;
            }
        }

        return SortOrder.DESCENDING;
    }

    public String getBackView() {
        return backView.get();
    }

    public String getBackViewSave() {
        return backViewSave;
    }

    public void setBackViewSave(String backViewSave) {
        this.backViewSave = backViewSave;
    }

    public int getDataTableFirstAttribute() {
        return dataTableFirstAttribute;
    }

    public void setDataTableFirstAttribute(int dataTableFirstAttribute) {
        this.dataTableFirstAttribute = dataTableFirstAttribute;
    }

    public void onPageChange(PageEvent event) {
        this.setDataTableFirstAttribute(((DataTable) event.getSource()).getFirst());
    }

    private boolean isMultilanguageEntity() {
        return clazz.isAnnotationPresent(MultilanguageEntity.class);
    }

    /**
     * Get currently active locale
     * 
     * @return Currently active locale
     */
    protected Locale getCurrentLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

    /**
     * delete current entity from list, return a callback result to UI for validate
     */
    public void deleteInlist() {
        boolean result = true;
        try {
            this.delete();
            getPersistenceService().commit();
        } catch (Exception e) {
            log.error("Failed to delete {}", entity, e);
            result = false;
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("result", result);
    }

    /**
     * delete current entity from detail page
     * 
     * @return back() page if deleted success, if not, return a callback result to UI for validate
     */
    public String deleteWithBack() {
        try {
            this.delete();
            getPersistenceService().commit();
            return back();

        } catch (Throwable t) {
            messages.getAll();
            messages.clear();
            if (t.getCause() instanceof EntityExistsException) {
                log.info("delete was unsuccessful because entity is used in the system {}", t);
                messages.error(new BundleKey("messages", "error.delete.entityUsed"));

            } else {
                log.info("unexpected exception when deleting {}", t);
                messages.error(new BundleKey("messages", "error.delete.unexpected"));
            }
        }
        FacesContext.getCurrentInstance().validationFailed();
        return null;
    }

    public CSVExportOptions csvOptions() {
        ParamBean param = ParamBean.getInstance();
        String characterEncoding = param.getProperty("csv.characterEncoding", "iso-8859-1");
        CSVExportOptions csvOption = new CSVExportOptions();
        csvOption.setSeparatorCharacter(';');
        csvOption.setCharacterEncoding(characterEncoding);
        return csvOption;
    }

    // dummy codes for avoiding to get custom field templates
    public List<CustomFieldTemplate> getCustomFieldTemplates() {
        return null;
    }

    public Filter getListFilter() {
        return listFilter;
    }

    public void setListFilter(Filter listFilter) {
        this.listFilter = listFilter;
    }

    public List<Filter> getListFilters() {
        return filterService.findByPrimaryTargetClass(clazz.getName());
    }

    public void runListFilter() {
        if (listFilter != null) {
            dataModel = null;
            filters = new HashMap<>();
            filters.put("$FILTER", listFilter);
            listFiltered = true;
        } else {
            filters.remove("$FILTER");
        }
    }

    public boolean isListFiltered() {
        return listFiltered;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    /**
     * @param activeMainTab Main tab to select
     */
    public void setActiveMainTab(int activeMainTab) {
        this.activeMainTab = activeMainTab;
    }

    /**
     * @return the activeMainTab
     */
    public int getActiveMainTab() {
        return activeMainTab;
    }

    /**
     * Get custom actions applicable to the entity - right now implemented in customFieldEntityBean only. Here provided for GUI compatibility issue only
     * 
     * @return A list of entity action scripts
     */
    public List<EntityCustomAction> getCustomActions() {
        return null;
    }

    /**
     * Delete item from a collection of values
     * 
     * @param values Collection of values
     * @param itemIndex An index of an item to remove
     * @return True/false if item was removed
     */
    @SuppressWarnings("rawtypes")
    public void deleteItemFromCollection(Collection values, int itemIndex) {

        int index = 0;
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (itemIndex == index) {
                iterator.remove();
                return;
            }
            index++;
        }
    }

    /**
     * Change value in a collection. Collection to update an item index are passed as attributes
     * 
     * @param event Value change event
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateItemInCollection(ValueChangeEvent event) {

        Collection values = (Collection) event.getComponent().getAttributes().get("values");

        values.remove(event.getOldValue());
        values.add(event.getNewValue());

        // Unpredictable results when changing several values at a time, as Set does not guarantee same value order - could be used only in Ajax and only with refresh
        // int itemIndex = (int) event.getComponent().getAttributes().get("itemIndex");
        // log.error("AKK changing value from {} to {} in index {} values {}", event.getOldValue(), event.getNewValue(), itemIndex, values.toArray());
        // ArrayList newValues = new ArrayList();
        // newValues.addAll(values);
        //
        // newValues.remove(itemIndex);
        // newValues.add(itemIndex, event.getNewValue());
        // values.clear();
        // values.addAll(newValues);
        // log.error("AKK end changing value from {} to {} in index {} values {}", event.getOldValue(), event.getNewValue(), itemIndex, values.toArray());
    }

    /**
     * Add a new blank item to collection. Instantiate a new item based on parametized collection type.
     * 
     * @param values A collection of values
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addItemToCollection(Collection values, Class itemClass) {

        try {
            values.add(itemClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate a new item of {} class", itemClass.getName());
        }
    }

    public List<T> listActive() {
        Map<String, Object> filters = getFilters();
        filters.put("disabled", false);
        PaginationConfiguration config = new PaginationConfiguration(filters);

        return getPersistenceService().list(config);
    }

    /**
     * crm/customers
     * 
     * 
     */
    public boolean canUserUpdateEntity() {
        if (this.writeAccessMap == null) {
            writeAccessMap = Collections.synchronizedMap(new HashMap<String, Boolean>());
        }
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        String requestURI = request.getRequestURI();

        if (writeAccessMap.get(requestURI) == null) {
            boolean hasWriteAccess = false;
            try {
                hasWriteAccess = PagePermission.getInstance().hasWriteAccess(request, identity);
            } catch (BusinessException e) {
                log.error("Error encountered checking for write access to {}", requestURI, e);
                hasWriteAccess = false;
            }
            writeAccessMap.put(requestURI, hasWriteAccess);
        }
        return writeAccessMap.get(requestURI);
    }

    public String getPartOfModules() {
        return partOfModules;
    }

    public void setPartOfModules(String partOfModules) {
        this.partOfModules = partOfModules;
    }

    public String getDescriptionOrCode() {
        if (entity instanceof BusinessEntity) {
            BusinessEntity be = (BusinessEntity) entity;
            if (org.meveo.commons.utils.StringUtils.isBlank(be.getDescription())) {
                return be.getCode();
            } else {
                return be.getDescription();
            }
        }

        return null;
    }

}