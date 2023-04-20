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

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 */
@SuppressWarnings("rawtypes")
@Stateless
public class ProductChargeTemplateMappingService extends PersistenceService<ProductChargeTemplateMapping> {

    @SuppressWarnings("unchecked")
    public ProductChargeTemplateMapping findByProductAndOfferTemplate(String productCode, String chargeTemplateCode) {
        QueryBuilder builder = new QueryBuilder(ProductChargeTemplateMapping.class, "ptm");
        builder.addCriterion("ptm.product.code", "=", productCode, false);
        if(chargeTemplateCode != null)
            builder.addCriterion("ptm.chargeTemplate.code", "=", chargeTemplateCode, false);
        
        var query = builder.getQuery(getEntityManager());
        var results = (List<ProductChargeTemplateMapping>) query.setMaxResults(1).getResultList();
        
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    public boolean checkExistenceByProductAndChargeAndCounterTemplate(String productCode, String chargeTemplateCode, String counterTemplateCode) {
        return (Long) getEntityManager().createQuery("SELECT COUNT(ptm.id) FROM ProductChargeTemplateMapping ptm" +
                        " WHERE ptm.product.code = :PRODUCT_CODE" +
                        " AND ptm.chargeTemplate.code = :CHARGE_CODE" +
                        " AND ptm.counterTemplate.code = :CT_CODE")
                .setParameter("CT_CODE", counterTemplateCode)
                .setParameter("PRODUCT_CODE", productCode)
                .setParameter("CHARGE_CODE", chargeTemplateCode)
                .getSingleResult() > 0;
    }
}
