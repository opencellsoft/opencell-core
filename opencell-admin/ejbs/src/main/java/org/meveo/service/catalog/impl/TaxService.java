/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

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
}