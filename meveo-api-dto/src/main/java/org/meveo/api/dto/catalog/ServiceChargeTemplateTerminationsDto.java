package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ServiceChargeTemplateTerminations")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateTerminationsDto implements Serializable {

	private static final long serialVersionUID = -7148418773670960365L;

	private List<ServiceChargeTemplateTerminationDto> serviceChargeTemplateTermination;

	public List<ServiceChargeTemplateTerminationDto> getServiceChargeTemplateTermination() {
		if (serviceChargeTemplateTermination == null)
			serviceChargeTemplateTermination = new ArrayList<ServiceChargeTemplateTerminationDto>();
		return serviceChargeTemplateTermination;
	}

	public void setServiceChargeTemplateTermination(
			List<ServiceChargeTemplateTerminationDto> serviceChargeTemplateTermination) {
		this.serviceChargeTemplateTermination = serviceChargeTemplateTermination;
	}

	@Override
	public String toString() {
		return "ServiceChargeTemplateTerminationsDto [serviceChargeTemplateTermination="
				+ serviceChargeTemplateTermination + "]";
	}

}
