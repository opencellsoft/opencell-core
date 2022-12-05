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
 * The Class ServiceToAddDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "ServiceToAdd")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceToAddDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3267838736094614395L;

    /** The service id. */
    private String serviceId;
    
    /** The subscription date. */
    private Date subscriptionDate;
    
    /** The param 1. */
    private String param1;
    
    /** The param 2. */
    private String param2;
    
    /** The param 3. */
    private String param3;

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
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the param 1.
     *
     * @return the param 1
     */
    public String getParam1() {
        return param1;
    }

    /**
     * Sets the param 1.
     *
     * @param param1 the new param 1
     */
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    /**
     * Gets the param 2.
     *
     * @return the param 2
     */
    public String getParam2() {
        return param2;
    }

    /**
     * Sets the param 2.
     *
     * @param param2 the new param 2
     */
    public void setParam2(String param2) {
        this.param2 = param2;
    }

    /**
     * Gets the param 3.
     *
     * @return the param 3
     */
    public String getParam3() {
        return param3;
    }

    /**
     * Sets the param 3.
     *
     * @param param3 the new param 3
     */
    public void setParam3(String param3) {
        this.param3 = param3;
    }
}
