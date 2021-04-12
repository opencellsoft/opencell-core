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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class PaymentScheduleInstancesDto.
 *
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleInstancesDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstancesDto extends SearchResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623298L;

    /** The PaymentScheduleInstances Dtos. */
    @XmlElementWrapper
    @XmlElement(name = "instance")
    @Schema(description = "list of payment schedule instance")
    private List<PaymentScheduleInstanceDto> instances = new ArrayList<>();
    
    
    
    /**
     * Instantiates a new payment schedule instances dto.
     */
    public PaymentScheduleInstancesDto() {
        
    }

    /**
     * Gets the instances.
     *
     * @return the instances
     */
    public List<PaymentScheduleInstanceDto> getInstances() {
        return instances;
    }

    /**
     * Sets the instances.
     *
     * @param instances the instances to set
     */
    public void setInstances(List<PaymentScheduleInstanceDto> instances) {
        this.instances = instances;
    }
    
    

}
