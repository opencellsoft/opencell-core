package org.meveo.api.dto.response.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.script.ServiceModelScriptDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceModelScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceModelScriptResponseDto extends BaseResponse {

	private static final long serialVersionUID = 3320673620683295748L;

	private ServiceModelScriptDto serviceModelScript;

	public ServiceModelScriptDto getServiceModelScript() {
		return serviceModelScript;
	}

	public void setServiceModelScript(ServiceModelScriptDto serviceModelScript) {
		this.serviceModelScript = serviceModelScript;
	}

	@Override
	public String toString() {
		return "ServiceModelScriptResponseDto [serviceModelScript=" + serviceModelScript + ", toString()="
				+ super.toString() + "]";
	}

}
