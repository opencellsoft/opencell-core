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
import org.meveo.admin.action.admin.CurrentUser;
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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.filter.FilterService;
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
    @CurrentUser
    protected User currentUser;
    
    @Inject
    protected Conversation conversation;

    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private CatMessagesService catMessagesService;
    
    @Inject
    private FilterService filterService;
    
    @Inject
    private ProviderService providerService;
    
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
    
    private boolean listFiltered = false;
    
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
                    ((BaseEntity) entity).setProvider(providerService.refreshOrRetrieve(currentProvider));
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

        String message = entity.isTransient()?"save.successful":"update.successful";
        
        if (!isMultilanguageEntity()) {
            entity = saveOrUpdate(entity);

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

                entity = saveOrUpdate(entity);

            } else {
                entity = saveOrUpdate(entity);

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

        messages.info(new BundleKey("messages", message));
        return back();
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
    protected T saveOrUpdate(T entity) throws BusinessException {
        if (entity.isTransient()) {
            getPersistenceService().create(entity);

        } else {
            entity = getPersistenceService().update(entity);
        }

        objectIdFromSet = (Long) entity.getId();

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

//                    cleanFilters.put(PersistenceService.SEARCH_CURRENT_USER, getCurrentUser());
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
        return currentUser; 
        
        // return ((MeveoUser) identity.getUser()).getUser();
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
			filters = new HashMap<String, Object>();

			filters.put("$FILTER", listFilter);

			listFiltered = true;
		} else {
			filters.remove("$FILTER");
		}
	}    
}