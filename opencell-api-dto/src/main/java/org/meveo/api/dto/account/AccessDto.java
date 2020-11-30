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

package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.IEnableDto;
import org.meveo.model.mediation.Access;

/**
 * The Class AccessDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Access")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessDto extends BaseEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6495211234062070223L;

    /** Code. */
    @XmlAttribute(required = false)
    private String code;

    /** Subscription. */
    @XmlElement(required = true)
    private String subscription;
    private Date subscriptionValidityDate;

    /** Starting date. */
    private Date startDate;

    /** Ending date. */
    private Date endDate;

    /** Custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

    /**
     * Instantiates a new access dto.
     */
    public AccessDto() {

    }

    /**
     * Instantiates a new access dto.
     *
     * @param access the Access entity
     * @param customFieldInstances the custom field instances
     */
    public AccessDto(Access access, CustomFieldsDto customFieldInstances) {
        code = access.getAccessUserId();
        startDate = access.getStartDate();
        endDate = access.getEndDate();

        if (access.getSubscription() != null) {
            subscription = access.getSubscription().getCode();
        }

        customFields = customFieldInstances;
        disabled = access.isDisabled();
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    public void setSubscriptionValidity(Date validityDate) {
        this.subscriptionValidityDate = validityDate;
    }

    public Date getSubscriptionValidityDate() {
        return subscriptionValidityDate;
    }

    @Override
    public String toString() {
        return "AccessDto [code=" + code + ", subscription=" + subscription + ", startDate=" + startDate + ", endDate=" + endDate + ", customFields=" + customFields + "]";
    }
}