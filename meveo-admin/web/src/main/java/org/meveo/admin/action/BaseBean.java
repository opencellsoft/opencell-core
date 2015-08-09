/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.Identity;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.LoggerFactory;

import com.lapis.jsfexporter.csv.CSVExportOptions;

/**
 * Base bean class. Other seam backing beans extends this class if they need functionality it provides.
 */
public abstract class BaseBean<T extends IEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Logger. */
    protected org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    protected Messages messages;

    @Inject
    protected Identity identity;

    @Inject
    @CurrentProvider
    protected Provider currentProvider;

    @Inject
    protected Conversation conversation;

    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private CatMessagesService catMessagesService;
    
//    @Inject
//    private CustomFieldJob customFieldJob;

    /** Search filters. */
    protected Map<String, Object> filters = new HashMap<String, Object>();

    /** Entity to edit/view. */
    protected T entity;

    /** Class of backing bean. */
    private Class<T> clazz;

//    private CustomFieldTemplate customFieldSelectedTemplate;
//    /**
//     * New custom field period
//     */
//    private CustomFieldPeriod customFieldNewPeriod;
//
//    private boolean customFieldPeriodMatched;
//
//    /**
//     * Custom field templates
//     */
//    protected List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();
    
    /**
     * Request parameter. Should form be displayed in create/edit or view mode
     */
    @Inject
    @RequestParam()
    private Instance<String> edit;

    // private boolean editSaved;

    protected int dataTableFirstAttribute;

    /**
     * Request parameter. A custom back view page instead of a regular list page
     */
    @Inject
    @RequestParam()
    private Instance<String> backView;

    private String backViewSave;

    /**
     * Request parameter. Used for loading in object by its id.
     */
    @Inject
    @RequestParam("objectId")
    private Instance<Long> objectIdFromParam;

    private Long objectIdFromSet;

    /** Helper field to enter language related field values. */
    protected Map<String, String> languageMessagesMap = new HashMap<String, String>();

