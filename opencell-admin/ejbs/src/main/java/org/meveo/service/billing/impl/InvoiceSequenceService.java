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