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
package org.meveo.model.payments;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "P")
public class Payment extends AccountOperation {

    private static final long serialVersionUID = 1L;
   
    /**
     * Number assigned by the Operator bank
     */
    @Column(name = "payment_order")
    private String paymentOrder;
    
   /**
    *  Amount of financial expenses exluded in the amount
    */
    @Column(name = "payment_fees")
    private BigDecimal fees = BigDecimal.ZERO; 

    /**
     * Comments Text free if litigation or special conditions
     */
    @Column(name = "comment", columnDefinition = "LONGTEXT")
    private String comment;
    

	/**
	 * @return the paymentOrder
	 */
	public String getPaymentOrder() {
		return paymentOrder;
	}

	/**
	 * @param paymentOrder the paymentOrder to set
	 */
	public void setPaymentOrder(String paymentOrder) {
		this.paymentOrder = paymentOrder;
	}

	/**
	 * @return the fees
	 */
	public BigDecimal getFees() {
		return fees;
	}

	/**
	 * @param fees the fees to set
	 */
	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
    
    

}
