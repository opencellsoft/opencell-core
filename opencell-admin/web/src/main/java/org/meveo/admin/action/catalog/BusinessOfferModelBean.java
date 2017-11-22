package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessProductModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class BusinessOfferModelBean extends GenericModuleBean<BusinessOfferModel> {

    private static final long serialVersionUID = 8222060379099238520L;

    @Inject
    private BusinessServiceModelService businessServiceModelService;

    @Inject
    private BusinessProductModelService businessProductModelService;

    @Inject
    protected BusinessOfferModelService businessOfferModelService;

    private Map<String, String> offerCFVs = new HashMap<>();
    private String serviceCodePrefix;
    private Map<String, String> serviceCFVs = new HashMap<>();
    private DualListModel<ServiceTemplate> serviceDualListModel;
    private String bomOfferInstancePrefix;

    public BusinessOfferModelBean() {
        super(BusinessOfferModel.class);
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

    public void onBOMOfferCreation(SelectEvent event) {
        messages.info(new BundleKey("messages", "message.bom.offerCreation.ok"));
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

    /**
     * Get a list of BSM and BPM modules linked to a BOM
     * 
     * @param bomEntity BOM entity
     * @return A list of BSM and BPM modules
     */
    public List<MeveoModule> getBusinessServiceAndProductModels(BusinessOfferModel bomEntity) {
        List<MeveoModule> result = new ArrayList<>();
        if (bomEntity != null && bomEntity.getModuleItems() != null) {
            for (MeveoModuleItem item : bomEntity.getModuleItems()) {
                if (item.getItemClass().equals(BusinessServiceModel.class.getName())) {
                    BusinessServiceModel bsm = businessServiceModelService.findByCode(item.getItemCode());
                    if (bsm != null) {
                        result.add(bsm);
                    } else {
                        log.warn("Can not find a BSM {} linked to BOM {}", item.getItemCode(), bomEntity.getCode());
                    }
                } else if (item.getItemClass().equals(BusinessProductModel.class.getName())) {
                    BusinessProductModel bpm = businessProductModelService.findByCode(item.getItemCode());
                    if (bpm != null) {
                        result.add(bpm);
                    } else {
                        log.warn("Can not find a BPM {} linked to BOM {}", item.getItemCode(), bomEntity.getCode());
                    }
                }
            }
        }

        return result;
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

    public String getOfferTemplateCodeFromModuleSource() {
        try {
            BusinessOfferModelDto dto = (BusinessOfferModelDto) MeveoModuleService.moduleSourceToDto(entity);
            return dto.getOfferTemplate().getCode();

        } catch (Exception e) {
            log.error("Failed to load module source {}", entity.getCode(), e);
            // throw new BusinessException("Failed to load module source", e);
        }
        return null;
    }
}