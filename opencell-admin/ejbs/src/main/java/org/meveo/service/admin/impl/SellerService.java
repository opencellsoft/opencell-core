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
package org.meveo.service.admin.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.InvoiceTypeService;

@Stateless
public class SellerService extends BusinessService<Seller> {

    @Inject
    private InvoiceTypeService invoiceTypeService;

    public boolean hasChildren(Seller seller) {
        QueryBuilder qb = new QueryBuilder(Seller.class, "s");

        qb.addCriterionEntity("seller", seller);

        try {
            return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult()) > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public void create(Seller seller) throws BusinessException {
        log.info("start of create seller");
        super.create(seller);
        InvoiceType commType = invoiceTypeService.getDefaultCommertial();
        log.debug("InvoiceTypeCode for commercial bill :" + (commType == null ? null : commType.getCode()));
        InvoiceType adjType = invoiceTypeService.getDefaultAdjustement();
        log.debug("InvoiceTypeCode for adjustement bill :" + (adjType == null ? null : adjType.getCode()));
    }
}