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

import java.io.Serializable;
import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class ServiceToTerminateDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "ServiceToTerminate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToTerminateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3267838736094614395L;

    /** The service id. */
    private String serviceId;
    
    /** The termination date. */
    private Date terminationDate;

    /**
     * Gets the service id.
     *
     * @return the service id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Sets the service id.
     *
     * @param serviceId the new service id
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

}
