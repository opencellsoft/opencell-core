package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 * @created 26 Oct 2017
 */
@XmlRootElement(name = "GetListServiceInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListServiceInstanceResponseDto extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private List<ServiceInstanceDto> serviceInstances;

    public List<ServiceInstanceDto> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstanceDto> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

}
