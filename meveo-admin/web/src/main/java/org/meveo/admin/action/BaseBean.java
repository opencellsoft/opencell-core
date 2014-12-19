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
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.Identity;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
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

	@PersistenceContext(unitName = "MeveoAdmin", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

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

	private boolean editSaved;

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

	/** Search filters. */
	protected Map<String, String> languageMessagesMap = new HashMap<String, String>();

	/**
	 * Datamodel for lazy dataloading in datatable.
	 */
	private LazyDataModel<T> dataModel;

	/**
	 * Bind datatable for search results.
	 */
	private DataTable dataTable;

	/**
	 * Selected Entities in multiselect datatable.
	 */
	private T[] selectedEntities;

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

	@PostConstruct
	private void init() {
		beginConversation();
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
				entity = (T) getPersistenceService().findById(getObjectId(),
						getFormFieldsToFetch());
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
				throw new IllegalStateException(
						"could not instantiate a class, abstract class");
			} catch (IllegalAccessException e) {
				log.error("Unexpected error!", e);
				throw new IllegalStateException(
						"could not instantiate a class, constructor not accessible");
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

	public String saveOrUpdate(boolean killConversation, String objectName,
			Long objectId) throws BusinessException {
		String outcome = saveOrUpdate(killConversation);

		if (killConversation) {
			endConversation();
		}

		// return objectId == null ? outcome : (outcome + "&" + objectName + "="
		// + objectId + "&cid=" + conversation.getId());
		return outcome;
	}

	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
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
			log.info(String.format("Deleting entity %s with id = %s",
					clazz.getName(), id));
			getPersistenceService().remove(id);
			messages.info(new BundleKey("messages", "delete.successful"));
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.info(
						"delete was unsuccessful because entity is used in the system",
						t);
				messages.error(new BundleKey("messages",
						"error.delete.entityUsed"));

			} else {
				log.info("unexpected exception when deleting!", t);
				messages.error(new BundleKey("messages",
						"error.delete.unexpected"));
			}
		}
	}

	public void delete() {
		try {
			log.info(String.format("Deleting entity %s with id = %s",
					clazz.getName(), getEntity().getId()));
			getPersistenceService().remove((Long) getEntity().getId());
			messages.info(new BundleKey("messages", "delete.successful"));
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.info(
						"delete was unsuccessful because entity is used in the system",
						t);
				messages.error(new BundleKey("messages",
						"error.delete.entityUsed"));

			} else {
				log.info("unexpected exception when deleting!", t);
				messages.error(new BundleKey("messages",
						"error.delete.unexpected"));
			}
		}
	}

	/**
	 * Delete checked entities. Add error message to {@link statusMessages} if
	 * unsuccessful.
	 */
	public void deleteMany() {
		try {
			if (selectedEntities != null && selectedEntities.length > 0) {
				Set<Long> idsToDelete = new HashSet<Long>();
				StringBuilder idsString = new StringBuilder();
				for (IEntity entity : selectedEntities) {
					idsToDelete.add((Long) entity.getId());
					idsString.append(entity.getId()).append(" ");
				}
				log.info(String.format(
						"Deleting multiple entities %s with ids = %s",
						clazz.getName(), idsString.toString()));

				getPersistenceService().remove(idsToDelete);
				messages.info(new BundleKey("messages",
						"delete.entitities.successful"));
			} else {
				messages.info(new BundleKey("messages",
						"delete.entitities.noSelection"));
			}
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.info(
						"delete was unsuccessful because entity is used in the system",
						t);
				messages.error(new BundleKey("messages",
						"error.delete.entityUsed"));

			} else {
				log.info("unexpected exception when deleting!", t);
				messages.error(new BundleKey("messages",
						"error.delete.unexpected"));
			}
		}
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
	public T getInstance() throws InstantiationException,
			IllegalAccessException {

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
			log.info(String.format("Disabling entity %s with id = %s",
					clazz.getName(), id));
			getPersistenceService().disable(id);
			messages.info(new BundleKey("messages", "disabled.successful"));

		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.info(
						"delete was unsuccessful because entity is used in the system",
						t);
				messages.error(new BundleKey("messages",
						"error.delete.entityUsed"));
			} else {
				log.info("unexpected exception when deleting!", t);
				messages.error(new BundleKey("messages",
						"error.delete.unexpected"));
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

	public LazyDataModel<T> getLazyDataModel(Map<String, Object> inputFilters,
			boolean forceReload) {
		if (dataModel == null || forceReload) {
			final Map<String, Object> filters = inputFilters;
			dataModel = new LazyDataModel<T>() {
				private static final long serialVersionUID = 1L;

				private Integer rowCount;

				private Integer rowIndex;

				@Override
				public List<T> load(int first, int pageSize, String sortField,
						SortOrder sortOrder, Map<String, String> loadingFilters) {

					if (!StringUtils.isBlank(getDefaultSort())
							&& StringUtils.isBlank(sortField)) {
						sortField = getDefaultSort();
					}

					Map<String, Object> copyOfFilters = new HashMap<String, Object>();
					copyOfFilters.putAll(filters);
					setRowCount((int) getPersistenceService().count(
							new PaginationConfiguration(first, pageSize,
									copyOfFilters, getListFieldsToFetch(),
									sortField, sortOrder)));
					if (getRowCount() > 0) {
						copyOfFilters = new HashMap<String, Object>();
						copyOfFilters.putAll(filters);
						return getPersistenceService().list(
								new PaginationConfiguration(first, pageSize,
										copyOfFilters, getListFieldsToFetch(),
										sortField, sortOrder));
					} else {
						return null; // no need to load then
					}
				}

				@Override
				public T getRowData(String rowKey) {
					return getPersistenceService().findById(
							Long.valueOf(rowKey));
				}

				@Override
				public Object getRowKey(T object) {
					return object.getId();
				}

				@Override
				public void setRowIndex(int rowIndex) {
					if (rowIndex == -1 || getPageSize() == 0) {
						this.rowIndex = rowIndex;
					} else {
						this.rowIndex = rowIndex % getPageSize();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				public T getRowData() {
					return ((List<T>) getWrappedData()).get(rowIndex);
				}

				@SuppressWarnings({ "unchecked" })
				@Override
				public boolean isRowAvailable() {
					if (getWrappedData() == null) {
						return false;
					}

					return rowIndex >= 0
							&& rowIndex < ((List<T>) getWrappedData()).size();
				}

				@Override
				public int getRowIndex() {
					return this.rowIndex;
				}

				@Override
				public void setRowCount(int rowCount) {
					this.rowCount = rowCount;
				}

				@Override
				public int getRowCount() {
					if (rowCount == null) {
						rowCount = (int) getPersistenceService().count();
					}
					return rowCount;
				}

			};
		}
		return dataModel;
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

	public T[] getSelectedEntities() {
		return selectedEntities;
	}

	public void setSelectedEntities(T[] selectedEntities) {
		this.selectedEntities = selectedEntities;
	}

	protected Long getObjectId() {
		if (objectIdFromParam != null && objectIdFromParam.get() != null) {
			objectIdFromSet = objectIdFromParam.get();
		}

		return objectIdFromSet;
	}

	public void setObjectId(Long objectId) {
		objectIdFromSet = objectId;
	}

	public boolean isEdit() {
		if (edit != null && edit.get() != null
				&& !edit.get().equals("" + editSaved)) {
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
		Provider provider = providerService.findById(currentProvider.getId(),
				true);
		return provider.getTradingLanguages();
	}

	public String getProviderLanguageCode() {
		if (getCurrentProvider() != null) {

			Provider provider = providerService.findById(
					currentProvider.getId(), true);
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
		return "";
	}

	public String getBackView() {
		return backView.get();
	}

}
