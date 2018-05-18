package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetListServiceInstanceResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetListServiceInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListServiceInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The service instances. */
    private List<ServiceInstanceDto> serviceInstances;

    /**
     * Gets the service instances.
     *
     * @return the service instances
     */
    public List<ServiceInstanceDto> getServiceInstances() {
        return serviceInstances;
    }

    /**
     * Sets the service instances.
     *
     * @param serviceInstances the new service instances
     */
    public void setServiceInstances(List<ServiceInstanceDto> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

}
