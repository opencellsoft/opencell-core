package org.meveo.admin.action.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.ServiceBasedLazyDataModel;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.crm.AccountModelScript;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class BusinessAccountModelBean extends BaseBean<BusinessAccountModel> {

	private static final long serialVersionUID = -3508425903046756219L;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	private MeveoModuleService meveoModuleService;

	/**
	 * Module Section
	 */
	private CustomEntityTemplate customEntity;
	private CustomFieldTemplate customField;
	private Filter filter;
	private ScriptInstance script;
	private JobInstance job;
	private Notification notification;
	private MeveoModule meveoModule;
	private MeasurableQuantity measurableQuantity;
	private Chart chart;
	private TreeNode root;
	private List<AccountModelScript> accountModelScripts;

	@PostConstruct
	public void init() {
		root = new DefaultTreeNode("Root");
	}

	public BusinessAccountModelBean() {
		super(BusinessAccountModel.class);
	}

	@Override
	public BusinessAccountModel initEntity() {
		BusinessAccountModel module = super.initEntity();
		if (module.getModuleItems() == null) {
			return module;
		}

		List<MeveoModuleItem> itemsToRemove = new ArrayList<MeveoModuleItem>();

		for (MeveoModuleItem item : module.getModuleItems()) {

			// Load an entity related to a module item. If it was not been able
			// to load (e.g. was deleted), mark it to be deleted and delete
			meveoModuleService.loadModuleItem(item, getCurrentProvider());

			if (item.getItemEntity() == null) {
				itemsToRemove.add(item);
				continue;
			}

			TreeNode classNode = getOrCreateNodeByClass(item.getItemClass());
			new DefaultTreeNode("item", item, classNode);

		}

		return module;
	}

	@Override
	protected IPersistenceService<BusinessAccountModel> getPersistenceService() {
		return businessAccountModelService;
	}
	
	public void refreshScript() {
		accountModelScripts = null;
		entity = getPersistenceService().refreshOrRetrieve(entity);
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// check for duplicate
		if (entity.isTransient() && meveoModuleService.findByCode(entity.getCode(), getCurrentProvider()) != null) {
			messages.error(new BundleKey("messages", "javax.persistence.EntityExistsException"));
			return null;
		}

		super.saveOrUpdate(killConversation);

		return null;
	}

	public CustomEntityTemplate getCustomEntity() {
		return customEntity;
	}

	public void setCustomEntity(CustomEntityTemplate itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public CustomFieldTemplate getCustomField() {
		return customField;
	}

	public void setCustomField(CustomFieldTemplate itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public ScriptInstance getScript() {
		return script;
	}

	public void setScript(ScriptInstance itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public JobInstance getJob() {
		return job;
	}

	public void setJob(JobInstance itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public MeveoModule getMeveoModule() {
		return meveoModule;
	}

	public void setMeveoModule(MeveoModule itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public MeasurableQuantity getMeasurableQuantity() {
		return measurableQuantity;
	}

	public void setMeasurableQuantity(MeasurableQuantity itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart itemEntity) {
		if (itemEntity != null) {
			MeveoModuleItem item = new MeveoModuleItem(itemEntity);
			if (!entity.getModuleItems().contains(item)) {
				entity.addModuleItem(item);
				new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
			}
		}
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	private TreeNode getOrCreateNodeByClass(String classname) {
		for (TreeNode node : root.getChildren()) {
			if (classname.equals(node.getType())) {
				return node;
			}
		}

		TreeNode node = new DefaultTreeNode(classname, ReflectionUtils.getHumanClassName(classname), root);
		node.setExpanded(true);
		return node;
	}

	public void removeTreeNode(TreeNode node) {
		MeveoModuleItem item = (MeveoModuleItem) node.getData();
		TreeNode parent = node.getParent();
		parent.getChildren().remove(node);
		if (parent.getChildCount() == 0) {
			parent.getParent().getChildren().remove(parent);
		}
		entity.removeItem(item);
	}

	public LazyDataModel<MeveoModule> getSubModules() {
		HashMap<String, Object> filters = new HashMap<String, Object>();

		if (!getEntity().isTransient()) {
			filters.put("ne id", entity.getId());
		}

		final Map<String, Object> finalFilters = filters;

		LazyDataModel<MeveoModule> meveoModuleDataModel = new ServiceBasedLazyDataModel<MeveoModule>() {

			private static final long serialVersionUID = -8167681362884293170L;

			@Override
			protected IPersistenceService<MeveoModule> getPersistenceServiceImpl() {
				return meveoModuleService;
			}

			@Override
			protected Map<String, Object> getSearchCriteria() {

				// Omit empty or null values
				Map<String, Object> cleanFilters = new HashMap<String, Object>();

				for (Map.Entry<String, Object> filterEntry : finalFilters.entrySet()) {
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

				// cleanFilters.put(PersistenceService.SEARCH_CURRENT_USER,
				// getCurrentUser());
				cleanFilters.put(PersistenceService.SEARCH_CURRENT_PROVIDER, getCurrentProvider());
				return BusinessAccountModelBean.this.supplementSearchCriteria(cleanFilters);
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

		return meveoModuleDataModel;
	}

	protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {
		return searchCriteria;
	}

	public List<AccountModelScript> getAccountModelScripts() {
		if (accountModelScripts == null || accountModelScripts.size() == 0) {
			accountModelScripts = new ArrayList<>();
			if (!entity.isTransient()) {
				entity = getPersistenceService().refreshOrRetrieve(entity);
				if (entity.getScript() != null) {
					accountModelScripts.add(entity.getScript());
				}
			}
		}

		return accountModelScripts;
	}

	public void setAccountModelScripts(List<AccountModelScript> accountModelScripts) {
		this.accountModelScripts = accountModelScripts;
	}

}
