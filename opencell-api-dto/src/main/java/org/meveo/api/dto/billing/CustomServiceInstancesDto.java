package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomServiceInstancesDto implements Serializable {

    private static final long serialVersionUID = -6816362012819614661L;

    private List<CustomServiceInstanceDto> serviceInstance;

    public List<CustomServiceInstanceDto> getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(List<CustomServiceInstanceDto> serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public void addServiceInstance(CustomServiceInstanceDto serviceToAdd) {
        if (serviceInstance == null) {
            serviceInstance = new ArrayList<>();
        }

        serviceInstance.add(serviceToAdd);
    }

    @Override
    public String toString() {
        return "ServiceInstancesDto [serviceInstance=" + serviceInstance + "]";
    }
}