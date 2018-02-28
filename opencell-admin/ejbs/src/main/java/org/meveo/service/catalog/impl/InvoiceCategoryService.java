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

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.service.base.BusinessService;

/**
 * InvoiceCategory service implementation.
 */
@Stateless
public class InvoiceCategoryService extends BusinessService<InvoiceCategory> {

    @Override
    public InvoiceCategory findByCode(String code) {
        if (code == null) {
            return null;
        }

        QueryBuilder qb = new QueryBuilder(InvoiceCategory.class, "c");
        qb.addCriterion("code", "=", code, false);

        try {
            return (InvoiceCategory) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public int getNbInvCatNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("invoiceCategory.getNbrInvoiceCatNotAssociated", Long.class).getSingleResult()).intValue();
    }

    public List<InvoiceCategory> getInvoiceCatNotAssociated() {
        return (List<InvoiceCategory>) getEntityManager().createNamedQuery("invoiceCategory.getInvoiceCatNotAssociated", InvoiceCategory.class).getResultList();
    }
}
