package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetServiceInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetServiceInstanceResponseDto extends BaseResponse {

	private static final long serialVersionUID = 3293751053613636590L;

	private ServiceInstanceDto serviceInstance;

	public ServiceInstanceDto getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstanceDto serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	@Override
	public String toString() {
		return "GetServiceInstanceResponseDto [serviceInstance=" + serviceInstance + "]";
	}

}
