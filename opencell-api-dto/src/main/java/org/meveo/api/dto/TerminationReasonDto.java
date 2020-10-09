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

package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.OverrideProrataEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;

import java.util.List;

/**
 * The Class TerminationReasonDto.
 *
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 */
@XmlRootElement(name = "TerminationReasonDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminationReasonDto extends BusinessEntityDto {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 6013621511892042364L;

    /**
     * The apply agreement.
     */
    private boolean applyAgreement;

    /**
     * The apply reimbursment.
     */
    private boolean applyReimbursment;

    /**
     * The apply termination charges.
     */
    private boolean applyTerminationCharges;

    /**
     * Override the prorata setting in the charge.
     */
    private OverrideProrataEnum overrideProrata;

    /**
     * reimburse Oneshots charges.
     */
    private boolean reimburseOneshots;

    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new termination reason dto.
     */
    public TerminationReasonDto() {

    }

    /**
     * Instantiates a new termination reason dto.
     *
     * @param subscriptionTerminationReason the SubscriptionTerminationReason entity
     */
    public TerminationReasonDto(SubscriptionTerminationReason subscriptionTerminationReason) {
        super(subscriptionTerminationReason);
        applyAgreement = subscriptionTerminationReason.isApplyAgreement();
        applyReimbursment = subscriptionTerminationReason.isApplyReimbursment();
        applyTerminationCharges = subscriptionTerminationReason.isApplyTerminationCharges();
        overrideProrata = subscriptionTerminationReason.getOverrideProrata();
        reimburseOneshots = subscriptionTerminationReason.isReimburseOneshots();
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(subscriptionTerminationReason.getDescriptionI18n()));
    }

    /**
     * Checks if is apply agreement.
     *
     * @return true, if is apply agreement
     */
    public boolean isApplyAgreement() {
        return applyAgreement;
    }

    /**
     * Sets the apply agreement.
     *
     * @param applyAgreement the new apply agreement
     */
    public void setApplyAgreement(boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    /**
     * Checks if is apply reimbursment.
     *
     * @return true, if is apply reimbursment
     */
    public boolean isApplyReimbursment() {
        return applyReimbursment;
    }

    /**
     * Sets the apply reimbursment.
     *
     * @param applyReimbursment the new apply reimbursment
     */
    public void setApplyReimbursment(boolean applyReimbursment) {
        this.applyReimbursment = applyReimbursment;
    }

    /**
     * Checks if is apply termination charges.
     *
     * @return true, if is apply termination charges
     */
    public boolean isApplyTerminationCharges() {
        return applyTerminationCharges;
    }

    /**
     * Sets the apply termination charges.
     *
     * @param applyTerminationCharges the new apply termination charges
     */
    public void setApplyTerminationCharges(boolean applyTerminationCharges) {
        this.applyTerminationCharges = applyTerminationCharges;
    }

    /**
     * Gets the override prorata value.
     *
     * @return the override prorata value
     */
    public OverrideProrataEnum getOverrideProrata() {
        return overrideProrata;
    }

    /**
     * Sets the override prorata value.
     *
     * @param overrideProrata the the override prorata.
     */
    public void setOverrideProrata(OverrideProrataEnum overrideProrata) {
        this.overrideProrata = overrideProrata;
    }

    /**
     * Check if refunding oneshots charges is enabled.
     *
     * @return True if refunding charge is enabled, false else.
     */
    public boolean isReimburseOneshots() {
        return reimburseOneshots;
    }

    /**
     * Enable/disable the refunding oneshot charges.
     *
     * @param reimburseOneshots
     */
    public void setReimburseOneshots(boolean reimburseOneshots) {
        this.reimburseOneshots = reimburseOneshots;
    }

    @Override
    public String toString() {
        return "TerminationReasonDto [code=" + getCode() + ", description=" + getDescription() + ", applyAgreement=" + applyAgreement + ", applyReimbursment=" + applyReimbursment
                + ", applyTerminationCharges=" + applyTerminationCharges + "]";
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}