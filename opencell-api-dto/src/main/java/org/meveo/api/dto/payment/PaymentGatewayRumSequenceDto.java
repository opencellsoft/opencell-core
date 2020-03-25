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

package org.meveo.api.dto.payment;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.model.payments.PaymentGatewayRumSequence;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class PaymentGatewayRumSequenceDto extends BusinessEntityDto {

	private static final long serialVersionUID = 2351834404897787123L;

	private GenericSequenceDto genericSequence;

	private String paymentGateway;

	public PaymentGatewayRumSequenceDto() {

	}

	public PaymentGatewayRumSequenceDto(PaymentGatewayRumSequence rumSequence) {
		paymentGateway = rumSequence.getPaymentGateway().getCode();
		genericSequence = new GenericSequenceDto(rumSequence.getGenericSequence());
	}

	public GenericSequenceDto getGenericSequence() {
		return genericSequence;
	}

	public void setGenericSequence(GenericSequenceDto genericSequence) {
		this.genericSequence = genericSequence;
	}

	public String getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

}
