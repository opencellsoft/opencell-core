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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CardPaymentMethodTokensDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CardPaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardPaymentMethodTokensDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6255115951743225824L;

    /** The card payment methods. */
    @XmlElementWrapper(name = "cardPaymentMethods")
    @XmlElement(name = "cardPaymentMethod")
    private List<CardPaymentMethodDto> cardPaymentMethods = new ArrayList<CardPaymentMethodDto>();

    /**
     * Instantiates a new card payment method tokens dto.
     */
    public CardPaymentMethodTokensDto() {
    }

    /**
     * Instantiates a new card payment method tokens dto.
     *
     * @param response the response
     */
    public CardPaymentMethodTokensDto(PaymentMethodTokensDto response) {
        setActionStatus(response.getActionStatus());
        for (PaymentMethodDto paymentMethodDto : response.getPaymentMethods()) {
            this.cardPaymentMethods.add(new CardPaymentMethodDto(paymentMethodDto));
        }

    }

    /**
     * Gets the card payment methods.
     *
     * @return the card payment methods
     */
    public List<CardPaymentMethodDto> getCardPaymentMethods() {
        return cardPaymentMethods;
    }

    /**
     * Sets the card payment methods.
     *
     * @param cardPaymentMethods the new card payment methods
     */
    public void setCardPaymentMethods(List<CardPaymentMethodDto> cardPaymentMethods) {
        this.cardPaymentMethods = cardPaymentMethods;
    }
}