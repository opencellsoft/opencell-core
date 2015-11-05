package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ServiceInstances")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInstancesDto implements Serializable {

	private static final long serialVersionUID = -6816362012819614661L;

	private List<ServiceInstanceDto> serviceInstance;

	public List<ServiceInstanceDto> getServiceInstance() {
		if (serviceInstance == null) {
			serviceInstance = new ArrayList<ServiceInstanceDto>();
		}

		return serviceInstance;
	}

	public void setServiceInstance(List<ServiceInstanceDto> serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	@Override
	public String toString() {
		return "ServiceInstancesDto [serviceInstance=" + serviceInstance + "]";
	}

}
