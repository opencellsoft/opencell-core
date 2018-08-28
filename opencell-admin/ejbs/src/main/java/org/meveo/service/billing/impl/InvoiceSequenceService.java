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
package org.meveo.service.billing.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.ApplicationProvider;

/**
 * The Class InvoiceTypeService.
 *
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 * 
 */
@Stateless
public class InvoiceSequenceService extends BusinessService<InvoiceSequence> {

    /** The service singleton. */
    @EJB
    private ServiceSingleton serviceSingleton;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private ProviderService providerService;


    /**
     * Gets the max current invoice number.
     *
     * @param invoiceSequenceCode the invoice sequence code
     * @return the max current invoice number
     * @throws BusinessException the business exception
     */
    public Long getMaxCurrentInvoiceNumber(String invoiceSequenceCode) throws BusinessException {
        Long max = getEntityManager().createNamedQuery("InvoiceSequence.currentInvoiceNb", Long.class)

            .setParameter("invoiceSequenceCode", invoiceSequenceCode).getSingleResult();

        return max == null ? 0 : max;

    }

    /**
     * @return
     * @throws BusinessException
     */
    public Long getCurrentGlobalInvoiceBb() throws BusinessException {
        appProvider = providerService.findById(appProvider.getId());
        Long currentInvoiceNb = appProvider.getInvoiceConfiguration().getCurrentInvoiceNb();
        if (currentInvoiceNb == null) {
            currentInvoiceNb = 0L;
        }
        return currentInvoiceNb;
    }

    /**
     * @param currentInvoiceNb
     * @throws BusinessException
     */
    public void setCurrentGlobalInvoiceBb(Long currentInvoiceNb) throws BusinessException {
        try {

            appProvider = providerService.findById(appProvider.getId());

            appProvider.getInvoiceConfiguration().setCurrentInvoiceNb(currentInvoiceNb);

        } catch (Exception e) {
            throw new BusinessException("Cant update global InvoiceTypeSequence : " + e.getMessage());
        }
    }
}