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

package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.admin.impl.GenericModuleService;
import org.meveo.service.script.service.ServiceModelScriptService;
import org.meveo.service.script.service.ServiceScriptInterface;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceModelService extends GenericModuleService<BusinessServiceModel> {

    @Inject
    private CatalogHierarchyBuilderService catalogHierarchyBuilderService;

    @Inject
    private ServiceModelScriptService serviceModelScriptService;

    public BusinessServiceModel findByBSMAndServiceTemplate(String bsm, String st) {
        QueryBuilder qb = new QueryBuilder(BusinessServiceModel.class, "b");
        qb.addCriterion("b.code", "=", bsm, true);
        qb.addCriterion("b.serviceTemplate.code", "=", st, true);

        try {
            return (BusinessServiceModel) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ServiceTemplate instantiateBSM(BusinessServiceModel bsm, String prefix, List<CustomFieldDto> customFields) throws BusinessException {
        
        ServiceScriptInterface serviceScipt = null;
        if (bsm.getScript() != null) {
            try {
                serviceScipt = serviceModelScriptService.beforeCreateServiceFromBSM(customFields, bsm.getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
            }
        }
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
        ServiceTemplate newServiceTemplateCreated = catalogHierarchyBuilderService.duplicateServiceTemplate(bsm.getServiceTemplate().getCode(), prefix, null, pricePlansInMemory,
            chargeTemplateInMemory);

        if (serviceScipt != null) {
            try {
                serviceModelScriptService.afterCreateServiceFromBSM(newServiceTemplateCreated, customFields, serviceScipt);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
            }
        }
        return newServiceTemplateCreated;

    }

    @SuppressWarnings("unchecked")
    public List<BusinessServiceModel> listByServiceTemplate(ServiceTemplate serviceTemplate) {
        QueryBuilder qb = new QueryBuilder(BusinessServiceModel.class, "t");
        qb.addCriterionEntity("serviceTemplate", serviceTemplate);

        List<BusinessServiceModel> result = (List<BusinessServiceModel>) qb.getQuery(getEntityManager()).getResultList();
        return (result == null || result.isEmpty()) ? null : result;
    }
    
    /**
     * Returns the count of all installed BSM
     * @param serviceTemplate serviceTemplate of BSM
     * @return count of install BSM
     */
    public Long countByServiceTemplate(ServiceTemplate serviceTemplate) {
        QueryBuilder qb = new QueryBuilder(BusinessServiceModel.class, "t");
        qb.addCriterionEntity("serviceTemplate", serviceTemplate);
        qb.addCriterion("installed", "=", true, true);

        return qb.count(getEntityManager());
    }
}