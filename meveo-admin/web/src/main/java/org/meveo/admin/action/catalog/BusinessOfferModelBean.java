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
import org.meveo.api.dto.catalog.ServiceCodeDto;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

@Named
@ViewScoped
public class BusinessOfferModelBean extends BaseBean<BusinessOfferModel> {

	private static final long serialVersionUID = 8222060379099238520L;

	@Inject
	private BusinessServiceModelService businessServiceModelService;

	@Inject
	private BusinessOfferModelService businessOfferModelService;

	@Inject
	private MeveoModuleService meveoModuleService;

	private Map<String, String> offerCFVs = new HashMap<>();
	private String serviceCodePrefix;
	private Map<String, String> serviceCFVs = new HashMap<>();
	private DualListModel<ServiceTemplate> serviceDualListModel;
	private String bomOfferInstancePrefix;
	private List<OfferModelScript> offerModelScripts;

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

	public BusinessOfferModelBean() {
		super(BusinessOfferModel.class);
	}

	@Override
	public BusinessOfferModel initEntity() {
		BusinessOfferModel module = super.initEntity();
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
	protected IPersistenceService<BusinessOfferModel> getPersistenceService() {
		return businessOfferModelService;
	}

	@Override
	protected String getListViewName() {
		return "businessOfferModels";
	}

	public void createOfferFromBOMPopup() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("resizable", false);
		options.put("draggable", false);
		options.put("scrollable", false);
		options.put("modal", true);
		options.put("width", 700);
		options.put("height", 400);

		Map<String, List<String>> params = new HashMap<String, List<String>>();
		List<String> values = new ArrayList<String>();
		values.add(getEntity().getId().toString());
		params.put("objectId", values);

		RequestContext.getCurrentInstance().openDialog("createOfferFromBOM", options, params);
	}

	public void createOfferFromBOM() throws BusinessException {
		List<ServiceCodeDto> serviceCodeDtos = new ArrayList<>();
		for (ServiceTemplate st : serviceDualListModel.getTarget()) {
			ServiceCodeDto serviceCodeDto = new ServiceCodeDto();
			serviceCodeDto.setCode(st.getCode());
			serviceCodeDtos.add(serviceCodeDto);
		}

		businessOfferModelService.createOfferFromBOM(getEntity(), bomOfferInstancePrefix, "", serviceCodeDtos, currentUser);
		RequestContext.getCurrentInstance().closeDialog(getEntity());
	}

	public void onBOMOfferCreation(SelectEvent event) {
		messages.info(new BundleKey("messages", "message.bom.offerCreation.ok"));
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

	public DualListModel<ServiceTemplate> getServiceDualListModel() {
		if (serviceDualListModel == null) {
			List<ServiceTemplate> perksSource = null;
			List<ServiceTemplate> perksTarget = new ArrayList<>();
			if (getEntity() != null) {
				List<ServiceTemplate> serviceTemplates = new ArrayList<>();
				for (OfferServiceTemplate ost : entity.getOfferTemplate().getOfferServiceTemplates()) {
					if (ost.getServiceTemplate() != null) {
						if (ost.isMandatory()) {
							perksTarget.add(ost.getServiceTemplate());
						} else {
							serviceTemplates.add(ost.getServiceTemplate());
						}
					}
				}
				perksSource = serviceTemplates;
			}

			serviceDualListModel = new DualListModel<ServiceTemplate>(perksSource, perksTarget);
		}

		return serviceDualListModel;
	}

	public List<ServiceTemplate> getBomServices() {
		List<ServiceTemplate> perksSource = null;
		if (getEntity() != null) {
			List<ServiceTemplate> serviceTemplates = new ArrayList<>();
			if (entity.getOfferTemplate() != null) {
				for (OfferServiceTemplate ost : entity.getOfferTemplate().getOfferServiceTemplates()) {
					if (ost.getServiceTemplate() != null) {
						serviceTemplates.add(ost.getServiceTemplate());
					}
				}
			}
			perksSource = serviceTemplates;
		}

		return perksSource;
	}

	public List<BusinessServiceModel> getBusinessServiceModels(BusinessOfferModel bomEntity) {
		List<BusinessServiceModel> result = new ArrayList<>();
		if (bomEntity != null && bomEntity.getModuleItems() != null) {
			for (MeveoModuleItem item : bomEntity.getModuleItems()) {
				if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
					result.add(businessServiceModelService.findByCode(item.getItemCode(), currentUser.getProvider()));
				}
			}
		}

		return result;
	}

	public void refreshScript() {
		offerModelScripts = null;
		entity = getPersistenceService().refreshOrRetrieve(entity);
	}

	public void setServiceDualListModel(DualListModel<ServiceTemplate> stDM) {
		serviceDualListModel = stDM;
	}

	public void onCreateOfferFromBOM(SelectEvent event) {
		System.out.println("test");
	}

	public String getServiceCodePrefix() {
		return serviceCodePrefix;
	}

	public void setServiceCodePrefix(String serviceCodePrefix) {
		this.serviceCodePrefix = serviceCodePrefix;
	}

	public Map<String, String> getServiceCFVs() {
		return serviceCFVs;
	}

	public void setServiceCFVs(Map<String, String> serviceCFVs) {
		this.serviceCFVs = serviceCFVs;
	}

	public Map<String, String> getOfferCFVs() {
		return offerCFVs;
	}

	public void setOfferCFVs(Map<String, String> offerCFVs) {
		this.offerCFVs = offerCFVs;
	}

	public String getBomOfferInstancePrefix() {
		return bomOfferInstancePrefix;
	}

	public void setBomOfferInstancePrefix(String bomOfferInstancePrefix) {
		this.bomOfferInstancePrefix = bomOfferInstancePrefix;
	}

	public List<OfferModelScript> getOfferModelScripts() {
		if (offerModelScripts == null || offerModelScripts.size() == 0) {
			offerModelScripts = new ArrayList<>();
			if (!entity.isTransient()) {
				entity = getPersistenceService().refreshOrRetrieve(entity);
				if (entity.getScript() != null) {
					offerModelScripts.add(entity.getScript());
				}
			}
		}
		return offerModelScripts;
	}

	public void setOfferModelScripts(List<OfferModelScript> offerModelScripts) {
		this.offerModelScripts = offerModelScripts;
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
				return BusinessOfferModelBean.this.supplementSearchCriteria(cleanFilters);
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
