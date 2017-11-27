package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInstancesDto implements Serializable {

    private static final long serialVersionUID = -6816362012819614661L;

    private List<ServiceInstanceDto> serviceInstance;

    public List<ServiceInstanceDto> getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(List<ServiceInstanceDto> serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

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
