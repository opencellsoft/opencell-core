package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetServiceInstanceResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetServiceInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetServiceInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3293751053613636590L;

    /** The service instance. */
    private ServiceInstanceDto serviceInstance;

    /**
     * Gets the service instance.
     *
     * @return the service instance
     */
    public ServiceInstanceDto getServiceInstance() {
        return serviceInstance;
    }

    /**
     * Sets the service instance.
     *
     * @param serviceInstance the new service instance
     */
    public void setServiceInstance(ServiceInstanceDto serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public String toString() {
        return "GetServiceInstanceResponseDto [serviceInstance=" + serviceInstance + "]";
    }
}