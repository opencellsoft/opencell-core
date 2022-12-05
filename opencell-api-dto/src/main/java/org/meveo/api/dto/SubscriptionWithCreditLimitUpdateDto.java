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

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class SubscriptionWithCreditLimitUpdateDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "SubscriptionWithCreditLimitUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitUpdateDto extends SubscriptionWithCreditLimitDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6700315102709912658L;

    /** The services to terminate. */
    private List<ServiceToTerminateDto> servicesToTerminate;

    /**
     * Gets the services to terminate.
     *
     * @return the services to terminate
     */
    public List<ServiceToTerminateDto> getServicesToTerminate() {
        return servicesToTerminate;
    }

    /**
     * Sets the services to terminate.
     *
     * @param servicesToTerminate the new services to terminate
     */
    public void setServicesToTerminate(List<ServiceToTerminateDto> servicesToTerminate) {
        this.servicesToTerminate = servicesToTerminate;
    }
}