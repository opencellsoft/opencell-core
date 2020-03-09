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

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Tax;
import org.meveo.service.base.BusinessService;

/**
 * Tax service implementation
 */
@Stateless
public class TaxService extends BusinessService<Tax> {

    /**
     * Get a number of Taxes not associated to any invoice subcategory
     * 
     * @return A number of Taxes
     */
    public int getNbTaxesNotAssociated() {
        return getEntityManager().createNamedQuery("Tax.getNbTaxesNotAssociated", Long.class).getSingleResult().intValue();
    }

    /**
     * Get a list of Taxes not associated to any invoice subcategory
     * 
     * @return A list of Tax entities
     */
    public List<Tax> getTaxesNotAssociated() {
        return getEntityManager().createNamedQuery("Tax.getTaxesNotAssociated", Tax.class).getResultList();
    }

    /**
     * Find a tax with ZERO percent
     * 
     * @return A tax with zero percent
     * @throws BusinessException When no tax with ZERO % was found.
     */
    public Tax getZeroTax() throws BusinessException {
        try {
            return getEntityManager().createNamedQuery("Tax.getZeroTax", Tax.class).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            throw new BusinessException("No tax defined with ZERO %");
        }
    }

    /**
     * Find a tax with a given percent
     * 
     * @param percent Percent to match
     * @return A tax with zero percent
     * @throws BusinessException When no tax with given % was found.
     */
    public Tax findTaxByPercent(BigDecimal percent) throws BusinessException {
        try {
            return getEntityManager().createNamedQuery("Tax.getTaxByPercent", Tax.class).setParameter("percent", percent).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            throw new BusinessException("No tax defined with " + percent.intValue() + " %");
        }
    }
    
    
    
    
}