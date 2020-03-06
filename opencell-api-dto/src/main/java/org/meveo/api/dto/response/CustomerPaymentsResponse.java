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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.PaymentDto;

/**
 * The Class CustomerPaymentsResponse.
 * 
 * @author anasseh
 * 
 */
@XmlRootElement(name = "CustomerPaymentsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerPaymentsResponse extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5831455659437348223L;

    /** The customer payment dto list. */
    private List<PaymentDto> customerPaymentDtoList;
    
    /** The balance. */
    private double balance;

    /**
     * Instantiates a new customer payments response.
     */
    public CustomerPaymentsResponse() {
        super();
    }

    /**
     * Gets the customer payment dto list.
     *
     * @return the customer payment dto list
     */
    public List<PaymentDto> getCustomerPaymentDtoList() {
        return customerPaymentDtoList;
    }

    /**
     * Sets the customer payment dto list.
     *
     * @param customerPaymentDtoList the new customer payment dto list
     */
    public void setCustomerPaymentDtoList(List<PaymentDto> customerPaymentDtoList) {
        this.customerPaymentDtoList = customerPaymentDtoList;
    }
    
    /**
     * Adds payment dto to the paymentDto list
     * 
     * @param paymentDro
     */
    public void addPaymentDto(PaymentDto paymentDro) {
        if (customerPaymentDtoList == null) {
            customerPaymentDtoList = new ArrayList<>();
        }
        customerPaymentDtoList.add(paymentDro);
    }

    /**
     * Gets the balance.
     *
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the balance.
     *
     * @param balance the new balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CustomerPaymentsResponse [customerPaymentDtoList=" + customerPaymentDtoList + ", balance=" + balance + ", toString()=" + super.toString() + "]";
    }

}
