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

/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.payments.MandatStateEnum;

/**
 * The Class MandatInfoDto.
 *
 * @author anasseh
 * @lastModifiedVersion 5.0
 */

@XmlRootElement(name = "MandatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MandatInfoDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8669878897612306520L;
    
    /** The id. */
    private String id;
    
    /** The reference. */
    private String reference;
    
    /** The state. */
    private MandatStateEnum state;
    
    /** The standard. */
    private String standard;
    
    /** The initial score. */
    private int initialScore;
    
    /** The date created. */
    private Date dateCreated;
    
    /** The date signed. */
    private Date dateSigned;
    
    /** The payment scheme. */
    private String paymentScheme;
    
    /** The bic. */
    private String bic;
    
    /** The iban. */
    private String iban;
    
    /** The bank name. */
    private String bankName;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     *
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public MandatStateEnum getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the state to set
     */
    public void setState(MandatStateEnum state) {
        this.state = state;
    }

    /**
     * Gets the standard.
     *
     * @return the standard
     */
    public String getStandard() {
        return standard;
    }

    /**
     * Sets the standard.
     *
     * @param standard the standard to set
     */
    public void setStandard(String standard) {
        this.standard = standard;
    }

    /**
     * Gets the initial score.
     *
     * @return the initialScore
     */
    public int getInitialScore() {
        return initialScore;
    }

    /**
     * Sets the initial score.
     *
     * @param initialScore the initialScore to set
     */
    public void setInitialScore(int initialScore) {
        this.initialScore = initialScore;
    }

    /**
     * Gets the date created.
     *
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the date created.
     *
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Gets the date signed.
     *
     * @return the dateSigned
     */
    public Date getDateSigned() {
        return dateSigned;
    }

    /**
     * Sets the date signed.
     *
     * @param dateSigned the dateSigned to set
     */
    public void setDateSigned(Date dateSigned) {
        this.dateSigned = dateSigned;
    }

    /**
     * Gets the payment scheme.
     *
     * @return the paymentScheme
     */
    public String getPaymentScheme() {
        return paymentScheme;
    }

    /**
     * Sets the payment scheme.
     *
     * @param paymentScheme the paymentScheme to set
     */
    public void setPaymentScheme(String paymentScheme) {
        this.paymentScheme = paymentScheme;
    }

    /**
     * Gets the bic.
     *
     * @return the bic
     */
    public String getBic() {
        return bic;
    }

    /**
     * Sets the bic.
     *
     * @param bic the bic to set
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Gets the iban.
     *
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets the iban.
     *
     * @param iban the iban to set
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Gets the bank name.
     *
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Sets the bank name.
     *
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return "MandatInfoDto [id=" + id + ", reference=" + reference + ", state=" + state + ", standard=" + standard + ", initialScore=" + initialScore + ", dateCreated="
                + dateCreated + ", dateSigned=" + dateSigned + ", paymentScheme=" + paymentScheme + ", bic=" + bic + ", iban=" + iban + ", bankName=" + bankName + "]";
    }
}
