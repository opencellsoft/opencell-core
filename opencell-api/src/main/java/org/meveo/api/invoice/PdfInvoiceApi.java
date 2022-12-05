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

package org.meveo.api.invoice;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
public class PdfInvoiceApi extends BaseApi {

    @Inject
    ProviderService providerService;

    @Inject
    InvoiceService invoiceService;

    @Inject
    BillingAccountService billingAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    public byte[] getPDFInvoice(String invoiceNumber, String customerAccountCode) throws Exception {
        if (StringUtils.isBlank(invoiceNumber)) {
            missingParameters.add("invoiceNumber");
        }

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("CustomerAccountCode");
        }

        handleMissingParameters();
        
        
        Invoice invoice = new Invoice();

        
        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new BusinessException("Cannot find customer account with code=" + customerAccountCode);
        }
        invoice = invoiceService.getInvoice(invoiceNumber, customerAccount);

        return invoiceService.getInvoicePdf(invoice);
    }
}