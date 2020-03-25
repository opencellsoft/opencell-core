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
