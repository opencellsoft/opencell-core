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
package org.meveo.admin.action;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import javax.enterprise.context.Conversation;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.filter.Filter;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.index.ElasticClient;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.view.ESBasedDataModel;
import org.meveo.util.view.PagePermission;
import org.meveo.util.view.ServiceBasedLazyDataModel;
import org.omnifaces.cdi.Param;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lapis.jsfexporter.csv.CSVExportOptions;

/**
 * Base GUI bean class. Used as a backing bean foundation for both detail and searchable list pages. Provides a brigde between xhtml pages and service level classes.
 * 
 * There is at least one backing bean per entity class. Majority of pages distinguish between detail and list views and have two backing beans, with view and conversation scopes.
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
public abstract class BaseBean<T extends IEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Logger. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Inject
    protected Messages messages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    protected Conversation conversation;

    @Inject
    protected PermissionService permissionService;

    @Inject
    private FilterService filterService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private FilterCustomFieldSearchBean filterCustomFieldSearchBean;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    protected FacesContext facesContext;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    /** Search filters. */
    protected Map<String, Object> filters = new HashMap<>();

    /** Entity to edit/view. */
    protected T entity;

    /** Class of backing bean. */
    private Class<T> clazz;

    /**
     * Request parameter. Should view be displayed in create/edit or view mode
     */
    @Inject
    @Param
    private String edit;

    protected int dataTableFirstAttribute;

    @Inject
    private MeveoModuleService meveoModuleService;
    private String partOfModules;

    /**
     * Request parameter. A custom back view page instead of a regular list page
     */
    @Inject
    @Param()
    private String backView;

    private String backViewSave;

    /** The back entity id. */
    @Inject
    @Param()
    private String backEntityId;

    /** The back tab. */
    @Inject
    @Param()
    private String backTab;

    /** The back main tab. */
    @Inject
    @Param()
    private String backMainTab;

    /**
     * Object identifier to load
     */
    private Long objectId;

    /**
     * Datamodel for lazy dataloading in datatable.
     */
    protected LazyDataModel<T> dataModel;

    /**
     * Selected Entities in multiselect datatable.
     */
    private List<T> selectedEntities;

    /**
     * Filter to apply in search
     */
    private Filter listFilter;

    protected boolean listFiltered = false;

    /**
     * Tracks active tabs in GUI - a main tab
     */
    private int activeMainTab = 0;

    /**
     * Tracks active tabs in GUI - a secondary tab
     */
    private int activeTab;

    /**
     * Cached access to entity (read/modification) rules
     */
    private Map<String, Boolean> writeAccessMap;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    private UploadedFile uploadedFile;

    private static final String SUPER_ADMIN_MANAGEMENT = "superAdminManagement";
    
    public static final String DEPRECATED_FEATURE = "DEPRECATED: This feature is deprecated and will be removed or replaced in a future release";

    /**
     * Constructor
     */
    public BaseBean() {
        super();
    }

    /**
     * Constructor
     * 
     * @param clazz Entity class
     */
    public BaseBean(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    /**
     * Returns entity class
     * 
     * @return Entity class
     */
    public Class<T> getClazz() {
        return clazz;
    }

    /**
     * Set entity class
     * 
     * @param clazz Entity class
     */
    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Begin conversation
     */
    protected void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    /**
     * End conversation
     */
    protected void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    /**
     * A generic pre-render view action will initiate a conversation. Should be initiated from xhtml list style pages
     */
    public void preRenderView() {
        beginConversation();
    }

    /**
     * Initiates entity from request parameter ID. If ID is provider, an entity will be retrieved from DB. If not provided, a new entity will be instantiated.
     * 
     * @return Entity from database or a new entity if no ID was provided in request or set manually by other means.
     */
    public T initEntity() {
        log.debug("Instantiating {} with id {}", this.getClass(), getObjectId());
        if (getObjectId() != null) {

            List<String> formFieldsToFetch = getFormFieldsToFetch();

            if (formFieldsToFetch == null) {
                entity = (T) getPersistenceService().findById(getObjectId(), true);
            } else {
                entity = (T) getPersistenceService().findById(getObjectId(), formFieldsToFetch, true);
            }
            loadPartOfModules();

        } else {
            try {
                entity = getInstance();

                // FIXME: If entity is Auditable, set here the creator and
                // creation time
            } catch (InstantiationException e) {
                log.error("Could not instantiate a class, abstract class", e);
            } catch (IllegalAccessException e) {
                log.error("Could not instantiate a class, constructor not accessible", e);
                throw new IllegalStateException("could not instantiate a class, constructor not accessible");
            }
        }
        if(entity instanceof BusinessEntity) {
            if(((BusinessEntity) entity).getCode() == null) {
                String entityClass = entity.getClass().getSimpleName();
                Optional.ofNullable(generateCode(entityClass))
                        .ifPresent(code -> ((BusinessEntity) entity).setCode(code));
            }
        }
        return entity;
    }

    private String generateCode(String entityClass) {
        CustomGenericEntityCode customGenericEntityCode = customGenericEntityCodeService.findByClass(entityClass);
        if(customGenericEntityCode != null) {
            return serviceSingleton.getGenericCode(customGenericEntityCode);
        }
        return null;
    }
    /**
     * Force to initialize entity with a given ID.
     * 
     * @param id Entity ID, or null for a new entity
     * @return Entity from database or a new entity if no ID was provided
     */
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
     * Can entity be used as module item
     * 
     * @return True if entity is annotated with @ModuleItem
     */
    private boolean isPartOfModules() {
        return clazz.isAnnotationPresent(ModuleItem.class);
    }

    /**
     * Does entity support image upload
     * 
     * @return True if entity is a subclass of IImageUpload class
     */
    protected boolean isImageUpload() {
        return IImageUpload.class.isAssignableFrom(clazz);
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

            partOfModules = meveoModuleService.getRelatedModulesAsString(businessEntity.getCode(), clazz.getName(), appliesTo);
        }
    }

    /**
     * The first time is called will either load an entity from DB or will instantiate a new entity
     * 
     * @return Entity in current view state
     */
    public T getEntity() {
        return entity != null ? entity : initEntity();
    }

    /**
     * Setter
     * 
     * @param entity Entity in current view state
     */
    public void setEntity(T entity) {
        this.entity = entity;
    }

    /**
     * Save entity to DB and redirect to a next view. A message will be displayed in GUI upon saving.
     * 
     * @param killConversation True if conversation be terminated
     * @param objectName Custom ID parameter name. NOTE: Not used at the moment. Could be used to redirect to a diferent view with a diferent ID parameter and value
     * @param objectId ID value. NOTE: Not used at the moment. Could be used to redirect to a diferent view with a diferent ID parameter and value
     * @return Next navigation view name as result of action execution
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public String saveOrUpdate(boolean killConversation, String objectName, Long objectId) throws BusinessException {
        String outcome = saveOrUpdate(killConversation);

        if (killConversation) {
            endConversation();
        }

        // return objectId == null ? outcome : (outcome + "&" + objectName + "=" + objectId + "&cid=" + conversation.getId());
        return outcome;
    }

    /**
     * Check whether entity code is updated or not.
     * 
     * @return boolean that indicate if entity code is updated
     */
    private boolean isUpdatedEntityCode() {
        BusinessEntity persistedBusinessEntity = (BusinessEntity) getPersistenceService().findById(getObjectId());
        BusinessEntity businessEntity = (BusinessEntity) entity;
        return !persistedBusinessEntity.getCode().equalsIgnoreCase(businessEntity.getCode());
    }

    /**
     * Save entity to DB and redirect to a next view. A message will be displayed in GUI upon saving.
     * 
     * @param killConversation True if conversation be terminated
     * @return Next navigation view name as result of action execution
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        String message = entity.isTransient() ? "save.successful" : "update.successful";

        if (!entity.isTransient()) {
            boolean allowEntityCodeUpdate = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("service.allowEntityCodeUpdate", "true"));
            if ((entity instanceof BusinessEntity)) {
                if (!currentUser.hasRole(SUPER_ADMIN_MANAGEMENT) && !allowEntityCodeUpdate && isUpdatedEntityCode()) {
                    messages.error(new BundleKey("messages", "error.superadminpermission.required"));
                    return null;
                }
            }
        }
        
        entity = saveOrUpdate(entity);
		
        if (killConversation) {
            endConversation();
        }

        messages.info(new BundleKey("messages", message));
        return back();
    }

    /**
     * Save entity to DB and redirect to a next view. A message will be displayed in GUI upon saving. In case or error, a callback parameter "result=false" is added to response.
     * 
     * @param killConversation True if conversation be terminated
     * @return Next navigation view name as result of action execution
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public String saveOrUpdateWithCallbackResult(boolean killConversation) throws BusinessException {
        boolean result = true;
        try {
            return this.saveOrUpdate(killConversation);
        } catch (Exception e) {
            result = false;
        }

        PrimeFaces.current().ajax().addCallbackParam("result", result);
        return null;
    }

    /**
     * Save method when used in popup (ajax). Same as {@link #saveOrUpdate(boolean)}, but does not redirect to the next view. Sets validation to failed if saveOrUpdate method
     * called does not return a value or fails.
     * 
     * @throws BusinessException business exception
     */
    @ActionMethod
    public void saveOrUpdateForPopup() throws BusinessException {
        try {
            String result = saveOrUpdate(false);
            if (result == null) {
                facesContext.validationFailed();
            }
        } catch (BusinessException e) {
            facesContext.validationFailed();
            throw e;
        }

        return;
    }

    /**
     * Save or update entity depending on if entity is transient.
     * 
     * @param entity Entity to save.
     * @return Updated entity
     * @throws BusinessException General business exception
     */
    protected T saveOrUpdate(T entity) throws BusinessException {
        if (entity.isTransient()) {
            getPersistenceService().create(entity);

        } else {
            entity = getPersistenceService().update(entity);
        }

        objectId = (Long) entity.getId();

        return entity;
    }

    /**
     * Lists all entities, sorted by description if bean is related to BusinessEntity type
     * 
     * @return A list of entities
     */
    public List<T> listAll() {
        if (clazz != null && BusinessEntity.class.isAssignableFrom(clazz)) {
            return getPersistenceService().list(new PaginationConfiguration("code", SortOrder.ASCENDING));
        } else {
            return getPersistenceService().list();
        }
    }

    /**
     * Returns navigation view name to go to after save() operation. By default it goes back to list view. Override if need different logic (for example return to one view for save
     * and another for update operations)
     * 
     * @return Next navigation view name as result of action execution
     */

    public String getViewAfterSave() {
        return getListViewName();
    }

    /**
     * Returns translation value for i18n map field, else the value itself or default value
     *
     * @author akadid abdelmounaim
     * @author Amine BEN AICHA
     * @lastModifiedVersion 5.0
     */
    public String getTranslation(Object fieldValue, String defaultValue) {
        if (fieldValue instanceof Map<?, ?>) {
            String lang = facesContext.getViewRoot().getLocale().getISO3Language().toUpperCase();
            Map<String, String> translationMap = (Map<String, String>) fieldValue;

            if (translationMap.isEmpty() || StringUtils.isBlank(translationMap.get(lang))) {
                return defaultValue;
            }

            return translationMap.get(lang);
        }
        return (String) fieldValue;
    }

    /**
     * Method to get Back link. Default view name is a list view in a format: entity's name + s;
     * 
     * @return Navigation view name
     */
    public String back() {
        if (backViewSave == null && backView != null) {
            // log.debug("backview parameter is " + backView.get());
            backViewSave = backView;
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
     * @return Next navigation view name as result of action execution
     */
    public String backAndEndConversation() {
        String outcome = back();
        endConversation();
        return outcome;
    }

    /**
     * Get a navigation view name that will display an entity creation page. By default is same as {@link #getEditViewName()}. Override this method if its view name does not fit.
     * 
     * @return Navigation view name
     */
    public String getNewViewName() {
        return getEditViewName();
    }

    /**
     * Get a navigation view name that will display an entity edit page. By default is in a format: entity's name + Detail;
     * 
     * @return Navigation view name
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
     * Get a navigation view name that will display an entity list page. Default view name is a list view in a format: entity's name + s;
     * 
     * @return List view name
     */
    protected String getListViewName() {
        String className = clazz.getSimpleName();
        StringBuilder sb = new StringBuilder(className);
        char[] dst = new char[1];
        sb.getChars(0, 1, dst, 0);
        sb.replace(0, 1, new String(dst).toLowerCase());
        if (className.endsWith("ay")) {
            sb.append("s");
        } else if (className.endsWith("y")) {
            sb.deleteCharAt(sb.length()-1);
            sb.append("ies");
        } else if (className.endsWith("s") || className.endsWith("x")) {
            sb.append("es");
        } else {
            sb.append("s");
        }

        return sb.toString();
    }

    /**
     * Get a custom ID parameter name that would match entity's name
     * 
     * @return Custom ID parameter name
     */
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
     * Delete Entity using it's ID. A message will be displayed in GUI if successful or was not able to delete because of constraints. In the later case request will be marked with
     * validation failed.
     * 
     * @param id Entity id to delete
     * @throws BusinessException business exception
     */
    @ActionMethod
    public void delete(Long id) throws BusinessException {

        deleteInternal(id, null, true);
    }

    /**
     * Delete Entity using it's ID. A message will be displayed in GUI if successful or was not able to delete because of constraints. In the later case request will be marked with
     * validation failed. Message display can be suppressed.
     * 
     * @param id Entity id to delete
     * @param code Entity's code - just for display in error messages
     * @param setOkMessages Shall success messages be set for display
     * @return True if entity was deleted. False if deletion failed because of constraint violation. In other exceptions, error is propagated up.
     * @throws BusinessException business exception
     */
    private boolean deleteInternal(Long id, String code, boolean setOkMessages) throws BusinessException {
        try {
            log.info("Deleting entity {} with id = {}", clazz.getName(), id);
            getPersistenceService().remove(id);

            if (setOkMessages) {
                messages.info(new BundleKey("messages", "delete.successful"));
            }

            return true;

        } catch (Exception e) {
            Throwable cause = e;
            while (cause != null) {

                if (cause instanceof org.hibernate.exception.ConstraintViolationException) {

                    String referencedBy = getPersistenceService().findReferencedByEntities(clazz, id);
                    log.info("Delete was unsuccessful because entity is used by other entities {}", referencedBy);

                    if (referencedBy != null) {
                        messages.error(new BundleKey("messages", "error.delete.entityUsedWDetails"), code == null ? "" : code, referencedBy);
                    } else {
                        messages.error(new BundleKey("messages", "error.delete.entityUsed"));
                    }
                    facesContext.validationFailed();
                    return false;
                }
                cause = cause.getCause();
            }

            throw e;
        }
    }

    /**
     * Delete a current entity. A message will be displayed in GUI if successful or was not able to delete because of constraints. In the later case request will be marked with
     * validation failed.
     * 
     * @throws BusinessException A general business exception
     */
    @ActionMethod
    public void delete() throws BusinessException {
        delete((Long) getEntity().getId());
    }

    /**
     * Delete checked entities in a list. A message will be displayed in GUI if successful or was not able to delete because of constraints. In the later case request will be
     * marked with validation failed.
     * 
     * @throws BusinessException A general business exception
     */
    @ActionMethod
    public void deleteMany() throws BusinessException {

        if (selectedEntities == null || selectedEntities.isEmpty()) {
            messages.info(new BundleKey("messages", "delete.entitities.noSelection"));
            return;
        }

        boolean allOk = true;
        for (IEntity entity : selectedEntities) {
            allOk = deleteInternal((Long) entity.getId(), entity instanceof BusinessEntity ? ((BusinessEntity) entity).getCode() : "", false) && allOk;
        }

        if (allOk) {
            messages.info(new BundleKey("messages", "delete.entitities.successful"));
        }
    }

    /**
     * Delete current entity from detail page and redirect to a previous page. Used mostly for deletion in detail pages.
     * 
     * @return Next navigation view name as calculated in {@link #back()} if deleted success, if not, return a NULL to remain in the same view page
     * @throws BusinessException A general business exception
     */
    @ActionMethod
    public String deleteWithBack() throws BusinessException {

        if (this.deleteInternal((Long) getEntity().getId(), null, true)) {
            return back();
        }
        return null;
    }

    /**
     * Gets search field map for searching in a list view
     * 
     * @return Filters map
     */
    public Map<String, Object> getFilters() {
        if (filters == null) {
            filters = new HashMap<>();
        }
        return filters;
    }

    /**
     * Clean search fields and reset datatable model
     */
    public void clean() {
        dataModel = null;
        filters = new HashMap<>();
        listFilter = null;
    }

    /**
     * Reset entity edit values to the last state.
     */
    public void resetFormEntity() {
        entity = null;
        entity = getEntity();
    }

    /**
     * Get new instance of entity class.
     * 
     * @return New entity instance
     * 
     * @throws IllegalAccessException Class instantiation exception
     * @throws InstantiationException Class instantiation exception
     */
    public T getInstance() throws InstantiationException, IllegalAccessException {

        return clazz.newInstance();
    }

    /**
     * Method that returns concrete PersistenceService for an entity class backing bean is bound to. That service is then used for operations on concrete entities (eg. save, delete
     * etc).
     * 
     * @return Persistence service
     */
    protected abstract IPersistenceService<T> getPersistenceService();

    /**
     * Override this method if you need to fetch any additional fields when retrieving a list of entities for a data table.
     * 
     * @return A list of field names that has to be fetched as part of list retrieval query
     */
    protected List<String> getListFieldsToFetch() {
        return null;
    }

    /**
     * Override this method if you need to fetch any additional fields when retrieving an entity to show it a edit form.
     * 
     * @return Return list of field names that has to be fetched as part of entity retrieval query
     */
    protected List<String> getFormFieldsToFetch() {
        return null;
    }

    /**
     * Disable current entity. A message will be displayed in GUI with action execution success.
     */
    @ActionMethod
    public void disable() {
        try {
        	Long currentId = (Long) entity.getId();
            log.info("Disabling entity {} with id = {}", clazz.getName(), currentId);
            setObjectId(currentId);
			entity = getPersistenceService().disable(currentId);
            messages.info(new BundleKey("messages", "disabled.successful"));

        } catch (Exception t) {
            log.info("unexpected exception when disabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Disable Entity using it's ID. A message will be displayed in GUI with action execution success.
     * 
     * @param id Entity id to disable
     */
    @ActionMethod
    public void disable(Long id) {
        try {
            log.info("Disabling entity {} with id = {}", clazz.getName(), id);
            getPersistenceService().disable(id);
            messages.info(new BundleKey("messages", "disabled.successful"));

        } catch (Throwable t) {
            log.info("unexpected exception when disabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Enable current entity. A message will be displayed in GUI with action execution success.
     */
    @ActionMethod
    public void enable() {
        try {
        	Long currentId = (Long) entity.getId();
            log.info("Enabling entity {} with id = {}", clazz.getName(), currentId);
            setObjectId(currentId);
            entity = getPersistenceService().enable(currentId);
            messages.info(new BundleKey("messages", "enabled.successful"));

        } catch (Exception t) {
            log.info("unexpected exception when enabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Enable Entity using it's ID. A message will be displayed in GUI with action execution success.
     * 
     * @param id Entity id to enable
     */
    @ActionMethod
    public void enable(Long id) {
        try {
            log.info("Enabling entity {} with id = {}", clazz.getName(), id);
            getPersistenceService().enable(id);
            messages.info(new BundleKey("messages", "enabled.successful"));

        } catch (Throwable t) {
            log.info("unexpected exception when enabling!", t);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }
    }

    /**
     * Get DataModel for primefaces lazy loading datatable component.
     * 
     * @return LazyDataModel implementation for Primefaces data list component
     */
    public LazyDataModel<T> getLazyDataModel() {
        return getLazyDataModel(filters, listFiltered);
    }

    /**
     * Get DataModel for primefaces lazy loading datatable component.
     * 
     * @param inputFilters Data search fields/values
     * @param forceReload True to disregard previous data and retrieve data again
     * @return LazyDataModel implementation for Primefaces data list component
     */
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
                    return getSearchCriteria(null);
                }

                @Override
                protected Map<String, Object> getSearchCriteria(Map<String, Object> customFilters) {
                    // Omit empty or null values
                    Map<String, Object> cleanFilters = new HashMap<>();

                    cleanupFilters(filters, cleanFilters);
                    cleanupFilters(MapUtils.emptyIfNull(customFilters), cleanFilters);

                    return BaseBean.this.supplementSearchCriteria(cleanFilters);
                }

                private void cleanupFilters(final Map<String, Object> filters, Map<String, Object> cleanFilters) {
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
     * Allows to overwrite, or add additional search criteria for filtering a list. Search criteria is a map with filter criteria name as a key and value as a value. Criteria name
     * consist of [&lt;condition&gt;]&lt;field name&gt; (e.g. "like firstName") where &lt;condition&gt; is a condition to apply to field value comparison and &lt;name&gt; is an
     * entity attribute name.
     * 
     * @param searchCriteria Search criteria - should be same as filters attribute
     * @return HashMap with filter criteria name as a key and value as a value
     */
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {
        return searchCriteria;
    }

    /**
     * Perform search when in list view
     */
    public void search() {
        filterCustomFieldSearchBean.buildFilterParameters(filters);
    }

    /**
     * Get a list of selected entities from a datatable when in list view
     * 
     * @return A list of selected entities
     */
    public List<T> getSelectedEntities() {
        return selectedEntities;
    }

    /**
     * A setter
     * 
     * @param selectedEntities A list of selected entities
     */
    public void setSelectedEntities(List<T> selectedEntities) {
        this.selectedEntities = selectedEntities;
    }

    /**
     * Get entity id as it was set from xhtml page as f:viewParam or manually from code
     * 
     * @return Entity id
     */
    public Long getObjectId() {
        return objectId;
    }

    /**
     * Set entity identifier. Set via f:viewParam from xhtml page or manually from code
     * 
     * @param objectId Entity Id
     */
    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    /**
     * Is view in edit mode
     * 
     * @return True if view in in edit mode
     */
    public boolean isEdit() {
        if (edit == null || org.meveo.commons.utils.StringUtils.isBlank(edit)) {
            return true;
        }

        return Boolean.valueOf(edit);
    }

    /**
     * Set object id to NULL
     */
    protected void clearObjectId() {
        objectId = null;
    }

    /**
     * Get a list of enabled languages
     * 
     * @return A list of enabled languages (trading languages)
     */
    public List<String> getProviderLanguages() {
        return tradingLanguageService.listLanguageCodes();
    }

    /**
     * Get a default provider's language code
     * 
     * @return Language code
     */
    public String getProviderLanguageCode() {
        if (appProvider.getLanguage() != null) {
            return appProvider.getLanguage().getLanguageCode();
        }
        return "";
    }

    /**
     * Get a default sort value for a list. Default value is ID field. If Filter is selected for data retrieval, sort will come from a filter definition
     * 
     * @return A sorting parameter for a list query
     */
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

    /**
     * Get a default sort order for a list. Default value is ASCENDING. If Filter is selected for data retrieval, sort order will come from a filter definition
     * 
     * @return Sort order
     */
    protected SortOrder getDefaultSortOrder() {
        if (listFilter != null && listFilter.getOrderCondition() != null) {
            if (listFilter.getOrderCondition().isAscending()) {
                return SortOrder.ASCENDING;
            }
        }

        return SortOrder.DESCENDING;
    }

    /**
     * Get back navigation view name as it was received as HTTP request parameter
     * 
     * @return Navigation view name
     */
    public String getBackView() {
        return backView;
    }

    /**
     * Get back navigation view name as it was overridden manually or copied from an original HTTP request parameter, to be preserved later for duration over ajax calls, and failed
     * form submissions,
     * 
     * @return Navigation view name
     */
    public String getBackViewSave() {
        return backViewSave;
    }

    /**
     * Override back navigation view name
     * 
     * @param backViewSave Navigation view name
     */
    public void setBackViewSave(String backViewSave) {
        this.backViewSave = backViewSave;
    }

    /**
     * Get index of the first data to display - see Primefaces data list component for more details
     * 
     * @return Index of the first data to display
     */
    public int getDataTableFirstAttribute() {
        return dataTableFirstAttribute;
    }

    /**
     * Set index of the first data to display - see Primefaces data list component for more details
     * 
     * @param dataTableFirstAttribute Index of the first data to display
     */
    public void setDataTableFirstAttribute(int dataTableFirstAttribute) {
        this.dataTableFirstAttribute = dataTableFirstAttribute;
    }

    /**
     * Handle data list page change event
     * 
     * @param event Page change event
     */
    public void onPageChange(PageEvent event) {
        this.setDataTableFirstAttribute(((DataTable) event.getSource()).getFirst());
    }

    /**
     * Get currently active locale
     * 
     * @return Currently active locale
     */
    public Locale getCurrentLocale() {
        return facesContext.getViewRoot().getLocale();
    }

    /**
     * Get CSV export options. See CSVExportOptions
     * 
     * @return CSV export options
     */
    public CSVExportOptions csvOptions() {
        ParamBean param = paramBeanFactory.getInstance();
        String characterEncoding = param.getProperty("csv.characterEncoding", "iso-8859-1");
        CSVExportOptions csvOption = new CSVExportOptions();
        csvOption.setSeparatorCharacter(';');
        csvOption.setCharacterEncoding(characterEncoding);
        return csvOption;
    }

    // Commented as no use was found neither in java nor xhtml
    // /**
    // * Dummy codes for avoiding to get custom field templates
    // *
    // * @return A list of custom field templates
    // */
    // public List<CustomFieldTemplate> getCustomFieldTemplates() {
    // return null;
    // }

    /**
     * Get Filter to apply in search
     * 
     * @return Filter to apply in search
     */
    public Filter getListFilter() {
        return listFilter;
    }

    /**
     * Set Filter to apply in search
     * 
     * @param listFilter Filter to apply in search
     */
    public void setListFilter(Filter listFilter) {
        this.listFilter = listFilter;
    }

    /**
     * Get a list of applicable Filters for a current entity type
     * 
     * @return A list of Filters
     */
    public List<Filter> getListFilters() {
        if (clazz != null) {
            return filterService.findByPrimaryTargetClass(clazz.getName());
        } else {
            return null;
        }
    }

    /**
     * Apply selected Filter to retrieve data for datalist component
     */
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

    /**
     * Is Filter currently applied?
     * 
     * @return True if search was done by Filter
     */
    public boolean isListFiltered() {
        return listFiltered;
    }

    /**
     * Get active secondary tab
     * 
     * @return Secondary tab position
     */
    public int getActiveTab() {
        return activeTab;
    }

    /**
     * Activate a given secondary tab
     * 
     * @param activeTab Secondary tab's position to activate
     */
    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    /**
     * Activate a given main tab
     * 
     * @param activeMainTab Main tab's position to activate
     */
    public void setActiveMainTab(int activeMainTab) {
        this.activeMainTab = activeMainTab;
    }

    /**
     * Get active main tab
     * 
     * @return Main tab position
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
     * A helper method to delete item from a collection of values. Used in conjunction with value list in formListField component.
     * 
     * @param values Collection of values
     * @param itemIndex An index of an item to remove
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
     * A helper method to change value in a collection. Collection to update an item index are passed as attributes. Used in conjunction with value list in formListField component.
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
     * A helper method to add a new blank item to collection. Instantiate a new item based on parameterized collection type. Used in conjunction with value list in formListField
     * component.
     * 
     * @param values A collection of values
     * @param itemClass Class of a new item
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addItemToCollection(Collection values, Class itemClass) {

        try {
            values.add(itemClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate a new item of {} class", itemClass.getName());
        }
    }

    /**
     * Get a list of only active entities (if applies)
     * 
     * @return A list of entities
     */
    public List<T> listActive() {
        Map<String, Object> filters = getFilters();
        filters.put("disabled", false);
        PaginationConfiguration config = new PaginationConfiguration(filters);

        return getPersistenceService().list(config);
    }

    /**
     * Determine if current user an modify a current entity
     * 
     * @return True if user has suficient permissions to modify a current entity
     */
    public boolean canUserUpdateEntity() {
        if (this.writeAccessMap == null) {
            writeAccessMap = Collections.synchronizedMap(new HashMap<String, Boolean>());
        }
        ExternalContext context = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        String requestURI = request.getRequestURI();

        if (writeAccessMap.get(requestURI) == null) {
            boolean hasWriteAccess = false;
            try {
                hasWriteAccess = PagePermission.getInstance().hasWriteAccess(request, currentUser);
            } catch (BusinessException e) {
                log.error("Error encountered checking for write access to {}", requestURI, e);
                hasWriteAccess = false;
            }
            writeAccessMap.put(requestURI, hasWriteAccess);
        }
        return writeAccessMap.get(requestURI);
    }

    /**
     * Is entity included in any of the modules
     * 
     * @return True if entity is included as part of any module
     */
    public String getPartOfModules() {
        return partOfModules;
    }

    /**
     * Set if entity included in any of the modules
     * 
     * @param partOfModules True if entity is included as part of any module
     */
    public void setPartOfModules(String partOfModules) {
        this.partOfModules = partOfModules;
    }

    /**
     * Get description or code if description is missing of a currently selected entity. Applies for BusinessEntity type entities only. In other cases NULL is returned.
     * 
     * @return Description or code value
     */
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

    /**
     * Handle image upload event. A GUI message will be displayed informing about upload success.
     * 
     * @param event Image upload event
     * @throws BusinessException General business exception
     */
    public void hfHandleFileUpload(FileUploadEvent event) throws BusinessException {
        uploadedFile = event.getFile();

        try {
            // When dealing with a BackingBean (BaseBean) with generic type T having an image field, and this Type T refers to another type ( or list of types ) E which also has an
            // image field
            // then keeping the ImageUploadEventHandler Type frozen to the Generic Type 'T' will cause an issue and leads to override the parent field image by the child one ...
            // That's why the IEntity is used instead of 'T' , to instantiate a flexible ImageUploadEventHandler
            ImageUploadEventHandler<IEntity> uploadHandler = new ImageUploadEventHandler<IEntity>(currentUser.getProviderCode());
            Object componentEntity = event.getComponent().getAttributes().get("componentEntity");

            IEntity currenttEntity = this.entity;
            if (componentEntity != null && componentEntity instanceof IEntity) {
                currenttEntity = (IEntity) componentEntity;
            }

            String filename = uploadHandler.handleImageUpload(currenttEntity, uploadedFile);
            if (filename != null) {
                ((IImageUpload) currenttEntity).setImagePath(filename);
                messages.info(new BundleKey("messages", "message.upload.succesful"));
            }
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "message.upload.fail"), e.getMessage());
        }
    }

    /**
     * Get uploaded file
     * 
     * @return Uploaded file
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * Set uploaded file
     * 
     * @param uploadedFile Uploaded file
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    /**
     * Find entities that reference a given class and ID. Used when deleting entities to determine what FK constraints are preventing to remove a given entity
     * 
     * @param Entity class to reference
     * @param id Entity ID
     * @return A concatenated list of entities (humanized classnames and their codes) E.g. Customer Account: first ca, second ca, third ca; Customer: first customer, second
     *         customer
     */
    @SuppressWarnings("rawtypes")
    private String findReferencedByEntities(Class<T> entityClass, Long id) {

        T referencedEntity = getPersistenceService().getEntityManager().getReference(entityClass, id);

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

                List entitiesMatched = getPersistenceService().getEntityManager().createQuery(sql.toString()).setParameter("id", referencedEntity).setMaxResults(10).getResultList();
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
     * @return the backEntityId
     */
    public String getBackEntityId() {
        return backEntityId;
    }

    /**
     * @return the backTab
     */
    public String getBackTab() {
        return backTab;
    }

    /**
     * @return the backMainTab
     */
    public String getBackMainTab() {
        return backMainTab;
    }

    /**
     * Validate that if two dates are provided, the From value is before the To value.
     * 
     * @param context Faces context
     * @param components Components being validated
     * @param values Values to validate
     * @return Is valid or not
     */
    public boolean validateDateRange(FacesContext context, List<UIInput> components, List<Object> values) {

        if (values.size() != 2) {
            throw new RuntimeException("Please bind validator to two components in the following order: dateFrom, dateTo");
        }
//        Date from = (Date) values.get(0);
//        Date to = (Date) values.get(1);
        Date from = null;
        Date to = null;

        if (values.get(0) != null) {
            if (values.get(0) instanceof String) {
                from = DateUtils.parseDateWithPattern((String) values.get(0), ParamBean.getInstance().getDateFormat());
            } else {
                from = (Date) values.get(0);
            }
        }
        if (values.get(1) != null) {
            if (values.get(1) instanceof String) {
                to = DateUtils.parseDateWithPattern((String) values.get(1), ParamBean.getInstance().getDateFormat());
            } else {
                to = (Date) values.get(1);
            }
        }

        // Check that two dates are one after another
        return !(from != null && to != null && from.compareTo(to) > 0);
    }
    
    public static void showDeprecatedWarning() {
    	List<FacesMessage> messageList = FacesContext.getCurrentInstance().getMessageList();
		if(messageList!=null && messageList.stream().anyMatch(x->FacesMessage.SEVERITY_WARN.equals(x.getSeverity())&& DEPRECATED_FEATURE.equals(x.getSummary()))) {
			return;
    	}
    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, DEPRECATED_FEATURE, DEPRECATED_FEATURE));
    }

}