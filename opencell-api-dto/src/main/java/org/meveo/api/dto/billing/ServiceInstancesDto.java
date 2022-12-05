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

package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceInstancesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInstancesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6816362012819614661L;

    /** The service instance. */
    private List<ServiceInstanceDto> serviceInstance;

    /**
     * Gets the service instance.
     *
     * @return the service instance
     */
    public List<ServiceInstanceDto> getServiceInstance() {
        return serviceInstance;
    }

    /**
     * Sets the service instance.
     *
     * @param serviceInstance the new service instance
     */
    public void setServiceInstance(List<ServiceInstanceDto> serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * Adds the service instance.
     *
     * @param serviceToAdd the service to add
     */
    public void addServiceInstance(ServiceInstanceDto serviceToAdd) {
        if (serviceInstance == null) {
            serviceInstance = new ArrayList<ServiceInstanceDto>();
        }

        serviceInstance.add(serviceToAdd);
    }

    @Override
    public String toString() {
        return "ServiceInstancesDto [serviceInstance=" + serviceInstance + "]";
    }
}