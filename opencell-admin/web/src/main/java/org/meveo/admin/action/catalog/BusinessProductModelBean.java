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

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessProductModelService;

/**
 * @author Edward P. Legaspi
 */
@Named

public class BusinessProductModelBean extends GenericModuleBean<BusinessProductModel> {

    private static final long serialVersionUID = -1731438369618574084L;

    @Inject
    protected BusinessProductModelService businessProductModelService;

    public BusinessProductModelBean() {
        super(BusinessProductModel.class);
    }

    @Override
    protected IPersistenceService<BusinessProductModel> getPersistenceService() {
        return businessProductModelService;
    }

    public List<BusinessOfferModel> getBusinessOfferModels(BusinessProductModel bpmEntity) {
        List<BusinessOfferModel> result = new ArrayList<>();

        if (bpmEntity != null) {
            List<MeveoModuleItem> meveoModuleItems = meveoModuleService.findByCodeAndItemType(bpmEntity.getCode(), BusinessProductModel.class.getName());
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
    
    public String getProductTemplateCodeFromModuleSource() {
        try {
            BusinessProductModelDto dto = (BusinessProductModelDto) MeveoModuleService.moduleSourceToDto(entity);
            return dto.getProductTemplate().getCode();

        } catch (Exception e) {
            log.error("Failed to load module source {}", entity.getCode(), e);
        }
        
        return null;
    }
}