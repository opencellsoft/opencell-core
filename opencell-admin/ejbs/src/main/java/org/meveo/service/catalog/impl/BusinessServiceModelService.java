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

    public ServiceTemplate createServiceFromBSM(BusinessServiceModel bsm, String prefix, List<CustomFieldDto> customFields) throws BusinessException {
        if (bsm.getScript() != null) {
            try {
                serviceModelScriptService.beforeCreateServiceFromBSM(customFields, bsm.getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
            }
        }
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();
        ServiceTemplate newServiceTemplateCreated = catalogHierarchyBuilderService.duplicateServiceTemplate(bsm.getServiceTemplate().getCode(), prefix, null, pricePlansInMemory,
            chargeTemplateInMemory);

        if (bsm.getScript() != null) {
            try {
                serviceModelScriptService.afterCreateServiceFromBSM(newServiceTemplateCreated, customFields, bsm.getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", bsm.getScript().getCode(), e);
            }
        }
        return newServiceTemplateCreated;

    }
}