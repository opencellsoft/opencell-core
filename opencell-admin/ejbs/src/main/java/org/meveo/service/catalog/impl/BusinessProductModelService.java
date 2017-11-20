package org.meveo.service.catalog.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.admin.impl.GenericModuleService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class BusinessProductModelService extends GenericModuleService<BusinessProductModel> {

    @Inject
    private ProductTemplateService productTemplateService;

    @Inject
    private CatalogHierarchyBuilderService catalogHierarchyBuilderService;

    public BusinessServiceModel findByBPMAndProductTemplate(String bsm, String productTemplateCode) {
        QueryBuilder qb = new QueryBuilder(BusinessProductModel.class, "b");
        qb.addCriterion("b.code", "=", bsm, true);
        qb.addCriterion("b.productTemplate.code", "=", productTemplateCode, true);

        try {
            return (BusinessServiceModel) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void instantiateFromBPM(ProductTemplate entity, BusinessProductModel bpm) throws BusinessException {
        // 1 - create the productTemplate
        productTemplateService.create(entity);

        String prefix = entity.getId() + "_";

        // 2 - instantiate
        List<PricePlanMatrix> pricePlansInMemory = new ArrayList<>();
        List<ChargeTemplate> chargeTemplateInMemory = new ArrayList<>();

        bpm = refreshOrRetrieve(bpm);
        catalogHierarchyBuilderService.duplicateProductPrices(bpm.getProductTemplate(), prefix, pricePlansInMemory, chargeTemplateInMemory);
        try {
            catalogHierarchyBuilderService.duplicateProductCharges(bpm.getProductTemplate(), entity, prefix, chargeTemplateInMemory);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(e.getMessage());
        }
    }

}
