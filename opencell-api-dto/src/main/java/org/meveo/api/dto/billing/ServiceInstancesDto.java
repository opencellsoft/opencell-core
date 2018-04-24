package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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