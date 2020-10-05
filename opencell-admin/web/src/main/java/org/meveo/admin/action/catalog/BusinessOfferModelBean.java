/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.action.catalog;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.model.catalog.*;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.catalog.impl.BusinessProductModelService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        PrimeFaces.current().dialog().openDynamic("createOfferFromBOM", options, params);
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