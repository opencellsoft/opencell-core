package org.meveo.admin.action.catalog;

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
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

@Named
@ViewScoped
public class BusinessServiceModelBean extends BaseBean<BusinessServiceModel> {

	private static final long serialVersionUID = 8222060379099238520L;

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	@Inject
	private MeveoModuleService meveoModuleService;

	private BusinessOfferModel businessOfferModel;

	private List<ServiceModelScript> serviceModelScripts;

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

	@PostConstruct
	public void init() {
		root = new DefaultTreeNode("Root");
	}

	public BusinessServiceModelBean() {
		super(BusinessServiceModel.class);
	}

	@Override
	public BusinessServiceModel initEntity() {
		BusinessServiceModel module = super.initEntity();
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
	protected IPersistenceService<BusinessServiceModel> getPersistenceService() {
		return businessServiceModelService;
	}

	@Override
	protected String getListViewName() {
		return "businessServiceModels";
	}

	public void refreshScript() {
		serviceModelScripts = null;
		entity = getPersistenceService().refreshOrRetrieve(entity);
	}

	public List<BusinessOfferModel> getBusinessOfferModels(BusinessServiceModel bsmEntity) {
		List<BusinessOfferModel> result = new ArrayList<>();

		if (bsmEntity != null) {
			List<MeveoModuleItem> meveoModuleItems = meveoModuleService.findByCodeAndItemType(bsmEntity.getCode(), BusinessServiceModel.class.getName());
			if (meveoModuleItems != null) {
				for (MeveoModuleItem meveoModuleItem : meveoModuleItems) {
					MeveoModule meveoModule = meveoModuleItem.getMeveoModule();
					result.add(businessOfferModelService.findByCode(meveoModule.getCode(), currentUser.getProvider()));
				}
			}
		}

		return result;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if (entity.isTransient() && meveoModuleService.findByCode(entity.getCode(), getCurrentProvider()) != null) {
			messages.error(new BundleKey("messages", "javax.persistence.EntityExistsException"));
			return null;
		}

		super.saveOrUpdate(killConversation);

		return null;
	}

	public BusinessOfferModel getBusinessOfferModel() {
		return businessOfferModel;
	}

	public void setBusinessOfferModel(BusinessOfferModel businessOfferModel) {
		this.businessOfferModel = businessOfferModel;
	}

	public List<ServiceModelScript> getServiceModelScripts() {
		if (serviceModelScripts == null || serviceModelScripts.size() == 0) {
			serviceModelScripts = new ArrayList<>();
			if (!entity.isTransient()) {
				entity = getPersistenceService().refreshOrRetrieve(entity);
				if (entity.getScript() != null) {
					serviceModelScripts.add(entity.getScript());
				}
			}
		}
		return serviceModelScripts;
	}

	public void setServiceModelScripts(List<ServiceModelScript> serviceModelScripts) {
		this.serviceModelScripts = serviceModelScripts;
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
				return BusinessServiceModelBean.this.supplementSearchCriteria(cleanFilters);
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

}
