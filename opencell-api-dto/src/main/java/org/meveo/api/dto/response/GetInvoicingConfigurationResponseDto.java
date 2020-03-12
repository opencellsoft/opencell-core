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

package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BillingCyclesDto;
import org.meveo.api.dto.CalendarsDto;
import org.meveo.api.dto.InvoiceCategoriesDto;
import org.meveo.api.dto.InvoiceSubCategoriesDto;
import org.meveo.api.dto.TaxesDto;
import org.meveo.api.dto.TerminationReasonsDto;

/**
 * The Class GetInvoicingConfigurationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetInvoicingConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoicingConfigurationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3000516095971053199L;

    /** The calendars. */
    private CalendarsDto calendars = new CalendarsDto();
    
    /** The taxes. */
    private TaxesDto taxes = new TaxesDto();
    
    /** The invoice categories. */
    private InvoiceCategoriesDto invoiceCategories = new InvoiceCategoriesDto();
    
    /** The invoice sub categories. */
    private InvoiceSubCategoriesDto invoiceSubCategories = new InvoiceSubCategoriesDto();
    
    /** The billing cycles. */
    private BillingCyclesDto billingCycles = new BillingCyclesDto();
    
    /** The termination reasons. */
    private TerminationReasonsDto terminationReasons = new TerminationReasonsDto();

    /**
     * Gets the calendars.
     *
     * @return the calendars
     */
    public CalendarsDto getCalendars() {
        return calendars;
    }

    /**
     * Sets the calendars.
     *
     * @param calendars the new calendars
     */
    public void setCalendars(CalendarsDto calendars) {
        this.calendars = calendars;
    }

    /**
     * Gets the taxes.
     *
     * @return the taxes
     */
    public TaxesDto getTaxes() {
        return taxes;
    }

    /**
     * Sets the taxes.
     *
     * @param taxes the new taxes
     */
    public void setTaxes(TaxesDto taxes) {
        this.taxes = taxes;
    }

    /**
     * Gets the invoice categories.
     *
     * @return the invoice categories
     */
    public InvoiceCategoriesDto getInvoiceCategories() {
        return invoiceCategories;
    }

    /**
     * Sets the invoice categories.
     *
     * @param invoiceCategories the new invoice categories
     */
    public void setInvoiceCategories(InvoiceCategoriesDto invoiceCategories) {
        this.invoiceCategories = invoiceCategories;
    }

    /**
     * Gets the invoice sub categories.
     *
     * @return the invoice sub categories
     */
    public InvoiceSubCategoriesDto getInvoiceSubCategories() {
        return invoiceSubCategories;
    }

    /**
     * Sets the invoice sub categories.
     *
     * @param invoiceSubCategories the new invoice sub categories
     */
    public void setInvoiceSubCategories(InvoiceSubCategoriesDto invoiceSubCategories) {
        this.invoiceSubCategories = invoiceSubCategories;
    }

    /**
     * Gets the billing cycles.
     *
     * @return the billing cycles
     */
    public BillingCyclesDto getBillingCycles() {
        return billingCycles;
    }

    /**
     * Sets the billing cycles.
     *
     * @param billingCycles the new billing cycles
     */
    public void setBillingCycles(BillingCyclesDto billingCycles) {
        this.billingCycles = billingCycles;
    }

    /**
     * Gets the termination reasons.
     *
     * @return the termination reasons
     */
    public TerminationReasonsDto getTerminationReasons() {
        return terminationReasons;
    }

    /**
     * Sets the termination reasons.
     *
     * @param terminationReasons the new termination reasons
     */
    public void setTerminationReasons(TerminationReasonsDto terminationReasons) {
        this.terminationReasons = terminationReasons;
    }

    @Override
    public String toString() {
        return "GetInvoicingConfigurationResponseDto [calendars=" + calendars + ", taxes=" + taxes + ", invoiceCategories=" + invoiceCategories + ", invoiceSubCategories="
                + invoiceSubCategories + ", billingCycles=" + billingCycles + ", terminationReasons=" + terminationReasons + ", toString()=" + super.toString() + "]";
    }
}