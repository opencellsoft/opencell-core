package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;

@Named
@ViewScoped
public class BusinessServiceModelBean extends GenericModuleBean<BusinessServiceModel> {

    private static final long serialVersionUID = 8222060379099238520L;

    @Inject
    private BusinessServiceModelService businessServiceModelService;

    private BusinessOfferModel businessOfferModel;

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

    public List<BusinessOfferModel> getBusinessOfferModels(BusinessServiceModel bsmEntity) {
        List<BusinessOfferModel> result = new ArrayList<>();

        if (bsmEntity != null) {
            List<MeveoModuleItem> meveoModuleItems = meveoModuleService.findByCodeAndItemType(bsmEntity.getCode(), BusinessServiceModel.class.getName());
            if (meveoModuleItems != null) {
                for (MeveoModuleItem meveoModuleItem : meveoModuleItems) {
                    MeveoModule meveoModule = meveoModuleItem.getMeveoModule();
                    if (meveoModule instanceof BusinessOfferModel) {
                        result.add((BusinessOfferModel) meveoModule);
                    }
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

    public String getServiceTemplateCodeFromModuleSource() {
        try {
            BusinessServiceModelDto dto = (BusinessServiceModelDto) MeveoModuleService.moduleSourceToDto(entity);
            return dto.getServiceTemplate().getCode();

        } catch (Exception e) {
            log.error("Failed to load module source {}", entity.getCode(), e);
            // throw new BusinessException("Failed to load module source", e);
        }
        return null;
    }
}