//    /** Helper field to enter values for HashMap<String,String> type fields */
//    protected Map<String, List<HashMap<String, String>>> mapTypeFieldValues = new HashMap<String, List<HashMap<String, String>>>();

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
    
    
//    @Inject
//    private CustomEntitySearchService cfSearchService;
//    
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
        log.debug("instantiating " + this.getClass());
        if (getObjectId() != null) {
            if (getFormFieldsToFetch() == null) {
                entity = (T) getPersistenceService().findById(getObjectId());
            } else {
                entity = (T) getPersistenceService().findById(getObjectId(), getFormFieldsToFetch());
            }

            loadMultiLanguageFields();

            // getPersistenceService().detach(entity);
        } else {
            try {
                entity = getInstance();
                if (entity instanceof BaseEntity) {
                    ((BaseEntity) entity).setProvider(getCurrentProvider());
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

//        initCustomFields();

        return entity;
    }

    /**
     * Load multi-language fields if applicable for a class (class contains annotation MultilanguageEntity)
     * 
     * @param entity Entity lo load fields for
     */
    private void loadMultiLanguageFields() {

        if (!isMultilanguageEntity()) {
            return;
        }

        languageMessagesMap.clear();

        for (CatMessages msg : catMessagesService.getCatMessagesList(clazz.getSimpleName() + "_" + entity.getId())) {
            languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
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

    public String saveOrUpdate(boolean killConversation, String objectName, Long objectId) throws BusinessException {
        String outcome = saveOrUpdate(killConversation);

        if (killConversation) {
            endConversation();
        }

        // return objectId == null ? outcome : (outcome + "&" + objectName + "="
        // + objectId + "&cid=" + conversation.getId());
        return outcome;
    }

    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String outcome = null;

//        updateCustomFieldsInEntity();

        if (!isMultilanguageEntity()) {
            outcome = saveOrUpdate(entity);

        } else {
            if (entity.getId() != null) {

                for (String msgKey : languageMessagesMap.keySet()) {
                    String description = languageMessagesMap.get(msgKey);
                    CatMessages catMsg = catMessagesService.getCatMessages(entity, msgKey);
                    if (catMsg != null) {
                        catMsg.setDescription(description);
                        catMessagesService.update(catMsg);
                    } else {
                        CatMessages catMessages = new CatMessages(entity, msgKey, description);
                        catMessagesService.create(catMessages);
                    }
                }

                outcome = saveOrUpdate(entity);

            } else {
                outcome = saveOrUpdate(entity);

                for (String msgKey : languageMessagesMap.keySet()) {
                    String description = languageMessagesMap.get(msgKey);
                    CatMessages catMessages = new CatMessages(entity, msgKey, description);
                    catMessagesService.create(catMessages);
                }
            }
        }

        if (killConversation) {
            endConversation();
        }

        return outcome;
    }
    
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
     * Save or update entity depending on if entity is transient.
     * 
     * @param entity Entity to save.
     * @throws BusinessException
     */
    protected String saveOrUpdate(T entity) throws BusinessException {
        if (entity.isTransient()) {
            getPersistenceService().create(entity);
            messages.info(new BundleKey("messages", "save.successful"));
        } else {
            getPersistenceService().update(entity);
            messages.info(new BundleKey("messages", "update.successful"));
        }

        return back();
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
        if (backView != null && backView.get() != null) {
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
     * TODO
     */
    public String getEditViewName() {
        String className = clazz.getSimpleName();
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
    public void delete(Long id) {
        try {
            log.info(String.format("Deleting entity %s with id = %s", clazz.getName(), id));
            getPersistenceService().remove(id);
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

    public void delete() {
        try {
            log.info(String.format("Deleting entity %s with id = %s", clazz.getName(), getEntity().getId()));
            getPersistenceService().remove((Long) getEntity().getId());
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
                log.info(String.format("Deleting multiple entities %s with ids = %s", clazz.getName(), idsString.toString()));

                getPersistenceService().remove(idsToDelete);
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
        if (filters == null)
            filters = new HashMap<String, Object>();
        return filters;
    }

    /**
     * Clean search fields in datatable.
     */
    public void clean() {
        dataModel = null;
        filters = new HashMap<String, Object>();
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
        return clazz.newInstance();
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
    public void disable() {
        try {
            log.info(String.format("Disabling entity %s with id = %s", clazz.getName(), entity.getId()));
            entity = getPersistenceService().disable(entity);
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
    public void disable(Long id) {
        try {
            log.info(String.format("Disabling entity %s with id = %s", clazz.getName(), id));
            getPersistenceService().disable(id);
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
    public void enable() {
        try {
            log.info(String.format("Enabling entity %s with id = %s", clazz.getName(), entity.getId()));
            entity = getPersistenceService().enable(entity);
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
    public void enable(Long id) {
        try {
            log.info(String.format("Enabling entity %s with id = %s", clazz.getName(), id));
            getPersistenceService().enable(id);
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
        return getLazyDataModel(filters, false);
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
            };
        }
        return dataModel;
    }

//    public Map<String, List<HashMap<String, String>>> getMapTypeFieldValues() {
//        return mapTypeFieldValues;
//    }
//
//    public void setMapTypeFieldValues(Map<String, List<HashMap<String, String>>> mapTypeFieldValues) {
//        this.mapTypeFieldValues = mapTypeFieldValues;
//    }

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
        if (objectIdFromParam != null && objectIdFromParam.get() != null) {
            objectIdFromSet = objectIdFromParam.get();
        }

        return objectIdFromSet;
    }

    public void setObjectId(Long objectId) {
        objectIdFromSet = objectId;
    }

    /**
     * true in edit mode
     * 
     * @return
     */
    public boolean isEdit() {
        return true;
    }

    protected void clearObjectId() {
        objectIdFromParam = null;
        objectIdFromSet = null;
    }

    public void onRowSelect(SelectEvent event) {

    }

    protected User getCurrentUser() {
        return ((MeveoUser) identity.getUser()).getUser();
    }

    public List<TradingLanguage> getProviderLanguages() {
        return getCurrentProvider().getTradingLanguages();
    }

    public String getProviderLanguageCode() {
        if (getCurrentProvider() != null) {
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
        return "id";
    }

    protected SortOrder getDefaultSortOrder() {
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

//    /**
//     * Remove a value from a map type field attribute used to gather field values in GUI
//     * 
//     * @param fieldName Field name
//     * @param valueInfo Value to remove
//     */
//    public void removeMapTypeFieldValue(String fieldName, Map<String, String> valueInfo) {
//        mapTypeFieldValues.get(fieldName).remove(valueInfo);
//    }
//
//    /**
//     * Add a value to a map type field attribute used to gather field values in GUI
//     * 
//     * @param fieldName Field name
//     */
//    public void addMapTypeFieldValue(String fieldName) {
//        if (!mapTypeFieldValues.containsKey(fieldName)) {
//            mapTypeFieldValues.put(fieldName, new ArrayList<HashMap<String, String>>());
//        }
//        mapTypeFieldValues.get(fieldName).add(new HashMap<String, String>());
//    }
//
//    /**
//     * Extract values from a Map type field in an entity to mapTypeFieldValues attribute used to gather field values in GUI
//     * 
//     * @param entityField Entity field
//     * @param fieldName Field name
//     */
//    public void extractMapTypeFieldFromEntity(Map<String, String> entityField, String fieldName) {
//
//        mapTypeFieldValues.remove(fieldName);
//
//        if (entityField != null) {
//            List<HashMap<String, String>> fieldValues = new ArrayList<HashMap<String, String>>();
//            mapTypeFieldValues.put(fieldName, fieldValues);
//            for (Entry<String, String> setInfo : entityField.entrySet()) {
//                HashMap<String, String> value = new HashMap<String, String>();
//                value.put("key", setInfo.getKey());
//                value.put("value", setInfo.getValue());
//                fieldValues.add(value);
//            }
//        }
//    }
//
//    /**
//     * Update Map type field in an entity from mapTypeFieldValues attribute used to gather field values in GUI
//     * 
//     * @param entityField Entity field
//     * @param fieldName Field name
//     */
//    public void updateMapTypeFieldInEntity(Map<String, String> entityField, String fieldName) {
//        entityField.clear();
//
//        if (mapTypeFieldValues.get(fieldName) != null) {
//            for (HashMap<String, String> valueInfo : mapTypeFieldValues.get(fieldName)) {
//                if (valueInfo.get("key") != null && !valueInfo.get("key").isEmpty()) {
//                    entityField.put(valueInfo.get("key"), valueInfo.get("value") == null ? "" : valueInfo.get("value"));
//                }
//            }
//        }
//    }

    /**
     * Load available custom fields (templates) and their values
     */
//    @SuppressWarnings("unchecked")
//	protected void initCustomFields() {
//
//        if (!this.getClass().isAnnotationPresent(CustomFieldEnabledBean.class)) {
//            return;
//        }
//
//        customFieldTemplates = getApplicateCustomFieldTemplates();
//
//        if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
//            for (CustomFieldTemplate cf : customFieldTemplates) {
//                CustomFieldInstance cfi = ((ICustomFieldEntity) entity).getCustomFields().get(cf.getCode());
//                if (cfi == null) {
//                    cf.setInstance(CustomFieldInstance.fromTemplate(cf));
//                } else {
//                    if(CustomFieldStorageTypeEnum.SINGLE.equals(cf.getStorageType())){
//               	 		if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//               	 			cfi.setBusinessEntity(SerializableUtil.decodeSingle(cfSearchService,cf.getEntityClazz(),cfi.getEntityValue()));
//               	 		}
//               	 	}else{
//               	 		if(cf.isVersionable()){
//               	 			for(CustomFieldPeriod cfp: cfi.getValuePeriods()){
//               	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//               	 					cfp.setBusinessEntity(SerializableUtil.decodeSingle(cfSearchService,cf.getEntityClazz(),cfp.getEntityValue()));
//               	 				}
//               	 			}
//               	 		}else{
//               	 			if(CustomFieldStorageTypeEnum.LIST.equals(cf.getStorageType())){
//               	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//               	 					cfi.setEntityList(SerializableUtil.decodeList(cfSearchService,cf.getEntityClazz(),cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
//               	 					cfi.setStringList((Set<String>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
//               	 					cfi.setLongList((Set<Long>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
//               	 					cfi.setDoubleList((Set<Double>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
//               	 					cfi.setDateList((Set<Date>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}
//               	 			}else if(CustomFieldStorageTypeEnum.MAP.equals(cf.getStorageType())){
//               	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//               	 					cfi.setEntityMap((Map<String,BusinessEntity>)SerializableUtil.decodeMap(cfSearchService,cf.getEntityClazz(),cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
//               	 					cfi.setStringMap((Map<String,String>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
//               	 					cfi.setLongMap((Map<String,Long>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
//               	 					cfi.setDoubleMap((Map<String,Double>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
//               	 					cfi.setDateMap((Map<String,Date>)SerializableUtil.decode(cfi.getEntityValue()));
//               	 				}
//               	 			}
//               	 		}
//               	 	}
//                    cf.setInstance(cfi);
//                }
//            }
//        }
//    }
//
//    private void updateCustomFieldsInEntity(){
//    	
//
//        if (!this.getClass().isAnnotationPresent(CustomFieldEnabledBean.class) || customFieldTemplates == null || customFieldTemplates.isEmpty()) {
//            return;
//        }
//
//        for (CustomFieldTemplate cf : customFieldTemplates) {
//            CustomFieldInstance cfi = cf.getInstance();
//       	 	if(CustomFieldStorageTypeEnum.SINGLE.equals(cf.getStorageType())){
//       	 		if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//       	 			BusinessEntity temp=cfi.getBusinessEntity();
//       	 			BusinessEntity result=new BusinessEntity();
//       	 			result.setId(temp.getId());
//       	 			cfi.setEntityValue(SerializableUtil.encode(result));
//       	 		}
//       	 	}else {
//       	 		if(cf.isVersionable()){
//       	 			for(CustomFieldPeriod cfp: cfi.getValuePeriods()){
//       	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//       	 					BusinessEntity temp=cfp.getBusinessEntity();
//       	 					if(temp!=null){
//       	 						BusinessEntity result=new BusinessEntity();
//       	 						result.setId(temp.getId());
//       	 						cfp.setEntityValue(SerializableUtil.encode(result));
//       	 					}
//       	 				}
//       	 			}
//       	 		}else if(CustomFieldStorageTypeEnum.LIST.equals(cf.getStorageType())){
//	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//	 					Set<BusinessEntity> result=new HashSet<BusinessEntity>();
//	 					BusinessEntity temp=null;
//	 					for(BusinessEntity list:cfi.getEntityList()){
//	 						temp=new BusinessEntity();
//	 						temp.setId(list.getId());
//	 						result.add(temp);
//	 					}
//	 					cfi.setEntityValue(cfi.getEntityList().size()==0?null:SerializableUtil.encode(result));
//	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getStringList().size()==0?null:SerializableUtil.encode(cfi.getStringList()));
//	 					cfi.setStringValue(null);
//	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getLongList().size()==0?null:SerializableUtil.encode(cfi.getLongList()));
//	 					cfi.setLongValue(null);
//	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getDoubleList().size()==0?null:SerializableUtil.encode(cfi.getDoubleList()));
//	 					cfi.setDoubleValue(null);
//	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getDateList().size()==0?null:SerializableUtil.encode(cfi.getDateList()));
//	 					cfi.setDateValue(null);
//	 				}
//	 			}else if(CustomFieldStorageTypeEnum.MAP.equals(cf.getStorageType())){
//	 				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
//	 					Map<String,BusinessEntity> result=new HashMap<String,BusinessEntity>();
//	 					BusinessEntity temp=null;
//	 					for(Map.Entry<String, BusinessEntity> entry:cfi.getEntityMap().entrySet()){
//	 						temp=new BusinessEntity();
//	 						temp.setId(entry.getValue().getId());
//	 						result.put(entry.getKey(),temp);
//	 					}
//	 					cfi.setEntityValue(cfi.getEntityMap().size()==0?null:SerializableUtil.encode(result));
//	 				}else if(CustomFieldTypeEnum.STRING.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getStringMap().size()==0?null:SerializableUtil.encode(cfi.getStringMap()));
//	 					cfi.setStringValue(null);
//	 				}else if(CustomFieldTypeEnum.LONG.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getLongMap().size()==0?null:SerializableUtil.encode(cfi.getLongMap()));
//	 					cfi.setLongValue(null);
//	 				}else if(CustomFieldTypeEnum.DOUBLE.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getDoubleMap().size()==0?null:SerializableUtil.encode(cfi.getDoubleMap()));
//	 					cfi.setDoubleValue(null);
//	 				}else if(CustomFieldTypeEnum.DATE.equals(cf.getFieldType())){
//	 					cfi.setEntityValue(cfi.getDateMap().size()==0?null:SerializableUtil.encode(cfi.getDateMap()));
//	 					cfi.setDateValue(null);
//	 				}
//	 			}
//       	 	}
//       	     // Not saving empty values
//            if (cfi.isValueEmpty()) {
//                if (!cfi.isTransient()) {
//                    ((ICustomFieldEntity) entity).getCustomFields().remove(cfi.getCode());
//                    log.debug("remove cfi {}",cfi.getCode());
//                }
//                // Existing value update
//            } else{
//            	if (!cfi.isTransient()) {
//            		cfi.updateAudit(getCurrentUser());
//                // Create a new instance from a template value
//            	} else {
//            		cfi.updateAudit(getCurrentUser()); 
//            		IEntity entity = getEntity();
//            		if (entity instanceof AccountEntity) {
//            			cfi.setAccount((AccountEntity) getEntity());
//            		} else if (entity instanceof Subscription) {
//            			cfi.setSubscription((Subscription) entity);
//            		} else if (entity instanceof Access) {
//            			cfi.setAccess((Access) entity);
//            		} else if (entity instanceof ChargeTemplate) {
//            			cfi.setChargeTemplate((ChargeTemplate) entity);
//            		} else if (entity instanceof ServiceTemplate) {
//            			cfi.setServiceTemplate((ServiceTemplate) entity);
//            		} else if (entity instanceof OfferTemplate) {
//            			cfi.setOfferTemplate((OfferTemplate) entity);
//            		} else if (entity instanceof JobInstance) {
//            			cfi.setJobInstance((JobInstance) entity);
//            		}else if (entity instanceof Provider) {
//            			cfi.setProvider((Provider)entity);
//            		}
//            	}
//            	((ICustomFieldEntity) entity).getCustomFields().put(cfi.getCode(), cfi);
//            }
//        }
//        }

    // protected void setAndSaveCustomFields() {
    // if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
    // for (CustomFieldTemplate cf : customFieldTemplates) {
    // CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(), getEntity(),getCurrentProvider());
    // if (cfi != null) {
    // if (cf.isValueEmpty()) {
    // customFieldInstanceService.remove(cfi);
    //
    // } else {
    // if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
    // cfi.setDateValue(cf.getDateValue());
    // } else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
    // cfi.setDoubleValue(cf.getDoubleValue());
    // } else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
    // cfi.setLongValue(cf.getLongValue());
    // } else if (cf.getFieldType() == CustomFieldTypeEnum.STRING
    // || cf.getFieldType() == CustomFieldTypeEnum.LIST) {
    // cfi.setStringValue(cf.getStringValue());
    // }
    // }
    //
    // } else if (!cf.isValueEmpty()) {
    // // create
    // cfi = new CustomFieldInstance();
    // cfi.setCode(cf.getCode());
    // IEntity entity = getEntity();
    // if (entity instanceof AccountEntity) {
    // cfi.setAccount((AccountEntity) getEntity());
    // } else if (entity instanceof Subscription) {
    // cfi.setSubscription((Subscription) entity);
    // } else if (entity instanceof Access) {
    // cfi.setAccess((Access) entity);
    // }else if (entity instanceof ChargeTemplate) {
    // cfi.setChargeTemplate((ChargeTemplate) entity);
    // } else if (entity instanceof ServiceTemplate) {
    // cfi.setServiceTemplate((ServiceTemplate) entity);
    // } else if (entity instanceof OfferTemplate) {
    // cfi.setOfferTemplate((OfferTemplate) entity);
    // }else if (entity instanceof JobInstance) {
    // cfi.setJobInstance((JobInstance) entity);
    // }
    //
    // if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
    // cfi.setDateValue(cf.getDateValue());
    // } else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
    // cfi.setDoubleValue(cf.getDoubleValue());
    // } else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
    // cfi.setLongValue(cf.getLongValue());
    // } else if (cf.getFieldType() == CustomFieldTypeEnum.STRING
    // || cf.getFieldType() == CustomFieldTypeEnum.LIST) {
    // cfi.setStringValue(cf.getStringValue());
    // }
    //
    // customFieldInstanceService.create(cfi, getCurrentUser(), getCurrentProvider());
    // }
    // }
    // }
    // }
    //
    // protected void deleteCustomFields() {
    // if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
    // for (CustomFieldTemplate cf : customFieldTemplates) {
    // CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(), getEntity(),getCurrentProvider());
    // if (cfi != null) {
    // customFieldInstanceService.remove(cfi);
    // }
    // }
    // }
    // }

//    public List<CustomFieldTemplate> getCustomFieldTemplates() {
//    	if(customFieldTemplates==null||customFieldTemplates.size()==0){
//    		if(entity!=null){
//    			initCustomFields();
//    		}else{
//    			initEntity();
//    		}
//    	}
//        return customFieldTemplates;
//    }
//
//    public void setCustomFieldTemplates(List<CustomFieldTemplate> customFieldTemplates) {
//        this.customFieldTemplates = customFieldTemplates;
//    }
//
//    public CustomFieldPeriod getCustomFieldNewPeriod() {
//        return customFieldNewPeriod;
//    }
//
//    public void setCustomFieldNewPeriod(CustomFieldPeriod customFieldNewPeriod) {
//        this.customFieldNewPeriod = customFieldNewPeriod;
//    }
//
//    public void setCustomFieldSelectedTemplate(CustomFieldTemplate customFieldSelectedTemplate) {
//        this.customFieldSelectedTemplate = customFieldSelectedTemplate;
//        this.customFieldPeriodMatched = false;
//        // Set a default value for new period data entry
//        this.customFieldNewPeriod = new CustomFieldPeriod();
//        this.customFieldNewPeriod.setValue(customFieldSelectedTemplate.getDefaultValueConverted(), customFieldSelectedTemplate.getFieldType());
//    }
//
//    public CustomFieldTemplate getCustomFieldSelectedTemplate() {
//        return customFieldSelectedTemplate;
//    }

//    public boolean isCustomFieldPeriodMatched() {
//        return customFieldPeriodMatched;
//    }

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
        boolean result = true;
        try {
            this.delete();
            getPersistenceService().commit();
            return back();
        } catch (Exception e) {
            result = false;
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("result", result);
        return null;
    }

    /**
     * Add a new customField period with a previous validation that matching period does not exists
     */
//    public void addNewCustomFieldPeriod() {
//
//        // Check that two dates are one after another
//        if (customFieldNewPeriod.getPeriodStartDate() != null && customFieldNewPeriod.getPeriodEndDate() != null
//                && customFieldNewPeriod.getPeriodStartDate().compareTo(customFieldNewPeriod.getPeriodEndDate()) >= 0) {
//            messages.error(new BundleKey("messages", "customFieldTemplate.periodIntervalIncorrect"));
//            FacesContext.getCurrentInstance().validationFailed();
//            return;
//        }
//
//        CustomFieldPeriod period = null;
//        // First check if any period matches the dates
//        if (!customFieldPeriodMatched) {
//            if (customFieldSelectedTemplate.getInstance().getCalendar() != null) {
//                period = customFieldSelectedTemplate.getInstance().getValuePeriod(customFieldNewPeriod.getPeriodStartDate(), false);
//            } else {
//                period = customFieldSelectedTemplate.getInstance().getValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getPeriodEndDate(), false, false);
//            }
//
//            if (period != null) {
//                customFieldPeriodMatched = true;
//                ParamBean paramBean = ParamBean.getInstance();
//                String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");
//
//                if (customFieldSelectedTemplate.getInstance().getCalendar() != null) {
//                    messages.error(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound.noNew"),
//                        DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern), DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
//                } else {
//                    messages.warn(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound"), DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern),
//                        DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
//                }
//                FacesContext.getCurrentInstance().validationFailed();
//                customFieldPeriodMatched = true;
//                return;
//            }
//        }
//
//        // Create period if passed period check or if user decided to create it anyway
//        if (customFieldSelectedTemplate.getInstance().getCalendar() != null) {
//            period = customFieldSelectedTemplate.getInstance().addValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getValue(),
//                customFieldSelectedTemplate.getFieldType(),customFieldNewPeriod.getLabel(),customFieldSelectedTemplate.getStorageType());
//        } else {
//            period = customFieldSelectedTemplate.getInstance().addValuePeriod(customFieldNewPeriod.getPeriodStartDate(), customFieldNewPeriod.getPeriodEndDate(),
//                customFieldNewPeriod.getValue(), customFieldSelectedTemplate.getFieldType(),customFieldNewPeriod.getLabel(),customFieldSelectedTemplate.getStorageType());
//        }
//        
//        if (customFieldSelectedTemplate.isVersionable()
//				&& customFieldSelectedTemplate.getCalendar() != null
//				&& customFieldSelectedTemplate.isTriggerEndPeriodEvent()) {
//			// create a timer
//			customFieldJob.triggerEndPeriodEvent(customFieldSelectedTemplate.getInstance(),
//					customFieldNewPeriod.getPeriodEndDate());
//		}
//        
//        customFieldNewPeriod = null;
//        customFieldPeriodMatched = false;
//    }

    /**
     * Get a list of custom field templates applicable to an entity.
     * 
     * @return A list of custom field templates
     */
    protected List<CustomFieldTemplate> getApplicateCustomFieldTemplates() {
        AccountLevelEnum accountLevel = this.getClass().getAnnotation(CustomFieldEnabledBean.class).accountLevel();
        List<CustomFieldTemplate> result= customFieldTemplateService.findByAccountLevel(accountLevel);
        log.debug("find {} by fieldType={} for {}",result.size(),accountLevel,this.getClass());
        return result;
    }
    
    public CSVExportOptions csvOptions(){
	    ParamBean param = ParamBean.getInstance();
		String characterEncoding = param.getProperty("csv.characterEncoding","iso-8859-1");
		CSVExportOptions csvOption=new CSVExportOptions();
		csvOption.setSeparatorCharacter(';');
		csvOption.setCharacterEncoding(characterEncoding);
		return csvOption;
	}
    //dummy codes for avoiding to get custom field templates
    public List<CustomFieldTemplate> getCustomFieldTemplates() {
    	return null;
    }
}
