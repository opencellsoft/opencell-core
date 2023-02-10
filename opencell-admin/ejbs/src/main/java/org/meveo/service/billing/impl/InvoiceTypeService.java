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
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.OCCTemplateService;

import java.util.Arrays;
import java.util.List;

/**
 * The Class InvoiceTypeService.
 *
 * @author anasseh
 * @author Phung tien lan
 * @lastModifiedVersion 10.0
 * 
 */
@Stateless
public class InvoiceTypeService extends BusinessService<InvoiceType> {

    /** The service singleton. */
    @EJB
    private ServiceSingleton serviceSingleton;

    /** The o CC template service. */
    @Inject
    OCCTemplateService oCCTemplateService;

    @Inject
    private ProviderService providerService;
    
    public final static String DEFAULT_ADVANCE_CODE = "ADV";

    /**
     * Gets the default type.
     *
     * @param invoiceTypeCode the invoice type code
     * @return the default type
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultType(String invoiceTypeCode) throws BusinessException {

        InvoiceType defaultInvoiceType = findByCode(invoiceTypeCode);
        if (defaultInvoiceType != null) {
            return defaultInvoiceType;
        }

        String occCode = "accountOperationsGenerationJob.occCode";
        String occCodeDefaultValue = "INV_STD";
        OperationCategoryEnum operationCategory = OperationCategoryEnum.DEBIT;
        if (getListAdjustementCode().contains(invoiceTypeCode)) {
            occCode = "accountOperationsGenerationJob.occCodeAdjustement";
            occCodeDefaultValue = "INV_CRN";
            operationCategory = OperationCategoryEnum.CREDIT;
        }

        defaultInvoiceType = serviceSingleton.createInvoiceType(occCode, occCodeDefaultValue, invoiceTypeCode, operationCategory);

        return defaultInvoiceType;
    }

    /**
     * Gets the default adjustement.
     *
     * @return the default adjustement
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultAdjustement() throws BusinessException {
        return getDefaultType(getListAdjustementCode().get(0));
    }

    /**
     * Gets the default commertial.
     *
     * @return the default commertial
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultCommertial() throws BusinessException {
        return getDefaultType(getCommercialCode());
    }

    /**
     * Gets the default quote.
     *
     * @return the default quote
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultQuote() throws BusinessException {
        return getDefaultType(getQuoteCode());
    }

    /**
     * Gets the default deposit invoice.
     *
     * @return the default deposit
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultDeposit() throws BusinessException {
        return getDefaultType(getDepositCode());
    }
    
    /**
     * Gets the default draft.
     *
     * @return the default prepaid
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultPrepaid() throws BusinessException {
        return getDefaultType(getPrepaidCode());
    }

    /**
     * Gets the default commercial order.
     *
     * @return the default commercial order
     * @throws BusinessException the business exception
     */
    public InvoiceType getDefaultCommercialOrder() throws BusinessException {
        return getDefaultType(getCommercialOrderCode());
    }
    
    /**
     * Gets the commercial code.
     *
     * @return the commercial code
     */
    public String getCommercialCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.commercial.code", "COM");
    }

    /**
     * Gets list of the adjustement codes.
     *
     * @return the list of adjustement codes
     */
    public List<String> getListAdjustementCode() {
        String listAdjustmentCode = paramBeanFactory.getInstance().getProperty("invoiceType.adjustement.code", "ADJ");
        return Arrays.asList(listAdjustmentCode.split("\\s*,\\s*"));
    }

    /**
     * Gets the quote code.
     *
     * @return the quote code
     */
    public String getQuoteCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.quote.code", "QUOTE");
    }
    
    /**
     * Gets the deposit code.
     *
     * @return the deposit code
     */
    public String getDepositCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.depositInvoice.code", "ADV");
    }
    /**
     * Gets the quote code.
     *
     * @return the quote code
     */
    public String getCommercialOrderCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.commercialOrder.code", "COMMERCIAL_ORDER");
    }
    /**
     * Gets the prepaid code.
     *
     * @return the prepaid code
     */
    public String getPrepaidCode() {
        return paramBeanFactory.getInstance().getProperty("invoiceType.prepaid.code", "PREPAID");
    }

    /**
     * Get a custom field code to track invoice numbering sequence for a given invoice type.
     *
     * @param invoiceType Invoice type
     * @return A custom field code
     */
    public String getCustomFieldCode(InvoiceType invoiceType) {
        String cfName = "INVOICE_SEQUENCE_" + invoiceType.getCode().toUpperCase();
        if (getListAdjustementCode().contains(invoiceType.getCode())) {
            cfName = "INVOICE_ADJUSTMENT_SEQUENCE";
        }
        if (getCommercialCode().equals(invoiceType.getCode())) {
            cfName = "INVOICE_SEQUENCE";
        }

        return cfName;
    }

	/**
	 * @return currentInvoiceNb
	 * @throws BusinessException business exception
	 */
	public Long getCurrentGlobalInvoiceBb() throws BusinessException {		
		Provider provider = providerService.findById(Provider.CURRENT_PROVIDER_ID, true);
		Long currentInvoiceNb = provider.getInvoiceConfiguration().getCurrentInvoiceNb();
		if (currentInvoiceNb == null) {
			currentInvoiceNb = 0L;
		}
		return currentInvoiceNb;
	}

	/**
	 * @param currentInvoiceNb
	 * @throws BusinessException business exception
	 */
	public void setCurrentGlobalInvoiceBb(Long currentInvoiceNb) throws BusinessException {
		try {			
			Provider provider = providerService.findById(Provider.CURRENT_PROVIDER_ID, true);
			provider.getInvoiceConfiguration().setCurrentInvoiceNb(currentInvoiceNb);

		} catch (Exception e) {
			throw new BusinessException("Cant update global InvoiceTypeSequence : " + e.getMessage());
		}
	}
}