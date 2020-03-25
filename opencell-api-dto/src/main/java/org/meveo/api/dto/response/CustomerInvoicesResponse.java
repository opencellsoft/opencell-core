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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;

/**
 * The Class CustomerInvoicesResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CustomerInvoicesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoicesResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623233L;

    /** The Customer invoice dto list. */
    private List<InvoiceDto> CustomerInvoiceDtoList;

    /**
     * Instantiates a new customer invoices response.
     */
    public CustomerInvoicesResponse() {
        super();
    }

    /**
     * Gets the customer invoice dto list.
     *
     * @return the customer invoice dto list
     */
    public List<InvoiceDto> getCustomerInvoiceDtoList() {
        return CustomerInvoiceDtoList;
    }

    /**
     * Sets the customer invoice dto list.
     *
     * @param customerInvoiceDtoList the new customer invoice dto list
     */
    public void setCustomerInvoiceDtoList(List<InvoiceDto> customerInvoiceDtoList) {
        CustomerInvoiceDtoList = customerInvoiceDtoList;
    }


    @Override
    public String toString() {
        return "CustomerInvoicesResponse [CustomerInvoiceDtoList=" + CustomerInvoiceDtoList + ", toString()=" + super.toString() + "]";
    }

}
