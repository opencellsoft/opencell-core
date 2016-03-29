package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.omnifaces.cdi.ViewScoped;

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

	public BusinessServiceModelBean() {
		super(BusinessServiceModel.class);
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

}
