package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class BusinessOfferBean extends BaseBean<BusinessOfferModel> {

	private static final long serialVersionUID = 8222060379099238520L;

	@Inject
	private BusinessOfferService businessOfferService;

	private Map<String, String> offerCFVs = new HashMap<>();
	private String serviceCodePrefix;
	private List<ServiceTemplate> serviceTemplatesToActivate;
	private Map<String, String> serviceCFVs = new HashMap<>();
	DualListModel<ServiceTemplate> dualListModel = new DualListModel<>();

	public BusinessOfferBean() {
		super(BusinessOfferModel.class);
	}

	@Override
	protected IPersistenceService<BusinessOfferModel> getPersistenceService() {
		return businessOfferService;
	}

	@Override
	protected String getListViewName() {
		return "businessOffers";
	}

	public void createOfferFromBOM() {
		System.out.println("test");
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("resizable", false);
		options.put("draggable", false);
		options.put("modal", true);
		options.put("width", 800);

		Map<String, List<String>> params = new HashMap<String, List<String>>();
		List<String> values = new ArrayList<String>();
		values.add(getEntity().getId().toString());
		params.put("objectId", values);

		RequestContext.getCurrentInstance().openDialog("createOfferFromBOM", options, params);
	}

	public DualListModel<ServiceTemplate> getServiceTemplates() {
		List<ServiceTemplate> targetList = new ArrayList<>();
//FIXME - for a proper dual list implementation see UserBean.get/setDualList and saveOrUpdate methods
		//dualListModel.setSource(entity.getOfferTemplate().getServiceTemplates());
		dualListModel.setTarget(targetList);

		return dualListModel;
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

	public List<ServiceTemplate> getServiceTemplatesToActivate() {
		return serviceTemplatesToActivate;
	}

	public void setServiceTemplatesToActivate(List<ServiceTemplate> serviceTemplatesToActivate) {
		this.serviceTemplatesToActivate = serviceTemplatesToActivate;
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

}
