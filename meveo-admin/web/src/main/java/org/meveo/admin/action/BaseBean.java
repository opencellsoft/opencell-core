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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.Identity;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.AccountEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Base bean class. Other seam backing beans extends this class if they need
 * functionality it provides.
 */
public abstract class BaseBean<T extends IEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Logger. */
	@Inject
	protected org.slf4j.Logger log;

	@Inject
	protected Messages messages;

	@Inject
	protected Identity identity;

	@Inject
	@CurrentProvider
	protected Provider currentProvider;

	@Inject
	ProviderService providerService;

	@Inject
	protected Conversation conversation;

	@Inject
	protected CustomFieldTemplateService customFieldTemplateService;

	@Inject
	protected CustomFieldInstanceService customFieldInstanceService;

	/** Search filters. */
	protected Map<String, Object> filters = new HashMap<String, Object>();

	/** Entity to edit/view. */
	protected T entity;

	/** Class of backing bean. */
	private Class<T> clazz;

	protected List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();

	/**
	 * Request parameter. Should form be displayed in create/edit or view mode
	 */
	@Inject
	@RequestParam()
	private Instance<String> edit;

	private boolean editSaved;

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

	/** Helper field to enter values for HashMap<String,String> type fields */
	protected Map<String, List<HashMap<String, String>>> mapTypeFieldValues = new HashMap<String, List<HashMap<String, String>>>();

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

	/**
	 * Constructor
	 */
	public BaseBean() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 *            Class.
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
	 * @param objectClass
	 *            Class of the object.
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

		return entity;
	}

	/**
	 * When opened to view or edit entity - this getter method returns it. In
	 * case entity is not loaded it will initialize it.
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
		String outcome = saveOrUpdate(entity);

		if (killConversation) {
			endConversation();
		}

		return outcome;
	}

	/**
	 * Save or update entity depending on if entity is transient.
	 * 
	 * @param entity
	 *            Entity to save.
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
	 * Lists all entities.
	 */
	public List<T> listAll() {
		return getPersistenceService().list();
	}

	/**
	 * Returns view after save() operation. By default it goes back to list
	 * view. Override if need different logic (for example return to one view
	 * for save and another for update operations)
	 */
	public String getViewAfterSave() {
		return getListViewName();
	}

	/**
	 * Method to get Back link. If default view name is different than override
	 * the method. Default name: entity's name + s;
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
	 * Go back and end conversation. BeforeRedirect flag is set to true, so
	 * conversation is first ended and then redirect is proceeded, that means
	 * that after redirect new conversation will have to be created (temp or
	 * long running) so that view will have all most up to date info because it
	 * will load everything from db when starting new conversation.
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
	 * Generating action name to get to entity creation page. Override this
	 * method if its view name does not fit.
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
	 * Delete Entity using it's ID. Add error message to {@link statusMessages}
	 * if unsuccessful.
	 * 
	 * @param id
	 *            Entity id to delete
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

		initEntity();
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

		initEntity();
	}

	/**
	 * Delete checked entities. Add error message to {@link statusMessages} if
	 * unsuccessful.
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
				log.info(String.format("Deleting multiple entities %s with ids = %s", clazz.getName(),
						idsString.toString()));

				getPersistenceService().remove(idsToDelete);
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

		initEntity();
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
	 * Method that returns concrete PersistenceService. That service is then
	 * used for operations on concrete entities (eg. save, delete etc).
	 * 
	 * @return Persistence service
	 */
	protected abstract IPersistenceService<T> getPersistenceService();

	/**
	 * Override this method if you need to fetch any fields when selecting list
	 * of entities in data table. Return list of field names that has to be
	 * fetched.
	 */
	protected List<String> getListFieldsToFetch() {
		return null;
	}

	/**
	 * Override this method if you need to fetch any fields when selecting one
	 * entity to show it a form. Return list of field names that has to be
	 * fetched.
	 */
	protected List<String> getFormFieldsToFetch() {
		return null;
	}

	/**
	 * Override this method when pop up with additional entity information is
	 * needed.
	 */
	protected String getPopupInfo() {
		return "No popup information. Override BaseBean.getPopupInfo() method.";
	}

	/**
	 * Delete Entity using it's ID. Add error message to {@link statusMessages}
	 * if unsuccessful.
	 * 
	 * @param id
	 *            Entity id to delete
	 */
	public void disable(Long id) {
		try {
			log.info(String.format("Disabling entity %s with id = %s", clazz.getName(), id));
			getPersistenceService().disable(id);
			messages.info(new BundleKey("messages", "disabled.successful"));

		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.info("delete was unsuccessful because entity is used in the system", t);
				messages.error(new BundleKey("messages", "error.delete.entityUsed"));
			} else {
				log.info("unexpected exception when deleting!", t);
				messages.error(new BundleKey("messages", "error.delete.unexpected"));
			}
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

	public Map<String, List<HashMap<String, String>>> getMapTypeFieldValues() {
		return mapTypeFieldValues;
	}

	public void setMapTypeFieldValues(Map<String, List<HashMap<String, String>>> mapTypeFieldValues) {
		this.mapTypeFieldValues = mapTypeFieldValues;
	}

	/**
	 * Allows to overwrite, or add additional search criteria for filtering a
	 * list. Search criteria is a map with filter criteria name as a key and
	 * value as a value. <br/>
	 * Criteria name consist of [<condition> ]<field name> (e.g.
	 * "like firstName") where <condition> is a condition to apply to field
	 * value comparison and <field name> is an entit attribute name.
	 * 
	 * @param searchCriteria
	 *            Search criteria - should be same as filters attribute
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

	public boolean isEdit() {
		if (edit != null && edit.get() != null && !edit.get().equals("" + editSaved)) {
			editSaved = Boolean.valueOf(edit.get());
		}
		return editSaved;
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
		Provider provider = providerService.findById(currentProvider.getId(), true);
		return provider.getTradingLanguages();
	}

	public String getProviderLanguageCode() {
		if (getCurrentProvider() != null) {

			Provider provider = providerService.findById(currentProvider.getId(), true);
			return provider.getLanguage().getLanguageCode();
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

	/**
	 * Remove a value from a map type field attribute used to gather field
	 * values in GUI
	 * 
	 * @param fieldName
	 *            Field name
	 * @param valueInfo
	 *            Value to remove
	 */
	public void removeMapTypeFieldValue(String fieldName, Map<String, String> valueInfo) {
		mapTypeFieldValues.get(fieldName).remove(valueInfo);
	}

	/**
	 * Add a value to a map type field attribute used to gather field values in
	 * GUI
	 * 
	 * @param fieldName
	 *            Field name
	 */
	public void addMapTypeFieldValue(String fieldName) {
		if (!mapTypeFieldValues.containsKey(fieldName)) {
			mapTypeFieldValues.put(fieldName, new ArrayList<HashMap<String, String>>());
		}
		mapTypeFieldValues.get(fieldName).add(new HashMap<String, String>());
	}

	/**
	 * Extract values from a Map type field in an entity to mapTypeFieldValues
	 * attribute used to gather field values in GUI
	 * 
	 * @param entityField
	 *            Entity field
	 * @param fieldName
	 *            Field name
	 */
	public void extractMapTypeFieldFromEntity(Map<String, String> entityField, String fieldName) {

		mapTypeFieldValues.remove(fieldName);

		if (entityField != null) {
			List<HashMap<String, String>> fieldValues = new ArrayList<HashMap<String, String>>();
			mapTypeFieldValues.put(fieldName, fieldValues);
			for (Entry<String, String> setInfo : entityField.entrySet()) {
				HashMap<String, String> value = new HashMap<String, String>();
				value.put("key", setInfo.getKey());
				value.put("value", setInfo.getValue());
				fieldValues.add(value);
			}
		}
	}

	/**
	 * Update Map type field in an entity from mapTypeFieldValues attribute used
	 * to gather field values in GUI
	 * 
	 * @param entityField
	 *            Entity field
	 * @param fieldName
	 *            Field name
	 */
	public void updateMapTypeFieldInEntity(Map<String, String> entityField, String fieldName) {
		entityField.clear();

		if (mapTypeFieldValues.get(fieldName) != null) {
			for (HashMap<String, String> valueInfo : mapTypeFieldValues.get(fieldName)) {
				if (valueInfo.get("key") != null && !valueInfo.get("key").isEmpty()) {
					entityField.put(valueInfo.get("key"), valueInfo.get("value") == null ? "" : valueInfo.get("value"));
				}
			}
		}
	}

	protected void initCustomFields(AccountLevelEnum accountLevel) {
		customFieldTemplates = customFieldTemplateService.findByAccountLevel(accountLevel);
		if (customFieldTemplates != null && customFieldTemplates.size() > 0 && !getEntity().isTransient()) {
			for (CustomFieldTemplate cf : customFieldTemplates) {
				CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(), getEntity());
				if (cfi != null) {
					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						cf.setDateValue(cfi.getDateValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						cf.setDoubleValue(cfi.getDoubleValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						cf.setLongValue(cfi.getLongValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING) {
						cf.setStringValue(cfi.getStringValue());
					}
				}
			}
		}
	}

	protected void saveCustomFields() {
		if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
			for (CustomFieldTemplate cf : customFieldTemplates) {
				CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(), getEntity());
				if (cfi != null) {
					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						cfi.setDateValue(cf.getDateValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						cfi.setDoubleValue(cf.getDoubleValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						cfi.setLongValue(cf.getLongValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING) {
						cfi.setStringValue(cf.getStringValue());
					}
				} else {
					// create
					cfi = new CustomFieldInstance();
					cfi.setCode(cf.getCode());
					IEntity entity = getEntity();
					if (entity instanceof AccountEntity) {
						cfi.setAccount((AccountEntity) getEntity());
					} else if (entity instanceof Subscription) {
						cfi.setSubscription((Subscription) entity);
					}

					if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						cfi.setDateValue(cf.getDateValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
						cfi.setDoubleValue(cf.getDoubleValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
						cfi.setLongValue(cf.getLongValue());
					} else if (cf.getFieldType() == CustomFieldTypeEnum.STRING) {
						cfi.setStringValue(cf.getStringValue());
					}

					customFieldInstanceService.create(cfi, getCurrentUser(), getCurrentProvider());
				}
			}
		}
	}

	public List<CustomFieldTemplate> getCustomFieldTemplates() {
		return customFieldTemplates;
	}

	public void setCustomFieldTemplates(List<CustomFieldTemplate> customFieldTemplates) {
		this.customFieldTemplates = customFieldTemplates;
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

}
